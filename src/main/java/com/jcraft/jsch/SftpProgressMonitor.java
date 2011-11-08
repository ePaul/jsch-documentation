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
 * A callback to get information about the progress of a file
 * transfer operation.
 *<p>
 *  An application will implement this interface to get information
 *  about a running file transfer, and maybe show it to the user.
 *  For example, it might wrap an {@link javax.swing.ProgressMonitor}.
 *</p>
 *<p>
 *  Additionally, this interface enables the application to stop the
 *  transfer by returning {@code false} from the {@link #count count} method.
 *</p>
 *<p>
 *  Several of the {@link ChannelSftp}'s {@code put} and {@code get} methods
 *  take an object of this type, and will call its methods as defined here.
 *</p>
 *
 * @see ChannelSftp
 */
public interface SftpProgressMonitor{

  /**
   * Direction constant for upload.
   */
  public static final int PUT=0;

  /**
   * Direction constant for download.
   */
  public static final int GET=1;
  public static final long UNKNOWN_SIZE = -1L;

  /**
   * Will be called when a new operation starts.
   * @param op a code indicating the direction of transfer,
   *     one of {@link #PUT} and {@link #GET}
   * @param dest the destination file name.
   * @param max the final count (i.e. length of file to transfer).
   */
  void init(int op, String src, String dest, long max);

  /**
   * Will be called periodically as more data is transfered.
   * @param count the number of bytes transferred so far
   * @return true if the transfer should go on,
   *        false if the transfer should be cancelled.
   */
  boolean count(long count);

  /**
   * Will be called when the transfer ended, either because all the data
   * was transferred, or because the transfer was cancelled.
   */
  void end();
}
