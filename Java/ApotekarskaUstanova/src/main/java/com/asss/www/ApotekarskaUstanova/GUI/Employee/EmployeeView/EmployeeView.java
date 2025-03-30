/*
 * Created by JFormDesigner on Tue Dec 03 19:29:36 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeView;

import java.awt.event.*;

import com.asss.www.ApotekarskaUstanova.GUI.Employee.ExEmployeeList.ExEmployeeList;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList.EmployeeList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author lniko
 */
public class EmployeeView extends JFrame {
    public EmployeeView() {
        initComponents();
        loadData();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            EmployeeView frame = new EmployeeView();
            frame.setTitle("Zaposleni");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        if (getPreviousForm() == 1){
            EmployeeList.start();
        } else if (getPreviousForm() == 2) {
            ExEmployeeList.start();
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel = new JPanel();
        Nazad = new JButton();
        label1 = new JLabel();
        imePrezime = new JTextField();
        label2 = new JLabel();
        town = new JTextField();
        tip_Label = new JLabel();
        tip = new JTextField();
        label3 = new JLabel();
        address = new JTextField();
        telefon_Label = new JLabel();
        telefon_edit = new JTextField();
        label4 = new JLabel();
        number = new JTextField();
        email_Label = new JLabel();
        email_edit = new JTextField();
        label5 = new JLabel();
        aptNumber = new JTextField();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel ========
        {
            panel.setBackground(new Color(0x3d8d7a));
            panel.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3,gap 5 5",
                // columns
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]",
                // rows
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]"));

            //---- Nazad ----
            Nazad.setText("Nazad");
            Nazad.setPreferredSize(new Dimension(90, 40));
            Nazad.setBackground(new Color(0xb3d8a8));
            Nazad.setForeground(Color.darkGray);
            Nazad.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NazadMouseClicked(e);
                }
            });
            panel.add(Nazad, "cell 1 1");

            //---- label1 ----
            label1.setText("Ime i Prezime:");
            label1.setForeground(new Color(0xfbffe4));
            panel.add(label1, "cell 1 3");

            //---- imePrezime ----
            imePrezime.setEditable(false);
            imePrezime.setBackground(new Color(0xb3d8a8));
            imePrezime.setForeground(Color.darkGray);
            panel.add(imePrezime, "cell 2 3 2 1");

            //---- label2 ----
            label2.setText("Grad:");
            label2.setForeground(new Color(0xfbffe4));
            panel.add(label2, "cell 4 3");

            //---- town ----
            town.setEditable(false);
            town.setBackground(new Color(0xb3d8a8));
            town.setForeground(Color.darkGray);
            panel.add(town, "cell 5 3 2 1");

            //---- tip_Label ----
            tip_Label.setText("Tip Zaposlenog:");
            tip_Label.setForeground(new Color(0xfbffe4));
            panel.add(tip_Label, "cell 1 4");

            //---- tip ----
            tip.setEditable(false);
            tip.setBackground(new Color(0xb3d8a8));
            tip.setForeground(Color.darkGray);
            panel.add(tip, "cell 2 4 2 1");

            //---- label3 ----
            label3.setText("Adresa:");
            label3.setForeground(new Color(0xfbffe4));
            panel.add(label3, "cell 4 4");

            //---- address ----
            address.setEditable(false);
            address.setBackground(new Color(0xb3d8a8));
            address.setForeground(Color.darkGray);
            panel.add(address, "cell 5 4 2 1");

            //---- telefon_Label ----
            telefon_Label.setText("Telefon:");
            telefon_Label.setForeground(new Color(0xfbffe4));
            panel.add(telefon_Label, "cell 1 5");

            //---- telefon_edit ----
            telefon_edit.setEditable(false);
            telefon_edit.setBackground(new Color(0xb3d8a8));
            telefon_edit.setForeground(Color.darkGray);
            panel.add(telefon_edit, "cell 2 5 2 1");

            //---- label4 ----
            label4.setText("Broj:");
            label4.setForeground(new Color(0xfbffe4));
            panel.add(label4, "cell 4 5");

            //---- number ----
            number.setEditable(false);
            number.setBackground(new Color(0xb3d8a8));
            number.setForeground(Color.darkGray);
            panel.add(number, "cell 5 5");

            //---- email_Label ----
            email_Label.setText("Email:");
            email_Label.setForeground(new Color(0xfbffe4));
            panel.add(email_Label, "cell 1 6");

            //---- email_edit ----
            email_edit.setEditable(false);
            email_edit.setBackground(new Color(0xb3d8a8));
            email_edit.setForeground(Color.darkGray);
            panel.add(email_edit, "cell 2 6 2 1");

            //---- label5 ----
            label5.setText("Stan:");
            label5.setForeground(new Color(0xfbffe4));
            panel.add(label5, "cell 4 6");

            //---- aptNumber ----
            aptNumber.setEditable(false);
            aptNumber.setBackground(new Color(0xb3d8a8));
            aptNumber.setForeground(Color.darkGray);
            panel.add(aptNumber, "cell 5 6");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        setSize(800, 500);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    private void loadData() {
        String urlString = "http://localhost:8080/api/employees/" + getSelectedEmployeeId();
        try {
            // Priprema URL-a
            URL url = new URL(urlString);

            // Otvaranje konekcije
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Dodavanje Authorization header-a sa JWT tokenom
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            // Provera odgovora servera
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Čitanje odgovora servera
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora u objekat Employees
                    ObjectMapper objectMapper = new ObjectMapper();
                    Employees employees = objectMapper.readValue(response, Employees.class);

                    // Popunjavanje podataka u UI komponentama
                    imePrezime.setText(employees.getName() + " " + employees.getSurname());
                    tip.setText(employees.getEmployeeType().getName());
                    telefon_edit.setText(employees.getMobile());
                    email_edit.setText(employees.getEmail());
                    town.setText(employees.getAddress().getTown().getName());
                    address.setText(employees.getAddress().getAddress());
                    number.setText(employees.getAddress().getNumber());
                    if (Integer.parseInt(employees.getAddress().getAptNumber()) == 0){
                        aptNumber.setVisible(false);
                        label5.setVisible(false);
                    }
                    aptNumber.setText(employees.getAddress().getAptNumber());
                }
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // Token nije validan
                JOptionPane.showMessageDialog(this, "Nevažeći token. Prijavite se ponovo.", "Greška", JOptionPane.WARNING_MESSAGE);
            } else {
                // Drugi statusni kodovi
                JOptionPane.showMessageDialog(this, "Greška pri dohvatu podataka: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel;
    private JButton Nazad;
    private JLabel label1;
    private JTextField imePrezime;
    private JLabel label2;
    private JTextField town;
    private JLabel tip_Label;
    private JTextField tip;
    private JLabel label3;
    private JTextField address;
    private JLabel telefon_Label;
    private JTextField telefon_edit;
    private JLabel label4;
    private JTextField number;
    private JLabel email_Label;
    private JTextField email_edit;
    private JLabel label5;
    private JTextField aptNumber;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static int selectedEmployeeId;
    private static int previousForm;

    public static int getSelectedEmployeeId() {
        return selectedEmployeeId;
    }

    public static void setSelectedEmployeeId(int selectedEmployeeId) {
        EmployeeView.selectedEmployeeId = selectedEmployeeId;
    }

    public static int getPreviousForm() {
        return previousForm;
    }

    public static void setPreviousForm(int previousForm) {
        EmployeeView.previousForm = previousForm;
    }
}
