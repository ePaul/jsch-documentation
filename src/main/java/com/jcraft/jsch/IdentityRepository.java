/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2012-2016 ymnk, JCraft,Inc. All rights reserved.

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

import java.util.Vector;

/**
 * A repository for identities (basically key pairs usable for authentication to a server).
 * The default implementation included in the library contains just a list of keys in memory,
 * but other implementations can be provided by the application, using e.g. a hardware storage
 * or external apps like ssh-agent.
 *
 * @see JSch#setIdentityRepository(IdentityRepository)
 * @see JSch#getIdentityRepository()
 * @see Session#setIdentityRepository(IdentityRepository)
 * @see Session#getIdentityRepository()
 * @since 0.1.46
 */
public interface IdentityRepository {
  public static final int UNAVAILABLE=0;
  public static final int NOTRUNNING=1;
  public static final int RUNNING=2;

  /**
   * The name of the repository instance. This seems to be used nowhere
   * within the library, but could be used by an application for showing it in the UI.
   */
  public String getName();

  /**
   * The status of the repository instance. This seems to be used nowhere
   * within the library, but could be used by an application for showing it in the UI.
   * @return One of {@link #UNAVAILABLE}, {@link #NOTRUNNING} and {@link #RUNNING}.
   */
  public int getStatus();

  /**
   * Returns all the identies of this repository, in the form of a vector of {@link Identity} objects.
   * This will be used by the library when implementing public key authentication.
   */
  public Vector getIdentities();

  /**
   * Adds a new identity to this repository, in the form of the raw (unencrypted) private key bytes.
   * @param identity the key, encoded as a byte[]. 
   * @return true if the identity was added successfully, false otherwise.
   */
  public boolean add(byte[] identity);

  /**
   * Removes an identy from the repository, given the public key.
   * @param blob the identity's public key, encoded as a byte[]. 
   * @return true if there was an identy with the given key to be removed,
   *  false otherwise.
   */
  public boolean remove(byte[] blob);

  /**
   * Removes all identities from this repository.
   */
  public void removeAll();

  /**
   * JSch will accept ciphered keys, but some implementations of
   * IdentityRepository can not.  For example, IdentityRepository for
   * ssh-agent and pageant only accept plain keys.  The following class has
   * been introduced to cache ciphered keys for them, and pass them
   * whenever they are de-ciphered.
   */
  static class Wrapper implements IdentityRepository {
    private IdentityRepository ir;
    private Vector cache = new Vector();
    private boolean keep_in_cache = false;
    Wrapper(IdentityRepository ir){
      this(ir, false);
    }
    Wrapper(IdentityRepository ir, boolean keep_in_cache){
      this.ir = ir;
      this.keep_in_cache = keep_in_cache;
    }
    public String getName() {
      return ir.getName();
    }
    public int getStatus() {
      return ir.getStatus();
    }
    public boolean add(byte[] identity) {
      return ir.add(identity);
    }
    public boolean remove(byte[] blob) {
      return ir.remove(blob);
    }
    public void removeAll() {
      cache.removeAllElements();
      ir.removeAll();
    }
    public Vector getIdentities() {
      Vector result = new Vector();
      for(int i = 0; i< cache.size(); i++){
        Identity identity = (Identity)(cache.elementAt(i));
        result.add(identity);
      }
      Vector tmp = ir.getIdentities();
      for(int i = 0; i< tmp.size(); i++){
        result.add(tmp.elementAt(i));
      }
      return result;
    }
    void add(Identity identity) {
      if(!keep_in_cache && 
         !identity.isEncrypted() && (identity instanceof IdentityFile)) {
        try {
          ir.add(((IdentityFile)identity).getKeyPair().forSSHAgent());
        }
        catch(JSchException e){
          // an exception will not be thrown.
        }
      }
      else
        cache.addElement(identity);
    }
    void check() {
      if(cache.size() > 0){
        Object[] identities = cache.toArray();
        for(int i = 0; i < identities.length; i++){
          Identity identity = (Identity)(identities[i]);
          cache.removeElement(identity);
          add(identity);
        }
      }
    }
  }
}
