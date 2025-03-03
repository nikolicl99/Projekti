/*
 * Created by JFormDesigner on Fri Dec 06 00:36:27 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Start.StartPage;

import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenu.MainMenu;
import com.asss.www.ApotekarskaUstanova.Util.PasswordUtil;
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

    private WebSocketClient webSocketClient;

    public StartPage() {
        initComponents();
    }

    private void loginMouseClicked(MouseEvent e) {
        try {
            String username = email.getText().trim();
            String password = new String(sifra.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Unesite sve podatke!", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println(username);
            String hashedPassword = PasswordUtil.hashPassword(password); // Koristi PasswordUtils za heširanje

            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(this, "Uspešno ste se prijavili!");
                dispose();
                MainMenu.start();
            } else {
                JOptionPane.showMessageDialog(this, "Prijava nije uspela. Proverite podatke.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Došlo je do greške: " + ex.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authenticate(String email, String hashedPassword) throws IOException {
        // Kreiraj JSON objekat za prijavu
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
//                    return "LOGIN_SUCCESS".equalsIgnoreCase(response.trim());
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
        login = new JButton();
        email = new JTextField();
        emailLabel = new JLabel();
        sifra = new JPasswordField();
        sifraLabel = new JLabel();

        //======== this ========
        setPreferredSize(new Dimension(500, 500));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel1 ========
        {

            //---- label1 ----
            label1.setText("Apoteka \"Bolji Zivot\"");

            //---- login ----
            login.setIcon(null);
            login.setForeground(Color.darkGray);
            login.setSelectedIcon(null);
            login.setText("Login");
            login.setBorder(new LineBorder(Color.black, 1, true));
            login.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    loginMouseClicked(e);
                }
            });

            //---- emailLabel ----
            emailLabel.setText("Email:");

            //---- sifraLabel ----
            sifraLabel.setText("Sifra:");

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(145, 145, 145)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGap(25, 25, 25)
                                        .addComponent(label1))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(emailLabel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(email, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(sifraLabel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(sifra, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(187, 187, 187)
                                .addComponent(login, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(153, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addComponent(label1)
                        .addGap(38, 38, 38)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(emailLabel))
                            .addComponent(email, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(sifraLabel))
                            .addComponent(sifra, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                        .addComponent(login, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JLabel label1;
    private JButton login;
    private JTextField email;
    private JLabel emailLabel;
    private JPasswordField sifra;
    private JLabel sifraLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
