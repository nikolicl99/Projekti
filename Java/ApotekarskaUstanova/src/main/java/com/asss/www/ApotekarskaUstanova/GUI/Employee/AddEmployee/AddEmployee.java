/*
 * Created by JFormDesigner on Tue Dec 03 20:32:03 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Employee.AddEmployee;

import java.awt.event.*;

import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Employee_Type;
import com.asss.www.ApotekarskaUstanova.Entity.Municipality;
import com.asss.www.ApotekarskaUstanova.Entity.Town;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList.EmployeeList;
import com.asss.www.ApotekarskaUstanova.Util.PasswordUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.*;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.http.HttpHeaders;  // Spring HttpHeaders

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author lniko
 */
public class AddEmployee extends JFrame {
    public AddEmployee() {
        initComponents();
        popunjavanjeCB();
        LoadMunicipalityData();
        if (apt_cb.isSelected()) {
            apt_edit.setVisible(true);
        }
        if (!apt_cb.isSelected()) {
            apt_edit.setVisible(false);
        }
        LoadTownData(getMunicipalityID((String) municipality_combo.getSelectedItem()));
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            AddEmployee frame = new AddEmployee();
            frame.setTitle("Dodavanje Zaposlenog");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void DodajMouseClicked(MouseEvent e) {
        // Prikupljanje podataka iz GUI-ja
        String ime = ime_edit.getText().trim();
        String prezime = prezime_edit.getText().trim();
        String email = email_edit.getText().trim();
        String sifra = sifra_edit.getText().trim();
        String telefon = telefon_edit.getText().trim();
        String nazivTipa = (String) tip_combo.getSelectedItem();
        String address = address_edit.getText(); // Ulica
        String number = number_edit.getText();   // Broj zgrade
        String aptNumber = apt_edit.getText().isEmpty() ? "0" : apt_edit.getText(); // Broj stana

        int idTipa = dobijanjeIDTipa(nazivTipa);
        String hashSifra = PasswordUtil.hashPassword(sifra);

        // Dobijanje ID-a grada
        String selectedTown = (String) town_combo.getSelectedItem();
        int townId = getTownID(selectedTown.split(" \\| ")[0]); // Razdvajanje imena i poštanskog broja

        if (townId == -1) {
            JOptionPane.showMessageDialog(null, "Grad nije pronađen!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Slanje podataka o adresi i dobijanje ID-a
        int addressId = sendAddressData(townId, address, number, aptNumber);
        if (addressId == -1) {
            JOptionPane.showMessageDialog(null, "Adresa nije sačuvana!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Adresa sačuvana sa ID: " + addressId);

        // Provera da li su polja popunjena
        if (ime.isEmpty() || prezime.isEmpty() || email.isEmpty() || sifra.isEmpty() || telefon.isEmpty() || nazivTipa == null || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sva polja moraju biti popunjena.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Unesite ispravnu email adresu.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Potvrda dodavanja zaposlenog
        int responseUser = JOptionPane.showConfirmDialog(this, "Da li ste sigurni da želite da dodate zaposlenog?", "Potvrda", JOptionPane.YES_NO_OPTION);
        if (responseUser != JOptionPane.YES_OPTION) return;

        String jwtToken = JwtResponse.getToken();
        if (jwtToken == null || jwtToken.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Niste ulogovani! Token nije dostupan.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Priprema JSON objekta sa ispravnom strukturom za backend
        JSONObject json = new JSONObject();
        json.put("name", ime);
        json.put("surname", prezime);
        json.put("email", email);
        json.put("password", hashSifra);
        json.put("mobile", telefon);

        // Kreiranje employeeType objekta
        JSONObject employeeTypeJson = new JSONObject();
        employeeTypeJson.put("id", idTipa);
        json.put("employeeType", employeeTypeJson);

        // Kreiranje address objekta
        JSONObject addressJson = new JSONObject();
        addressJson.put("id", addressId);
        json.put("address", addressJson);

        // Slanje zahteva na API koristeći HttpURLConnection
        try {
            URL url = new URL("http://localhost:8080/api/employees/add");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
            connection.setDoOutput(true);

            // Slanje JSON podataka
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Provera odgovora servera
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(this, "Zaposleni uspešno dodat.", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                resetFields();
            } else {
                // Čitanje odgovora za detaljniju grešku
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        responseCode == HttpURLConnection.HTTP_OK ?
                                connection.getInputStream() : connection.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JOptionPane.showMessageDialog(this, "Greška prilikom dodavanja zaposlenog. Status: "
                                + responseCode + "\nDetalji: " + response.toString(),
                        "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri unosu podataka u bazu: " + ex.getMessage(),
                    "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metoda za validaciju email adrese
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    // Metoda za resetovanje polja
    private void resetFields() {
        ime_edit.setText("");
        prezime_edit.setText("");
        email_edit.setText("");
        sifra_edit.setText("");
        telefon_edit.setText("");
        tip_combo.setSelectedIndex(0);
//        slika.setIcon(null); // Clear image preview
        address_edit.setText("");
        number_edit.setText("");
        apt_edit.setText("");

        // Resetovanje ComboBox-a - Opcija 1: Postavljanje na prvu poziciju (ako je prazno ili defaultna opcija)
        LoadMunicipalityData();



        // Resetovanje CheckBox-a
        apt_cb.setSelected(false);  // Ako imaš CheckBox, poništava izbor

        if (apt_cb.isSelected()) {
            apt_edit.setVisible(true);
        }
        if (!apt_cb.isSelected()) {
            apt_edit.setVisible(false);
        }
    }

    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        EmployeeList.start();
    }

    private int sendAddressData(int townId, String address, String number, String aptNumber) {
        try {
            URL url = new URL("http://localhost:8080/api/address/add");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            connection.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"town\": {\"id\": %d}, \"address\": \"%s\", \"number\": \"%s\", \"aptNumber\": \"%s\"}",
                    townId, address, number, aptNumber
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    JSONObject jsonResponse = new JSONObject(response);
                    return jsonResponse.getInt("id");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Greška pri dodavanju adrese!", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Greška pri komunikaciji sa serverom.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
        return -1; // Ako dođe do greške, vraća -1
    }

    private void LoadMunicipalityData() {
        try {
            URL url = new URL("http://localhost:8080/api/municipality");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Municipality> municipalities = objectMapper.readValue(response, new TypeReference<List<Municipality>>() {});
                    // Dodavanje u ComboBox
                    municipality_combo.removeAllItems();
                    for (Municipality municipality : municipalities) {
                        municipality_combo.addItem(municipality.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri popunjavanju ComboBox-a.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getMunicipalityID(String municipalityName) {
        try {
            String encodedMunicipalityName = URLEncoder.encode(municipalityName, StandardCharsets.UTF_8);
            // URL za API poziv prema opštini
//            System.out.println("Opstina pre promene: " + municipalityName);
            encodedMunicipalityName = encodedMunicipalityName.replace("+", "%20");
//            System.out.println("Opstina nakon promene: " + municipalityName);
            System.out.println("Encoded naziv opstine: " + encodedMunicipalityName);
            URL url = new URL("http://localhost:8080/api/municipality/name/" + encodedMunicipalityName);
//            System.out.println("URL poziv: " + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora
                    JSONObject jsonResponse = new JSONObject(response);
                    return jsonResponse.getInt("id");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Opština sa datim nazivom nije pronađena.", "Greška", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri komunikaciji sa serverom.", "Greška", JOptionPane.ERROR_MESSAGE);
        }

        return -1; // Ako dođe do greške, vraćamo -1
    }

    private void LoadTownData(int municipalityId) {
        try {
            URL url = new URL("http://localhost:8080/api/town/municipality/" + municipalityId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Town> town_List = objectMapper.readValue(response, new TypeReference<List<Town>>() {});

                    // Dodavanje u ComboBox
                    town_combo.removeAllItems();
                    for (Town town : town_List) {
                        town_combo.addItem(town.getName() + " | " + town.getPostalCode());
//                        setTownName(town.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri popunjavanju ComboBox-a.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getTownID(String townName) {
        try {
            // URL za API poziv prema opštini
            String encodedTownName = URLEncoder.encode(townName, StandardCharsets.UTF_8);
            encodedTownName = encodedTownName.replace("+", "%20");
            URL url = new URL("http://localhost:8080/api/town/name/" + encodedTownName);
            System.out.println("URL za trazenje id-ja grada: " + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora
                    JSONObject jsonResponse = new JSONObject(response);
                    return jsonResponse.getInt("id");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Grad sa datim nazivom nije pronađen.", "Greška", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri komunikaciji sa serverom.", "Greška", JOptionPane.ERROR_MESSAGE);
        }

        return -1; // Ako dođe do greške, vraćamo -1
    }

    private void apt_cb(ActionEvent e) {
        if (apt_cb.isSelected()) {
            apt_edit.setVisible(true);
        }
        if (!apt_cb.isSelected()) {
            apt_edit.setVisible(false);
        }
    }

    private void municipality_comboItemStateChanged(ItemEvent e) {
        try {
            // Pauza od 1 sekunde (1000 milisekundi)
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        String selectedMunicipality = (String) municipality_combo.getSelectedItem();
        if (selectedMunicipality != null && !selectedMunicipality.isEmpty()) {
            LoadTownData(getMunicipalityID(selectedMunicipality));
        } else {
            System.out.println("No municipality selected or invalid selection.");
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        Nazad = new JButton();
        ime_label = new JLabel();
        ime_edit = new JTextField();
        municipality_label = new JLabel();
        municipality_combo = new JComboBox();
        prezime_label = new JLabel();
        prezime_edit = new JTextField();
        town_label = new JLabel();
        town_combo = new JComboBox();
        email_label = new JLabel();
        email_edit = new JTextField();
        address_label = new JLabel();
        address_edit = new JTextField();
        sifra_label = new JLabel();
        sifra_edit = new JPasswordField();
        number_label = new JLabel();
        number_edit = new JTextField();
        telefon_label = new JLabel();
        telefon_edit = new JTextField();
        apt_cb = new JCheckBox();
        apt_edit = new JTextField();
        tip_label = new JLabel();
        tip_combo = new JComboBox();
        Dodaj = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0x3d8d7a));
            panel1.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
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
            Nazad.setBackground(new Color(0xb3d8a8));
            Nazad.setForeground(Color.darkGray);
            Nazad.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NazadMouseClicked(e);
                }
            });
            panel1.add(Nazad, "cell 1 1");

            //---- ime_label ----
            ime_label.setText("Ime:");
            ime_label.setForeground(new Color(0xfbffe4));
            panel1.add(ime_label, "cell 1 2");

            //---- ime_edit ----
            ime_edit.setBackground(new Color(0xb3d8a8));
            ime_edit.setForeground(Color.darkGray);
            panel1.add(ime_edit, "cell 2 2 2 1");

            //---- municipality_label ----
            municipality_label.setText("Opstina:");
            municipality_label.setForeground(new Color(0xfbffe4));
            panel1.add(municipality_label, "cell 4 2");

            //---- municipality_combo ----
            municipality_combo.setBackground(new Color(0xb3d8a8));
            municipality_combo.setForeground(Color.darkGray);
            municipality_combo.addItemListener(e -> municipality_comboItemStateChanged(e));
            panel1.add(municipality_combo, "cell 5 2 2 1");

            //---- prezime_label ----
            prezime_label.setText("Prezime:");
            prezime_label.setForeground(new Color(0xfbffe4));
            panel1.add(prezime_label, "cell 1 3");

            //---- prezime_edit ----
            prezime_edit.setBackground(new Color(0xb3d8a8));
            prezime_edit.setForeground(Color.darkGray);
            panel1.add(prezime_edit, "cell 2 3 2 1");

            //---- town_label ----
            town_label.setText("Grad:");
            town_label.setForeground(new Color(0xfbffe4));
            panel1.add(town_label, "cell 4 3");

            //---- town_combo ----
            town_combo.setBackground(new Color(0xb3d8a8));
            town_combo.setForeground(Color.darkGray);
            panel1.add(town_combo, "cell 5 3 2 1");

            //---- email_label ----
            email_label.setText("Email:");
            email_label.setForeground(new Color(0xfbffe4));
            panel1.add(email_label, "cell 1 4");

            //---- email_edit ----
            email_edit.setBackground(new Color(0xb3d8a8));
            email_edit.setForeground(Color.darkGray);
            panel1.add(email_edit, "cell 2 4 2 1");

            //---- address_label ----
            address_label.setText("Adresa:");
            address_label.setForeground(new Color(0xfbffe4));
            panel1.add(address_label, "cell 4 4");

            //---- address_edit ----
            address_edit.setBackground(new Color(0xb3d8a8));
            address_edit.setForeground(Color.darkGray);
            panel1.add(address_edit, "cell 5 4 2 1");

            //---- sifra_label ----
            sifra_label.setText("Lozinka:");
            sifra_label.setForeground(new Color(0xfbffe4));
            panel1.add(sifra_label, "cell 1 5");

            //---- sifra_edit ----
            sifra_edit.setBackground(new Color(0xb3d8a8));
            sifra_edit.setForeground(Color.darkGray);
            panel1.add(sifra_edit, "cell 2 5 2 1");

            //---- number_label ----
            number_label.setText("Broj:");
            number_label.setForeground(new Color(0xfbffe4));
            panel1.add(number_label, "cell 4 5");

            //---- number_edit ----
            number_edit.setBackground(new Color(0xb3d8a8));
            number_edit.setForeground(Color.darkGray);
            panel1.add(number_edit, "cell 5 5");

            //---- telefon_label ----
            telefon_label.setText("Telefon:");
            telefon_label.setForeground(new Color(0xfbffe4));
            panel1.add(telefon_label, "cell 1 6");

            //---- telefon_edit ----
            telefon_edit.setBackground(new Color(0xb3d8a8));
            telefon_edit.setForeground(Color.darkGray);
            panel1.add(telefon_edit, "cell 2 6 2 1");

            //---- apt_cb ----
            apt_cb.setText("Stan:");
            apt_cb.setForeground(new Color(0xfbffe4));
            apt_cb.setBackground(new Color(0x3d8d7a));
            apt_cb.addActionListener(e -> apt_cb(e));
            panel1.add(apt_cb, "cell 4 6");

            //---- apt_edit ----
            apt_edit.setBackground(new Color(0xb3d8a8));
            apt_edit.setForeground(Color.darkGray);
            panel1.add(apt_edit, "cell 5 6");

            //---- tip_label ----
            tip_label.setText("Tip Zaposlenog:");
            tip_label.setForeground(new Color(0xfbffe4));
            panel1.add(tip_label, "cell 1 7");

            //---- tip_combo ----
            tip_combo.setBackground(new Color(0xb3d8a8));
            tip_combo.setForeground(Color.darkGray);
            panel1.add(tip_combo, "cell 2 7 2 1");

            //---- Dodaj ----
            Dodaj.setText("Dodaj");
            Dodaj.setBackground(new Color(0xb3d8a8));
            Dodaj.setForeground(Color.darkGray);
            Dodaj.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    DodajMouseClicked(e);
                }
            });
            panel1.add(Dodaj, "cell 2 9");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    private void popunjavanjeCB() {
        try {
            URL url = new URL("http://localhost:8080/api/employee-type");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Employee_Type> types = objectMapper.readValue(response, new TypeReference<List<Employee_Type>>() {});

                    // Dodavanje u ComboBox
                    tip_combo.removeAllItems();
                    for (Employee_Type type : types) {
                        tip_combo.addItem(type.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri popunjavanju ComboBox-a.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int dobijanjeIDTipa(String naziv_Tipa) {
        System.out.println("naziv tipa: " + naziv_Tipa);
        try {
            // URL za API poziv
            URL url = new URL("http://localhost:8080/api/employee-type/name/" + URLEncoder.encode(naziv_Tipa, StandardCharsets.UTF_8));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Dodavanje Authorization header-a sa JWT tokenom
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            // Provera odgovora API-ja
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora (pretpostavljamo da API vraća samo ID)
                    JSONObject jsonResponse = new JSONObject(response);
                    return jsonResponse.getInt("id");
                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                JOptionPane.showMessageDialog(this,
                        "Tip zaposlenog sa datim nazivom ne postoji.",
                        "Obaveštenje", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Greška: API je vratio status " + responseCode,
                        "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Greška pri komunikaciji sa serverom.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
        }

        return -1; // Vraćamo -1 ako dođe do greške
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JButton Nazad;
    private JLabel ime_label;
    private JTextField ime_edit;
    private JLabel municipality_label;
    private JComboBox municipality_combo;
    private JLabel prezime_label;
    private JTextField prezime_edit;
    private JLabel town_label;
    private JComboBox town_combo;
    private JLabel email_label;
    private JTextField email_edit;
    private JLabel address_label;
    private JTextField address_edit;
    private JLabel sifra_label;
    private JPasswordField sifra_edit;
    private JLabel number_label;
    private JTextField number_edit;
    private JLabel telefon_label;
    private JTextField telefon_edit;
    private JCheckBox apt_cb;
    private JTextField apt_edit;
    private JLabel tip_label;
    private JComboBox tip_combo;
    private JButton Dodaj;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private File selectedImage;
}