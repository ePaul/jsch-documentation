/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.io.File;
import javax.swing.*;

/**
 * This program will demonstrate the user authentification by public key.
 *
 * You will be asked username, hostname, private key file and
 * (if necessary) passphrase. If everything works fine, you will get
 * the shell prompt.
 */
public class UserAuthHB{
  public static void main(String[] arg)
    throws Exception
  {
    JSch.setLogger(new MyLogger());
    JSch jsch=new JSch();

    // we let the user choose the private key file.

    JFileChooser chooser = new JFileChooser(new File("."));
    chooser.setDialogTitle("Choose your host's private key");
    chooser.setFileHidingEnabled(false);
    int returnVal = JFileChooser.APPROVE_OPTION;// chooser.showOpenDialog(null);
    String hostIdentity;
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      hostIdentity = "./example-hostkey";//chooser.getSelectedFile().getAbsolutePath();
      System.out.println("You chose "+ hostIdentity +".");
      jsch.addIdentity(hostIdentity);
    }
    else {
      return;
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

    session.setConfig("StrictHostKeyChecking", "no");

    session.setConfig("hostbased.hostIdentity", hostIdentity);
    session.setConfig("hostbased.hostname", "jsch.example.com");
    session.setConfig("hostbased.localuser", "testuser");

    session.setConfig("userauth.hostbased",
                      "com.jcraft.jsch.UserAuthHostBased");
    session.setConfig("PreferredAuthentications", "hostbased");

    // passphrase for the key will be given via UserInfo interface,
    // if necessary (i.e if the key is encrypted).
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);

    session.connect();

    Channel channel=session.openChannel("shell");

    channel.setInputStream(System.in);
    channel.setOutputStream(System.out);

    channel.connect();
  }


  /**
   * A Logger implementation.
   */
  public static class MyLogger implements com.jcraft.jsch.Logger {
    static java.util.Hashtable name=new java.util.Hashtable();
    static{
      name.put(new Integer(DEBUG), "DEBUG: ");
      name.put(new Integer(INFO), "INFO: ");
      name.put(new Integer(WARN), "WARN: ");
      name.put(new Integer(ERROR), "ERROR: ");
      name.put(new Integer(FATAL), "FATAL: ");
    }
    public boolean isEnabled(int level){
      return true;
    }
    public void log(int level, String message){
      System.err.print(name.get(new Integer(level)));
      System.err.println(message);
    }
  }

}
