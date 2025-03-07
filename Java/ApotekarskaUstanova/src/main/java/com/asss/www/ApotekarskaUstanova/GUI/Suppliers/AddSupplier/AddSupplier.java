/*
 * Created by JFormDesigner on Mon Feb 17 00:25:27 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Suppliers.AddSupplier;

import java.awt.event.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;

import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Municipality;
import com.asss.www.ApotekarskaUstanova.Entity.Town;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierList.SupplierList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.*;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author lniko
 */
public class AddSupplier extends JFrame {
    public AddSupplier() {
        initComponents();
        LoadMunicipalityData();
        if (apt_cb.isSelected()) {
            apt_edit.setVisible(true);
        }
        if (!apt_cb.isSelected()) {
            apt_edit.setVisible(false);
        }
        LoadTownData(getMunicipalityID((String) municipality_combo.getSelectedItem()));

    }

    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        SupplierList.start();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            AddSupplier frame = new AddSupplier();
            frame.setTitle("Dobavljaci");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void DodajMouseClicked(MouseEvent e) {
        String name = name_edit.getText();
        String email = email_edit.getText();
        String phone = telefon_edit.getText();
        String address = address_edit.getText(); // Uneta ulica
        String number = number_edit.getText();   // Broj zgrade
        String aptNumber;

        if (apt_edit.getText().isEmpty()) {
            aptNumber = "0";
        } else {
            aptNumber = apt_edit.getText();   // Broj stana
        }


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

        String jwtToken = JwtResponse.getToken();
        if (jwtToken == null || jwtToken.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Niste ulogovani! Token nije dostupan.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String url = "http://localhost:8080/api/suppliers/add";
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("address", addressId);
        json.put("email", email);
        json.put("phone", phone);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        try {
            // Send POST request with employee data including the image
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Handle the server response
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                JOptionPane.showMessageDialog(this, "Zaposleni uspešno dodat.", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                resetFields();
            } else {
                JOptionPane.showMessageDialog(this, "Greška prilikom dodavanja zaposlenog.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri unosu podataka u bazu.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        // Resetovanje tekstualnih polja
        name_edit.setText("");
        email_edit.setText("");
        telefon_edit.setText("");
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

    private int sendAddressData(int townId, String address, String number, String aptNumber) {
        try {
            URL url = new URL("http://localhost:8080/api/address/add");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            connection.setDoOutput(true);

            // JSON telo zahteva
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

                    System.out.println("Response: " + response);

                    // Parsiranje JSON odgovora (očekujemo ID)
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
        name_label = new JLabel();
        name_edit = new JTextField();
        email_label = new JLabel();
        email_edit = new JTextField();
        address_label = new JLabel();
        address_edit = new JTextField();
        telefon_label = new JLabel();
        telefon_edit = new JTextField();
        number_label = new JLabel();
        number_edit = new JTextField();
        municipality_label = new JLabel();
        municipality_combo = new JComboBox();
        apt_cb = new JCheckBox();
        apt_edit = new JTextField();
        town_label = new JLabel();
        town_combo = new JComboBox();
        Dodaj = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
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
            Nazad.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NazadMouseClicked(e);
                }
            });
            panel1.add(Nazad, "cell 1 1");

            //---- name_label ----
            name_label.setText("Naziv:");
            panel1.add(name_label, "cell 1 3");
            panel1.add(name_edit, "cell 2 3 2 1");

            //---- email_label ----
            email_label.setText("Email:");
            panel1.add(email_label, "cell 1 4");
            panel1.add(email_edit, "cell 2 4 2 1");

            //---- address_label ----
            address_label.setText("Adresa:");
            panel1.add(address_label, "cell 4 4");
            panel1.add(address_edit, "cell 5 4 2 1");

            //---- telefon_label ----
            telefon_label.setText("Telefon:");
            panel1.add(telefon_label, "cell 1 5");
            panel1.add(telefon_edit, "cell 2 5 2 1");

            //---- number_label ----
            number_label.setText("Broj:");
            panel1.add(number_label, "cell 4 5");
            panel1.add(number_edit, "cell 5 5");

            //---- municipality_label ----
            municipality_label.setText("Opstina:");
            panel1.add(municipality_label, "cell 1 6");

            //---- municipality_combo ----
            municipality_combo.addItemListener(e -> municipality_comboItemStateChanged(e));
            panel1.add(municipality_combo, "cell 2 6 2 1");

            //---- apt_cb ----
            apt_cb.setText("Stan");
            apt_cb.addActionListener(e -> apt_cb(e));
            panel1.add(apt_cb, "cell 4 6");
            panel1.add(apt_edit, "cell 5 6");

            //---- town_label ----
            town_label.setText("Grad:");
            panel1.add(town_label, "cell 1 7");
            panel1.add(town_combo, "cell 2 7 2 1");

            //---- Dodaj ----
            Dodaj.setText("Dodaj");
            Dodaj.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    DodajMouseClicked(e);
                }
            });
            panel1.add(Dodaj, "cell 6 9");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 800, GroupLayout.PREFERRED_SIZE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JButton Nazad;
    private JLabel name_label;
    private JTextField name_edit;
    private JLabel email_label;
    private JTextField email_edit;
    private JLabel address_label;
    private JTextField address_edit;
    private JLabel telefon_label;
    private JTextField telefon_edit;
    private JLabel number_label;
    private JTextField number_edit;
    private JLabel municipality_label;
    private JComboBox municipality_combo;
    private JCheckBox apt_cb;
    private JTextField apt_edit;
    private JLabel town_label;
    private JComboBox town_combo;
    private JButton Dodaj;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on


//    public static String getTownName() {
//        return townName;
//    }
//
//    public void setTownName(String townName) {
//        this.townName = townName;
//    }
}
