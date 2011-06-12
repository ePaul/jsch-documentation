/**
 * JZlib: a port of zlib to Java.
 *<p>
 * This package implements the <strong>deflate</strong> compressed data
 *  format described in
 *  <a href="http://tools.ietf.org/html/rfc1951">RFC 1951</a> and the
 *  <strong>zlib</strong> compressed data format described in
 *  <a href="http://tools.ietf.org/html/rfc1950">RFC 1950</a> (which is
 *  just a slim wrapper around the deflate format).
 *</p>
 * <p>
 *  To use this package, you will normally use a
 *  {@link com.jcraft.jzlib.ZStream}, or the stream wrappers.
 *</p>
 * <ul>
 * <li> {@link com.jcraft.jzlib.ZStream} provides an API very like
 *   the original zlib API, thus you can use this if you are comfortable
 *   with this, or need precise control over the individual packets and
 *   the flushing behaviour.</li>
 * <li> The stream wrappers {@link com.jcraft.jzlib.ZInputStream} and
 *  {@link com.jcraft.jzlib.ZOutputStream} instead use the common
 *   {@link java.io.InputStream}/{@link java.io.OutputStream}
 *   API, and can be used wherever an InputStream or OutputStream
 *   is expected.</li>
 * </ul>
 *
 *<p>
 * Following is some general information from the
 *   <a href="http://www.jcraft.com/jzlib/">home page</a>.
 *</p>
 *<hr/>
 * <p>
 * JZlib is a re-implementation of 
 *                <a href="http://www.info-zip.org/pub/infozip/zlib/">zlib</a>
 *              in pure Java.<br />
 *            The first and final aim for hacking this stuff is
 *           to add the packet compression support to pure Java SSH systems.
 * </p>
 * 
 * <h2>Zlib</h2>
 * <p>
 *       The zlib is designed to be a free, general-purpose,
 *       legally unencumbered -- that is, not covered by any patents -- lossless data-compression library for use on virtually any computer hardware and operating system. 
 *       The zlib was written by 
 *       <a href="mailto:jloup at gzip dot org">Jean-loup Gailly</a> (compression) and
 *       <a href="mailto:madler at alumni dot caltech dot edu">Mark Adler</a> (decompression).
 * </p>
 * 
 * <h2>Features</h2>
 *    <ul>
 *       <li> Needless to say, JZlib can inflate data, which is deflated by zlib
 *            and JZlib can generate deflated data,
 *            which is acceptable and is inflated by zlib.</li>
 *       <li> JZlib supports all compression levels and
 *            all flushing modes in zlib. </li>
 *       <li> JZlib does not support gzip file handling.</li>
 *       <li> The performance has not been estimated yet,
 *            but it will not be so bad in deflating/inflating data stream
 *            on the low bandwidth network.</li>
 *       <li> JZlib is licensed under a
 *             <a href="http://www.jcraft.com/jzlib/LICENSE.txt">BSD style license</a></li>
 *       <li> No invention has been done in developing JZlib.
 *            So, if zlib is patent free,
 *            JZlib is also not covered by any patents.</li>
 *    </ul>
 * 
 * <h2>Download</h2>
 * <p>
 *  You can get the newest versions of JZlib on the <a href="http://www.jcraft.com/jzlib/">JZlib home page</a>.
 * </p>
 * 
 * <h2>Credits</h2>
 * <p>
 *       JZlib has been developed by <a href="mailto:ymnk@jcraft.com">ymnk</a>,
 *       but he has <b>just</b> re-implemented zlib in pure Java (this stuff
 *       was just required to improve the service on
 *         <a href="http://wiredx.net">WiredX.net</a>).
 *       So, all credit should go to authors
 *       <a href="mailto:jloup at gzip dot org">Jean-loup Gailly</a> and
 *       <a href="mailto:madler at alumni dot caltech dot edu">Mark Adler</a>
 *       and contributors of zlib.
 * </p>
 * 
 */
package com.jcraft.jzlib;