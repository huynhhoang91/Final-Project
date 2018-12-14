package chat.client;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The view of the MVC pattern. This class wait until the user will send the message to other clients or until the server will send some message
 * and output the refreshed information to the user.
 *
 */
public class ClientView extends JFrame {
    private final Client controller;

    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane3;
    private JTextArea messagesTxtArea;
    private JLabel nameLabel;
    private JTextArea onlineUsersTextArea;
    private JButton sendButton;
    private JTextField textField;
    private String clientName;
    private boolean isConnected;

    public ClientView(ClientController controller) {
        this.controller = controller;
        initComponents();
    }

    public ClientView(String frameName) {
        controller = null;
        this.setName(frameName);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        textField = new JTextField();
        sendButton = new JButton();
        jScrollPane1 = new JScrollPane();
        messagesTxtArea = new JTextArea();
        nameLabel = new JLabel();
        jScrollPane3 = new JScrollPane();
        onlineUsersTextArea = new JTextArea();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        sendButton.setText("SEND");

        messagesTxtArea.setColumns(20);
        messagesTxtArea.setRows(5);
        messagesTxtArea.setEditable(false);
        jScrollPane1.setViewportView(messagesTxtArea);

        nameLabel.setText("Name: ");

        onlineUsersTextArea.setColumns(20);
        onlineUsersTextArea.setRows(5);
        onlineUsersTextArea.setEditable(false);
        jScrollPane3.setViewportView(onlineUsersTextArea);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 365, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(nameLabel)
                                                        .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(textField, GroupLayout.PREFERRED_SIZE, 435, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sendButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(0, 6, Short.MAX_VALUE)
                                                .addComponent(nameLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(textField, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sendButton, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().trim().equals("")) {
                    controller.sendTextMessage(textField.getText());
                    textField.setText("");
                }
            }
        });

        pack();
        setVisible(true);
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new ClientView("TEST").setVisible(true));
    }

    public String getServerAddress() {
        return JOptionPane.showInputDialog(
                this,
                "Enter server address: ",
                "Client configuration",
                JOptionPane.QUESTION_MESSAGE);
    }

    public int getServerPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(
                    this,
                    "Enter server port: ",
                    "Client configuration",
                    JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "The server port is incorrect. Try again",
                        "Client configuration",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String getUserName() {
        return JOptionPane.showInputDialog(
                this,
                "Enter your name: ",
                "Client configuration",
                JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * If the server accept the client, the user will be notified
     *
     * @param clientConnected
     */
    public void notifyConnectionStatusChanged(boolean clientConnected) {
        textField.setEditable(clientConnected);
        if (clientConnected) {
            JOptionPane.showMessageDialog(
                    this,
                    "The connection with server is established",
                    "Chat",
                    JOptionPane.INFORMATION_MESSAGE);
            isConnected = true;
            nameLabel.setText(nameLabel.getText() + " " + clientName);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Client is not connected to the server",
                    "Chat",
                    JOptionPane.ERROR_MESSAGE);
            isConnected = false;
        }

    }

    /**
     * Refresh message history after new message is received
     */
    public void refreshMessages() {
        messagesTxtArea.append(controller.getModel().getNewMessage() + "\n");
    }

    /**
     * Refresh online user list after new member was joined
     */
    public void refreshUsers() {
        ClientModel model = controller.getModel();
        StringBuilder sb = new StringBuilder();
        for (String userName : model.getAllUserNames()) {
            sb.append(userName).append("\n");
        }
        onlineUsersTextArea.setText(sb.toString());
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean isConnected() {
        return isConnected;
    }
}

