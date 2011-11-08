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
 * An object supporting compression and decompression of data streams.
 * If during key exchange some compression protocol was negiotated, this
 * protocol name is looked up in the
 *  {@linkplain Session#getConfig session configuration} to retrieve the
 * class name to be used for compressing/decompressing the data stream.
 *
 * Then an instance is created with the no-argument constructor, and
 * initialized with the {@link #init} method.
 *
 * One Compression object will be used either for compression
 * or decompression, not both.
 */
public interface Compression{

  /**
   * Constant for inflating (decompressing) mode.
   */
  static public final int INFLATER=0;

  /**
   * Constant for deflating (compressing) mode.
   */
  static public final int DEFLATER=1;

  /**
   * Initializes the compression engine.
   * @param type one of {@link #INFLATER} or {@link #DEFLATER}.
   *    In the first case the library later will only call {@link #uncompress},
   *    in the second case only {@link #compress}.
   * @param level the compression level. This is only relevant for the
   *    {@link #DEFLATER} mode.
   */
  void init(int type, int level);

  /**
   * Compresses a chunk of data.
   *
   * @param buf the buffer containing the uncompressed data.
   * @param start the position in the buffer where the uncompressed
   *      data starts. At the same position we will put the compressed
   *      data.
   * @param len an array, containing in {@code len[0]} the length of the
   *    uncompressed chunk. After returning, this will contain the length
   *    of the compressed version of this chunk.
   * @return an array containing the compressed data. This will either be
   *  {@code buf} (if there was enough space), or a new array.
   */
  byte[] compress(byte[] buf, int start, int[] len);

  /**
   * Uncompresses a chunk of data.
   *
   * @param buf the buffer containing the compressed data. We will put
   *        the uncompressed data in the same buffer, if there is enough
   *        space. Otherwise, there will be a new buffer created.
   * @param start the position in {@code buf} where the chunk of
   *      compressed data starts. 
   * @param len an array, containing in {@code len[0]} the length of the
   *      compressed chunk. After returning, it will contain the
   *      length of the uncompressed version of this chunk.
   * @return either {@code buf} (if there was enough space for the
   *    uncompressed data) or a new buffer containing all the data
   *    from {@code buf} before {@code start} and then the result of 
   *    uncompressing the compressed chunk.
   */
  byte[] uncompress(byte[] buf, int start, int[] len);
}
