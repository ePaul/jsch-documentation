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
 * A random number generator.
 *<p>
 * The library used an object (or in fact, multiple ones in different places)
 * implementing this interface to generate random numbers. These are used for
 * several purposes:
 *</p><ul>
 * <li>to create key pairs </li>
 * <li>To salt the known hosts entries in the hashed format</li>
 * <li>For random padding of a packet before encrypting</li>
 * <li>For the random session cookie</li>
 *</ul>
 *<p>
 *  The library will choose the implementation class by the configuration
 *  option {@code random}, and instantiate it using the no-argument
 *  constructor.
 *</p>
 *<p>
 *  The library includes a default implementation, based on a
 *  {@link java.security.SecureRandom new SecureRandom()}.
 *</p>
 *<p>
 *  An application might implement this interface to provide an alternative
 *  random number generator, maybe based on some hardware device.
 *</p>
 */
public interface Random{

  /**
   * Fills a segment of a byte array with random bits.
   * @param foo the array to put the random data into.
   * @param start the position in the array from where on the random data
   *        should be put.
   * @param len the length of the segment to be filled with random data,
   *        in bytes. There will be {@code 8 * len} random bits generated.
   */
  void fill(byte[] foo, int start, int len);
}
