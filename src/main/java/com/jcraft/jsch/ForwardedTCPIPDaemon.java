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
 * A local deamon <em>process</em> executed when a host connects to
 * to a forwarded port at the remote side.
 * An application should implement this interface if it wants to handle
 * such connections internally instead of forwarding them to another
 * host/port on the local side.
 *<p>
 * All implementations should provide a no-argument constructor, as this
 * one is used when creating an instance.
 *</p>
 *<p>
 * When someone connects to the remote socket, we create an instance using
 * the no-argument constructor, then call
 *  {@link #setChannel setChannel} (with the streams connected to the
 *  remote socket) and  {@link #setArg setArg} with the arguments given
 *  when the channel was created. Then we create a new Thread executing the
 *  {@link #run} method.
 *</p>
 *
 * @see Session#setPortForwardingR(String, int, String, Object[])
 */
public interface ForwardedTCPIPDaemon extends Runnable{

  /**
   * Sets the streams to be used for communication.
   * This method should not block (or try to read/write from/to these streams),
   * all interaction should be done in the {@link #run} method.
   * @param channel the channel connected to the remote socket.
   *    This object may be used to disconnect, for example.
   * @param in  all data arriving from the remote socket can be read
   *     from this stream.
   * @param out all data written to this stream will be sent to the
   *     remote socket.
   */
  void setChannel(ChannelForwardedTCPIP channel, InputStream in, OutputStream out);

  /**
   * Sets additional arguments given when the forwarding was created.
   *
   * @param arg arguments to be used by the deamon, the meaning is application
   *   specific. We should not change this array, as all subsequent
   *   daemons for this same port forwarding will be affected.
   *
   * @see Session#setPortForwardingR(String, int, String, Object[])
   */
  void setArg(Object[] arg);

  /**
   * Does the actual connection handling. This method will be run in
   * an own thread (the others will be called from the channel's
   *  {@link Channel#connect connect()}) and should close the channel
   *  at the end (otherwise we will have a dangling connection, if the
   *  remote client host does not close it).
   */
  public void run();
}
