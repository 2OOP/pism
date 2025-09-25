package org.toop.app.gui;

import org.toop.events.WindowEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalServerSelector {
    private JPanel panel1;
    private JButton serverButton;
    private JButton localButton;
    private final JFrame frame;
    Locale locale = AppContext.getLocale();
    ResourceBundle resourceBundle = ResourceBundle.getBundle("Localization", locale);

    public LocalServerSelector() {
        frame = new JFrame(resourceBundle.getString("windowTitleServerSelector"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel1);
        frame.setSize(1920, 1080);
        frame.setLocationRelativeTo(null); // Sets to center
        frame.setVisible(true);

        serverButton.addActionListener(e -> onServerClicked());
        serverButton.setText(resourceBundle.getString("buttonSelectServer"));
        localButton.addActionListener(e -> onLocalClicked());
        localButton.setText(resourceBundle.getString("buttonSelectLocal"));
        new EventFlow().listen(WindowEvents.LanguageChanged.class, this::changeLanguage);
    }
    private void changeLanguage(WindowEvents.LanguageChanged event) {
        locale = AppContext.getLocale();
        resourceBundle = ResourceBundle.getBundle("Localization", locale);
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
