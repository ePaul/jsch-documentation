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


/**
 * Usually not to be used by applications.
 * The base class for all User authentication methods supported
 * by this SSH implementation.
 *
 * The actual method will be negiotiated between client and server,
 * and then we'll look up the implementation class by the configuration
 * option with the choosen method as a name.
 */
public abstract class UserAuth{
  protected static final int SSH_MSG_USERAUTH_REQUEST=               50;
  protected static final int SSH_MSG_USERAUTH_FAILURE=               51;
  protected static final int SSH_MSG_USERAUTH_SUCCESS=               52;
  protected static final int SSH_MSG_USERAUTH_BANNER=                53;
  protected static final int SSH_MSG_USERAUTH_INFO_REQUEST=          60;
  protected static final int SSH_MSG_USERAUTH_INFO_RESPONSE=         61;
  protected static final int SSH_MSG_USERAUTH_PK_OK=                 60;

  protected UserInfo userinfo;
  protected Packet packet;
  protected Buffer buf;
  protected String username;

  /**
   * Will be called by the Session to do the authentication.
   * Subclasses will override this method and do the actual
   * authentication, using the session's read/write methods.
   *<p>
   * This implementation fills the protected variables {@link #username},
   * {@link #packet}, {@link #buf} and {@link #userinfo} and returns
   * {@code true}. Subclasses will usually call {@code super.start(session)}
   * as first statement in the implementation.
   *</p>
   * @return true if the authentication was successful, else false.
   * @throws JSchPartialAuthException if the authentication was partially
   *   successful, i.e. not yet sufficient to login, but enough to continue
   *   with more methods.
   */
  public boolean start(Session session) throws Exception{
    this.userinfo=session.getUserInfo();
    this.packet=session.packet;
    this.buf=packet.getBuffer();
    this.username=session.getUserName();
    return true;
  }
}
