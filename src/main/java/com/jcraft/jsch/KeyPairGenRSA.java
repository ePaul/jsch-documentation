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
 * A generator for an RSA key pair.
 *<p>
 * The library contains a default implementation of this class, based on
 * the JCE classes available in Java SE from 1.4.
 *</p>
 *<p>
 * The actual implementation class is chosen by the configuration option
 * {@code "keypairgen.rsa"}, and then instantiated using the no-argument
 * constructor. The library uses each instance only for one key generation.
 *</p>
 *<p>
 * This object creates a key pair consisting of a public key
 *  ({@link #getE e}, {@link #getN N}) and a private key
 *  ({@link #getD d}, {@link #getN N}),
 *   with <code>e * d = 1 (mod &phi;(N))</code>.
 *</p>
 *<p> It also provides an
 *  alternative form of the private key
 *  ({@link #getP p}, {@link #getQ q}, {@link #getEP eP},
 *   {@link #getEQ eQ}, {@link #getC c}),
 *  with {@code p}, {@code q} primes, {@code N = p * q},
 *  {@code eP = d mod p-1}, {@code eQ = e mod q-1},
 *  and {@code c * q = 1 mod p} (i.e. {@code c} is an inverse of {@code q}
 *  modulo {@code p}).
 *  This form allows more efficient signing and
 *  decryption operations, using the Chinese Remainder Theorem.
 *</p>
 * @see java.security.interfaces.RSAPrivateCrtKey
 * @see java.security.interfaces.RSAPublicKey
 * @see <a href="http://tools.ietf.org/html/rfc3447#section-3">RFC 3447,
 *    Public-Key Cryptography Standards (PKCS) #1: RSA Cryptography
 *                      Specifications Version 2.1, Section 3: Key types</a>
 */
public interface KeyPairGenRSA{

  /**
   * Generates a new key pair.
   * The other methods can then be called to retrieve
   * the key components.
   * @param key_size the number of bits of the key to be produced.
   */
  void init(int key_size) throws Exception;

  /**
   * The decryption exponent {@code d}.
   */
  byte[] getD();

  /**
   * The encryption exponent {@code e}.
   */
  byte[] getE();

  /**
   * The modulus of the key-pair.
   */
  byte[] getN();

  /**
   * The chinese remainder coefficient c,
   * i.e. the inverse of q modulo p.
   */
  byte[] getC();

  /**
   * The exponent to use modulo p.
   */
  byte[] getEP();

  /**
   * The exponent to use modulo q.
   */
  byte[] getEQ();

  /**
   * The prime p.
   */
  byte[] getP();

  /**
   * The prime q.
   */
  byte[] getQ();
}
