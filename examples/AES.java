/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

public class AES{
  public static void main(String[] arg){

    try{
      JSch jsch=new JSch();

      //jsch.setKnownHosts("/home/foo/.ssh/known_hosts");

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
      //session.setPassword("your password");
 
      // username and password will be given via UserInfo interface.
      UserInfo ui = new SwingDialogUserInfo();
      session.setUserInfo(ui);

      session.setConfig("cipher.s2c", "aes128-cbc,3des-cbc,blowfish-cbc");
      session.setConfig("cipher.c2s", "aes128-cbc,3des-cbc,blowfish-cbc");
      session.setConfig("CheckCiphers", "aes128-cbc");

      session.connect();

      Channel channel=session.openChannel("shell");

      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);

      channel.connect();
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

}


