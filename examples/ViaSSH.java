/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */

import com.jcraft.jsch.*;
import java.util.NoSuchElementException;
import javax.swing.*;

/**
 * This program demonstrates how to create an SSH session over
 * an SSH proxy.
 *
 * You will be asked proxy-user@proxy-server and passwd,
 * then target-user@target-name and password.
 * If everything works fine, you will get the shell prompt.
 *
 * @author Paŭlo Ebermann
 */
public class ViaSSH {
  public static void main(String[] arg)
    throws JSchException, InterruptedException
  {
    JSch jsch=new JSch();
      
    String[] proxyInfo = queryUserAndHost("proxy server",
                                          arg.length > 0 ? arg[0] : null);

    // the gateway session

    Session gateway=jsch.getSession(proxyInfo[0], proxyInfo[1]);
      

    // password will be given via UserInfo interface.
    UserInfo ui=new SwingDialogUserInfo();
    gateway.setUserInfo(ui);
    gateway.connect();

    String[] targetInfo = queryUserAndHost("target server",
                                           arg.length > 1 ? arg[1] : null);

    // the target session

    Session session=jsch.getSession(targetInfo[0], targetInfo[1]);

    // we use an SSH proxy for our real connection.
    session.setProxy(new ProxySSH(gateway));

    // password will be given via UserInfo interface.
    session.setUserInfo(ui);

    System.err.println("connecting session ...");
    session.connect();

    System.err.println("session connected.");
    System.err.println("opening shell channel ...");
      
    Channel channel=session.openChannel("shell");

    channel.setOutputStream(System.out, true);
    channel.setExtOutputStream(System.err, true);

    channel.setInputStream(System.in, true);
      
    channel.connect();

    System.err.println("shell channel connected.");

    do {
      Thread.sleep(100);
    } while(!channel.isEOF());
    System.err.println("exitcode: " + channel.getExitStatus());
    session.disconnect();
    Thread.sleep(50);
      
    gateway.disconnect();
  }

  /**
   * queries the user for a username + hostname, if {@code useThis}
   * does not already contain it.
   * @return an array with the username in the first and hostname
   *   in the second component.
   */
  private static String[] queryUserAndHost(String promptSuffix,
                                           String useThis) {
    if(useThis == null || !useThis.contains("@")) {
      useThis = JOptionPane.showInputDialog("Enter username@hostname for " +
                                            promptSuffix,
                                            System.getProperty("user.name") +
                                            "@localhost");
    }
    if(useThis == null) {
      // TODO: better exception
      throw new NoSuchElementException("User does not want!");
    }
    return useThis.split("@");
  }

}
