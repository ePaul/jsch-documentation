import com.jcraft.jsch.*;
import com.jcraft.jsch.Logger;
import java.io.*;
import java.util.*;


/**
 * From http://stackoverflow.com/q/7906818/600500  (user Fuzzy),
 * added main method and slight fixes by Paŭlo Ebermann.
 *
 * This program connects to a server, executes a command there and outputs
 * the output of this command.
 */
public class FuzzyTest {

    private static final int AUTHENTICATION_METHOD_PASSWORD = 1;
    private static final int AUTHENTICATION_METHOD_KEY = 2;


    public static void main(String[] params)
        throws Exception 
    {
        if(params.length < 6) {
            System.err.println("Verwendung: \n" +
                               "java FuzzyTest user (-pw password | -key keyfile) host port command");
            return;
        }
        int authtype = 0;
        if(params[1].equals("-pw")) {
            authtype = AUTHENTICATION_METHOD_PASSWORD;
        }
        else if(params[1].equals("-key")) {
            authtype = AUTHENTICATION_METHOD_KEY;
        }
        String result =
            testSSHCommand(params[0], params[2], params[3],
                           Integer.parseInt(params[4]), params[5], authtype);
        System.out.println("Output: »" + result + "«");
    }


    public static String testSSHCommand ( String username, String password, String hostname, int port, String command, int authtype) throws Exception {    

        JSch jsch = new JSch();

        Logger l = new Logger() {
                public boolean isEnabled(int i) {
                    return true;
                }

                public void log(int i, String s) {
                    System.out.println("Log(jsch," + i + "): " + s);
                }
            };
        JSch.setLogger(l);


        if (authtype != AUTHENTICATION_METHOD_PASSWORD) {
            System.out.println("authmethod was "+authtype+" with key filename of "+password);
            jsch.addIdentity(password);
        } 

        Session session = jsch.getSession(username, hostname, 22);

        if (authtype != AUTHENTICATION_METHOD_KEY) {
            session.setPassword(password);
        }


        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        if (session.isConnected() ) {
            ChannelExec channelssh = (ChannelExec)          
                session.openChannel("exec");      
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            channelssh.setOutputStream(os);
            channelssh.setCommand(command);
            channelssh.connect();
            Thread.sleep(1000);
            channelssh.disconnect();
            Thread.sleep(1000);
            session.disconnect();

            return os.toString();
        } else {
            return "";
        }
    }

}