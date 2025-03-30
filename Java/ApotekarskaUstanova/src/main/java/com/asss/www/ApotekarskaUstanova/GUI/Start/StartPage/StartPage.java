/*
 * Created by JFormDesigner on Fri Dec 06 00:36:27 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Start.StartPage;

import com.asss.www.ApotekarskaUstanova.GUI.CashRegister.CashRegister.CashRegister;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin.MainMenuAdmin;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuInventory.MainMenuInventory;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Util.PasswordUtil;
import net.miginfocom.swing.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import java.awt.event.MouseEvent;
/**
 * @author lniko
 */

@Configuration
public class StartPage extends JFrame {

    public StartPage() {
        initComponents();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            StartPage frame = new StartPage();
            frame.setTitle("Apotekarska Ustanova");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void loginMouseClicked(MouseEvent e) {
        try {
            String username = email.getText().trim();
            String password = String.valueOf(sifra.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Unesite sve podatke!", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authenticate(username, password)) {
                dispose();
                if (JwtResponse.getTypeId() == 1) {
                    MainMenuAdmin.start();
                } else if (JwtResponse.getTypeId() == 2) {
                    CashRegister.start();
                } else if (JwtResponse.getTypeId() == 4) {
                    MainMenuInventory.start();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Prijava nije uspela. Proverite podatke.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Došlo je do greške: " + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authenticate(String email, String hashedPassword) throws IOException {
        String jsonPayload = String.format("{\"email\":\"%s\", \"password\":\"%s\"}",
                email, hashedPassword);

        // Kreiraj konekciju ka serveru
        URL url = new URL("http://localhost:8080/api/auth/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Proveri odgovor
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                if (scanner.hasNextLine()) {
                    String response = scanner.nextLine();
                    return true;
                }
            }
        }
        return false;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        label1 = new JLabel();
        emailLabel = new JLabel();
        email = new JTextField();
        sifraLabel = new JLabel();
        sifra = new JPasswordField();
        login = new JButton();

        //======== this ========
        setPreferredSize(new Dimension(500, 500));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0x3d8d7a));
            panel1.setMinimumSize(new Dimension(254, 166));
            panel1.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3,gap 5 5",
                // columns
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]",
                // rows
                "[75]" +
                "[75]" +
                "[75]" +
                "[75]" +
                "[75]" +
                "[75]" +
                "[75]" +
                "[75]" +
                "[75]" +
                "[75]"));

            //---- label1 ----
            label1.setText("Apoteka \"Bolji Zivot\"");
            label1.setFont(new Font("Inter", Font.PLAIN, 18));
            label1.setForeground(new Color(0xfbffe4));
            panel1.add(label1, "cell 3 2 4 1,alignx center,growx 0");

            //---- emailLabel ----
            emailLabel.setText("Email:");
            emailLabel.setForeground(new Color(0xfbffe4));
            panel1.add(emailLabel, "cell 3 3");

            //---- email ----
            email.setForeground(Color.darkGray);
            email.setBackground(new Color(0xb3d8a8));
            panel1.add(email, "cell 4 3 3 1,growx");

            //---- sifraLabel ----
            sifraLabel.setText("Sifra:");
            sifraLabel.setForeground(new Color(0xfbffe4));
            panel1.add(sifraLabel, "cell 3 4");

            //---- sifra ----
            sifra.setForeground(Color.darkGray);
            sifra.setBackground(new Color(0xb3d8a8));
            panel1.add(sifra, "cell 4 4 3 1,growx");

            //---- login ----
            login.setIcon(null);
            login.setForeground(Color.darkGray);
            login.setSelectedIcon(null);
            login.setText("Login");
            login.setBorder(new LineBorder(Color.black, 1, true));
            login.setBackground(new Color(0xb3d8a8));
            login.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    loginMouseClicked(e);
                }
            });
            panel1.add(login, "cell 4 6 2 1,grow");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JLabel label1;
    private JLabel emailLabel;
    private JTextField email;
    private JLabel sifraLabel;
    private JPasswordField sifra;
    private JButton login;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
