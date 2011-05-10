/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import javax.swing.*;

/**
 * This program will demonstrate X11 forwarding.
 *
 * You will be asked username, hostname, displayname,
 * X-authentication cookie and passwd.
 * If your X server does not run at 127.0.0.1:0, please enter
 * correct displayname. You need your X server be reachable by
 * TCP, not only unix-sockets or other local means.
 *
 * If everything works fine, you will get the shell prompt.
 * Try X applications; for example, xlogo.
 *
 * You can also supply arguments by command line:
 *   user@host  cookie  display
 */
public class X11Forwarding{
  public static void main(String[] arg)
    throws Exception
  {
    JSch jsch=new JSch();

    // get the target host and open session.

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


    // get the right display

    String display;
    if( arg.length > 2) {
      display = arg[2];
    }
    else {
      display = System.getenv("DISPLAY");
      if(display == null) {
        display = ":0";
      }
      if(display.startsWith(":")) {
        display = "localhost" + display;
      }
      // ask the user:
      display=
        JOptionPane.showInputDialog("Please enter display name", display);
    }
    String xhost=display.substring(0, display.indexOf(':'));
    if(xhost.equals("")) {
      xhost = "localhost";
    }
    int xport=Integer.parseInt(display.substring(display.indexOf(':')+1));

    session.setX11Host(xhost);
    session.setX11Port(xport+6000);

    // setup the X cookie.

    String cookie =
      arg.length > 1 ? arg[1] :
      JOptionPane.showInputDialog("please enter cookie for display\n" +
                                  "(try \"xauth list "+display+"\")");
    session.setX11Cookie(cookie);

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
