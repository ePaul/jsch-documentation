/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2015-2016 ymnk, JCraft,Inc. All rights reserved.

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
 * An interface with the mathematical operations needed for the Elliptic Curve Diffie Hellman key exchanges.
 * Might be implemented to provide optimized operations for some curve.
 * Its implementation class name needs to be given to {@link Jsch#setConfig Jsch.setConfig("ecdh-sha2-nistp")}.}
 */
public interface ECDH {

  /**
   * Initializes this instance for key pairs of a specific size.
   */
  void init(int size) throws Exception;

  /**
   * calculates and returns the shared secret for this key exchange.
   * @param r the x coordinate of the remote partner's point, encoded as a byte[].
   * @param s the y coordinate of the remote partner's point, encoded as a byte[].
   * @return the shared secret, in the form of a byte[].
   * @throws Exception if anything goes wrong.
   */
  byte[] getSecret(byte[] r, byte[] s) throws Exception;

  /**
   * Retrieves the public key (i.e. an elliptic curve point) to be sent to the remote side.
   * @return the point, encoded as a byte[].
   * @throws Exception if anything goes wrong.
   */
  byte[] getQ() throws Exception;

  /**
   * Validates a public key (i.e. an elliptic curve point) sent by the remote side.
   * @param r the x coordinate of the point, encoded as a byte[].
   * @param s the y coordinate of the point, encoded as a byte[].
   * @return true if the given point is actually on the curve, false otherwise.
   * @throws Exception if anything goes wrong.
   */
  boolean validate(byte[] r, byte[] s) throws Exception;
}
