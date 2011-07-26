import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

import com.jcraft.jsch.*;



class SwingWorkerExample {

    JTextField hostField;
    JTextField userNameField;
    JTextField passwordField;
    JPanel panel;


    public SwingWorkerExample() {
        JPanel p = panel = new JPanel(new GridLayout(0,2));
        hostField = new JTextField(20);
        userNameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton testButton = new JButton("connect!");
        testButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    testConnectionButtonActionPerformed(ev);
                }
            });
        p.add(new JLabel("host:"));
        p.add(hostField);
        p.add(new JLabel("user:"));
        p.add(userNameField);
        p.add(new JLabel("password:"));
        p.add(passwordField);
        p.add(testButton);
    }

    public JPanel getPanel() {
        return panel;
    }

    private void testConnectionButtonActionPerformed(ActionEvent evt) {

        SwingWorker sw = new SwingWorker(){

                protected Object doInBackground() throws Exception {
                    try {
                        JSch jsch = new JSch();

                        String host = hostField.getText();
                        String username = userNameField.getText();
                        String password = passwordField.getText();

                        Session session = jsch.getSession(username, host);
                        session.setPassword(password);
                        session.setConfig("StrictHostKeyChecking", "no");

                        session.setTimeout(20000);
                        System.out.println("Connecting to server...");
                        session.connect();

                        return session;
                    }
                    catch(Exception ex) {
                        ex.printStackTrace();
                        throw ex;
                    }
                }

                public void done(){
                    try {
                        System.out.println(get());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

        sw.execute();

    }


    public static void main(String[] egal) {
        EventQueue.invokeLater(new Runnable(){public void run() {
            SwingWorkerExample ex = new SwingWorkerExample();
            JFrame f = new JFrame("bla");
            f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            f.setContentPane(ex.getPanel());
            f.pack();
            f.setVisible(true);
        }});
    }
}