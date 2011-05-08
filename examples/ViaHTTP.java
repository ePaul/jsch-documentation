/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import javax.swing.*;

/**
 * This program will demonstrate the ssh session via HTTP proxy.
 *
 * You will be asked username, hostname, proxy-server and passwd. 
 * If everything works fine, you will get the shell prompt.
 */
public class ViaHTTP{
  public static void main(String[] arg)
    throws Exception
  {

    String proxy_host;
    int proxy_port;

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

    // setup of proxy

    String proxy=JOptionPane.showInputDialog("Enter proxy server",
                                             "hostname:port");
    proxy_host=proxy.substring(0, proxy.indexOf(':'));
    proxy_port=Integer.parseInt(proxy.substring(proxy.indexOf(':')+1));

    session.setProxy(new ProxyHTTP(proxy_host, proxy_port));

    // password will be given via UserInfo interface.
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);

    session.connect();

    Channel channel=session.openChannel("shell");

    channel.setInputStream(System.in);
    channel.setOutputStream(System.out);

    channel.connect();
  }

}
