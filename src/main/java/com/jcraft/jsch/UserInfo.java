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
 * Allows user interaction.
 * The application can provide an implementation of this interface to
 * the {@link Session} to allow for feedback to the user and
 * retrieving information (e.g. passwords, passphrases or a confirmation)
 * from the user.
 *<p>
 *  If an object of this interface also implements
 *   {@link UIKeyboardInteractive}, it can also be used for
 *   keyboard-interactive authentication as described in
 *   RFC 4256.
 *</p>
 * <p>
 *   Most of the examples include an implementation of this
 *   interface based on Swings {@link javax.swing.JOptionPane}.
 * </p>
 *
 * @see Session#setUserInfo
 */
public interface UserInfo{

  /**
   * Prompts the user for a password used for authentication for
   * the remote server.
   * @param message the prompt string to be shown to the user.
   * @return true if the user entered a password. This password then
   *   can be retrieved by {@link #getPassword}.
   */
  boolean promptPassword(String message);


  /**
   * Returns the password entered by the user.
   * This should be only called after a successful {@link #promptPassword}.
   */
  String getPassword();

  /**
   * Prompts the user for a passphrase for a public key.
   * @param message the prompt message to be shown to the user.
   * @return true if the user entered a passphrase. The passphrase then can
   *   be retrieved by {@link #getPassphrase}.
   */
  boolean promptPassphrase(String message);

  /**
   * Returns the passphrase entered by the user.
   * This should be only called after a successful {@link #promptPassphrase}.
   */
  String getPassphrase();

  /**
   * Prompts the user to answer a yes-no-question.
   *<p>
   * Note: These are currently used to decide whether to create nonexisting
   *   files or directories, whether to replace an existing host key, and
   *   whether to connect despite a non-matching key.
   *</p>
   * @param message the prompt message to be shown to the user.
   * @return {@code true} if the user answered with "Yes", else {@code false}.
   */
  boolean promptYesNo(String message);

  /**
   * Shows an informational message to the user.
   * @param message the message to show to the user.
   */
  void showMessage(String message);
}
