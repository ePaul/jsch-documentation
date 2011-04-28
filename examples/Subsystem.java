/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

public class Subsystem{
  public static void main(String[] arg){
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

      /*
      channel.setInputStream(System.in);
      ((ChannelSubsystem)channel).setErrStream(System.err);
      InputStream in = channel.getInputStream();
      channel.connect();

      byte[] tmp=new byte[1024];
      while(true){
        while(in.available()>0){
          int i=in.read(tmp, 0, 1024);
          if(i<0)break;
          System.out.print(new String(tmp, 0, i));
        }
        if(channel.isClosed()){
          System.out.println("exit-status: "+channel.getExitStatus());
          break;
        }
        try{Thread.sleep(1000);}catch(Exception ee){}
      }
      channel.disconnect();
      session.disconnect();
      */
    }
    catch(Exception e){
      System.out.println(e);
    }
  }

}
