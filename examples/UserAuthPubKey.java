/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

/**
 * An example showing how to use public-key authentication
 * (using a private key in a file).
 */
public class UserAuthPubKey{
  public static void main(String[] arg)
    throws Exception
  {
    JSch jsch=new JSch();

    // we let the user choose the private key file.

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Choose your privatekey(ex. ~/.ssh/id_dsa)");
    chooser.setFileHidingEnabled(false);
    int returnVal = chooser.showOpenDialog(null);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      System.out.println("You chose "+
                         chooser.getSelectedFile().getAbsolutePath()+".");
      jsch.addIdentity(chooser.getSelectedFile().getAbsolutePath()
                       //			 , "passphrase"
                       );
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

}
