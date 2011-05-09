/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
package com.jcraft.jsch;

import java.util.Vector;
import com.jcraft.jsch.*;

/**
 * UserAuth implementation for Host based authentication.
 *<p>
 * For this to work, on a OpenSSH server the following settings
 * are needed (relative to the default):</p><ul>
 * <li>In /etc/ssh/sshd_config, these options need to be set/changed:
 * <dl><dt>HostbasedAuthentication yes</dt><dd>To enable it at all.</dd>
 *  <dt>IgnoreRhosts no</dt><dd>If you want to let users themselves decide
 *    from which hosts to login by using ~/.rhosts or ~/.shosts files.
 *   You don't need this if you put the hosts and users in
 *   /etc/ssh/shosts.equiv instead. </dd>
 * <dt>HostbasedUsesNameFromPacketOnly yes</dt><dd> To enable logging
 *   in when the client host name used in the auth message is not your
 *   actual host name as viewed by the server. This allows using a proxy.
 *  </dd></dl></li>
 * <li>Either in /etc/ssh/ssh_known_hosts or in ~/.ssh/known_hosts put the
 *   name and key of the host.</li>
 * <li>Either in /etc/ssh/shosts.equiv or ~/.shosts put a line
 *     with the (fully qualified) client server name and
 *     (optionally) client user name.  (The semantics is a bit
 *    different here: In shosts.equiv, a host without user names maps all
 *    users of the named host to same-named users of the current host.
 *    With a named user, it allows this user to login as any user on
 *    the current host (except root).
 *    For ~/.shosts, either only the user with same name or the named user are
 *    accepted to login into the account, in whose home directory the file is.
 * </li>
 *  
 */
public class UserAuthHostBased extends UserAuth {

  public UserAuthHostBased() {
    System.err.println("new UserAuthHostBased()");
  }


  public boolean start(Session session) throws Exception {
    Logger log = JSch.getLogger();

    log.log(Logger.DEBUG, this + ".start() ... ");
    super.start(session);
        
    String idName = session.getConfig("hostbased.hostIdentity");
    if(idName == null) {
      log.log(Logger.WARN, "no host identity name.");
      // TODO: error message
      return false;
    }
    String hostName = session.getConfig("hostbased.hostname");
    if(hostName == null) {
      hostName = "localhost"; // TODO: get FQDN
    }
    String localUser = session.getConfig("hostbased.localuser");
    if(localUser == null) {
      localUser = System.getProperty("user.name");
    }

    log.log(Logger.DEBUG ,"idName: " + idName);
    log.log(Logger.DEBUG ,"hostName: " + hostName);
    log.log(Logger.DEBUG ,"localUser: " + localUser);

    Vector identities = session.jsch.identities;
    Identity hostIdentity = null;

    // find the host identity
    synchronized(identities) {
      for(int i = 0; i < identities.size(); i++) {
        Identity id = (Identity) identities.get(i);
        if(idName.equals(id.getName())) {
          hostIdentity = id;
          break;
        }
      }
    }
    if(hostIdentity == null) {
      log.log(Logger.WARN ,"no host identity found.");
      // TODO: error message
      return false;
    }
    log.log(Logger.DEBUG ,"hostIdentity: " + hostIdentity);
    byte[] pubkeyblob = hostIdentity.getPublicKeyBlob();
    if(pubkeyblob == null) {
      log.log(Logger.WARN ,"no public key blob.");
      // no public key => error
      return false;
    }

    // TODO: check if identity is encrypted, decrypt if necessary.
    
    if(hostIdentity.isEncrypted()) {
      log.log(Logger.INFO ,"encrypted!");
    }
    else {
      log.log(Logger.INFO ,"not encrypted.");
    }
    boolean result = hostIdentity.setPassphrase(new byte[0]);
    log.log(Logger.INFO ,"usable? " + result);


    // http://tools.ietf.org/html/rfc4252#section-9
    //
    //   byte      SSH_MSG_USERAUTH_REQUEST
    //   string    user name
    //   string    service name
    //   string    "hostbased"
    //   string    public key algorithm for host key
    //   string    public host key and certificates for client host
    //   string    client host name expressed as the FQDN in US-ASCII
    //   string    user name on the client host in ISO-10646 UTF-8
    //              encoding [RFC3629]
    //   string    signature

    packet.reset();

    buf.putByte((byte)SSH_MSG_USERAUTH_REQUEST);
    buf.putString(Util.str2byte(username));
    buf.putString(Util.str2byte("ssh-connection"));
    buf.putString(Util.str2byte("hostbased"));
    buf.putString(Util.str2byte(hostIdentity.getAlgName()));
    buf.putString(pubkeyblob);
    buf.putString(Util.str2byte(hostName));
    buf.putString(Util.str2byte(localUser));

    // we have to sign this:
    //    string     sessionID
    //    (mixed)    all the data collected yet in buf

    byte[] sessID = session.getSessionId();
    Buffer tempBuffer = new Buffer((sessID.length + 4) + (buf.index - 5));
    tempBuffer.putString(sessID);
    tempBuffer.putByte(buf.buffer, 5, buf.index - 5);
      
    byte[] signature = hostIdentity.getSignature(tempBuffer.buffer);
    if(signature == null) {
      log.log(Logger.WARN ,"signature problem.");
      // TODO: error message
      return false;
    }

    buf.putString(signature);

    session.write(packet);
    log.log(Logger.INFO, "SSH_MSG_USERAUTH_REQUEST sent");

    while(true) {
      buf=session.read(buf);
      int command = buf.getCommand()&0xff;

     
      if(command == SSH_MSG_USERAUTH_SUCCESS){
        log.log(Logger.INFO, "SSH_MSG_USERAUTH_SUCCESS received");
        return true;
      }
      else if(command == SSH_MSG_USERAUTH_BANNER){
        log.log(Logger.INFO, "SSH_MSG_USERAUTH_BANNER received");
        // throw away first 6 bytes to start after the command byte:
        buf.getInt(); buf.getByte(); buf.getByte();
        
        byte[] _message=buf.getString();
        byte[] lang=buf.getString();
        String message=Util.byte2str(_message);
        if(userinfo!=null){
          userinfo.showMessage(message);
        }
        continue;
      }
      else if(command==SSH_MSG_USERAUTH_FAILURE){
        log.log(Logger.INFO, "SSH_MSG_USERAUTH_FAILURE received");
        buf.getInt(); buf.getByte(); buf.getByte(); 
        byte[] foo=buf.getString();
        int partial_success=buf.getByte();
        //System.err.println(new String(foo)+
        //                   " partial_success:"+(partial_success!=0));
        if(partial_success!=0){
          throw new JSchPartialAuthException(Util.byte2str(foo));
        }
        return false;
      }
      // unknown command
      log.log(Logger.INFO, "unknown command " + command + " received");
      return false;
    }

  }




}