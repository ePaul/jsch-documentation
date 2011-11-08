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
 *
 * A cipher object encapsulates some encryption or decryption algorithm.
 *<p>
 * The Cipher implementations used by the library can be selected using
 * configuration options. The package {@code com.jcraft.jsch.jce} contains
 * such implementations based on the cryptographic algorithms given by the
 * Java Cryptography Extension (JCE), these are the default values of the
 * concerning options.
 *</p>
 */
public interface Cipher{

  /**
   * Encryption mode constant for {@link #init}.
   */
  static int ENCRYPT_MODE=0;

  /**
   * Decryption mode constant for {@link #init}.
   */
  static int DECRYPT_MODE=1;

  /**
   * Returns the size of the identity vector for this cipher.
   */
  int getIVSize(); 

  /**
   * Returns the block size of this algorithm.
   */
  int getBlockSize(); 

  /**
   * Initializes the Cipher object for a new encryption
   * or decryption operation.
   * @param mode one of {@link #ENCRYPT_MODE} or {@link #DECRYPT_MODE}.
   * @param key the key to use for the crypting operation.
   * @param iv the initialization vector necessary for operation.
   */
  void init(int mode, byte[] key, byte[] iv) throws Exception;

  /**
   * Encrypts or decrypts some more data.
   * @param input the array from which the plaintext (for encrypting) or
   *    ciphertext (for decrypting) should be taken.
   * @param inOffset the position in {@code input} at which the data is
   *    to be found.
   * @param len the length of the input in bytes. The same number of output
   *    bytes will be produced.
   * @param output the array into which the ciphertext (for encrypting) or
   *    plaintext (for decrypting) will be written.
   * @param outOffset the position in {@code output} from which on the data
   *    should be written.
   */
  void update(byte[] input, int inOffset, int len,
              byte[] output, int outOffset) throws Exception;

  /**
   * Checks whether this cipher is in Cipher Block Chaining mode.
   * @return true if this cipher is in CBC mode,
   *   false if this cipher is in some other mode.
   */
  boolean isCBC();
}
