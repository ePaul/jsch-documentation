/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

/**
 * This program will demonstrate the packet compression.
 *
 * You will be asked username, hostname and passwd. 
 * If everything works fine, you will get the shell prompt. 
 * In this program, all data between sshd server and jsch
 * will be compressed, if the server supports this.
 */
public class Compression{
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

    // username and password will be given via UserInfo interface.
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);

    // set the configuration to enable compression.

    session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
    session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
    session.setConfig("compression_level", "9");

    session.connect();

    Channel channel=session.openChannel("shell");

    channel.setInputStream(System.in);
    channel.setOutputStream(System.out);

    channel.connect();
  }

}
