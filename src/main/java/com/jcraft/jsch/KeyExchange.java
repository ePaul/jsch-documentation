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
 * Abstract base class for key exchange algorithms.
 * 
 * The concrete implementation will be chosen based on the configuration
 * and on the negotiation between client and server.
 * @see <a href="http://tools.ietf.org/html/rfc4253#section-7">RFC 4253,
 *     7.  Key Exchange</a>
 * @see <a href="http://tools.ietf.org/html/rfc4253#section-8">RFC 4253,
 *     8.  Diffie-Hellman Key Exchange</a>
 */
public abstract class KeyExchange{

  static final int PROPOSAL_KEX_ALGS=0;
  static final int PROPOSAL_SERVER_HOST_KEY_ALGS=1;
  static final int PROPOSAL_ENC_ALGS_CTOS=2;
  static final int PROPOSAL_ENC_ALGS_STOC=3;
  static final int PROPOSAL_MAC_ALGS_CTOS=4;
  static final int PROPOSAL_MAC_ALGS_STOC=5;
  static final int PROPOSAL_COMP_ALGS_CTOS=6;
  static final int PROPOSAL_COMP_ALGS_STOC=7;
  static final int PROPOSAL_LANG_CTOS=8;
  static final int PROPOSAL_LANG_STOC=9;
  static final int PROPOSAL_MAX=10;

  //static String kex_algs="diffie-hellman-group-exchange-sha1"+
  //                       ",diffie-hellman-group1-sha1";

//static String kex="diffie-hellman-group-exchange-sha1";
  static String kex="diffie-hellman-group1-sha1";
  static String server_host_key="ssh-rsa,ssh-dss";
  static String enc_c2s="blowfish-cbc";
  static String enc_s2c="blowfish-cbc";
  static String mac_c2s="hmac-md5";     // hmac-md5,hmac-sha1,hmac-ripemd160,
                                        // hmac-sha1-96,hmac-md5-96
  static String mac_s2c="hmac-md5";
//static String comp_c2s="none";        // zlib
//static String comp_s2c="none";
  static String lang_c2s="";
  static String lang_s2c="";

  /**
   * constant used by {@link #getState} when no more key exchange
   * packet is expected.
   */
  public static final int STATE_END=0;

  protected Session session=null;
  protected HASH sha=null;
  protected byte[] K=null;
  protected byte[] H=null;
  protected byte[] K_S=null;


  /**
   * Initializes the key exchange object.
   *
   * The parameters here seem to be specific to the
   *   Diffie-Hellman Key exchange specified by RFC 4253, other
   *   Key Exchange mechanisms might need other parameters. 
   * @param session the session object.
   * @param V_S the server's identification string sent before negotiation
   * @param V_C the client's identification string sent before negotiation
   * @param I_S the server's complete SSH_MSG_KEXINIT message.
   * @param I_C the server's complete SSH_MSG_KEXINIT message.
   * @see <a href="http://tools.ietf.org/html/rfc4253#section-7">RFC 4253,
   *     7.  Key Exchange</a>
   * @see <a href="http://tools.ietf.org/html/rfc4253#section-8">RFC 4253,
   *     8.  Diffie-Hellman Key Exchange</a>
   */
  public abstract void init(Session session, 
			    byte[] V_S, byte[] V_C, byte[] I_S, byte[] I_C) throws Exception;

  /**
   * Does the next step in the key exchange algorithm.
   * @param buf the received packet. It will have the same
   *     message-type as {@link #getState} returned before.
   * @return false if there was some problem,
   *         true if everything was okay.
   */
  public abstract boolean next(Buffer buf) throws Exception;

  /**
   * Returns the type of key used by the server.
   * With current algorithms, this is either {@code "DSA"} or {@code "RSA"}.
   */
  public abstract String getKeyType();

  /**
   * returns the identifier of the next SSH packet expected,
   * or {@link #STATE_END} if the KeyExchange was already
   * successfully finished.
   */
  public abstract int getState();

  /*
  void dump(byte[] foo){
    for(int i=0; i<foo.length; i++){
      if((foo[i]&0xf0)==0)System.err.print("0");
      System.err.print(Integer.toHexString(foo[i]&0xff));
      if(i%16==15){System.err.println(""); continue;}
      if(i%2==1)System.err.print(" ");
    }
  } 
  */

  protected static String[] guess(byte[]I_S, byte[]I_C){
    String[] guess=new String[PROPOSAL_MAX];
    Buffer sb=new Buffer(I_S); sb.setOffSet(17);
    Buffer cb=new Buffer(I_C); cb.setOffSet(17);

    for(int i=0; i<PROPOSAL_MAX; i++){
      byte[] sp=sb.getString();  // server proposal
      byte[] cp=cb.getString();  // client proposal
      int j=0;
      int k=0;

      loop:
      while(j<cp.length){
	while(j<cp.length && cp[j]!=',')j++; 
	if(k==j) return null;
	String algorithm=Util.byte2str(cp, k, j-k);
	int l=0;
	int m=0;
	while(l<sp.length){
	  while(l<sp.length && sp[l]!=',')l++; 
	  if(m==l) return null;
	  if(algorithm.equals(Util.byte2str(sp, m, l-m))){
	    guess[i]=algorithm;
	    break loop;
	  }
	  l++;
	  m=l;
	}	
	j++;
	k=j;
      }
      if(j==0){
	guess[i]="";
      }
      else if(guess[i]==null){
	return null;
      }
    }

    if(JSch.getLogger().isEnabled(Logger.INFO)){
      JSch.getLogger().log(Logger.INFO, 
                           "kex: server->client"+
                           " "+guess[PROPOSAL_ENC_ALGS_STOC]+
                           " "+guess[PROPOSAL_MAC_ALGS_STOC]+
                           " "+guess[PROPOSAL_COMP_ALGS_STOC]);
      JSch.getLogger().log(Logger.INFO, 
                           "kex: client->server"+
                           " "+guess[PROPOSAL_ENC_ALGS_CTOS]+
                           " "+guess[PROPOSAL_MAC_ALGS_CTOS]+
                           " "+guess[PROPOSAL_COMP_ALGS_CTOS]);
    }

//    for(int i=0; i<PROPOSAL_MAX; i++){
//      System.err.println("guess: ["+guess[i]+"]");
//    }

    return guess;
  }

  /**
   * returns the finger print of the server's public key.
   *
   * This uses the {@link HASH} implementation given by 
   * {@link Session#getConfig(String) session.getConfig("md5")}.
   * @return the (lowercase) hexadecimal representation of
   *   the MD5 hash of the server's public key.
   */
  public String getFingerPrint(){
    HASH hash=null;
    try{
      Class c=Class.forName(session.getConfig("md5"));
      hash=(HASH)(c.newInstance());
    }
    catch(Exception e){ System.err.println("getFingerPrint: "+e); }
    return Util.getFingerPrint(hash, getHostKey());
  }
  byte[] getK(){ return K; }
  byte[] getH(){ return H; }
  HASH getHash(){ return sha; }
  byte[] getHostKey(){ return K_S; }
}
