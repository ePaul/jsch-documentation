/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import javax.swing.*;

/**
 * This program enables you to connect to sshd server and get the shell prompt.
 *
 * You will be asked username, hostname and passwd. 
 * If everything works fine, you will get the shell prompt. Output will
 * be ugly because of lack of terminal-emulation, but you can issue commands.
 */
public class Shell{
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
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);

    // making a connection with timeout.
    session.connect(30000);

    Channel channel=session.openChannel("shell");

    channel.setInputStream(System.in);
    channel.setOutputStream(System.out);

    channel.connect(3*1000);
  }

}
