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
 * Provides a way to prompt the user for {@code keyboard-interactive}
 *  authentication.
 * This interface will be implemented by applications in
 *  {@link UserInfo} implementations to support {@code keyboard-interactive}
 * authentication as defined in RFC 4256.
 *<p>
 * Additionally, it is used in case of password-based authorization when the
 * server requests a password change.
 *</p>
 * <p>
 *   Most of the examples include an implementation of this
 *   interface based on Swings {@link javax.swing.JOptionPane}.
 * </p>
 *
 * @see <a href="http://tools.ietf.org/html/rfc4256">RFC 4256:
 *       Generic Message Exchange Authentication for
 *          the Secure Shell Protocol (SSH)</a>
 */
public interface UIKeyboardInteractive{

  /**
   * Retrieves answers from the user to a number of questions.
   * 
   *
   * @param destination identifies the user/host pair where we want to login.
   *        (This was not sent by the remote side).
   * @param name the name of the request (could be shown in the
   *       window title). This may be empty.
   * @param instruction an instruction string to be shown to the user.
   *             This may be empty, and may contain new-lines.
   * @param prompt a list of prompt strings.
   * @param echo for each prompt string, whether to show the
   *      texts typed in ({@code true}) or to mask them ({@code false}).
   *     This array will have the same length as {@code prompt}.
   *
   * @return the answers as given by the user. This must be an array of
   *     same length as {@code prompt}, if the user confirmed.
   *    If the user cancels the input, the return value should be
   *    {@code null}.
   *
   * @see <a href="http://tools.ietf.org/html/rfc4256#section-3.2">RFC 4256,
   *    3.2.  Information Requests</a>
   * @see <a href="http://tools.ietf.org/html/rfc4256#section-3.3">RFC 4256,
   *    3.3.  User Interface</a>
   */
  String[] promptKeyboardInteractive(String destination,
				     String name,
				     String instruction,
				     String[] prompt,
				     boolean[] echo);
}
