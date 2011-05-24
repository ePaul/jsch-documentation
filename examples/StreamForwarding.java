/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;
import java.io.InputStream;

/**
 *  This program will demonstrate the stream forwarding. The given Java
 *  I/O streams will be forwarded to the given remote host and port on
 *  the remote side.  It is similar to the -L option of ssh command,
 *  but you don't have to assign and open a local TCP port.
 * 
 *  You will be asked username, hostname,  passwd and host:hostport.
 *  If everything works fine, System.in and System.out streams will be
 *  forwarded to remote port and you can send messages from command line.
 */
public class StreamForwarding{
  public static void main(String[] arg)
    throws Exception
  {
    int port;

    JSch.setLogger(new Logger.MyLogger());
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
    session.connect();

    String foo=JOptionPane.showInputDialog("Enter host and port", 
                                           "host:port");
    host=foo.substring(0, foo.indexOf(':'));
    port=Integer.parseInt(foo.substring(foo.indexOf(':')+1));

    System.out.println("System.{in,out} will be forwarded to "+
                       host+":"+port+".");

    // setup forwarding:

    Channel channel=session.openChannel("direct-tcpip");
    ((ChannelDirectTCPIP)channel).setInputStream(System.in);
    ((ChannelDirectTCPIP)channel).setOutputStream(System.out);
    ((ChannelDirectTCPIP)channel).setHost(host);
    ((ChannelDirectTCPIP)channel).setPort(port);
    channel.connect();

  }

}
