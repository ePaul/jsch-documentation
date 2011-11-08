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

import java.net.*;
import java.io.*;

/**
 * A factory for ServerSockets. This interface works similarly to
 * {@link javax.net.ServerSocketFactory}, but does not depend on
 * the 1.4 Java version.
 * <p>
 *   An application may want to implement this interface to have control
 *   over the server socket creation for local port forwardings, for example
 *   to configure the server socket (or the Sockets it creates) before use.
 * </p>
 *
 * @see SocketFactory
 * @see Session#setPortForwardingL(String, int, String, int,
 *                                 ServerSocketFactory)
 */
public interface ServerSocketFactory{

  /**
   * Creates a ServerSocket.
   * @param port the port to listen on.
   * @param backlog the number of not-yet accepted connections which
   *     may be waiting at the same time before new ones will be rejected.
   * @param bindAddr the local network interface the socket will be
   *      listening on. If {@code null}, listen on all local addresses.
   * @throws IOException if some network error occured, like the port
   *      was already in use.
   */
  public ServerSocket createServerSocket(int port, int backlog,
                                         InetAddress bindAddr) throws IOException;
}
