package org.toop.frontend.UI;

import javax.swing.*;

public class LocalServerSelector {
    private JPanel panel1;
    private JButton serverButton;
    private JButton localButton;
    private final JFrame frame;

    public LocalServerSelector() {
        frame = new JFrame("Server Selector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel1);
        frame.setSize(1920, 1080);
        frame.setLocationRelativeTo(null); // Sets to center
        frame.setVisible(true);

        serverButton.addActionListener(e -> onServerClicked());
        localButton.addActionListener(e -> onLocalClicked());
        
    }

    private void onServerClicked() {
        frame.dispose();
        new RemoteGameSelector();
    }

    private void onLocalClicked() {
        frame.dispose();
        new LocalGameSelector();
    }

}
