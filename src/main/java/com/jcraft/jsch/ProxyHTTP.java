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
import java.net.*;


/**
 * A {@link Proxy} implementation using a HTTP proxy.
 *
 * This uses the HTTP CONNECT method as described in Sections 5.2 and 5.3
 * of RFC 2817.
 * @see
 *   <a href="http://tools.ietf.org/html/draft-luotonen-web-proxy-tunneling-01">
 *   Internet Draft <em>Tunneling TCP based protocols through Web proxy
 *    servers</em> (Ari Luotonen, expired 1999)</a>
 * @see <a href="http://tools.ietf.org/html/rfc2817#section-5">RFC 2817,
 *     Section 5. Upgrade across Proxies</a>
 *
 */
public class ProxyHTTP implements Proxy{
  private static int DEFAULTPORT=80;
  private String proxy_host;
  private int proxy_port;
  private InputStream in;
  private OutputStream out;
  private Socket socket;

  private String user;
  private String passwd;

  /**
   * Creates a new ProxyHTTP object.
   * @param proxy_host the proxie's host name, maybe including the port
   *    number separated by {@code :}. (The default port is 80.)
   */
  public ProxyHTTP(String proxy_host){
    int port=DEFAULTPORT;
    String host=proxy_host;
    // do we really need to call  indexOf(':')  three times? -- P.E.
    if(proxy_host.indexOf(':')!=-1){
      try{
	host=proxy_host.substring(0, proxy_host.indexOf(':'));
	port=Integer.parseInt(proxy_host.substring(proxy_host.indexOf(':')+1));
      }
      catch(Exception e){
      }
    }
    this.proxy_host=host;
    this.proxy_port=port;
  }

  /**
   * Creates a new ProxyHTTP object.
   * @param proxy_host the proxie's host name.
   * @param proxy_port the port number of the proxy.
   */
  public ProxyHTTP(String proxy_host, int proxy_port){
    this.proxy_host=proxy_host;
    this.proxy_port=proxy_port;
  }

  /**
   * Sets the user name and password needed for authentication
   * to the proxy. This has no relation to any authentication on
   * the target server.
   *<p>
   *  If the proxy needs authentication, this method should be called
   *  before calling {@link #connect} (i.e. before passing the Proxy
   *  object to the JSch library).
   *  This class supports only Basic Authentication as defined in RFC 2617,
   *  i.e. sending user name and password in plaintext. (Both will be
   *  encoded using first UTF-8 and then Base64.)
   *</p>
   * @param user the user name
   * @param passwd the password.
   * @see <a href="http://tools.ietf.org/html/rfc2617#section-2">RFC 2617,
   *  section 2 Basic Authentication Scheme</a>
   */
  public void setUserPasswd(String user, String passwd){
    this.user=user;
    this.passwd=passwd;
  }

  // javadoc will be copied from interface. -- P.E.
  public void connect(SocketFactory socket_factory, String host, int port, int timeout) throws JSchException{
    try{
      if(socket_factory==null){
        socket=Util.createSocket(proxy_host, proxy_port, timeout);    
        in=socket.getInputStream();
        out=socket.getOutputStream();
      }
      else{
        socket=socket_factory.createSocket(proxy_host, proxy_port);
        in=socket_factory.getInputStream(socket);
        out=socket_factory.getOutputStream(socket);
      }
      if(timeout>0){
        socket.setSoTimeout(timeout);
      }
      socket.setTcpNoDelay(true);

      out.write(Util.str2byte("CONNECT "+host+":"+port+" HTTP/1.0\r\n"));

      if(user!=null && passwd!=null){
	byte[] code=Util.str2byte(user+":"+passwd);
	code=Util.toBase64(code, 0, code.length);
	out.write(Util.str2byte("Proxy-Authorization: Basic "));
	out.write(code);
	out.write(Util.str2byte("\r\n"));
      }

      out.write(Util.str2byte("\r\n"));
      out.flush();

      int foo=0;

      StringBuffer sb=new StringBuffer();
      while(foo>=0){
        foo=in.read(); if(foo!=13){sb.append((char)foo);  continue;}
        foo=in.read(); if(foo!=10){continue;}
        break;
      }
      if(foo<0){
        throw new IOException();
      }

      String response=sb.toString(); 
      String reason="Unknow reason";
      int code=-1;
      try{
        foo=response.indexOf(' ');
        int bar=response.indexOf(' ', foo+1);
        code=Integer.parseInt(response.substring(foo+1, bar));
        reason=response.substring(bar+1);
      }
      catch(Exception e){
      }
      if(code!=200){
        throw new IOException("proxy error: "+reason);
      }

      /*
      while(foo>=0){
        foo=in.read(); if(foo!=13) continue;
        foo=in.read(); if(foo!=10) continue;
        foo=in.read(); if(foo!=13) continue;      
        foo=in.read(); if(foo!=10) continue;
        break;
      }
      */

      int count=0;
      while(true){
        count=0;
        while(foo>=0){
          foo=in.read(); if(foo!=13){count++;  continue;}
          foo=in.read(); if(foo!=10){continue;}
          break;
        }
        if(foo<0){
          throw new IOException();
        }
        if(count==0)break;
      }
    }
    catch(RuntimeException e){
      throw e;
    }
    catch(Exception e){
      try{ if(socket!=null)socket.close(); }
      catch(Exception eee){
      }
      String message="ProxyHTTP: "+e.toString();
      if(e instanceof Throwable)
        throw new JSchException(message, (Throwable)e);
      throw new JSchException(message);
    }
  }

  // javadoc will be copied from interface. -- P.E.
  public InputStream getInputStream(){ return in; }

  // javadoc will be copied from interface. -- P.E.
  public OutputStream getOutputStream(){ return out; }

  // javadoc will be copied from interface. -- P.E.
  public Socket getSocket(){ return socket; }

  // javadoc will be copied from interface. -- P.E.
  public void close(){
    try{
      if(in!=null)in.close();
      if(out!=null)out.close();
      if(socket!=null)socket.close();
    }
    catch(Exception e){
    }
    in=null;
    out=null;
    socket=null;
  }


  /**
   * returns the default proxy port - this is 80 as defined for HTTP.
   */
  public static int getDefaultPort(){
    return DEFAULTPORT;
  }
}
