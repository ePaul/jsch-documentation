/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

public class StreamForwarding{
  public static void main(String[] arg){
    int port;

    try{
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
      Channel channel=session.openChannel("direct-tcpip");
      ((ChannelDirectTCPIP)channel).setInputStream(System.in);
      ((ChannelDirectTCPIP)channel).setOutputStream(System.out);
      ((ChannelDirectTCPIP)channel).setHost(host);
      ((ChannelDirectTCPIP)channel).setPort(port);
      channel.connect();
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

}


