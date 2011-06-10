/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2001 Lapo Luchini.

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
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHORS
OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
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
import java.io.*;

/**
 * An output stream wrapper around a {@link ZStream}.
 * This can work either as a deflating or inflating stream,
 * depending on the constructor being used.
 */
public class ZOutputStream extends OutputStream {

  protected ZStream z=new ZStream();
  protected int bufsize=512;
  protected int flush=JZlib.Z_NO_FLUSH;
  protected byte[] buf=new byte[bufsize];
  /**
   * A one-byte buffer, used by {@link #write(int)}.
   */
  protected byte[] buf1=new byte[1];
  protected boolean compress;

  /**
   * The underlying output stream. All compressed (or decompressed)
   * data will be written to this.
   */
  protected OutputStream out;


  /**
   * Creates a new decompressing (inflating) ZOutputStream.
   * The stream expects the written data to be in zlib format,
   * and writes the decompressed data to the argument stream.
   */
  public ZOutputStream(OutputStream out) {
    super();
    this.out=out;
    z.inflateInit();
    compress=false;
  }

  /**
   * Creates a new compressing (deflating) ZOutputStream.
   * The stream will compress any written data, and write
   * it to the argument stream in zlib format.
   * @param out the stream that will receive the compressed
   *   data.
   * @param level the deflation level. This should be
   *   {@link JZlib#Z_NO_COMPRESSION Z_NO_COMPRESSION},
   *   {@link JZlib#Z_DEFAULT_COMPRESSION Z_DEFAULT_COMPRESSION} or a
   *   value between {@link JZlib#Z_BEST_SPEED Z_BEST_SPEED} (1) and
   *   {@link JZlib#Z_BEST_COMPRESSION Z_BEST_COMPRESSION} (9) (both inclusive).
   */
  public ZOutputStream(OutputStream out, int level) {
    this(out, level, false);
  }

  /**
   * Creates a new compressing (deflating) ZOutputStream.
   * The stream will compress any written data, and write
   * it to the argument stream in either zlib or plain
   * deflate format.
   * @param out the stream that will receive the compressed
   *   data.
   * @param level the deflation level. This should be
   *   {@link JZlib#Z_NO_COMPRESSION Z_NO_COMPRESSION},
   *   {@link JZlib#Z_DEFAULT_COMPRESSION Z_DEFAULT_COMPRESSION} or a
   *   value between {@link JZlib#Z_BEST_SPEED Z_BEST_SPEED} (1) and
   *   {@link JZlib#Z_BEST_COMPRESSION Z_BEST_COMPRESSION} (9) (both inclusive).
   * @param nowrap if {@code true}, the stream uses the plain deflate
   *   format. If {@code false}, the stream uses the {@code zlib} format
   *   (which includes a header and checksum).
   */
  public ZOutputStream(OutputStream out, int level, boolean nowrap) {
    super();
    this.out=out;
    z.deflateInit(level, nowrap);
    compress=true;
  }

  /**
   * Writes a single byte. This is a shortcut to calling
   * {@link #write(byte[], int, int) write(new byte[]{b},0,1)}
   * (but reusing the array for multiple calls).
   * @param b the byte to write.
   */
  public void write(int b) throws IOException {
    buf1[0]=(byte)b;
    write(buf1, 0, 1);
  }

  /**
   * Writes a sequence of bytes, compressing or decompressing it.
   * @param b the array holding the data
   * @param off the offset in {@code b} where the data starts.
   * @param len the length of the data.
   */
  public void write(byte b[], int off, int len) throws IOException {
    if(len==0)
      return;
    int err;
    z.next_in=b;
    z.next_in_index=off;
    z.avail_in=len;
    do{
      z.next_out=buf;
      z.next_out_index=0;
      z.avail_out=bufsize;
      if(compress)
        err=z.deflate(flush);
      else
        err=z.inflate(flush);
      if(err!=JZlib.Z_OK)
        throw new ZStreamException((compress?"de":"in")+"flating: "+z.msg);
      out.write(buf, 0, bufsize-z.avail_out);
    } 
    while(z.avail_in>0 || z.avail_out==0);
  }

  /**
   * Returns the current flush mode, which will be used for every
   * {@link #write}.
   */
  public int getFlushMode() {
    return(flush);
  }

  /**
   * Sets the flush mode. This will be used for every {@link #write}.
   */
  public void setFlushMode(int flush) {
    this.flush=flush;
  }


  /**
   * Finishes the compressing/decompressing, without closing the
   * underlying stream. This will flush the buffer, end the block
   * and (if not in plain deflate mode) add or compare the checksum.
   */
  public void finish() throws IOException {
    int err;
    do{
      z.next_out=buf;
      z.next_out_index=0;
      z.avail_out=bufsize;
      if(compress){ err=z.deflate(JZlib.Z_FINISH);  }
      else{ err=z.inflate(JZlib.Z_FINISH); }
      if(err!=JZlib.Z_STREAM_END && err != JZlib.Z_OK)
      throw new ZStreamException((compress?"de":"in")+"flating: "+z.msg);
      if(bufsize-z.avail_out>0){
	out.write(buf, 0, bufsize-z.avail_out);
      }
    }
    while(z.avail_in>0 || z.avail_out==0);
    flush();
  }

  /**
   * Cleans up the deflater/inflater state.
   *
   * This will be automatically called by {@link #close}.
   */
  public void end() {
    if(z==null)
      return;
    if(compress){ z.deflateEnd(); }
    else{ z.inflateEnd(); }
    z.free();
    z=null;
  }

  /**
   * Closes the stream. This will flush out anything written so far,
   * release any resources, and close the underlying stream, too.
   */
  public void close() throws IOException {
    try{
      try{finish();}
      catch (IOException ignored) {}
    }
    finally{
      end();
      out.close();
      out=null;
    }
  }

  /**
   * Returns the total number of bytes input so far.
   */
  public long getTotalIn() {
    return z.total_in;
  }

  /**
   * Returns the total number of bytes output so far.
   */
  public long getTotalOut() {
    return z.total_out;
  }

  /**
   * flushes the underlying stream.
   */
  public void flush() throws IOException {
    out.flush();
  }

}
