/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2002-2011 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.jcraft.jsch;

import java.io.*;

/**
 * A Channel which allows forwarding a pair of local
 * streams to/from a TCP-connection to a server on the
 * remote side.
 *
 * This class is used internally by the local port forwarding,
 * but it also can be used directly by client software
 * to forward a pair of InputStream and OutputStream.
 *
 * @see Session#openChannel Session.openChannel("direct-tcpip")
 * @see Session#setPortForwardingL(String, int, String, int)
 * @see <a href="http://tools.ietf.org/html/rfc4254#section-7.2">RFC 4254, section 7.2 TCP/IP Forwarding Channels</a>
 */
public class ChannelDirectTCPIP extends Channel{

  static private final int LOCAL_WINDOW_SIZE_MAX=0x20000;
  static private final int LOCAL_MAXIMUM_PACKET_SIZE=0x4000;

  String host;
  int port;

  String originator_IP_address="127.0.0.1";
  int originator_port=0;

  ChannelDirectTCPIP(){
    super();
    setLocalWindowSizeMax(LOCAL_WINDOW_SIZE_MAX);
    setLocalWindowSize(LOCAL_WINDOW_SIZE_MAX);
    setLocalPacketSize(LOCAL_MAXIMUM_PACKET_SIZE);
  }

  void init (){
    try{ 
      io=new IO();
    }
    catch(Exception e){
      System.err.println(e);
    }
  }

  /**
   * opens the channel.
   */
  public void connect() throws JSchException{
    try{
      Session _session=getSession();
      if(!_session.isConnected()){
        throw new JSchException("session is down");
      }
      Buffer buf=new Buffer(150);
      Packet packet=new Packet(buf);
      // send
      // byte   SSH_MSG_CHANNEL_OPEN(90)
      // string channel type         //
      // uint32 sender channel       // 0
      // uint32 initial window size  // 0x100000(65536)
      // uint32 maxmum packet size   // 0x4000(16384)

      packet.reset();
      buf.putByte((byte)90);
      buf.putString(Util.str2byte("direct-tcpip"));
      buf.putInt(id);
      buf.putInt(lwsize);
      buf.putInt(lmpsize);
      buf.putString(Util.str2byte(host));
      buf.putInt(port);
      buf.putString(Util.str2byte(originator_IP_address));
      buf.putInt(originator_port);
      _session.write(packet);

      int retry=10;
      long start=System.currentTimeMillis();
      long timeout=connectTimeout;
      if(timeout!=0L) retry = 1;
      synchronized(this){
        while(this.getRecipient()==-1 &&
              _session.isConnected() &&
               retry>0){
          if(timeout>0L){
            if((System.currentTimeMillis()-start)>timeout){
              retry=0;
              continue;
            }
          }
          try{
            long t = timeout==0L ? 5000L : timeout;
            if(_session.jsch.getLogger().isEnabled(Logger.DEBUG)) {
              _session.jsch.getLogger().log(Logger.DEBUG, "waiting max. "+t+" ms for channel reply ...");
            }
            this.notifyme=1;
            wait(t);
          }
          catch(java.lang.InterruptedException e){ 
          }
          finally{ 
            this.notifyme=0;
          }
          retry--;
        }
        if(_session.jsch.getLogger().isEnabled(Logger.DEBUG)) {
          _session.jsch.getLogger().log(Logger.DEBUG, "... finished waiting for channel reply");
        }
      }
      if(!_session.isConnected()){
	throw new JSchException("session is down");
      }
      if(this.getRecipient()==-1){  // timeout
        throw new JSchException("channel is not opened (timeout).");
      }
      if(this.open_confirmation==false){  // SSH_MSG_CHANNEL_OPEN_FAILURE
        throw new JSchException("channel is not opened (failure).");
      }

      connected=true;

      if(io.in!=null){
        thread=new Thread(this);
        thread.setName("DirectTCPIP thread "+_session.getHost());
        if(_session.daemon_thread){
          thread.setDaemon(_session.daemon_thread);
        }
        thread.start();
      }
    }
    catch(Exception e){
      io.close();
      io=null;
      Channel.del(this);
      if (e instanceof JSchException) {
        throw (JSchException) e;
      }
    }
  }


  /**
   * Not for external use - the channel transfer loop.
   */
  public void run(){

    Buffer buf=new Buffer(rmpsize);
    Packet packet=new Packet(buf);
    int i=0;

    try{
      Session _session=getSession();
      while(isConnected() &&
            thread!=null && 
            io!=null && 
            io.in!=null){
        i=io.in.read(buf.buffer, 
                     14, 
                     buf.buffer.length-14
                     -Session.buffer_margin
                     );

        if(i<=0){
          eof();
          break;
        }
        if(close)break;
        packet.reset();
        buf.putByte((byte)Session.SSH_MSG_CHANNEL_DATA);
        buf.putInt(recipient);
        buf.putInt(i);
        buf.skip(i);
        _session.write(packet, this, i);
      }
    }
    catch(Exception e){
    }
    disconnect();
    //System.err.println("connect end");
  }

  /**
   * Sets the InputStream to be forwarded. Everything read from this stream
   * is forwarded to the remote server.
   *
   * This should be called before {@link #connect}.
   */
  public void setInputStream(InputStream in){
    io.setInputStream(in);
  }

  /**
   * Sets the OutputStream to be forwarded. Everything sent by the remote
   * server will be written to this stream.
   *
   * This should be called before {@link #connect}.
   */
  public void setOutputStream(OutputStream out){
    io.setOutputStream(out);
  }

  /**
   * Sets the remote host name (or IP address) to connect to
   * (which should be valid at the remote side).
   *
   * This should be called before {@link #connect}.
   * @see #setPort
   */
  public void setHost(String host){this.host=host;}

  /**
   * Sets the remote port number to connect to.
   *
   * This should be called before {@link #connect}.
   * @see #setHost
   */
  public void setPort(int port){this.port=port;}

  /**
   * Sets the local originator IP address we pretend the connection
   * came from. The default value is {@code "127.0.0.1"}.
   *
   * This should be called before {@link #connect}.
   * @see #setOrgPort
   */
  public void setOrgIPAddress(String foo){this.originator_IP_address=foo;}

  /**
   * Sets the local originator port number we pretend the connection
   * came from. The default value is {@code 0}.
   *
   * This should be called before {@link #connect}.
   * @see #setOrgIPAddress
   */
  public void setOrgPort(int foo){this.originator_port=foo;}
}
