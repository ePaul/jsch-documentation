/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2005-2011 ymnk, JCraft,Inc. All rights reserved.

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


/**
 * A channel connected to a subsystem of the server process.
 *
 * Such a channel is created with:
 *  <pre>
 *    ChannelExec channel = (ChannelExec)session.{@link Session#openChannel openChannel}("subsystem");
 *    channel.{@link #setSubsystem setSubsystem}(cmd);
 *  </pre>
 *
 * @see <a href="http://tools.ietf.org/html/rfc4254#section-6.5">RFC 4254,
 *   section 6.5.  Starting a Shell or a Command</a>
 * @see ChannelSftp
 */
public class ChannelSubsystem extends ChannelSession{
  boolean xforwading=false;
  boolean pty=false;
  boolean want_reply=true;
  String subsystem="";
  // javadoc is copied from superclass -- P.E.
  public void setXForwarding(boolean foo){ xforwading=foo; }
  // javadoc is copied from superclass -- P.E.
  public void setPty(boolean foo){ pty=foo; }

  /**
   * Indicates whether we want a confirmation/error reply to this request.
   * (I have no idea what the difference is, and the method is called
   *  neither in the library nor the examples.)
   * The default is true.
   */
  public void setWantReply(boolean foo){ want_reply=foo; }

  /**
   * Sets the name of the remote subsystem name.
   * @param foo the name of the subsystem. It should consist of only
   *   printable ASCII characters and be a subsystem name recognized
   *   by the remote server process.
   * @see <a href="http://tools.ietf.org/html/rfc4250#section-4.6.1">RFC 4250,
   *  section 4.6.1 Conventions for Names</a>
   */
  public void setSubsystem(String foo){ subsystem=foo; }


  // javadoc is copied from superclass -- P.E.
  public void start() throws JSchException{
    Session _session=getSession();
    try{
      // could have used super.sendRequests() here,
      // instead of reimplementing it.
      Request request;
      if(xforwading){
        request=new RequestX11();
        request.request(_session, this);
      }
      if(pty){
	request=new RequestPtyReq();
	request.request(_session, this);
      }
      request=new RequestSubsystem();
      ((RequestSubsystem)request).request(_session, this, subsystem, want_reply);
    }
    catch(Exception e){
      if(e instanceof JSchException){ throw (JSchException)e; }
      if(e instanceof Throwable)
        throw new JSchException("ChannelSubsystem", (Throwable)e);
      throw new JSchException("ChannelSubsystem");
    }
    if(io.in!=null){
      thread=new Thread(this);
      thread.setName("Subsystem for "+_session.host);
      if(_session.daemon_thread){
        thread.setDaemon(_session.daemon_thread);
      }
      thread.start();
    }
  }

  void init() throws JSchException {
    io.setInputStream(getSession().in);
    io.setOutputStream(getSession().out);
  }

  /**
   * Sets the error stream. The standard error output of the remote
   * process will be sent to this stream.
   *
   * The stream will be closed on {@link #disconnect}.
   */
  public void setErrStream(java.io.OutputStream out){
    setExtOutputStream(out);
  }

  /**
   * Gets the error stream. The standard error output of the
   * remote process can be read from this stream.
   * 
   * This method is a polling alternative to {@link #setErrStream}.
   */
  public java.io.InputStream getErrStream() throws java.io.IOException {
    return getExtInputStream();
  }
}
