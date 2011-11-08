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
 * An implementation of the Cipher {@code none}, i.e. unencrypted transport.
 * This is used during key-exchange until the first real Cipher can be used.
 *
 * <blockquote>
 *  The "none" algorithm specifies that no encryption is to be done.
 *  Note that this method provides no confidentiality protection, and it
 *  is NOT RECOMMENDED.  Some functionality (e.g., password
 *  authentication) may be disabled for security reasons if this cipher
 *  is chosen.
 * </blockquote> 
 *
 * The implementation here consists mainly of no-ops.
 * 
 * @see <a href="http://tools.ietf.org/html/rfc4253#section-6.3">RFC 4253,
 *    section 6.3 Encryption</a>
 * @see <a href="http://tools.ietf.org/html/rfc2410">RFC 2410,
 *     The NULL Encryption Algorithm and Its Use With IPsec</a>
 */
public class CipherNone implements Cipher{
  private static final int ivsize=8;
  private static final int bsize=16;
  public int getIVSize(){return ivsize;} 
  public int getBlockSize(){return bsize;}
  public void init(int mode, byte[] key, byte[] iv) throws Exception{
  }
  public void update(byte[] foo, int s1, int len, byte[] bar, int s2) throws Exception{
  }
  public boolean isCBC(){return false; }
}
