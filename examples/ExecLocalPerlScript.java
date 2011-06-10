/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

/**
 * This example demonstrates execution of a local
 * perl script on the server, without uploading
 * it as a file.
 *<p>
 *  Provide the name of the script file on the
 *  command line.
 *  You will be asked username, hostname, passwd.
 *  If everything works fine, the Perl script
 *</p>
 *<p>
 * Inspired by this Stack Overflow question:
 * <a href="http://stackoverflow.com/q/6251406/600500">send multiple files
 *    from windows machine to a linux remote server “Jsch code”</a>
 *</p>
 */
public class ExecLocalPerlScript {
  public static void main(String[] arg)
    throws Exception
  {

    if(arg.length < 1) {
      System.err.println("usage:");
      System.err.println("  java ExecLocalPerlScript script.pl [user@host]");
    }

    InputStream scriptStream =
      new BufferedInputStream(new FileInputStream(arg[0]));

    JSch jsch=new JSch();

    String host=null;
    if(arg.length>1){
      host=arg[1];
    }
    else{
      host=JOptionPane.showInputDialog("Enter username@hostname",
                                       System.getProperty("user.name")+
                                       "@localhost"); 
    }
    String user=host.substring(0, host.indexOf('@'));
    host=host.substring(host.indexOf('@')+1);

    Session session=jsch.getSession(user, host, 22);
      
    // password will be given via UserInfo interface.
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);
    session.connect();

    String command = "perl -w - README";

    Channel channel=session.openChannel("exec");
    ((ChannelExec)channel).setCommand(command);


    // input = script
    channel.setInputStream(scriptStream);

    ((ChannelExec)channel).setErrStream(System.err);

    InputStream in=channel.getInputStream();

    channel.connect();

    // loop to read the output until the command finished.
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
  }

}
