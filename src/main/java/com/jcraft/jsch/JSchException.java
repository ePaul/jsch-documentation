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
 * Will be thrown if anything goes wrong with the SSH protocol.
 */
public class JSchException extends Exception{
  // we reimplement the 'cause'/getCause mechanism
  // because we want to be usable with pre-1.4
  // JREs, too. (I suppose.)  -- P.E.

  //private static final long serialVersionUID=-1319309923966731989L;
  private Throwable cause=null;
  /**
   * Creates a JSchException without message. 
   */
  public JSchException () {
    super();
  }

  /**
   * Creates a JSchException with message.
   */
  public JSchException (String s) {
    super(s);
  }

  /**
   * Creates a JSchException with message and cause
   * @param s the message to be shown to the user.
   * @param e a nested Throwable, which indicates the cause of this Exception.
   */
  public JSchException (String s, Throwable e) {
    super(s);
    this.cause=e;
  }

  /**
   * retrieves the cause.
   */
  public Throwable getCause(){
    return this.cause;
  }
}
