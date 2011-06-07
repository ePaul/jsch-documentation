/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
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
 * The core compression class: A pair of buffers with
 * some metadata for compression or decompression, and
 * the necessary compression/decompression methods.
 *
 * This corresponds to the
 * <a href="http://zlib.net/manual.html#Stream">z_stream_s</a>
 * data structure (and z_streamp pointer type) in zlib, together
 * with most of the (non-utility) functions defined in zlib.h.
 */
final public class ZStream{

  static final private int MAX_WBITS=15;        // 32K LZ77 window
  static final private int DEF_WBITS=MAX_WBITS;

  static final private int Z_NO_FLUSH=0;
  static final private int Z_PARTIAL_FLUSH=1;
  static final private int Z_SYNC_FLUSH=2;
  static final private int Z_FULL_FLUSH=3;
  static final private int Z_FINISH=4;

  static final private int MAX_MEM_LEVEL=9;

  static final private int Z_OK=0;
  static final private int Z_STREAM_END=1;
  static final private int Z_NEED_DICT=2;
  static final private int Z_ERRNO=-1;
  static final private int Z_STREAM_ERROR=-2;
  static final private int Z_DATA_ERROR=-3;
  static final private int Z_MEM_ERROR=-4;
  static final private int Z_BUF_ERROR=-5;
  static final private int Z_VERSION_ERROR=-6;

  /**
   * The input buffer. The library will read from here.
   *
   * This may be updated by the application when providing more data.
   */
  public byte[] next_in;     // next input byte
  /**
   * The index of the next input byte in {@link #next_in}.
   * This will be updated (incremented) by the library as it is
   * reading data from the buffer.
   *
   * It might be reset by the application when providing more data.
   */
  public int next_in_index;

  /**
   * The number of bytes available for reading in {@link #next_in}.
   * (This must not be more than {@code next_in.length - next_in_index}.
   *
   * This must be updated by the application when providing more data.
   *
   * This will be updated (decremented) by the library when reading
   * data from the buffer.
   */
  public int avail_in;       // number of bytes available at next_in

  /**
   * The total number of bytes read so far.
   * This is updated (incremented) by the library on each call of
   * {@link #inflate} and {@link #deflate} (if it reads data) and can
   * be used by the application for statistics purposes. It is not
   * used by the library.
   */
  public long total_in;      // total nb of input bytes read so far

  /**
   * The output buffer.
   *
   * The library will put output data into this buffer at
   *  {@link #next_out_index}.
   * The application will have to read the output from this buffer.
   */
  public byte[] next_out;    // next output byte should be put there

  /**
   * The position of the next output byte in {@link #next_out_index}.
   * This will be updated (incremented) by the library when producing
   * output.
   * It might be reset by the application after reading the output.
   */
  public int next_out_index;

  /**
   * The remaining available space in the output buffer.
   * This will be updated (decremented) by the library when producing
   * output.
   * It might be reset by the application after reading output.
   */
  public int avail_out;      // remaining free space at next_out

  /**
   * The total number of bytes produced so far.
   * This is updated (incremented) by the library on each call
   * of {@link #inflate} and {@link #deflate} (if it produces data)
   * and can be used by the application for statistics purposes.
   * It is not used by the library.
   */
  public long total_out;     // total nb of bytes output so far

  /**
   * last produced error message, in human readable form (english).
   * {@code null} if there was no error.
   */
  public String msg;

  /**
   * The compressing state, used by deflate-related methods.
   */
  Deflate dstate; 

  /**
   * The decompressing state, used by inflate-related methods.
   */
  Inflate istate; 

  int data_type; // best guess about the data type: ascii or binary

  /**
   * The Adler32 checksum of the uncompressed data read (or
   * decompressed) so far.
   *<p>
   * If {@link #inflate} returned {@link JZlib#Z_NEED_DICT Z_NEED_DICT},
   * this field contains the Adler32 checksum of the needed dictionary.
   *</p>
   */
  public long adler;
  Adler32 _adler=new Adler32();

  /**
   * Initializes the stream for decompression (inflating), using
   * the default (maximum) window size and the zlib format.
   */
  public int inflateInit(){
    return inflateInit(DEF_WBITS);
  }

  /**
   * Initializes the stream for decompression (inflating), using
   * the default (maximum) window size.
   * @param nowrap if {@code true}, the stream uses the plain deflate
   *    format. If {@code false}, the stream uses the {@code zlib} format
   *   (which includes a header and checksum).
   */
  public int inflateInit(boolean nowrap){
    return inflateInit(DEF_WBITS, nowrap);
  }

  /**
   * Initializes the stream for decompression (inflating), using
   * zlib format.
   * @param w the base two logarithm of the window size (e.g. the size
   *   of the history buffer). This should be in the range {@code 8 .. 15},
   *   resulting in window sizes between 256 and 32768 bytes.
   *   This must be as least as large as the window size used
   *   for compressing.
   */
  public int inflateInit(int w){
    return inflateInit(w, false);
  }

  /**
   * Initializes the stream for decompression (inflating).
   * @param w the base two logarithm of the window size (e.g. the size
   *   of the history buffer). This should be in the range {@code 8 .. 15},
   *   resulting in window sizes between 256 and 32768 bytes.
   *   This must be as least as large as the window size used
   *   for compressing.
   * @param nowrap if {@code true}, the stream uses the plain deflate
   *    format. If {@code false}, the stream uses the {@code zlib} format
   *   (which includes a header and checksum).
   */
  public int inflateInit(int w, boolean nowrap){
    istate=new Inflate();
    return istate.inflateInit(this, nowrap?-w:w);
  }

  /**
   * {@code inflate} decompresses as much data as possible, and stops when
   * the input buffer becomes empty or the output buffer becomes full.
   * It may introduce some output latency (reading input without producing
   * any output) except when forced to flush.
   *<p>
   *  The detailed semantics are as follows. {@code inflate} performs one or
   *  both of the following actions:
   *</p>
   *<ul>
   *  <li>Decompress more input from {@link #next_in}, starting
   *    at {@link #next_in_index}, and update {@link #next_in_index} and
   *    {@link #avail_in} accordingly. If not all input can be processed
   *    (because there is not enough room in the output buffer),
   *    {@link #next_in_index} is updated and processing will resume
   *     at this point for the next call of inflate().</li>
   *  <li>Provide more output in {@link #next_out}, starting
   *     at {@link #next_out_index} and update {@link #next_out_index}
   *     and {@link #avail_out} accordingly. {@code inflate()} provides
   *     as much output as possible, until there is no more input data
   *     or no more space in the output buffer (see below about the
   *     {@code flush} parameter).</li>
   *</ul>
   *<p>
   *  Before the call of {@code inflate()}, the application should ensure
   *  that at least one of the actions is possible, by providing more
   *  input and/or consuming more output, and updating the {code next_*_index}
   *  and {@code avail_*} values accordingly. The application can consume
   *  the uncompressed output when it wants, for example when the output
   *  buffer is full ({@link #avail_out}{@code == 0}), or after each call
   *  of {@code inflate()}. If {@code inflate} returns {@link JZlib#Z_OK Z_OK}
   *  and with zero {@link #avail_out}, it must be called again after
   *  making room in the output buffer because there might be more output
   *  pending.
   *</p>
   *<p>
   *  If a preset dictionary is needed after this call (see
   *  {@link #inflateSetDictionary}), {@code inflate} sets {@link #adler}
   *  to the adler32 checksum of the dictionary chosen by the compressor
   *  and returns {@link JZlib#Z_NEED_DICT Z_NEED_DICT}; otherwise it
   *  sets {@link #adler}
   *  to the adler32 checksum of all output produced so far (that is,
   *  {@link #total_out} bytes) and returns {@link JZlib#Z_OK Z_OK},
   *  {@link JZlib#Z_STREAM_END Z_STREAM_END} or an error code as
   *  described below.
   *  At the end of the stream (for zlib format), {@code inflate()} checks
   *  that its computed adler32 checksum is equal to that saved by the
   *  compressor and returns {@link JZlib#Z_STREAM_END Z_STREAM_END} only if the
   *  checksum is correct.
   *</p>
   *<p>
   *  {@code inflate()} will decompress and check either zlib-wrapped
   *   or plain deflate data, depending on the inflateInit method used
   *  (and its {@code nowrap} parameter).
   *</p>
   * @param flush one of
   *    {@link JZlib#Z_NO_FLUSH Z_NO_FLUSH},
   *    {@link JZlib#Z_SYNC_FLUSH Z_SYNC_FLUSH}, and
   *    {@link JZlib#Z_FINISH Z_FINISH}.
   * <p>
   *  {@link JZlib#Z_NO_FLUSH Z_NO_FLUSH} is the usual value for
   *  non-interactive usage.
   * </p>
   * <p>
   *  {@link JZlib#Z_SYNC_FLUSH Z_SYNC_FLUSH} requests that {@code inflate()}
   *  flush as much output as possible to the output buffer.
   * </p>
   * <p>
   *  {@code inflate()} should normally be called until it returns
   *  {@link JZlib#Z_STREAM_END Z_STREAM_END} or an error. However if
   *  all decompression is to be performed in a single step (a single
   *  call of {@code inflate}), the parameter {@code flush} should be set
   *  to {@link JZlib#Z_FINISH Z_FINISH}.
   *  In this case all pending input is processed and all pending output
   *  is flushed; {@link #avail_out} must be large enough to hold all
   *  the uncompressed data. (The size of the uncompressed data may have
   *  been saved by the compressor for this purpose.) The next operation
   *  on this stream must
   *  be {@link #inflateEnd} to deallocate the decompression state. The use
   *  of {@link JZlib#Z_FINISH Z_FINISH} is never required, but can be
   *  used to inform
   *  {@code inflate} that a faster approach may be used for the single
   *  {@code inflate()} call. 
   *</p>
   *<p>
   * In this implementation, {@code inflate()} always flushes as much output as
   * possible to the output buffer, and always uses the faster approach
   * on the first call. So the only effect of the flush parameter in this
   * implementation is on the return value of {@code inflate()}, as noted
   * below.</p>
   * @return <ul>
   *  <li>{@link JZlib#Z_OK Z_OK} if some progress has been made (more input
   *    processed or more output produced),</li>
   *  <li>{@link JZlib#Z_STREAM_END Z_STREAM_END} if the end of the
   *    compressed data has been reached and all uncompressed output
   *    has been produced (and for the zlib format, the checksum matches),</li>
   *  <li>{@link JZlib#Z_NEED_DICT Z_NEED_DICT} if a preset dictionary is
   *    needed at this point,</li>
   *  <li>{@link JZlib#Z_DATA_ERROR Z_DATA_ERROR} if the input data
   *    was corrupted (input stream not conforming to the zlib format
   *    or incorrect check value),</li>
   *  <li>{@link JZlib#Z_STREAM_ERROR Z_STREAM_ERROR} if the stream structure
   *    was inconsistent (for example if {@link #next_in} or
   *    {@link #next_out} was {@code null}),</li>
   *  <li>{@link JZlib#Z_BUF_ERROR Z_BUF_ERROR} if no progress is possible
   *    or if there was not enough room in the output buffer when
   *    {@link JZlib#Z_FINISH Z_FINISH} is used.</li>
   * </ul>
   * <p>
   *   Note that {@link JZlib#Z_BUF_ERROR Z_BUF_ERROR} is not fatal, and
   *   {@code inflate()} can be called again with more input and more output
   *   space to continue decompressing. If
   *   {@link JZlib#Z_DATA_ERROR Z_DATA_ERROR} is
   *   returned, the application may then call {@link #inflateSync()} to
   *   look for a good compression block if a partial recovery of the
   *   data is desired.
   * </p>
   */
  public int inflate(int flush){
    if(istate==null) return Z_STREAM_ERROR;
    return istate.inflate(this, flush);
  }

  /**
   * All dynamically allocated data structures for this stream are freed.
   * This function discards any unprocessed input and does not flush any
   * pending output. 
   *
   * @return {@link JZlib#Z_OK Z_OK} if success,
   *    {@link JZlib#Z_STREAM_ERROR Z_STREAM_ERROR} if the stream state
   *    was inconsistent. In the error case, {@link #msg} may be set.
   */
  public int inflateEnd(){
    if(istate==null) return Z_STREAM_ERROR;
    int ret=istate.inflateEnd(this);
    istate = null;
    return ret;
  }

  /**
   * Skips invalid compressed data until a full flush point (see above
   *  the description of {@link #deflate} with
   *  {@link JZlib#Z_FULL_FLUSH Z_FULL_FLUSH}) can be found, or until all
   *  available input is skipped. No output is provided.
   *<p>
   *  In the success case, the application may save the current current
   *  value of {@link #total_in} which indicates where valid compressed
   *  data was found. In the error case, the application may repeatedly
   *  call {@link #inflateSync}, providing more input each time, until
   *  success or end of the input data.
   *</p>
   * @return 
   *   {@link JZlib#Z_OK Z_OK} if a full flush point has been found,
   *   {@link JZlib#Z_BUF_ERROR Z_BUF_ERROR} if no more input was provided,
   *   {@link JZlib#Z_DATA_ERROR Z_DATA_ERROR} if no flush point has been
   *   found, or
   *   {@link JZlib#Z_STREAM_ERROR Z_STREAM_ERROR} if the stream structure
   *   was inconsistent.
   */
  public int inflateSync(){
    if(istate == null)
      return Z_STREAM_ERROR;
    return istate.inflateSync(this);
  }

  /**
   * Initializes the decompression dictionary from the given uncompressed
   * byte sequence.
   * <p>
   *  This function must be called immediately after a call of {@link #inflate},
   *  if that call returned {@link JZlib#Z_NEED_DICT Z_NEED_DICT}. The
   *  dictionary chosen by the compressor can be determined from the
   *  adler32 value returned by that call of inflate. The compressor and
   *  decompressor must use exactly the same dictionary (see
   * {@link #deflateSetDictionary}).
   * </p>
   * <p>
   *  For raw inflate (i.e. decompressing plain deflate data without
   *  a zlib header), this
   *  function can be called immediately after {@link #inflateInit} and
   *  before any call of {@link #inflate} to set the dictionary.
   *  The application must insure that the dictionary that was used for
   *  compression is provided.
   * </p>
   * <p>
   *  {@code inflateSetDictionary} does not perform any decompression:
   *  this will be done by subsequent calls of {@link #inflate}.
   * </p>
   * @param dictionary an array containing uncompressed data to use as the
   *   dictionary for future {@link #inflate} calls.
   * @param dictLength the length of the data in the array.
   * @return  {@link JZlib#Z_OK Z_OK} if success,
   *    {@link JZlib#Z_STREAM_ERROR Z_STREAM_ERROR} if a parameter
   *    is invalid (such as {@code null} dictionary) or the stream
   *    state is inconsistent,
   *   {@link JZlib#Z_DATA_ERROR Z_DATA_ERROR} if the given dictionary
   *    doesn't match the expected one (incorrect adler32 value).
   */
  public int inflateSetDictionary(byte[] dictionary, int dictLength){
    if(istate == null)
      return Z_STREAM_ERROR;
    return istate.inflateSetDictionary(this, dictionary, dictLength);
  }

  /**
   * initializes the stream for deflation in zlib format,
   * using the given compression level and the maximum lookback window size.
   * @param level the deflation level. This should be
   *   {@link JZlib#Z_NO_COMPRESSION Z_NO_COMPRESSION},
   *   {@link JZlib#Z_DEFAULT_COMPRESSION Z_DEFAULT_COMPRESSION} or a
   *   value between {@link JZlib#Z_BEST_SPEED Z_BEST_SPEED} (1) and
   *  {@link JZlib#Z_BEST_COMPRESSION Z_BEST_COMPRESSION} (9) (both inclusive).
   */
  public int deflateInit(int level){
    return deflateInit(level, MAX_WBITS);
  }
  
  /**
   * initializes the stream for deflation,
   * using the given compression level and the maximum lookback window size.
   * @param level the deflation level. This should be
   *   {@link JZlib#Z_NO_COMPRESSION Z_NO_COMPRESSION},
   *   {@link JZlib#Z_DEFAULT_COMPRESSION Z_DEFAULT_COMPRESSION} or a
   *   value between {@link JZlib#Z_BEST_SPEED Z_BEST_SPEED} (1) and
   *   {@link JZlib#Z_BEST_COMPRESSION Z_BEST_COMPRESSION} (9) (both inclusive).
   * @param nowrap if {@code true}, the stream uses the plain deflate
   *    format. If {@code false}, the stream uses the {@code zlib} format
   *    (which includes a header and checksum).
   */
  public int deflateInit(int level, boolean nowrap){
    return deflateInit(level, MAX_WBITS, nowrap);
  }
  /**
   * initializes the stream for deflation in zlib format,
   * using the given compression level and lookback window size.
   * @param level the deflation level. This should be
   *   {@link JZlib#Z_NO_COMPRESSION Z_NO_COMPRESSION},
   *   {@link JZlib#Z_DEFAULT_COMPRESSION Z_DEFAULT_COMPRESSION} or a
   *   value between {@link JZlib#Z_BEST_SPEED Z_BEST_SPEED} (1) and
   *   {@link JZlib#Z_BEST_COMPRESSION Z_BEST_COMPRESSION} (9) (both inclusive).
   * @param bits the base two logarithm of the window size (e.g. the size
   *   of the history buffer). This should be in the range {@code 8 .. 15},
   *   resulting in window sizes between 256 and 32768 bytes.
   *   Larger values result in better compression with more memory usage.
   */
  public int deflateInit(int level, int bits){
    return deflateInit(level, bits, false);
  }

  /**
   * Initializes the stream for deflation,
   * using the given compression level and given lookback window size.
   * @param level the deflation level. This should be
   *   {@link JZlib#Z_NO_COMPRESSION Z_NO_COMPRESSION},
   *   {@link JZlib#Z_DEFAULT_COMPRESSION Z_DEFAULT_COMPRESSION} or a
   *   value between {@link JZlib#Z_BEST_SPEED Z_BEST_SPEED} (1) and
   *   {@link JZlib#Z_BEST_COMPRESSION Z_BEST_COMPRESSION} (9) (both inclusive).
   * @param bits the base two logarithm of the window size (e.g. the size
   *   of the history buffer). This should be in the range {@code 8 .. 15},
   *   resulting in window sizes between 256 and 32768 bytes.
   *   Larger values result in better compression with more memory usage.
   * @param nowrap if {@code true}, the stream uses the plain deflate
   *    format. If {@code false}, the stream uses the {@code zlib} format
   *   (which includes a header and checksum).
   */
  public int deflateInit(int level, int bits, boolean nowrap){
    dstate=new Deflate();
    return dstate.deflateInit(this, level, nowrap?-bits:bits);
  }

  /**
   * {@code deflate} compresses as much data as possible, and stops when the
   * input buffer becomes empty or the output buffer becomes full. It may
   * introduce some output latency (reading input without producing any
   * output) except when forced to flush.
   *<p>
   * The detailed semantics are as follows. {@code deflate} performs one or
   * both of the following actions:
   *</p>
   *<ul>
   * <li>Compress more input from {@link #next_in}, starting at
   *   {@link #next_in_index}, and update {@link #next_in_index} and
   *   {@link #avail_in} accordingly.
   *   If not all input can be processed (because there is not enough room in
   *   the output buffer), {@link #next_in_index} and {@link #avail_in} are
   *   updated and processing will resume at this point for the next call
   *   of {@code deflate()}.</li>
   * <li>Provide more output in {@link #next_out}, starting at
   *   {@link #next_out_index} and update {@link #next_out_index} and
   *   {@link #avail_out} accordingly.
   *   This action is forced if the parameter {@code flush} is non zero (i.e.
   *   not {@link JZlib#Z_NO_FLUSH Z_NO_FLUSH}). Forcing flush frequently
   *   degrades the  compression ratio, so this parameter should be set
   *   only when necessary (in interactive applications). Some output may
   *   be provided even if {@code flush} is not set.</li>
   *</ul>
   *<p>
   * Before the call of {@code deflate()}, the application should ensure that
   * at least one of the actions is possible, by providing more input and/or
   * consuming more output, and updating {@link #avail_in} or {@link #avail_out}
   * accordingly; {@link #avail_out} should never be zero before the call.
   * The application can consume the compressed output when it wants, for
   * example when the output buffer is full ({@link #avail_out}{@code == 0}), or
   * after each call of {@code deflate()}. If deflate returns
   *  {@link JZlib#Z_OK Z_OK}
   * and with zero {@link #avail_out}, it must be called again after making room
   * in the output buffer because there might be more output pending.
   *</p>
   * @param flush whether and how to flush output.
   *  <p>
   *   Normally the parameter {@code flush} is set to
   *   {@link JZlib#Z_NO_FLUSH Z_NO_FLUSH},
   *   which allows {@code deflate} to decide how much data to accumulate
   *   before producing output, in order to maximize compression.
   *  </p>
   *  <p>
   *   If the parameter {@code flush} is set to
   *   {@link JZlib#Z_SYNC_FLUSH Z_SYNC_FLUSH}, all
   *   pending output is flushed to the output buffer and the output is
   *   aligned on a byte boundary, so that the decompressor can get all
   *   input data available so far. (In particular {@link #avail_in} is
   *   zero after the call if enough output space has been provided
   *   before the call.) <em>Flushing may degrade compression for some
   *   compression algorithms and so it should be used only when
   *   necessary</em>. This completes the current deflate block and follows
   *   it with an empty stored block that is three bits plus filler bits
   *   to the next byte, followed by four bytes (00 00 ff ff).
   *  </p>
   *  <p>
   *   If {@code flush} is set to
   *   {@link JZlib#Z_PARTIAL_FLUSH Z_PARTIAL_FLUSH}, all pending
   *   output is flushed to the output buffer, but the output is not aligned
   *   to a byte boundary. All of the input data so far will be available to
   *   the decompressor, as for {@link JZlib#Z_SYNC_FLUSH Z_SYNC_FLUSH}.
   *   This completes
   *   the current deflate block and follows it with an empty fixed codes
   *   block that is 10 bits long. This assures that enough bytes are output
   *   in order for the decompressor to finish the block before the empty
   *   fixed code block.
   *  </p>
   *  <p>
   *   If {@code flush} is set to {@link JZlib#Z_FULL_FLUSH Z_FULL_FLUSH},
   *   all output is flushed as with {@link JZlib#Z_SYNC_FLUSH Z_SYNC_FLUSH},
   *   and the compression
   *   state is reset so that decompression can restart from this point
   *   if previous compressed data has been damaged or if random access
   *   is desired. Using {@link JZlib#Z_FULL_FLUSH Z_FULL_FLUSH} too
   *   often can seriously degrade compression.
   *   On the decompression side, such reset points can be found with
   *   {@link #inflateSync}.
   *  </p>
   *  <p>
   *   If {@code deflate} returns with {@link #avail_out}{@code == 0}, this
   *   function must be called again with the same value of the {@code flush}
   *   parameter and more output space (updated {@link #avail_out}), until
   *   the flush is complete (deflate returns with non-zero {@link #avail_out}).
   *   In the case of a {@link JZlib#Z_FULL_FLUSH Z_FULL_FLUSH} or
   *   {@link JZlib#Z_SYNC_FLUSH Z_SYNC_FLUSH}, make sure that
   *   {@link #avail_out} is
   *   greater than six to avoid repeated flush markers due to
   *   {@link #avail_out}{@code == 0} on return.
   *  </p>
   *  <p>
   *   If the parameter {@code flush} is set to {@link JZlib#Z_FINISH Z_FINISH},
   *   pending input is processed, pending output is flushed and deflate
   *   returns with {@link JZlib#Z_STREAM_END Z_STREAM_END} if there was
   *   enough output
   *   space; if deflate returns with {@link JZlib#Z_OK Z_OK}, this function
   *   must be called again with {@link JZlib#Z_FINISH Z_FINISH} and more output
   *   space (updated {@link #avail_out}) but no more input data, until
   *   it returns with {@link JZlib#Z_STREAM_END Z_STREAM_END} or an error.
   *   After {@code deflate} has returned
   *   {@link JZlib#Z_STREAM_END Z_STREAM_END}, the only
   *   possible operation on the stream is {@link #deflateEnd}.
   *  </p>
   *  <p>
   *   Z_FINISH can be used immediately after {@link #deflateInit} if all
   *   the compression is to be done in a single step. In this case,
   *   {@link #avail_out} must be large enough to compress everything.
   *   If {@code deflate} does not return
   *    {@link JZlib#Z_STREAM_END Z_STREAM_END},
   *   then it must be called again as described above.
   *  </p>
   * @return <ul>
   *   <li>{@link JZlib#Z_OK Z_OK} if some progress
   *     has been made (more input processed or more output produced),</li>
   *   <li>{@link JZlib#Z_STREAM_END Z_STREAM_END} if all input has been
   *     consumed and all output has been produced (only when {@code flush}
   *     is set to {@link JZlib#Z_FINISH Z_FINISH}),</li>
   *   <li>{@link JZlib#Z_STREAM_ERROR Z_STREAM_ERROR} if the stream state
   *     was inconsistent (for example if {@link #next_in} or
   *     {@link #next_out} was {@code null}),</li>
   *   <li>{@link JZlib#Z_BUF_ERROR Z_BUF_ERROR} if no progress is possible
   *     (for example {@link #avail_in} or {@link #avail_out} was zero).</li>
   *</ul>
   * <p>
   *     Note that {@link JZlib#Z_BUF_ERROR Z_BUF_ERROR} is not fatal, and
   *    {@code deflate()} can be called again with more input and more
   *    output space to continue compressing.
   * </p>
   */
  public int deflate(int flush){
    if(dstate==null){
      return Z_STREAM_ERROR;
    }
    return dstate.deflate(this, flush);
  }

  /**
   * All dynamically allocated data structures for this stream are freed.
   * This function discards any unprocessed input and does not flush any
   * pending output.
   *
   * @return {@link JZlib#Z_OK Z_OK} if success,
   *    {@link JZlib#Z_STREAM_ERROR Z_STREAM_ERROR}
   *    if the stream state was inconsistent,
   *   {@link JZlib#Z_DATA_ERROR Z_DATA_ERROR} if
   *    the stream was freed prematurely (some input or output was discarded).
   *    In the error case, {@link #msg} may be set.
   */
  public int deflateEnd(){
    if(dstate==null) return Z_STREAM_ERROR;
    int ret=dstate.deflateEnd();
    dstate=null;
    return ret;
  }

  /**
   * Dynamically update the compression level and compression strategy.
   *<p>
   *  This can be used to switch between compression and straight copy of
   *  the input data, or to switch to a different kind of input data
   *  requiring a different strategy. If the compression level is
   *  changed, the input available so far is compressed with the
   *  old level (and may be flushed); the new level will take effect
   *  only at the next call of deflate().
   *</p>
   *<p>
   * Before the call of {@code deflateParams}, the stream state must be
   * set as for a call of {@link #deflate()}, since the currently available
   * input may have to be compressed and flushed. In particular,
   * {@link #avail_out} must be non-zero.
   *</p>
   * @param level the deflation level. This should be
   *   {@link JZlib#Z_NO_COMPRESSION Z_NO_COMPRESSION},
   *   {@link JZlib#Z_DEFAULT_COMPRESSION Z_DEFAULT_COMPRESSION} or a
   *   value between {@link JZlib#Z_BEST_SPEED Z_BEST_SPEED} (1) and
   *   {@link JZlib#Z_BEST_COMPRESSION Z_BEST_COMPRESSION} (9) (both inclusive).
   * @param strategy one of {@link JZlib#Z_DEFAULT_STRATEGY Z_DEFAULT_STRATEGY},
   *   {@link JZlib#Z_FILTERED Z_FILTERED} and
   *   {@link JZLib#HUFFMAN_ONLY HUFFMAN_ONLY}. (See the description
   *    of these constants for details on each.)
   * @return
   *   {@link JZlib#Z_OK Z_OK} if success,
   *   {@link JZlib#Z_STREAM_ERROR Z_STREAM_ERROR} if the source stream
   *    state was inconsistent or if a parameter was invalid,
   *   {@link JZlib#Z_BUF_ERROR Z_BUF_ERROR} if {@link #avail_out} was zero.
   */
  public int deflateParams(int level, int strategy){
    if(dstate==null) return Z_STREAM_ERROR;
    return dstate.deflateParams(this, level, strategy);
  }

  /**
   * Initializes the compression dictionary from the given byte sequence,
   * without producing any compressed output.
   *<p>
   * This function must be
   *  called immediately after {@link #deflateInit}, before any call
   *  of {@link #deflate}. The compressor and decompressor must use
   *  exactly the same dictionary (see {@link #inflateSetDictionary}).
   *</p>
   *<p>
   * The dictionary should consist of strings (byte sequences) that are
   * likely to be encountered later in the data to be compressed, with
   * the most commonly used strings preferably put towards the end of
   * the dictionary. Using a dictionary is most useful when the data
   * to be compressed is short and can be predicted with good accuracy;
   * the data can then be compressed better than with the default empty
   * dictionary.
   *</p>
   *<p>
   * Depending on the size of the compression data structures selected
   * by {@link #deflateInit}, a part of the dictionary may in effect be
   * discarded, for example if the dictionary is larger than the window
   * size used in {@link #deflateInit}. Thus the strings most likely
   * to be useful should be put at the end of the dictionary, not at
   * the front. In addition, the current implementation of deflate will
   * use at most the window size minus 262 bytes of the provided dictionary.
   *</p>
   *<p>
   * Upon return of this function, {@link #adler} is set to the adler32
   * value of the dictionary; the decompressor may later use this value
   * to determine which dictionary has been used by the compressor.
   * (The adler32 value applies to the whole dictionary even if only a
   * subset of the dictionary is actually used by the compressor.) If a
   * raw deflate was requested (i.e. {@link #deflateInit(int,boolean)}
   * was invoked with {@code nowrap == true}, then the adler32 value is
   * not computed and {@link #adler} is not set.
   *</p>
   * <p>
   *  {@code deflateSetDictionary} does not perform any compression:
   *  this will be done by deflate().
   * </p>
   * @param dictionary an array containing the dictionary (from the start).
   * @param dictLength the length of the dictionary. (This should be at most
   *    {@code dictionary.length}.
   * @return
   *   {@link JZlib#Z_OK Z_OK} if success, or
   *   {@link JZlib#Z_STREAM_ERROR Z_STREAM_ERROR} if a parameter is
   *    invalid (such as a {@code null} dictionary) or the stream state
   *    is inconsistent (for example if {@link #deflate} has already
   *    been called for this stream).
   */
  public int deflateSetDictionary (byte[] dictionary, int dictLength){
    if(dstate == null)
      return Z_STREAM_ERROR;
    return dstate.deflateSetDictionary(this, dictionary, dictLength);
  }

  // Flush as much pending output as possible. All deflate() output goes
  // through this function so some applications may wish to modify it
  // to avoid allocating a large strm->next_out buffer and copying into it.
  // (See also read_buf()).
  void flush_pending(){
    int len=dstate.pending;

    if(len>avail_out) len=avail_out;
    if(len==0) return;

    if(dstate.pending_buf.length<=dstate.pending_out ||
       next_out.length<=next_out_index ||
       dstate.pending_buf.length<(dstate.pending_out+len) ||
       next_out.length<(next_out_index+len)){
      System.out.println(dstate.pending_buf.length+", "+dstate.pending_out+
			 ", "+next_out.length+", "+next_out_index+", "+len);
      System.out.println("avail_out="+avail_out);
    }

    System.arraycopy(dstate.pending_buf, dstate.pending_out,
		     next_out, next_out_index, len);

    next_out_index+=len;
    dstate.pending_out+=len;
    total_out+=len;
    avail_out-=len;
    dstate.pending-=len;
    if(dstate.pending==0){
      dstate.pending_out=0;
    }
  }

  // Read a new buffer from the current input stream, update the adler32
  // and total number of bytes read.  All deflate() input goes through
  // this function so some applications may wish to modify it to avoid
  // allocating a large strm->next_in buffer and copying from it.
  // (See also flush_pending()).
  int read_buf(byte[] buf, int start, int size) {
    int len=avail_in;

    if(len>size) len=size;
    if(len==0) return 0;

    avail_in-=len;

    if(dstate.noheader==0) {
      adler=_adler.adler32(adler, next_in, next_in_index, len);
    }
    System.arraycopy(next_in, next_in_index, buf, start, len);
    next_in_index  += len;
    total_in += len;
    return len;
  }

  /**
   * Frees most memory used by this object.
   * Calling this is normally not necessary in Java, simply ceasing
   * to refer to this object should be enough.
   */
  public void free(){
    next_in=null;
    next_out=null;
    msg=null;
    _adler=null;
  }
}
