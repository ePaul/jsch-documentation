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
public class ZInputStream extends FilterInputStream {

  /**
   * The wrapped ZStream, which does the work.
   */
  protected ZStream z=new ZStream();

  /**
   * The size of our internal buffer {@link #buf}.
   */
  protected int bufsize=512;

  /**
   * The flushing mode in use.
   */
  protected int flush=JZlib.Z_NO_FLUSH;

  /**
   * The internal buffer used for reading the
   * original stream and passing input to the
   * ZStream.
   */
  protected byte[] buf=new byte[bufsize];

  /**
   * A one-byte buffer, used by {@link #read()}.
   */
  protected byte[] buf1=new byte[1];

  /**
   * Wether we are inflating (false) or deflating (true).
   */
  protected boolean compress;

  /**
   * The source input stream. The data will be read from this before
   * being compressed or decompressed.
   */
  protected InputStream in=null;

  /**
   * Creates a new decompressing (inflating) ZInputStream
   * reading zlib formatted data.
   * @param in the base stream, which should contain data in
   *   zlib format.
   */
  public ZInputStream(InputStream in) {
    this(in, false);
  }

  /**
   * Creates a new decompressing (inflating) ZInputStream,
   * reading either zlib or plain deflate data.
   * @param in the base stream, which should contain data
   *   in the right format.
   * @param nowrap if true, the input is plain deflate data.
   *   If false, it is in zlib format (i.e. with a small header
   *   and a checksum).
   */
  public ZInputStream(InputStream in, boolean nowrap) {
    super(in);
    this.in=in;
    z.inflateInit(nowrap);
    compress=false;
    z.next_in=buf;
    z.next_in_index=0;
    z.avail_in=0;
  }

  /**
   * Creates a compressing (deflating) ZInputStream,
   * producing zlib format data.
   * The stream reads uncompressed data from the base
   * stream, and produces compressed data in zlib format.
   * @param in the base stream from which to read uncompressed data.
   * @param level the compression level which will be used.
   */
  public ZInputStream(InputStream in, int level) {
    super(in);
    this.in=in;
    z.deflateInit(level);
    compress=true;
    z.next_in=buf;
    z.next_in_index=0;
    z.avail_in=0;
  }

  /*public int available() throws IOException {
    return inf.finished() ? 0 : 1;
  }*/

  /**
   * Reads one byte of data.
   * @return the read byte, or -1 on end of input.
   */
  public int read() throws IOException {
    if(read(buf1, 0, 1)==-1)
      return(-1);
    return(buf1[0]&0xFF);
  }

  private boolean nomoreinput=false;

  /**
   * reads some data from the stream.
   * This will compress or decompress data from the
   * underlying stream.
   * @param b the buffer in which to put the data.
   * @param off the offset in b on which we should
   *     put the data.
   * @param len how much data to read maximally.
   * @return the amount of data actually read.
   */
  public int read(byte[] b, int off, int len) throws IOException {
    if(len==0)
      return(0);
    int err;
    z.next_out=b;
    z.next_out_index=off;
    z.avail_out=len;
    do {
      if((z.avail_in==0)&&(!nomoreinput)) { // if buffer is empty and more input is avaiable, refill it
	z.next_in_index=0;
	z.avail_in=in.read(buf, 0, bufsize);//(bufsize<z.avail_out ? bufsize : z.avail_out));
	if(z.avail_in==-1) {
	  z.avail_in=0;
	  nomoreinput=true;
	}
      }
      if(compress)
	err=z.deflate(flush);
      else
	err=z.inflate(flush);
      if(nomoreinput&&(err==JZlib.Z_BUF_ERROR))
        return(-1);
      if(err!=JZlib.Z_OK && err!=JZlib.Z_STREAM_END)
	throw new ZStreamException((compress ? "de" : "in")+"flating: "+z.msg);
      if((nomoreinput||err==JZlib.Z_STREAM_END)&&(z.avail_out==len))
	return(-1);
    } 
    while(z.avail_out==len&&err==JZlib.Z_OK);
    //System.err.print("("+(len-z.avail_out)+")");
    return(len-z.avail_out);
  }

  /**
   * skips some amount of (compressed or decompressed) input.
   *
   * In this implementation, we will simply read some data and
   * discard it. We will skip maximally 512 bytes on each call.
   * @return the number of bytes actually skipped.
   */
  public long skip(long n) throws IOException {
    int len=512;
    if(n<len)
      len=(int)n;
    byte[] tmp=new byte[len];
    return((long)read(tmp));
  }

  /**
   * Returns the current flush mode used for each compressing/decompressing
   * call. Normally this should be {@link JZlib#Z_NO_FLUSH Z_NO_FLUSH}.
   */
  public int getFlushMode() {
    return(flush);
  }

  /**
   * Returns the current flush mode used for each compressing/decompressing
   * call. Normally this should be {@link JZlib#Z_NO_FLUSH Z_NO_FLUSH}.
   */
  public void setFlushMode(int flush) {
    this.flush=flush;
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
   * Closes this stream.
   * This closes the underlying stream, too.
   */
  public void close() throws IOException{
    in.close();
    // TODO: Shouldn't we also close the ZStream
    // (i.e. call inflateEnd/deflateEnd)?
  }
}
