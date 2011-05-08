/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

/**
 * This program will demonstrate how to use "aes128-cbc".
 *
 * You will be asked username, hostname and passwd.
 * If everything works fine, you will get the shell prompt.
 * 
 * This example shows how to change the default preference order
 * of encryption algorithms.
 */
public class AES{
  public static void main(String[] arg)
    throws Exception
  {

    JSch jsch=new JSch();

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
    UserInfo ui = new SwingDialogUserInfo();
    session.setUserInfo(ui);

    // set a new preference order of encryption algorithms.
    session.setConfig("cipher.s2c", "aes128-cbc,3des-cbc,blowfish-cbc");
    session.setConfig("cipher.c2s", "aes128-cbc,3des-cbc,blowfish-cbc");

    // check for existence of this algorithm before using it.
    session.setConfig("CheckCiphers", "aes128-cbc");

    session.connect();

    Channel channel=session.openChannel("shell");

    channel.setInputStream(System.in);
    channel.setOutputStream(System.out);

    channel.connect();
  }

}
