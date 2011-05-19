/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.util.HashMap;
import java.util.Map;

import java.io.*;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelDirectTCPIP;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


/**
 * A socket Implemention which bases its operation on a JSch session (opening a new Channel).
 */
class TunnelSocketImpl extends SocketImpl {

  private Map<Integer, Object> options;
  private Session baseSession;
	
  private InputStream in;
  private OutputStream out;
	
  /**
   * A {@link ChannelDirectTCPIP} for outgoing connections.
   */
  private ChannelDirectTCPIP channel;
	
	
  TunnelSocketImpl(Session s) {
    options = new HashMap<Integer, Object>();
    this.baseSession = s;
  }
	
	
	
  /* (non-Javadoc)
   * @see java.net.SocketOptions#getOption(int)
   */
  @Override
    public Object getOption(int key) throws SocketException {
    return options.get(key);
  }

  /* (non-Javadoc)
   * @see java.net.SocketOptions#setOption(int, java.lang.Object)
   */
  @Override
    public void setOption(int key, Object value) throws SocketException {
    options.put(key, value);
    // TODO Auto-generated method stub

  }
	

  @Override
    protected void create(boolean stream) throws IOException {
    if(!stream) {
      throw new SocketException("datagram sockets are not supported.");
    }
		
  }

  /* (non-Javadoc)
   * @see java.net.SocketImpl#connect(java.lang.String, int)
   */
  @Override
    protected void connect(String host, int port) throws IOException {
    try {
      this.channel = (ChannelDirectTCPIP)baseSession.openChannel("direct-tcpip");
      System.err.println("connect(" + host + ", " + port + ")");
      System.err.println("session.connected? " + baseSession.isConnected());
      channel.setHost(host);
      channel.setPort(port);
      channel.setOrgIPAddress("127.0.0.1");
      channel.setOrgPort(2006);

      PipedInputStream pIn = new PipedInputStream();
      PipedOutputStream pOut = new PipedOutputStream(pIn);

      channel.setOutputStream(pOut);
      this.out = channel.getOutputStream();
      this.in = pIn;

      channel.connect();
    }
    catch(JSchException ex) {
      SocketException sex = new SocketException();
      sex.initCause(ex);
      throw sex;
    }

  }

  


  /* (non-Javadoc)
   * @see java.net.SocketImpl#connect(java.net.InetAddress, int)
   */
  @Override
    protected void connect(InetAddress host, int port) throws IOException {
    this.connect(host.getHostAddress(), port);
    this.address = host;
  }

  /* (non-Javadoc)
   * @see java.net.SocketImpl#connect(java.net.SocketAddress, int)
   */
  @Override
    protected void connect(SocketAddress address, int timeout) throws IOException {
    if(address instanceof InetSocketAddress) {
      this.connect(((InetSocketAddress)address).getAddress(),
                   ((InetSocketAddress)address).getPort());
    }
    else {
      throw new SocketException("unknown socketAddress type: " + address);
    }
  }


  /* (non-Javadoc)
   * @see java.net.SocketImpl#bind(java.net.InetAddress, int)
   */
  @Override
    protected void bind(InetAddress host, int port) throws IOException {
    // TODO no idea when this will be called. For ServerSockets?
    System.err.println(this+".bind (" + host + "," + port+")");

  }

  /* (non-Javadoc)
   * @see java.net.SocketImpl#listen(int)
   */
  @Override
    protected void listen(int backlog) throws IOException {
    System.err.println(this+".listen (" + backlog+")");

  }

  /* (non-Javadoc)
   * @see java.net.SocketImpl#accept(java.net.SocketImpl)
   */
  @Override
    protected void accept(SocketImpl si) throws IOException {
    // TODO no idea when this will be called. For ServerSockets?
    System.err.println(this + ".accept(" + si+")");
  }


  /* (non-Javadoc)
   * @see java.net.SocketImpl#getInputStream()
   */
  @Override
    protected InputStream getInputStream() throws IOException {
    return in;
  }

  /* (non-Javadoc)
   * @see java.net.SocketImpl#getOutputStream()
   */
  @Override
    protected OutputStream getOutputStream() throws IOException {
    return out;
  }

  /* (non-Javadoc)
   * @see java.net.SocketImpl#available()
   */
  @Override
    protected int available() throws IOException {
    return in.available();
  }

  /* (non-Javadoc)
   * @see java.net.SocketImpl#close()
   */
  @Override
    protected void close() throws IOException {
    System.err.println(this+".close()");
    channel.disconnect();
  }

	
  /* (non-Javadoc)
   * @see java.net.SocketImpl#sendUrgentData(int)
   */
  @Override
    protected void sendUrgentData(int arg0) throws IOException {
    // TODO Auto-generated method stub

  }



}
