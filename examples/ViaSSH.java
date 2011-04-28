/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */

import com.jcraft.jsch.*;
import com.jcraft.jsch.Logger;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.swing.*;

/**
 * Connection using a SSH gateway as proxy.
 * 
 * It does not work reliably yet - this seems timing-related.
 * @author Paŭlo Ebermann
 */
public class ViaSSH {
  public static void main(String[] arg){


    try{
      JSch jsch=new JSch();
      JSch.setLogger(new Logger(){

		@Override
		public boolean isEnabled(int level) {
			return true;
		}

		@Override
		public void log(int level, String message) {
			System.err.println("["+level+"]"+message);
			
		}});

      String[] proxyInfo = queryUserAndHost("proxy server",
    		  arg.length > 0 ? arg[0] : null);
      System.err.println(Arrays.toString(proxyInfo));

      Session gateway=jsch.getSession(proxyInfo[0], proxyInfo[1]);
      

      // username and password will be given via UserInfo interface.
      UserInfo ui=new SwingDialogUserInfo();
      gateway.setUserInfo(ui);
      gateway.connect();

      String[] targetInfo = queryUserAndHost("target server", arg.length > 1 ? arg[1] : null);
      System.err.println(Arrays.toString(targetInfo));

      Session session=jsch.getSession(targetInfo[0], targetInfo[1]);

      // we use the proxy for our connection.
      session.setProxy(new SshGatewayProxy(gateway));

      // username and password will be given via UserInfo interface.
      session.setUserInfo(ui);

      System.err.println("connecting session ...");
      session.connect();

      System.err.println("session connected.");
      System.err.println("opening shell channel ...");
      
      Channel channel=session.openChannel("shell");

      channel.setOutputStream(System.out, true);
      channel.setExtOutputStream(System.err, true);

      channel.setInputStream(System.in, true);
      
      //Writer w = new OutputStreamWriter(channel.getOutputStream(), "UTF-8");

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
    catch(Exception e){
      e.printStackTrace();
    }
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
    if(useThis == null)
      throw new NoSuchElementException("User does not want!");
    return useThis.split("@");
  }


  /**
   * A Proxy implementation using an SSH Session to a gateway node
   * as the tunnel. 
   */
  private static class SshGatewayProxy implements Proxy {

    public SshGatewayProxy(Session gateway) {
      this.gateway = gateway;
    }
		
    private Session gateway;
		
		
    private ChannelDirectTCPIP channel;
    private InputStream iStream;
    private OutputStream oStream;
		
    public void close() {
      channel.disconnect();
    }

    /**
     * connects to the remote server.
     * @param ignore the socket factory. This is not used.
     * @param host the remote host to use.
     * @param port the port number to use.
     * @param timeout the timeout for connecting. (TODO: This is not used, for now.)
     * @throws Exception if there was some problem.
     */
    public void connect(SocketFactory ignore, String host,
                        int port, int timeout)
      throws Exception
    {
      System.err.println("creating tunnel channel to " + host + ":" + port +"...");
      channel = (ChannelDirectTCPIP)gateway.openChannel("direct-tcpip");
      channel.setHost(host);
      channel.setPort(port);
      channel.setOrgIPAddress("127.0.0.1");
      channel.setOrgPort((int)(Math.random()*20000+2000));
      channel.connect();
      iStream = channel.getInputStream();
      oStream = channel.getOutputStream();
      System.err.println("created tunnel channel: " + channel.isConnected());
    }

    /**
     * Returns an input stream to read data from the remote server. 
     */
    public InputStream getInputStream()
    {
      return iStream;
    }

    public OutputStream getOutputStream()
    {
      return oStream;
    }

    public Socket getSocket() {
      // there is no socket.
      return null;
    }
		
  }

}


