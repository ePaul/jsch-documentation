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

import java.io.InputStream;
import java.util.Vector;

/**
 * This class serves as a central configuration point, and
 * as a factory for {@link Session} objects configured with these
 * settings.
 * <ul>
 *   <li>Use {@link #getSession getSession} to start a new Session.</li>
 *   <li>Use one of the {@link #addIdentity addIdentity} methods for
 *       public-key authentication.</li>
 *   <li>Use {@link #setKnownHosts setKnownHosts} to enable
 *       checking of host keys.</li>
 *   <li>See {@link #setConfig(String, String) setConfig} for a list of
 *       configuration options.</li>
 * </ul>
 */
public class JSch{
  static java.util.Hashtable config=new java.util.Hashtable();
  static{
//  config.put("kex", "diffie-hellman-group-exchange-sha1");
    config.put("kex", "diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1");
    config.put("server_host_key", "ssh-rsa,ssh-dss");
//    config.put("server_host_key", "ssh-dss,ssh-rsa");

    config.put("cipher.s2c", 
               "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-cbc,aes256-cbc");
    config.put("cipher.c2s",
               "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-cbc,aes256-cbc");

    config.put("mac.s2c", "hmac-md5,hmac-sha1,hmac-sha1-96,hmac-md5-96");
    config.put("mac.c2s", "hmac-md5,hmac-sha1,hmac-sha1-96,hmac-md5-96");
    config.put("compression.s2c", "none");
    // config.put("compression.s2c", "zlib@openssh.com,zlib,none");
    config.put("compression.c2s", "none");
    // config.put("compression.c2s", "zlib@openssh.com,zlib,none");

    config.put("lang.s2c", "");
    config.put("lang.c2s", "");

    config.put("compression_level", "6");

    config.put("diffie-hellman-group-exchange-sha1", 
                                "com.jcraft.jsch.DHGEX");
    config.put("diffie-hellman-group1-sha1", 
	                        "com.jcraft.jsch.DHG1");
    config.put("diffie-hellman-group14-sha1", 
	                        "com.jcraft.jsch.DHG14");

    config.put("dh",            "com.jcraft.jsch.jce.DH");
    config.put("3des-cbc",      "com.jcraft.jsch.jce.TripleDESCBC");
    config.put("blowfish-cbc",  "com.jcraft.jsch.jce.BlowfishCBC");
    config.put("hmac-sha1",     "com.jcraft.jsch.jce.HMACSHA1");
    config.put("hmac-sha1-96",  "com.jcraft.jsch.jce.HMACSHA196");
    config.put("hmac-md5",      "com.jcraft.jsch.jce.HMACMD5");
    config.put("hmac-md5-96",   "com.jcraft.jsch.jce.HMACMD596");
    config.put("sha-1",         "com.jcraft.jsch.jce.SHA1");
    config.put("md5",           "com.jcraft.jsch.jce.MD5");
    config.put("signature.dss", "com.jcraft.jsch.jce.SignatureDSA");
    config.put("signature.rsa", "com.jcraft.jsch.jce.SignatureRSA");
    config.put("keypairgen.dsa",   "com.jcraft.jsch.jce.KeyPairGenDSA");
    config.put("keypairgen.rsa",   "com.jcraft.jsch.jce.KeyPairGenRSA");
    config.put("random",        "com.jcraft.jsch.jce.Random");

    config.put("none",           "com.jcraft.jsch.CipherNone");

    config.put("aes128-cbc",    "com.jcraft.jsch.jce.AES128CBC");
    config.put("aes192-cbc",    "com.jcraft.jsch.jce.AES192CBC");
    config.put("aes256-cbc",    "com.jcraft.jsch.jce.AES256CBC");

    config.put("aes128-ctr",    "com.jcraft.jsch.jce.AES128CTR");
    config.put("aes192-ctr",    "com.jcraft.jsch.jce.AES192CTR");
    config.put("aes256-ctr",    "com.jcraft.jsch.jce.AES256CTR");
    config.put("3des-ctr",      "com.jcraft.jsch.jce.TripleDESCTR");
    config.put("arcfour",      "com.jcraft.jsch.jce.ARCFOUR");
    config.put("arcfour128",      "com.jcraft.jsch.jce.ARCFOUR128");
    config.put("arcfour256",      "com.jcraft.jsch.jce.ARCFOUR256");

    config.put("userauth.none",    "com.jcraft.jsch.UserAuthNone");
    config.put("userauth.password",    "com.jcraft.jsch.UserAuthPassword");
    config.put("userauth.keyboard-interactive",    "com.jcraft.jsch.UserAuthKeyboardInteractive");
    config.put("userauth.publickey",    "com.jcraft.jsch.UserAuthPublicKey");
    config.put("userauth.gssapi-with-mic",    "com.jcraft.jsch.UserAuthGSSAPIWithMIC");
    config.put("gssapi-with-mic.krb5",    "com.jcraft.jsch.jgss.GSSContextKrb5");

    config.put("zlib",             "com.jcraft.jsch.jcraft.Compression");
    config.put("zlib@openssh.com", "com.jcraft.jsch.jcraft.Compression");

    config.put("StrictHostKeyChecking",  "ask");
    config.put("HashKnownHosts",  "no");
    //config.put("HashKnownHosts",  "yes");
    config.put("PreferredAuthentications", "gssapi-with-mic,publickey,keyboard-interactive,password");

    config.put("CheckCiphers", "aes256-ctr,aes192-ctr,aes128-ctr,aes256-cbc,aes192-cbc,aes128-cbc,3des-ctr,arcfour,arcfour128,arcfour256");
    config.put("CheckKexes", "diffie-hellman-group14-sha1");
  }

  /**
   * a pool of all sessions currently active for this JSch instance.
   * Updated by the sessions on connect and disconnect, nowhere used.
   */
  java.util.Vector pool=new java.util.Vector();
  java.util.Vector identities=new java.util.Vector();
  private HostKeyRepository known_hosts=null;

  /**
   * A no-op Logger implementation.
   */
  private static final Logger DEVNULL=new Logger(){
      public boolean isEnabled(int level){return false;}
      public void log(int level, String message){}
    };
  static Logger logger=DEVNULL;

  /**
   * Creates a new JSch object.
   */
  public JSch(){

    try{
      String osname=(String)(System.getProperties().get("os.name"));
      if(osname!=null && osname.equals("Mac OS X")){
        config.put("hmac-sha1",     "com.jcraft.jsch.jcraft.HMACSHA1"); 
        config.put("hmac-md5",      "com.jcraft.jsch.jcraft.HMACMD5"); 
        config.put("hmac-md5-96",   "com.jcraft.jsch.jcraft.HMACMD596"); 
        config.put("hmac-sha1-96",  "com.jcraft.jsch.jcraft.HMACSHA196"); 
      }
    }
    catch(Exception e){
    }

  }

  /**
   * Creates a new Session on port 22.
   * @param username the remote user name to use.
   * @param host the host to connect to.
   * @return The new session object. It is not yet connected.
   */
  public Session getSession(String username, String host) throws JSchException { return getSession(username, host, 22); }
  
  /**
   * Creates a new Session.
   * @param username the remote user name to use.
   * @param host the host to connect to.
   * @param port the port number used for the TCP connection.
   * @return The new session object. It is not yet connected.
   */
  public Session getSession(String username, String host, int port) throws JSchException {
    if(username==null){
      throw new JSchException("username must not be null.");
    }
    if(host==null){
      throw new JSchException("host must not be null.");
    }
    Session s=new Session(this); 
    s.setUserName(username);
    s.setHost(host);
    s.setPort(port);
    //pool.addElement(s);
    return s;
  }


  /**
   * Adds a session to our session pool.
   * This is invoked by the sessions on {@link Session#connect}, and
   * should supposedly have package-access.
   */
  protected void addSession(Session session){
    synchronized(pool){
      pool.addElement(session);
    }
  }

  
  /**
   * Removes a session from our session pool.
   * This is invoked by the sessions on {@link Session#disconnect}, and
   * should supposedly have package-access.
   */
  protected boolean removeSession(Session session){
    synchronized(pool){
      return pool.remove(session);
    }
  }

  /**
   * Sets the Host key repository. This will be used by
   * sessions {@linkplain Session#connect connected} in the future to
   * validate the host keys offered by the remote hosts.
   */
  public void setHostKeyRepository(HostKeyRepository hkrepo){
    known_hosts=hkrepo;
  }

  /**
   * Creates a host key repository from a file name.
   * This method uses the same format as OpenSSH's
   * {@code known_hosts} file (I hope).
   *<p>
   * This has no effect if {@link #setHostKeyRepository} was already
   * called with an object which is not of class {@link KnownHosts}.
   * @param filename the name of the file to be loaded.
   */
  public void setKnownHosts(String filename) throws JSchException{
    // Fun fact: the implemenation of this method contains
    //           9 times the term "known hosts" (not counting
    //           this comment), and almost nothing else.
    //   (It's the same with the next method.)    -- P.E.
    if(known_hosts==null) known_hosts=new KnownHosts(this);
    if(known_hosts instanceof KnownHosts){
      synchronized(known_hosts){
	((KnownHosts)known_hosts).setKnownHosts(filename); 
      }
    }
  }

  /**
   * Creates a Host key repository from an InputStream.
   * This method uses the same format as OpenSSH's
   * {@code known_hosts} file (I hope).
   *<p>
   * This has no effect if {@link #setHostKeyRepository} was already
   * called with an object which is not of class {@link KnownHosts}.
   * @param stream an InputStream with the list of known hosts.
   */
  public void setKnownHosts(InputStream stream) throws JSchException{ 
    if(known_hosts==null) known_hosts=new KnownHosts(this);
    if(known_hosts instanceof KnownHosts){
      synchronized(known_hosts){
	((KnownHosts)known_hosts).setKnownHosts(stream); 
      }
    }
  }

  /**
   * Returns the current host key repository. If this was not yet set
   * by one of the methods {@link #setKnownHosts(InputStream)},
   * {@link #setKnownHosts(String)} or {@link #setHostKeyRepository},
   * this creates a new (empty) repository of class {@link KnownHosts},
   * sets this as the current repository and returns it.
   */
  public HostKeyRepository getHostKeyRepository(){ 
    if(known_hosts==null) known_hosts=new KnownHosts(this);
    return known_hosts; 
  }

  /**
   * Adds an identity to be used for public-key authentication.
   * @param prvkey the file name of the private key file.
   *   This is also used as the identifying name of the key.
   *   The corresponding public key is assumed to be in a file
   *   with the same name with suffix {@code .pub}.
   */
  public void addIdentity(String prvkey) throws JSchException{
    addIdentity(prvkey, (byte[])null);
  }

  /**
   * Adds an identity to be used for public-key authentication.
   * @param prvkey the file name of the private key file.
   *   This is also used as the identifying name of the key.
   *   The corresponding public key is assumed to be in a file
   *   with the same name with suffix {@code .pub}.
   * @param passphrase the passphrase necessary to access the key.
   *    The String will be encoded in UTF-8 to get the actual passphrase.
   */
  public void addIdentity(String prvkey, String passphrase) throws JSchException{
    byte[] _passphrase=null;
    if(passphrase!=null){
      _passphrase=Util.str2byte(passphrase);
    }
    addIdentity(prvkey, _passphrase);
    if(_passphrase!=null)
      Util.bzero(_passphrase);
  }

  /**
   * Adds an identity to be used for public-key authentication.
   * @param prvkey the file name of the private key file.
   *   This is also used as the identifying name of the key.
   *   The corresponding public key is assumed to be in a file
   *   with the same name with suffix {@code .pub}.
   * @param passphrase the passphrase necessary to access the key.
   */
  public void addIdentity(String prvkey, byte[] passphrase) throws JSchException{
    Identity identity=IdentityFile.newInstance(prvkey, null, this);
    addIdentity(identity, passphrase);
  }

  /**
   * Adds an identity to be used for public-key authentication.
   * @param prvkey the file name of the private key file.
   *   This is also used as the identifying name of the key.
   * @param pubkey the file name of the public key file.
   * @param passphrase the passphrase necessary to access the private key.
   */
  public void addIdentity(String prvkey, String pubkey, byte[] passphrase) throws JSchException{
    Identity identity=IdentityFile.newInstance(prvkey, pubkey, this);
    addIdentity(identity, passphrase);
  }

  /**
   * Adds an identity to be used for public-key authentication.
   * @param name a name identifying the key pair.
   * @param prvkey the private key data. This will be zeroed
   *    out after creating the Identity object.
   * @param pubkey the public key data.
   * @param passphrase the passphrase necessary to access the private key.
   */
  public void addIdentity(String name, byte[]prvkey, byte[]pubkey, byte[] passphrase) throws JSchException{
    Identity identity=IdentityFile.newInstance(name, prvkey, pubkey, this);
    addIdentity(identity, passphrase);
  }

  /**
   * Adds an identity to be used for public-key authentication.
   * @param identity the Identity object encapsulating the key pair
   *    and algorithm (or a hardware device containing them).
   * @param passphrase the passphrase necessary to access the private key.
   */
  public void addIdentity(Identity identity, byte[] passphrase) throws JSchException{
    if(passphrase!=null){
      try{ 
        byte[] goo=new byte[passphrase.length];
        System.arraycopy(passphrase, 0, goo, 0, passphrase.length);
        passphrase=goo;
        identity.setPassphrase(passphrase); 
      }
      finally{
        Util.bzero(passphrase);
      }
    }
    synchronized(identities){
      if(!identities.contains(identity)){
	identities.addElement(identity);
      }
    }
  }

  /**
   * Removes an identity by name.
   * (The name is the result of the {@link Identity#getName getName}
   *  method of the Identity object.)
   *
   * This identity will not be used for future connections anymore.
   * (We also {@link Identity#clear clear} the identity, causing it
   *  to forget its passphrase.)
   * @param name the name of the identity to remove.
   */
  public void removeIdentity(String name) throws JSchException{
    synchronized(identities){
      for(int i=0; i<identities.size(); i++){
        Identity identity=(Identity)(identities.elementAt(i));
	if(!identity.getName().equals(name))
          continue;
        // why not removeElementAt(i)?  -- P.E.
        identities.removeElement(identity);
        identity.clear();
        break;
      }
    }
  }


  /**
   * lists the names of the identities available.
   * @return a vector of strings, each being the name
   *   of one of the added identities.
   */
  public Vector getIdentityNames() throws JSchException{
    Vector foo=new Vector();
    synchronized(identities){
      for(int i=0; i<identities.size(); i++){
        Identity identity=(Identity)(identities.elementAt(i));
        foo.addElement(identity.getName());
      }
    }
    return foo;
  }


  /**
   * Removes all identities. Public key authentication will not
   * work anymore until another identity is added.
   */
  public void removeAllIdentity() throws JSchException{
    synchronized(identities){
      Vector foo=getIdentityNames();
      for(int i=0; i<foo.size(); i++){
        String name=((String)foo.elementAt(i));
        removeIdentity(name);
      }
    }
  }

  /**
   * Retrieves a default configuration option.
   *
   * This method is used to retrieve default values if a session
   * does not have a specific option set.
   * @see Session#getConfig
   * @see #setConfig(String, String)
   */
  public static String getConfig(String key){ 
    synchronized(config){
      return (String)(config.get(key));
    } 
  }

  /**
   * Sets multiple default configuration options at once.
   * The given hashtable should only contain Strings.
   * @see #setConfig(String, String)
   * @throws ClassCastException if the Hashtable contains
   *   keys or values which are not Strings. In this case some
   *   string key-value pairs may already have been set.
   */
  public static void setConfig(java.util.Hashtable newconf){
    synchronized(config){
      for(java.util.Enumeration e=newconf.keys() ; e.hasMoreElements() ;) {
	String key=(String)(e.nextElement());
	config.put(key, (String)(newconf.get(key)));
      }
    }
  }

  /**
   * Sets a default configuration option.
   * This option is used by all sessions, if for these sessions was
   * not set a specific value for this key with
   *  {@link Session#setConfig session.setConfig}.
   *
   * <div style="margin: 1ex; padding: 0em 2em 0em 1em; border: dotted thin;
   *      max-width: 50em; background: #eeeeee;">
   * <p>Here is the list of configuration options used by the
   *  program. They all have sensible default values (use the
   *  source if you want to know the defaults).
   * </p>
   * <h3 id="config-alglist">Algorithm lists</h3>
   * <p>
   * These options contain a (comma-separated, without spaces)
   * list of algorithms, which will be offered to the server, and
   * from which one will be selected by negotiation during key exchange.
   * These should confirm to the format defined by RFC 4250, and be
   * accompanied by an "implementation" option.
   * </p>
   *<dl>
   *  <dt>{@code kex}</dt><dd>Key exchange algorithms</dd>
   *  <dt>{@code server_host_key}</dt>
   *  <dd>Algorithms supported for the server host key.</dd>
   *  <dt>{@code cipher.s2c}</dt><dd>encryption algorithms used for
   *     server-to-client transport. See
   *      <a href="#config-others">CheckCiphers</a>.</dd>
   *  <dt>{@code cipher.c2s}</dt><dd>encryption algorithms used for
   *     client-to-server transport. See
   *      <a href="#config-others">CheckCiphers</a>.</dd>
   *  <dt>{@code mac.c2s}</dt><dd>message authentication code algorithms
   *     for client-to-server transport.</dd>
   *  <dt>{@code mac.s2c}</dt><dd>message authentication code algorithms
   *     for server-to-client transport.</dd>
   *  <dt>{@code compression.c2s}</dt><dd>Compression algorithms
   *    for client-to-server transport. The default is "none",
   *    but this library also supports "zlib" and "zlib@openssh.com".
   *    (Other compression algorithms can't be put in, it seems.)</dd>
   *  <dt>{@code compression.s2c}</dt><dd>Compression algorithms
   *    for server-to-client transport. The default is "none",
   *    but this library also supports "zlib" and "zlib@openssh.com".
   *    (Other compression algorithms can't be put in, it seems.)</dd>
   *  <dt>{@code lang.s2c}</dt><dd>Language preferences for server-to-client
   *    human readable messages (should normally be empty)</dd>
   *  <dt>{@code lang.c2s}</dt><dd>Language preferences for client-to-server
   *    human readable messages (should normally be empty)</dd>
   *</dl>
   * <p>
   * During key exchange, the first option in the client's list
   * (i.e. the option value) which also appears on the server's list
   * will be choosen for each algorithm. Thus the order matters here.
   *</p>
   * <h3 id="config-impl">Implementation classes</h3>
   * <p>The following options contain the class name of
   *    classes implementing a specific algorithm. They should 
   *    implement the interface or abstract class mentioned here.
   * </p>
   * <p>
   *   The classes must be findable using the class loader which loaded
   *   the JSch library (e.g. by a simple {@link Class#forName} inside
   *   the library classes), and must have a no-argument constructor, which
   *   will be called to instantiate the objects needed. Then the actual
   *   interface methods will be used.
   * </p>
   * <h4>Key exchange algorithms ({@link KeyExchange})</h4>
   * <dl><dt>{@code diffie-hellman-group-exchange-sha1}</dt>
   *     <dd>Diffie-Hellman Key Exchange with a group chosen by
   *          the server
   *     (<a href="http://tools.ietf.org/html/rfc4419">RFC 4419</a>).</dd>
   *     <dt>{@code diffie-hellman-group1-sha1}</dt>
   *     <dd>Diffie-Hellman key exchange with a fixed group.
   *     (<a href="http://tools.ietf.org/html/rfc4253#section-7">RFC 4253,
   *       section 7</a>)</dd>
   * </dl>
   *<h4>Symmetric Encryption algorithms ({@link Cipher})</h4>
   *<p>(The mentioned ones have implementations included in the library,
   *    of course you can add more, adding them to {@code cipher.s2c}
   *    and/or {@code cipher.c2s}. The RFC mentioned is the RFC which defined
   *    the keywords, here with links:
   *    <a href="http://tools.ietf.org/html/rfc4253#section-6.3">RFC 4253,
   *         SSH Transport Layer Protocol, Section 6.3 Encryption</a>,
   *    <a href="http://tools.ietf.org/html/rfc4344">RFC 4344,
   *       SSH Transport Layer Encryption Modes</a> (which defines the CTR
   *       mode for most of the ciphers of RFC 4253), and
   *   <a href="http://tools.ietf.org/html/rfc4345">RFC 4345, Improved
   *       Arcfour Modes for SSH</a>.)
   *</p>
   * <dl>
   *   <dt>{@code 3des-cbc}</dt><dd>three-key 3DES in CBC mode (RFC 4253)</dd>
   *   <dt>{@code 3des-ctr}</dt>
   *   <dt>{@code blowfish-cbc}</dt><dd>Blowfish in CBC mode (RFC 4253)</dd>
   *   <dt>{@code aes256-cbc}</dt><dd>AES in CBC mode, with a 256-bit key
   *        (RFC 4253)</dd>
   *   <dt>{@code aes192-cbc}</dt><dd>AES with a 192-bit key (RFC 4253)</dd>
   *   <dt>{@code aes128-cbc}</dt><dd>AES with a 128-bit key (RFC 4253)</dd>
   *   <dt>{@code aes128-ctr}</dt><dd>AES (Rijndael) in SDCTR mode, with
   *          128-bit key (RFC 4344)</dd>
   *   <dt>{@code aes192-ctr}</dt><dd>AES with 192-bit key (RFC 4344)</dd>
   *   <dt>{@code aes256-ctr}</dt><dd>AES with 256-bit key (RFC 4344)</dd>
   *   <dt>{@code arcfour}</dt><dd>the ARCFOUR stream cipher with
   *      a 128-bit key (RFC 4253)</dd>
   *   <dt>{@code arcfour128}</dt><dd>a variant of the ARCFOUR cipher
   *      (still with 128-bit key), which discards the first 1536 bytes of
   *      keystream before encryption will begin. (RFC 4345)</dd>
   *   <dt>{@code arcfour256}</dt><dd>Like arcfour128, but with a
   *       256-bit key. (RFC 4345).</d>
   *   <dt>{@code none}</dt><dd>The null cipher (i.e. no encryption)
   *       (RFC 4345, RFC 2410)</dd>
   * </dl>
   * <h4>Message Authentication Code algorithms ({@link MAC})</h4>
   * <p>These keywords are defined in
   *   <a href="http://tools.ietf.org/html/rfc4253#section-6.4">RFC 4253,
   *     section 6.4 Data Integrity</a>. The basic HMAC algorithm is defined
   *     in <a href="http://tools.ietf.org/html/rfc2104">RFC 2104</a>.</p>
   * <dl>
   *   <dt>{@code hmac-sha1}</dt><dd>HMAC-SHA1
   *         (digest length = key length = 20)</dd>
   *   <dt>{@code hmac-sha1-96}</dt><dd>first 96 bits of HMAC-SHA1
   *         (digest length = 12, key length = 20)</dd>
   *   <dt>{@code hmac-md5}</dt><dd>HMAC-MD5 (digest length = key
   *                               length = 16)</dd>
   *   <dt>{@code hmac-md5-96}</dt><dd>first 96 bits of HMAC-MD5 (digest
   *                               length = 12, key length = 16)</dd>
   * </dl>
   * <h4>Compression methods ({@link Compression})</h4>
   * <p>(It is now hardcoded that only these two (and {@code none}) are
   *   actually accepted, even if providing more ones with
   *   {@code compression.s2c} or {@code compression.c2s}. I think
   *   the reason is the special handling necessary for
   *   {@code zlib@openssh.com}.)
   * </p>
   * <dl>
   *   <dt>{@code zlib}</dt><dd>zlib compression as defined by
   *          RFC 1950+1951</dd>
   *   <dt>{@code zlib@openssh.com}</dt>
   *   <dd>A variant of the zlib compression where the compression
   *       only starts after the client user is authenticated. This
   *       is described in the Internet-Draft 
   *       <a href="http://tools.ietf.org/html/draft-miller-secsh-compression-delayed-00">draft-miller-secsh-compression-delayed-00</a>.</dd>
   * </dl>
   *
   * <h4 id="config-auth">User Authentication methods ({@link UserAuth})</h4>
   * <p>Here the user sends a list of methods, and we have a list of
   *    methods in the option {@code PreferredAuthentications} (in preference
   *    order).
   *    We take the first of our methods which is supported by the server,
   *    get the {@code userauth.}<var>method</var> variable to load the
   *    implementing class, and try to authenticate. This will repeat until
   *    we are authenticated or no more methods left.
   * </p>
   *  <p>The following ones are built in:</p>
   * <dl>
   *   <dt>{@code userauth.none}</dt>
   *      <dd>mainly for getting the list of methods the server supports.</dd>
   *   <dt>{@code userauth.password}</dt>
   *      <dd>usual password authentication.</dd>
   *   <dt>{@code userauth.keyboard-interactive}</dt>
   *      <dd>Using the generic message exchange authentication mechanism,
   *          as defined in
   *        <a href="http://tools.ietf.org/html/rfc4256">RFC 4256</a>.</dd>
   *   <dt>{@code userauth.publickey}</dt>
   *      <dd>public key authentication, using an {@link Identity}.</dd>
   *   <dt>{@code userauth.gssapi-with-mic}</dt>
   *      <dd>Using the GSS-API (see below) as defined in
   *        <a href="http://tools.ietf.org/html/rfc4462#section-3">RFC 4462,
   *      section 3</a>. </dd>
   * </dl>
   * <p>For the GSS-API mechanism we need an implementation
   *    of {@link GSSContext} to refer to, which will be chosen
   *    by the configuration option {@code gssapi-with-mic.}<var>method</var>,
   *   the method being chosen from a list given by the server. For now,
   *   we (hardcoded) only support the {@code krb5} method, resulting in:
   *</p>
   *  <dl><dt>{@code gssapi-with-mic.krb5}</dt><dd>Kerberos 5
   *          authentication.</dt>
   *  </dl>
   *
   *
   * <h4>Miscellaneous algorithms</h4>
   * <p>The following options do not correspond to algorithm names as defined
   *   in the SSH protocols, but are used to implement the underlying
   *   cryptographic functions.</p>
   * <dl>
   *   <dt>{@code dh}</dt><dd>{@link DH} - Diffie-Hellman mathematics.</dd>
   *   <dt>{@code random}</dt><dd>{@link Random} - random number
   *            generation.</dd>
   *   <dt>{@code signature.dss}</dt><dd>{@link SignatureDSA}</dd>
   *   <dt>{@code signature.rsa}</dt><dd>{@link SignatureRSA}</dd>
   *   <dt>{@code keypairgen.dsa}</dt><dd>{@link KeyPairGenDSA}</dd>
   *   <dt>{@code keypairgen.rsa}</dt><dd>{@link KeyPairGenRSA}</dd>
   * </dl>
   * <p>And the cryptographic hash algorithms ({@link HASH}):</p>
   * <dl>
   *   <dt>{@code sha-1}</dt><dd>The Secure Hash Algorithm, version 1.</dd>
   *   <dt>{@code md5}</dt><dd>The Message Digest 5 algorithm.</dd>
   * </dl>
   *   
   * <h3 id="config-others">Other options</h3>
   *<p>
   * Here are options not fitting in any of the other categories.
   *</p>
   * <dl>
   *   <dt>{@code compression_level}</dt><dd>The compression level
   *      for client-to-server transport. This will only be used if the
   *      negotiated compression method is one of {@code zlib} and
   *      {@code zlib@openssh.com} (other methods are not supported
   *      anyway).</dd>
   *   <dt>{@code PreferredAuthentications}</dt><dd>A preference
   *      order of our <a href="config-auth">preferred authentication
   *      methods</a>. Each of them should have a corresponding
   *      userauth.<var>method</em> configuration
   *     option defining the implementation class.</dd>
   *   <dt>{@code StrictHostKeyChecking}</dt><dd>Indicates what to do if the
   *     server's host key changed or the server is unknown.
   *     One of {@code yes} (refuse connection),
   *     {@code ask} (ask the user whether to add/change the key)
   *     and {@code no} (always insert the new key).</dd>
   *   <dt>{@code HashKnownHosts}</dt><dd>If this is "yes" and we are using
   *     the default known-hosts file implementation, new added
   *     server keys will be hashed.</dd>
   *   <dt>{@code CheckCiphers}</dt><dd>A list of Ciphers which should be first
   *     checked for availability. All ciphers in this list which are not
   *     working will be removed from the {@code ciphers.c2s} and
   *     {@code ciphers.s2c} before sending these lists to the server
   *     in a KEX_INIT message.</dd>
   * </dl>
   * </div>
   * @param key the option name.
   * @param value the option value.
   * @see Session#setConfig
   * @see #getConfig
   */
  public static void setConfig(String key, String value){
    config.put(key, value);
  }

  /**
   * sets the Logger to be used by this library.
   * @param logger the new logger. If {@code null}, we use a buildt-in
   *  Logger which logs nothing.
   */
  public static void setLogger(Logger logger){
    if(logger==null) logger=DEVNULL;
    JSch.logger=logger;
  }

  /**
   * returns the current Logger.
   */
  static Logger getLogger(){
    return logger;
  }
}
