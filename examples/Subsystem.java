/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import javax.swing.*;

/**
 * This program demonstrates using some subsystem.
 *
 * You will be asked username, hostname, passwd and the
 * subsystem name.
 * If everything works fine, the given subsystem will be
 * executed in the remote server, and can be interacted with.
 */
public class Subsystem{
  public static void main(String[] arg)
    throws Exception
  {
    JSch jsch=new JSch();  

    String host;
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
      
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);
    session.connect();

    String subsystem=JOptionPane.showInputDialog("Enter subsystem name", "");

    Channel channel=session.openChannel("subsystem");
    ((ChannelSubsystem)channel).setSubsystem(subsystem);
    ((ChannelSubsystem)channel).setPty(true);

    channel.setInputStream(System.in);
    ((ChannelSubsystem)channel).setErrStream(System.err);
    channel.setOutputStream(System.out);
    channel.connect();

  }

}
