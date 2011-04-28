/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

public class Daemon{
  public static void main(String[] arg){

    int rport;

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
    catch(Exception e){
      System.out.println(e);
    }
  }

  public static class Parrot implements ForwardedTCPIPDaemon{
    ChannelForwardedTCPIP channel;
    Object[] arg;
    InputStream in;
    OutputStream out;

    public void setChannel(ChannelForwardedTCPIP c, InputStream in, OutputStream out){
      this.channel=c;
      this.in=in;
      this.out=out;
    }
    public void setArg(Object[] arg){this.arg=arg;}
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
