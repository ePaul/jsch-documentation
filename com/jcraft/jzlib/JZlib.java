/* -*-mode:java; c-basic-offset:2; -*- */
/*
Copyright (c) 2000,2001,2002,2003 ymnk, JCraft,Inc. All rights reserved.

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
/*
 * This program is based on zlib-1.1.3, so all credit should go authors
 * Jean-loup Gailly(jloup@gzip.org) and Mark Adler(madler@alumni.caltech.edu)
 * and contributors of zlib.
 */

package com.jcraft.jzlib;

/**
 * This static class holds all the constants used by the compression
 * and decompression algorithms.
 *<p>
 * There is no point in creating instances of this class.
 *</p>
 *<p>
 * The constants here are in these groups:
 *</p>
 * <dl>
 *  <dt>compression level constants:</dt>
 *  <dd>{@link #Z_NO_COMPRESSION}, {@link #Z_BEST_SPEED},
 *    {@link #Z_BEST_COMPRESSION}, {@link #Z_DEFAULT_COMPRESSION}</dd>
 *  <dt>compression strategy constants:</dt>
 *  <dd>{@link #Z_FILTERED}, {@link #Z_HUFFMAN_ONLY},
 *    {@link #Z_DEFAULT_STRATEGY}.</dd>
 *  <dt>flushing type constants:</dt>
 *  <dd>{@link #Z_NO_FLUSH}, {@link #Z_PARTIAL_FLUSH},
 *    {@link #Z_SYNC_FLUSH}, {@link #Z_FULL_FLUSH}, {@link #Z_FINISH}.</dd>
 *  <dt>return code constants:</dt>
 *  <dd>{@link #Z_OK}, {@link #Z_STREAM_END}, {@link #Z_NEED_DICT},
 *    {@link #Z_ERRNO}, {@link #Z_STREAM_ERROR}, {@link #Z_DATA_ERROR},
 *    {@link #Z_MEM_ERROR}, {@link #Z_BUF_ERROR}, {@link #Z_VERSION_ERROR}.</dd>
 * </dl>
 *<p>
 * Additionally, the {@link #version()} method provides the corresponding
 * zlib version string.
 *</p>
 *<p>
 * The documentation is partly copied or paraphrased from
 * the <a href="http://zlib.net/manual.html">zlib manual</a>.
 *</p>
 */
final public class JZlib{
  
  /**
   * The zlib version number this JZlib version is derived from.
   */
  private static final String version="1.0.2";

  /**
   * returns the zlib version number this library is derived from.
   */
  public static String version(){return version;}

  // ---------- compression level constants ---------
  /**
   * Compression level constant for no compression,
   * for use with {@link ZStream#deflateInit}.
   */
  static final public int Z_NO_COMPRESSION=0;

  /**
   * Compression level constant for best speed,
   * for use with {@link ZStream#deflateInit}.
   */
  static final public int Z_BEST_SPEED=1;

  /**
   * Compression level constant for maximal compression,
   * for use with {@link ZStream#deflateInit}.
   */
  static final public int Z_BEST_COMPRESSION=9;

  /**
   * Compression level constant for default compression,
   * for use with {@link ZStream#deflateInit}.
   */
  static final public int Z_DEFAULT_COMPRESSION=(-1);

  // --------- compression strategy constants --------

  /**
   * Compression strategy constant for filtered data.
   * For use with {@link ZStream#deflateParams}.
   *
   * Filtered data consists mostly of small values with
   * a somewhat random distribution.
   */
  static final public int Z_FILTERED=1;

  /**
   * Compression strategy constant: use only Huffman encoding
   * (no string match).
   * For use with {@link ZStream#deflateParams}.
   */
  static final public int Z_HUFFMAN_ONLY=2;

  /**
   * Compression strategy constant: default strategy.
   * For use with {@link ZStream#deflateParams}.
   * Use this for normal data.
   */
  static final public int Z_DEFAULT_STRATEGY=0;

  // ------ flushing type constants ----------

  /**
   * Flushing type constant: no flush.
   * For use with {@link ZStream#deflate} and {@link ZStream#inflate}.
   *
   * This should be used normally for compression, since it allows
   * the library to decide how much data to collect before compressing
   * it, maximizing the compression.
   *
   * For decompression, this will produce as much output as
   * the library deems appropriate.
   */
  static final public int Z_NO_FLUSH=0;

  /**
   * Flushing type constant: partial flush.
   * For use with {@link ZStream#deflate} and {@link ZStream#inflate}.
   *
   * For compression, this will flush all output to far to the output
   * buffer, completing the block and following it with an empty block.
   * The output is not necessarily aligned with a byte boundary, but the
   * empty block makes sure that the data so far is included in full bytes
   * in the output.
   */
  static final public int Z_PARTIAL_FLUSH=1;

  /**
   * Flushing type constant: synchronizing flush.
   * For use with {@link ZStream#deflate} and {@link ZStream#inflate}.
   *
   * For compression, this will flush all output so far to the output
   * buffer and align it on a byte boundary, so everything which was
   * input before can be consumed at the output side.
   * It finishes the current block and adds an empty block.
   *
   * This should not be used to often, as it degrades compression
   * for some algorithms.
   *
   * For decompression, this requests to flush as much output as possible
   * to the output buffer.
   */
  static final public int Z_SYNC_FLUSH=2;

  /**
   * Flushing type constant: full flush.
   * For use with {@link ZStream#deflate} and {@link ZStream#inflate}.
   *
   * For compression, this flushes all output like {@link #Z_SYNC_FLUSH},
   * and then resets the compression state, so decompression can restart from
   * here (with inflateSync) if the data is damaged.
   *
   * Don't use this too often, as it seriously degrades performance.
   */
  static final public int Z_FULL_FLUSH=3;

  /**
   * Flushing type constant: finish.
   * For use with {@link ZStream#deflate} and {@link ZStream#inflate}.
   *
   * For compression, this flushes all output and then finishes up the
   * stream. If there is enough output space, then the stream must be either
   * closed or reset.
   *
   * For decompression, this will make sure that the stream will be
   * decompressed completely, returning {@link #Z_BUF_ERROR} if there
   * is not enough output space.
   */
  static final public int Z_FINISH=4;

  // ------------ return code constants ----------

  /**
   * Return code constant for inflate/deflate (and most other methods): Okay.
   * This means there was some input processed or output produced.
   *
   * For configuration methods, this means that the call succeeded.
   */
  static final public int Z_OK=0;
  /**
   * Return code constant for inflate/deflate: stream end.
   *
   * For compression, this means that after giving {@link #Z_FINISH} as
   * the flush parameter, all input was processed and completely flushed
   * to the output.
   *
   * For decompression, this means that the end of the compressed stream
   * is reached and fully decompressed.
   */
  static final public int Z_STREAM_END=1;

  /**
   * Return code constant for inflate: need dictionary.
   * 
   * This means that {@link ZStream#inflateSetDictionary} should be called.
   * The Adler32 checksum of the needed dictionary is in {@link ZStream#adler}.
   */
  static final public int Z_NEED_DICT=2;

  /**
   * Return code constant for errors outside of the compression library.
   * This inheritance from zlib seems not to be used by JZlib.
   * (It means "see the errno global variable for information
   *  about the error".)
   */
  static final public int Z_ERRNO=-1;

  /**
   * Return code constant for deflateInit, deflate, deflateEnd, inflateInit,
   * inflate, inflateEnd, deflateSetDictionary, deflateParams,
   * inflateSetDictionary, inflateSync,
   *
   * This means the stream state is inconsistent, or the parameters are not
   * valid (for the configuration methods).
   */
  static final public int Z_STREAM_ERROR=-2;

  /**
   * Return code constant for deflateEnd, inflate, inflateSetDictionary,
   * inflateSync.
   *
   * This means that the compressed data doesn't conform to the format,
   * the dictionary has not the right checksum, or similar.
   */
  static final public int Z_DATA_ERROR=-3;
  /**
   * Return code constant, not used by JZlib.
   *
   * In zlib this was returned if some memory allocation did not succeed.
   * In Java this would give an {@link OutOfMemoryError} instead.
   */
  static final public int Z_MEM_ERROR=-4;

  /**
   * Return code constant for deflate, inflate, deflateParams, inflateSync.
   *
   * This means that no progress could be made because there is either
   * no free output space or no unused input (and such is needed).
   */
  static final public int Z_BUF_ERROR=-5;

  /**
   * Return code constant, not used by JZlib.
   *
   * In zlib this is returned by inflateInit or deflateInit if the library
   * detects some version incompatibility. This is not really possible in
   * Java because of the separate compilation.
   */
  static final public int Z_VERSION_ERROR=-6;

  // TODO: make constructor private?

}
