/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import javax.swing.*;

/**
 * This program will demonstrate X11 forwarding.
 *
 * You will be asked username, hostname, displayname and passwd. 
 * If your X server does not run at 127.0.0.1:0, please enter
 * correct displayname. You need your X server be reachable by
 * TCP, not only unix-sockets.
 *
 * If everything works fine, you will get the shell prompt.
 * Try X applications; for example, xlogo.
 */
public class X11Forwarding{
  public static void main(String[] arg)
    throws Exception
  {

    String xhost="127.0.0.1";
    int xport=0;

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

      String display=JOptionPane.showInputDialog("Please enter display name", 
						 xhost+":"+xport);
      xhost=display.substring(0, display.indexOf(':'));
      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

      session.setX11Host(xhost);
      session.setX11Port(xport+6000);

      // username and password will be given via UserInfo interface.
      UserInfo ui=new SwingDialogUserInfo();
      session.setUserInfo(ui);
      session.connect();

      Channel channel=session.openChannel("shell");

      channel.setXForwarding(true);

      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);

      channel.connect();
  }

}


