/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

/**
 * This program will demonstrate the 'known_hosts' file handling.
 * 
 * You will be asked username, hostname, a path for 'known_hosts' and passwd. 
 * If everything works fine, you will get the shell prompt.
 * In current implementation, jsch only reads 'known_hosts' for checking
 * and does not modify it.
 */
public class KnownHosts{
  public static void main(String[] arg)
    throws Exception
  {

    JSch jsch=new JSch();

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Choose your known_hosts (ex. ~/.ssh/known_hosts)");
    chooser.setFileHidingEnabled(false);
    int returnVal=chooser.showOpenDialog(null);
    if(returnVal==JFileChooser.APPROVE_OPTION) {
      System.out.println("You chose "+
                         chooser.getSelectedFile().getAbsolutePath()+".");

      // set file as host key repository.
      jsch.setKnownHosts(chooser.getSelectedFile().getAbsolutePath());
    }


    // list known hosts:

    HostKeyRepository hkr=jsch.getHostKeyRepository();
    HostKey[] hks=hkr.getHostKey();
    if(hks!=null){
      System.out.println("Host keys in "+hkr.getKnownHostsRepositoryID());
      for(int i=0; i<hks.length; i++){
        HostKey hk=hks[i];
        System.out.println(hk.getHost()+" "+
                           hk.getType()+" "+
                           hk.getFingerPrint(jsch));
      }
      System.out.println("");
    }

    String host=null;
    if(arg.length>0){
      host=arg[0];
    }
    else{
      host=JOptionPane.showInputDialog("Enter username@hostname",
                                       System.getProperty("user.name")+
                                       "@localhost"); 
    }
    String user=host.substring(0, host.indexOf('@'));
    host=host.substring(host.indexOf('@')+1);

    Session session=jsch.getSession(user, host, 22);

    // password will be given via UserInfo interface.
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);

    /*
    // In adding to known_hosts file, host names will be hashed. 
    session.setConfig("HashKnownHosts",  "yes");
    */

    session.connect();

    // print out current session's host key:
    
    HostKey hk=session.getHostKey();
    System.out.println("HostKey: "+
                       hk.getHost()+" "+
                       hk.getType()+" "+
                       hk.getFingerPrint(jsch));
    

    Channel channel=session.openChannel("shell");

    channel.setInputStream(System.in);
    channel.setOutputStream(System.out);

    channel.connect();
  }

}
