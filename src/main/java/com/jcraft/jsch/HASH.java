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
 * The interface for a (cryptographic) Hash algorithm.
 *<p>
 * This is a slimmed-down version of {@link java.security.MessageDigest}.
 *</p>
 *<p>
 * Several parts of the library will look up the implementation class to
 * use in the {@linkplain JSch#setConfig configuration} and then instantiate
 * it using the no-argument constructor. The used algorithm names are only
 * "md5" and "sha-1", for now.
 *</p>
 *<p>
 *  The library includes default implementations of MD5 and SHA-1 based
 *  on Java's MessageDigest.
 *</p>
 */
public interface HASH{
  /**
   * initializes the algorithm for new input data.
   * This will be called before the first call to {@link #update}.
   */
  void init() throws Exception;

  /**
   * returns the size of the hash which will be produced from input.
   * This usually will be a constant function, like 16 for MD5 and
   * 20 for SHA1.
   * @return the length of the produced hash, in bytes.
   * @see java.security.MessageDigest#getDigestLength MessageDigest.getDigestLength()
   */
  int getBlockSize();

  /**
   * Updates the algorithm with new data.
   * The data will not be changed, only our internal state.
   * @param foo an array containing new data to hash.
   * @param start the index of the start of the data in {@code foo}.
   * @param len the length of the new added data.
   * @see java.security.MessageDigest#update(byte[], start, len) MessageDigest.update()
   */
  void update(byte[] foo, int start, int len) throws Exception;

  /**
   * calculates and returns the digest for all the data
   * hashed up so far.
   * @return an array containing the hash. This will have length
   *   {@link #getBlockSize}.
   * @see java.security.MessageDigest#digest() MessageDigest.digest()
   */
  byte[] digest() throws Exception;
}
