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
 * A user identity for public-key authentication.
 * This object encapsulates a key pair and the signature algorithm.
 * It is used by the Session objects on connecting to authenticate
 * to the server.
 *<p>
 *  The library contains a default implementation which is used by
 *  {@link JSch#addIdentity(String) JSch.addIdentity()} when used
 *  with non-Identity parameters.
 *</p>
 *
 * @see JSch#addIdentity(Identity, byte[])
 * @see <a href="http://tools.ietf.org/html/rfc4252#section-7">RFC 4252,
 *    section 7.  Public Key Authentication Method: "publickey"</a>
 */
public interface Identity{

  /**
   * returns the name of this identity. This is only used by the
   * library for bookkeeping purposes (and allows the application
   * to {@link JSch#removeIdentity} remove an identity), is not sent
   * to the server.
   */
  public String getName();

  /**
   * Returns the name of the algorithm. This will be sent together with
   * the public key to the server for authorization purposes. The server
   * will use the signature checking algorithm to check the signature.
   */
  public String getAlgName();

  /**
   * Returns the public key data. This will be sent to the server, which
   * then will check the key is authorized for this user (and whether
   * the signature is done with the corresponding private key).
   */
  public byte[] getPublicKeyBlob();


  /**
   * Checks whether the private key is encrypted.
   * @return {@code true} if the key is encrypted, i.e. a call
   *   of {@link #setPassphrase} is needed, {@code false} if the key
   *   is ready to be used (e.g. {@link #getSignature} can be called).
   */
  public boolean isEncrypted();


  /**
   * Provides a passphrase to decrypt the private key.
   * @return {@code true} if the passphrase was right and
   *   {@link #getSignature} can now be used, {@code false} if
   *   the passphrase was wrong.
   */
  public boolean setPassphrase(byte[] passphrase) throws JSchException;

  /**
   * Not to be called by the application.
   *
   * I see no reason for this method to be in the interface at all - it is
   * never used, and not clear what it does. 
   */
  // in IdentityFile it is called from {@link #setPassphrase} to do
  // the actual decryption, but it can not reasonably called from the
  // outside, and should be private there, too.  -- P.E.
  public boolean decrypt();


  /**
   * Signs some data with our private key and signature algorithm.
   * @return a signature of {@code data}, or {@code null} if there
   *    was some problem.
   */
  public byte[] getSignature(byte[] data);

  /**
   * Clears all data related to the private key.
   * This will be called by the library when the identity
   * is removed.
   */
  public void clear();
}
