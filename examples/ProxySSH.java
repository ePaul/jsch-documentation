/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */

import java.net.Socket;
import java.io.*;
import com.jcraft.jsch.*;

/**
 * A Proxy implementation using a JSch Session to a gateway host
 * as the tunnel. The Session will not be closed on close of the Proxy,
 * only the tunneling channel.
 *
 * @author Paŭlo Ebermann
 */
public class ProxySSH implements Proxy {

  public ProxySSH(Session gateway) {
    this.gateway = gateway;
  }
                
  private Session gateway;
                
                
  private ChannelDirectTCPIP channel;
  private InputStream iStream;
  private OutputStream oStream;
                
  /**
   * closes the socket + streams.
   */
  public void close() {
    channel.disconnect();
  }

  /**
   * connects to the remote server.
   * @param ignore the socket factory. This is not used.
   * @param host the remote host to use.
   * @param port the port number to use.
   * @param timeout the timeout for connecting.
   *     (TODO: This is not used, for now.)
   * @throws Exception if there was some problem.
   */
  public void connect(SocketFactory ignore, String host,
                      int port, int timeout)
    throws Exception
  {
    channel = (ChannelDirectTCPIP)gateway.openChannel("direct-tcpip");
    channel.setHost(host);
    channel.setPort(port);
    // important: first create the streams, then connect.
    iStream = channel.getInputStream();
    oStream = channel.getOutputStream();
    channel.connect();
  }

  /**
   * Returns an input stream to read data from the remote server. 
   */
  public InputStream getInputStream()
  {
    return iStream;
  }

  /**
   * Returns an output stream to write data to the remote server.
   */
  public OutputStream getOutputStream()
  {
    return oStream;
  }

  /**
   * returns {@code null}, as there is no usable socket.
   */
  public Socket getSocket() {
    // there is no socket.
    return null;
  }
                
}
