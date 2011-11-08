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
import java.net.Socket;

/**
 * Allows routing connections through some proxy.
 * <p>
 *  A Proxy object creates a Socket and it's two streams for a remote
 *  server. It typically does this by first connecting to a proxy server,
 *  negiotiating some conditions (or providing a password) and then returning
 *  the streams as the connection will be forwarded to the target host.
 * </p>
 * <p>
 *  The library (i.e. {@link Session}) uses a proxy only if given with
 *  {@link Session#setProxy setProxy} before connecting. When connecting,
 *  it will use the methods defined in this interface (except close), and
 *  it will invoke {@link #close} on disconnecting.
 * </p>
 * <p>
 *  Some implementing classes for common proxy types are delivered with
 *  the library: {@link ProxyHTTP}, {@link ProxySOCKS4}, {@link ProxySOCKS5}.
 *  An application might also create their own implementations and provide
 *  these to the session before connecting.
 * </p>
 * @see Session#setProxy
 */
public interface Proxy {

  /**
   * Opens a connection to the target server.
   * After successful invocation of this method the other methods
   * can be called to retrieve the results.
   * @param socket_factory a factory for sockets. Might be {@code null}, then
   *    the implementation will use plain sockets.
   * @param host the SSH server host we want to connect to.
   * @param port the port at the SSH server.
   * @param timeout how long to wait maximally for a connection, in
   *    milliseconds. If {@code 0}, wait as long as needed.
   * @throws Exception if it was not possible to create the connection to
   *   the target host for some reason.
   */
  void connect(SocketFactory socket_factory, String host, int port, int timeout) throws Exception;

  /**
   * Returns an InputStream to read data from the remote server.
   * If the SSH protocol is tunneled through another protocol for
   * proxying purposes, this InputStream has to do the unwrapping.
   */
  InputStream getInputStream();
  /**
   * Returns an OutputStream to write data to the remote server.
   * If the SSH protocol is tunneled through another protocol for
   * proxying purposes, this OutputStream has to do the wrapping.
   */
  OutputStream getOutputStream();

  /**
   * Returns the socket used for the connection.
   * This will only be used for timeout-configurations.
   */
  Socket getSocket();

  /**
   * Closes the connection. This should close the underlying socket as well.
   */
  void close();
}
