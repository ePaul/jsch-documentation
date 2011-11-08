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
 * A Message Authentication Code algorithm which will be used by the library
 * to make sure the messages are not tampered with.
 * <p>
 *  This interface is a slimmed-down version of {@link javax.crypto.Mac}.
 *</p>
 * <p>
 *   The library gets the implementation class from a configuration option
 *   (with the name of the algorithm as key) and then instantiates an object
 *   using the no-argument constructor. The algorithms to be used are
 *   negotiated during key exchange.
 * <p>
 * <p>
 *   The library includes two implementations for each of the algorithms
 *   {@code hmac-sha1}, {@code hmac-sha1-96}, {@code hmac-md5},
 *   {@code hmac-md5-96}, one based on JCE's {@link javax.crypto.Mac} class
 *   (available in Java SE from 1.4 on) and another one done manually
 *   (using {@link java.security.MessageDigest}).
 *   The latter ones are by default used on {@code Mac OS X}, the former ones
 *   on other systems.
 * </p>
 * @see <a href="http://tools.ietf.org/html/rfc4253#section-6.4">RFC 4253,
 *   Section 6.4, Data Integrity</a>
 * @see <a href="http://tools.ietf.org/html/rfc2104">RFC 2104,
 *    HMAC: Keyed-Hashing for Message Authentication</a>
 * @see javax.crypto.Mac
 */
public interface MAC {

  /**
   * The name of the algorithm, as defined in RFC 4253.
   * @return for the build-in algorithms, one of {@code hmac-sha1},
   *  {@code hmac-sha1-96}, {@code hmac-md5} and {@code hmac-md5-96}.
   */
  String getName();

  /**
   * The size of the produced MAC, i.e. the digest length, in bytes.
   */
  int getBlockSize(); 

  /**
   * Initializes the MAC, providing the key.
   */
  void init(byte[] key) throws Exception;

  /**
   * Updates the MAC with some data.
   * @param foo an array containing the data to authenticate.
   * @param start the position in {@code foo} where the data starts.
   * @param len the length of the data.
   */
  void update(byte[] foo, int start, int len);

  /**
   * Updates the MAC with 4 bytes of data.
   * @param foo a 32 bit value, which will be interpreted as
   *    4 bytes in big-endian order.
   */
  void update(int foo);

  /**
   * Finalizes the production of the digest,
   * producing the digest value.
   * @param buf an array to put the authentication code into.
   * @param offset the position in {@code buf} where the output
   *   should begin.
   */
  void doFinal(byte[] buf, int offset);
}
