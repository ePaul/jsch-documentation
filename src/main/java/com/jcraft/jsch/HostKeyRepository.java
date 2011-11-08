/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2004-2011 ymnk, JCraft,Inc. All rights reserved.

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
 * A repository for known host keys.
 * This will be used when connecting remote servers to check that their
 * public keys are the same as we think they should be.
 * <p>
 *   The library contains an {@linkplain KnownHosts implementation}
 *   based on the OpenSSH known Hosts file format - this will be the
 *   default implementation if no other is given explicitely.
 * </p>
 * <p>
 *   An application might want to implement this class to provide an
 *   alternative repository of valid server keys to use.
 * </p>
 * @see JSch#setHostKeyRepository
 */
public interface HostKeyRepository{

  /**
   * Constant for the result of {@link #check}:
   *   The host has the given key.
   */
  final int OK=0;

  /**
   * Constant for the result of {@link #check}:
   *   The host does not exist yet in the list.
   */
  final int NOT_INCLUDED=1;

  /**
   * Constant for the result of {@link #check}:
   *   The host has another key. (This could be indicating
   *  a man-in-the-middle attack.)
   */
  final int CHANGED=2;

  /**
   * Checks whether some host has a given key.
   * @param host the host name to check
   * @param key the public key the remote host
   *    uses.
   * @return one of the constants {@link #OK} (this host is known to use
   *    this key), {@link #NOT_INCLUDED} (the host is unknown) or
   *    {@link #CHANGED} (the host is known to use another key).
   */
  int check(String host, byte[] key);

  /**
   * Adds a hostname-key-pair to the repository.
   * @param hostkey the key to add
   * @param ui an UserInfo object which may be used to ask the
   *     user whether to create the file (and directory), or other
   *     similar questions, if necessary.
   */
  void add(HostKey hostkey, UserInfo ui);

  /**
   * Removes all keys of a host from the repository.
   */
  void remove(String host, String type);

  /**
   * removes a specific key of a host from the repository.
   * @param host the host name whose key is to remove.
   * @param type the type of key to remove. If null, all keys of
   *    this host will be removed (without looking at {@code key}).
   * @param key the key to be removed. If null, all keys of the
   *    given type and host will be removed.
   * @throws NullPointerException if {@code host == null}.
   */
  void remove(String host, String type, byte[] key);

  /**
   * returns an identifier for this repository.
   * This could be the file name of the file being accessed, for example.
   *<p>
   * This will be used for messages to the user speaking about the
   * repository.
   *</p>
   */
  String getKnownHostsRepositoryID();

  /**
   * returns all host keys in this repository.
   * This method does the same as
   *    {@link #getHostKey(String,String) getHostKey(null,null)}.
   *
   * This method should have been named {@code getHostKeys()}.
   */
  HostKey[] getHostKey();

  /**
   * returns all host keys of a certain host.
   * This method should have been named {@code getHostKeys(...)}.
   * @param host the name of the host whose keys should be retrieved.
   *    If null, retrieves all host keys (independent of type).
   * @param type the type of keys which should be retrieved.
   *    If null, retrieves all keys of the given host.
   */
  HostKey[] getHostKey(String host, String type);
}
