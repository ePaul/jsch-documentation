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
 * A DSA signing or signature checking algorithm.
 * <p>
 *  This interface is a slimmed down and specialized (on DSA) version
 *  of {@link java.security.Signature}.
 * </p>
 * <p>
 *  It will be used by the library to check the server's signature
 *  during key exchange, and to prove our own possession of the 
 *  private key for public-key authentication in the default {@link Identity}
 *  implementation.
 * </p>
 * <p>
 *   The library will choose the implementation class by the configuration
 *   option {@code signature.dsa}, and instantiate it using the no-argument
 *   constructor. For signature checking, the usage would look like this:
 * </p>
 *<pre>
 *  sig = class.newInstance();
 *  sig.init();
 *  sig.setPubKey(y, p, q, g);
 *  sig.update(H); // maybe more than once
 *  boolean ok = sig.verify(sig_of_H);
 *</pre>
 *<p>For signing, the usage would look like this:</p>
 *<pre>
 *  sig = class.newInstance();
 *  sig.init();
 *  sig.setPrvKey(x, p, q, g);
 *  sig.update(H); // maybe more than once
 *  byte[] sig_of_H = sig.sign();
 *</pre>
 * <p>
 *   The library contains a default implementation based on
 *   {@link java.security.Signature}.
 * </p>
 * @see SignatureRSA
 */
public interface SignatureDSA{
  void init() throws Exception;

  /**
   * Sets the public key and prepares this signature object
   * for signature verifying.
   * @param y the public key {@code y = g^x mod p}.
   * @param p the <em>big prime</em>
   * @param q the <em>small prime</em>
   * @param g the generator
   * @see KeyPairGenDSA
   */
  void setPubKey(byte[] y, byte[] p, byte[] q, byte[] g) throws Exception;

  /**
   * Sets the private key and prepares this signature object
   * for signing.
   * @param x the private key
   * @param p the <em>big prime</em>
   * @param q the <em>small prime</em>
   * @param g the generator
   * @see KeyPairGenDSA
   */
  void setPrvKey(byte[] x, byte[] p, byte[] q, byte[] g) throws Exception;

  /**
   * adds some more data to be signed/verified.
   * @param H the array containing the data to be verified.
   */
  void update(byte[] H) throws Exception;

  /**
   * Verifies that the given signature is a correct signature.
   * @param sig an array containing the signature for the data
   *   given by {@link #update}.
   * @return true if the signature is correct,
   *    false if the signature is not correct.
   */
  boolean verify(byte[] sig) throws Exception;

  /**
   * Signs the data given so far to the {@link #update} method.
   * @return a signature for the data.
   */
  byte[] sign() throws Exception;
}
