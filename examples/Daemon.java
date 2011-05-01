/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

/**
 *  This program will demonstrate how to provide a network service like
 *  inetd by using remote port-forwarding functionality.
 * 
 *  You will be asked username@hostname, port, and passwd.
 *  If everything works fine, you will see a message about the
 *  forwarding in stdout.
 * 
 *  Try the port on the remote host - the parrot will repeat everything
 *  you send. (You can quit by sending a message starting with ".", or
 *  simply by closing the stream from your side.)
 */
public class Daemon{
  public static void main(String[] arg)
    throws Exception
  {

    int rport;

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

    String foo=JOptionPane.showInputDialog("Enter remote port number", 
                                           "8888");
    rport=Integer.parseInt(foo);

    // username and password will be given via UserInfo interface.
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);

    session.connect();

    //session.setPortForwardingR(rport, Parrot.class.getName());
    session.setPortForwardingR(rport, "Daemon$Parrot");
    System.out.println(host+":"+rport+" <--> "+"Parrot");
  }

  /**
   * A very simple implementation of ForwardedTCPIPDaemon, the
   * Parrot simply repeats what the other side sends. It quits
   * after the first chunk read which started with ".".
   */
  public static class Parrot implements ForwardedTCPIPDaemon{
    ChannelForwardedTCPIP channel;
    InputStream in;
    OutputStream out;

    /**
     * supplies the channel and streams.
     */
    public void setChannel(ChannelForwardedTCPIP c,
                           InputStream in, OutputStream out) {
      this.channel=c;
      this.in=in;
      this.out=out;
    }

    /** we ignore any arguments. */
    public void setArg(Object[] arg){ /* ignored. */}

    /**
     * runs the daemon.
     */
    public void run(){
      try{
        byte[] buf=new byte[1024];
        System.out.println("remote port: "+channel.getRemotePort());
        System.out.println("remote host: "+channel.getSession().getHost());
        while(true){
          int i=in.read(buf, 0, buf.length);
          if(i<=0)break;
          out.write(buf, 0, i);
          out.flush();
          if(buf[0]=='.')break;
        }
      }
      catch(JSchException e){
        System.out.println("session is down.");
      }
      catch(IOException e){
      }
    }
  }

}
