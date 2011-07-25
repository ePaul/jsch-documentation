
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

import com.jcraft.jsch.*;



class SwingWorkerExample {

    public static void main(String[] egal) {
        EventQueue.invokeLater(new Runnable(){public void run() {
            JButton testButton = new JButton("connect!");
            testButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        testConnectionButtonActionPerformed(ev);
                    }
                });
            JFrame f = new JFrame("bla");
            f.getContentPane().add(testButton);
            f.pack();
            f.setVisible(true);
        }});
    }

    private static void testConnectionButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                     

        SwingWorker sw = new SwingWorker(){

            protected Object doInBackground() throws Exception {
                JSch jsch = new JSch();

                String host = "ssh";
                String username = "ebermann";
                String password = "xxxxxx";

                Session session = jsch.getSession(username, host);
                session.setPassword(password);

                session.setTimeout(20000);
                System.out.println("Connecting to server...");
                session.connect();

                return null;
            }

            public void done(){
                try {
                    System.out.println(get().toString());
                } catch (Exception ex) {
                    System.err.println(ex);
                } 
            }
        };

        sw.execute();

    }  


}