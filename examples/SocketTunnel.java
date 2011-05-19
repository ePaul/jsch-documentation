/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */

import javax.swing.JOptionPane;
import com.jcraft.jsch.*;
import java.io.*;
import java.net.*;

public class SocketTunnel {

  public static void main(String[] arg)
    throws IOException, JSchException
  {

    JSch.setLogger(new MyLogger());

    JSch jsch=new JSch();

    String host=null;
    if(arg.length>0){
      host=arg[0];
    }
    else{
      host=JOptionPane.showInputDialog("Enter username@hostname",
                                       "root@141.20.23.223"
                                       /* System.getProperty("user.name")+
                                          "@localhost"*/ ); 
    }
    String user=host.substring(0, host.indexOf('@'));
    host=host.substring(host.indexOf('@')+1);

    Session session=jsch.getSession(user, host, 22);
    session.setConfig("StrictHostKeyChecking", "no");

    // password will be given via UserInfo interface.
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);

    // making a connection with timeout.
    session.connect(30000);

    Socket target;
    if(arg.length>1) {
      int forwardPort = session.setPortForwardingL(0, "localhost", 2006);
      target = new Socket("localhost", forwardPort);
    }
    else {
      target = new TunnelSocket(session);
      target.connect(new InetSocketAddress("localhost", 2006));
    }
    
    InputStream in = target.getInputStream();
    byte[] buf = new byte[50];
    int len;
    while((len = in.read(buf)) > 0 ) {
      System.out.write(buf, 0, len);
    }
    in.close();
    target.close();

    session.disconnect();
  }

  private static class TunnelSocket extends Socket {
    TunnelSocket (Session sess) throws IOException {
      super(new TunnelSocketImpl(sess));
    }
  }


  /**
   * A Logger implementation.
   */
  public static class MyLogger implements com.jcraft.jsch.Logger {
    static java.util.Hashtable name=new java.util.Hashtable();
    static{
      name.put(new Integer(DEBUG), "DEBUG: ");
      name.put(new Integer(INFO), "INFO: ");
      name.put(new Integer(WARN), "WARN: ");
      name.put(new Integer(ERROR), "ERROR: ");
      name.put(new Integer(FATAL), "FATAL: ");
    }
    public boolean isEnabled(int level){
      return true;
    }
    public void log(int level, String message){
      System.err.print(name.get(new Integer(level)));
      System.err.println(message);
    }
  }



}