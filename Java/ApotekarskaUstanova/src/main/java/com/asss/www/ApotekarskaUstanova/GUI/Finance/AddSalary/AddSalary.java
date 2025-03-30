/*
 * Created by JFormDesigner on Sun Mar 16 23:04:23 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Finance.AddSalary;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.asss.www.ApotekarskaUstanova.Dto.SalariesDto;
import com.asss.www.ApotekarskaUstanova.Dto.SalaryDetailsDto;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.GUI.Finance.EverySalary.EverySalary;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.OrderSupplies.OrderSupplies;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.*;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * @author lniko
 */
public class AddSalary extends JFrame {
    public AddSalary() {
        initComponents();
        loadEmployeesCB();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            AddSalary frame = new AddSalary();
            frame.setTitle("Salary");
            frame.setSize(800, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void loadEmployeesCB() {
        String urlString = "http://localhost:8080/api/employees";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora u listu dobavljača
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Employees> employees = objectMapper.readValue(response, new TypeReference<List<Employees>>() {
                    });

                    // Čišćenje postojećih stavki u JComboBox-u
                    employee.removeAllItems();

                    // Dodavanje imena dobavljača u JComboBox
                    for (Employees e : employees) {
                        employee.addItem(e.getId() + " - " + e.getName() + " - " + e.getSurname()); // Pretpostavimo da klasa Supplier ima metodu getName()
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                JOptionPane.showMessageDialog(this, "Nevažeći token. Prijavite se ponovo.", "Greška", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri dohvatu podataka: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer addSalary(int employeeId, double amount, LocalDate paymentDate, String month, int year) {
        JSONObject json = new JSONObject();
        json.put("employeeId", employeeId);
        json.put("amount", amount);
        json.put("paymentDate", paymentDate.toString());
        json.put("month", month);
        json.put("year", year);

        // Slanje zahteva na API koristeći HttpURLConnection
        try {
            URL url = new URL("http://localhost:8080/api/salaries");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            connection.setDoOutput(true);

            // Slanje JSON podataka
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Provera odgovora servera
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Čitanje odgovora servera
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    return Integer.parseInt(response); // Pretpostavka da server vraća samo ID kao broj
                }
            } else {
                // Čitanje greške sa servera
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorResponse.append(line);
                }
                reader.close();

                JOptionPane.showMessageDialog(null,
                        "Greška prilikom dodavanja plate. Status: " + responseCode +
                                "\nDetalji: " + errorResponse.toString(),
                        "Greška", JOptionPane.ERROR_MESSAGE);
                return -1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Greška pri unosu podataka u bazu: " + ex.getMessage(),
                    "Greška", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private void addSalaryDetails(int salaryId, double hoursWorked, double overtimeHours,
                                  double bonus, double deductions, String notes) {
        JSONObject json = new JSONObject();
        json.put("salaryId", salaryId);
        json.put("hoursWorked", hoursWorked);
        json.put("overtimeHours", overtimeHours);
        json.put("bonus", bonus);
        json.put("deductions", deductions);
        json.put("notes", notes);

        try {
            URL url = new URL("http://localhost:8080/api/salary-details");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            connection.setDoOutput(true);

            // Slanje JSON podataka
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Provera odgovora servera
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(this,
                        "Podaci o plati uspešno dodati.",
                        "Uspeh", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Čitanje greške sa servera
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorResponse.append(line);
                }
                reader.close();

                JOptionPane.showMessageDialog(this,
                        "Greška prilikom dodavanja podataka o plati. Status: " + responseCode +
                                "\nDetalji: " + errorResponse.toString(),
                        "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Greška pri unosu podataka u bazu: " + ex.getMessage(),
                    "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void addMouseClicked(MouseEvent e) {
        String selectedEmployee = (String) employee.getSelectedItem();
        int employeeId = Integer.parseInt(selectedEmployee.split(" - ")[0]); // Pretpostavimo da je ID prvi deo

        double hoursWorked = Double.parseDouble(nrHours.getText());
        double overtimeHours = Double.parseDouble(nrOvertime.getText());
        double bonus = Double.parseDouble(nrBonus.getText());
        double deductions = Double.parseDouble(nrDeduction.getText());
        String notes = nrNotes.getText();

        // Izračunajte iznos plate (možete dodati logiku za izračunavanje)
        double amount = (hoursWorked * 10) + (overtimeHours * 15) + bonus - deductions; // Primer izračuna

        LocalDate paymentDate = LocalDate.now();
        String month = paymentDate.getMonth().toString();
        int year = paymentDate.getYear();

        // Dodajte platu
        int salaryId = addSalary(employeeId, amount, paymentDate, month, year);

        if (salaryId != -1) {
            // Dodajte detalje plate
            addSalaryDetails(salaryId, hoursWorked, overtimeHours, bonus, deductions, notes);
        }
    }

    private void backMouseClicked(MouseEvent e) {
        dispose();
        EverySalary.start();
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        back = new JButton();
        employee = new JComboBox();
        label1 = new JLabel();
        nrHours = new JTextField();
        label2 = new JLabel();
        nrOvertime = new JTextField();
        label3 = new JLabel();
        nrBonus = new JTextField();
        label4 = new JLabel();
        nrDeduction = new JTextField();
        label5 = new JLabel();
        nrNotes = new JTextField();
        add = new JButton();

        //======== this ========
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0x3d8d7a));
            panel1.setLayout(new MigLayout(
                "fill,hidemode 3",
                // columns
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]",
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

            //---- back ----
            back.setText("Nazad");
            back.setBackground(new Color(0xb3d8a8));
            back.setForeground(Color.darkGray);
            back.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    backMouseClicked(e);
                }
            });
            panel1.add(back, "cell 1 1");

            //---- employee ----
            employee.setBackground(new Color(0xb3d8a8));
            employee.setForeground(Color.darkGray);
            panel1.add(employee, "cell 3 1 4 1,growx");

            //---- label1 ----
            label1.setText("Broj sati:");
            label1.setForeground(new Color(0xfbffe4));
            panel1.add(label1, "cell 2 3 2 1");

            //---- nrHours ----
            nrHours.setBackground(new Color(0xb3d8a8));
            nrHours.setForeground(Color.darkGray);
            panel1.add(nrHours, "cell 4 3 2 1,growx");

            //---- label2 ----
            label2.setText("Broj prekomernih sati:");
            label2.setForeground(new Color(0xfbffe4));
            panel1.add(label2, "cell 2 4 2 1");

            //---- nrOvertime ----
            nrOvertime.setBackground(new Color(0xb3d8a8));
            nrOvertime.setForeground(Color.darkGray);
            panel1.add(nrOvertime, "cell 4 4 2 1,growx");

            //---- label3 ----
            label3.setText("Bonus:");
            label3.setForeground(new Color(0xfbffe4));
            panel1.add(label3, "cell 2 5 2 1");

            //---- nrBonus ----
            nrBonus.setBackground(new Color(0xb3d8a8));
            nrBonus.setForeground(Color.darkGray);
            panel1.add(nrBonus, "cell 4 5 2 1,growx");

            //---- label4 ----
            label4.setText("Odbici:");
            label4.setForeground(new Color(0xfbffe4));
            panel1.add(label4, "cell 2 6 2 1");

            //---- nrDeduction ----
            nrDeduction.setBackground(new Color(0xb3d8a8));
            nrDeduction.setForeground(Color.darkGray);
            panel1.add(nrDeduction, "cell 4 6 2 1,growx");

            //---- label5 ----
            label5.setText("Dodatno:");
            label5.setForeground(new Color(0xfbffe4));
            panel1.add(label5, "cell 2 7 2 1");

            //---- nrNotes ----
            nrNotes.setBackground(new Color(0xb3d8a8));
            nrNotes.setForeground(Color.darkGray);
            panel1.add(nrNotes, "cell 4 7 2 1,growx");

            //---- add ----
            add.setText("Dodaj");
            add.setBackground(new Color(0xb3d8a8));
            add.setForeground(Color.darkGray);
            add.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    addMouseClicked(e);
                }
            });
            panel1.add(add, "cell 6 8");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 793, Short.MAX_VALUE)
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
    private JButton back;
    private JComboBox employee;
    private JLabel label1;
    private JTextField nrHours;
    private JLabel label2;
    private JTextField nrOvertime;
    private JLabel label3;
    private JTextField nrBonus;
    private JLabel label4;
    private JTextField nrDeduction;
    private JLabel label5;
    private JTextField nrNotes;
    private JButton add;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
