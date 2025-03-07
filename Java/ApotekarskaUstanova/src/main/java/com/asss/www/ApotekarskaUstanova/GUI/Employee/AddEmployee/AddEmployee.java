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
        setupImageUploadButton();
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

    private void setupImageUploadButton() {
        dodaj_sliku_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open file chooser to select image
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select an Image");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedImage = fileChooser.getSelectedFile();
                    // Učitavanje slike u ImageIcon
                    ImageIcon icon = new ImageIcon(selectedImage.getPath());
                    // Uzmi sliku iz ImageIcon
                    Image image = icon.getImage();
                    // Skaliraj sliku da odgovara dimenzijama JLabel-a
                    Image scaledImage = image.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    // Postavi skaliranu sliku kao ikonu
                    slika.setIcon(new ImageIcon(scaledImage));  // Set the image as preview in the label
                    System.out.println("width: " + slika.getWidth());
                    System.out.println("height: " + slika.getHeight());
                }

            }
        });
    }

    private String convertImageToBase64(File selectedImage) {
        String imageBase64 = null;
        if (selectedImage != null) {
            try (InputStream inputStream = new FileInputStream(selectedImage)) {
                // Provera veličine slike (opciono)
                if (selectedImage.length() > 10 * 1024 * 1024) { // Na primer, 10MB limit
                    JOptionPane.showMessageDialog(this, "Slika je prevelika.", "Greška", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                byte[] imageBytes = inputStream.readAllBytes();
                imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Greška pri učitavanju slike.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
        return imageBase64;
    }

    // Dodajemo metodu za skaliranje slike u memoriji
    private BufferedImage scaleImageToSize(Image originalImage, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    // Dodajemo metodu za konvertovanje BufferedImage u Base64
    private String convertBufferedImageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri konvertovanju slike u Base64.", "Greška", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void DodajMouseClicked(MouseEvent e) {
        // Gather data from the form fields
        String ime = ime_edit.getText().trim();
        String prezime = prezime_edit.getText().trim();
        String email = email_edit.getText().trim();
        String sifra = sifra_edit.getText().trim();
        String telefon = telefon_edit.getText().trim();
        String nazivTipa = (String) tip_combo.getSelectedItem();
        String address = address_edit.getText(); // Uneta ulica
        String number = number_edit.getText();   // Broj zgrade
        String aptNumber;

        if (apt_edit.getText().isEmpty()) {
            aptNumber = "0";
        } else {
            aptNumber = apt_edit.getText();   // Broj stana
        }

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

        // Validate form inputs
        if (ime.isEmpty() || prezime.isEmpty() || email.isEmpty() || sifra.isEmpty() || telefon.isEmpty() || nazivTipa == null) {
            JOptionPane.showMessageDialog(this, "Sva polja moraju biti popunjena.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Unesite ispravnu email adresu.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Konvertujte originalnu sliku u Base64
        String originalImageBase64 = convertImageToBase64(selectedImage);
        if (originalImageBase64 == null && selectedImage != null) {
            JOptionPane.showMessageDialog(this, "Greška prilikom dodavanja slike.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Skalirajte originalnu sliku na 400x300 i konvertujte je u Base64
        String scaledImageBase64 = null;
        if (selectedImage != null) {
            Image originalImage = new ImageIcon(selectedImage.getPath()).getImage();
            BufferedImage scaledImage = scaleImageToSize(originalImage, 400, 300);
            scaledImageBase64 = convertBufferedImageToBase64(scaledImage);
        }

        // Proceed with confirmation and API call
        int response = JOptionPane.showConfirmDialog(this, "Da li ste sigurni da želite da dodate zaposlenog sa unetim podacima?", "Potvrda", JOptionPane.YES_NO_OPTION);
        if (response != JOptionPane.YES_OPTION) return;

        String jwtToken = JwtResponse.getToken();
        if (jwtToken == null || jwtToken.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Niste ulogovani! Token nije dostupan.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prepare the API URL and employee data in JSON format
        String apiUrl = "http://localhost:8080/api/employees/add";
        JSONObject json = new JSONObject();
        json.put("name", ime);
        json.put("surname", prezime);
        json.put("email", email);
        json.put("password", sifra);
        json.put("mobile", telefon);
        json.put("type", nazivTipa);
        json.put("address", addressId);
        if (originalImageBase64 != null) {
            json.put("profileImage", originalImageBase64);  // Dodajte originalnu sliku
            System.out.println("original image: " + originalImageBase64);
        }
        if (scaledImageBase64 != null) {
            json.put("scaledProfileImage", scaledImageBase64);
            System.out.println("scaled image: " + scaledImageBase64);// Dodajte skaliranu sliku
        }

        // Set up HTTP headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        try {
            // Send POST request with employee data including the image
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

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

    // Metoda za validaciju email adrese
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            // Pretvaranje bajtova u heksadecimalni string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metoda za resetovanje polja
    private void resetFields() {
        ime_edit.setText("");
        prezime_edit.setText("");
        email_edit.setText("");
        sifra_edit.setText("");
        telefon_edit.setText("");
        tip_combo.setSelectedIndex(0);
        slika.setIcon(null); // Clear image preview
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
        slika = new JLabel();
        ime_label = new JLabel();
        ime_edit = new JTextField();
        prezime_label = new JLabel();
        prezime_edit = new JTextField();
        email_label = new JLabel();
        email_edit = new JTextField();
        dodaj_sliku_button = new JButton();
        sifra_label = new JLabel();
        sifra_edit = new JPasswordField();
        municipality_label = new JLabel();
        municipality_combo = new JComboBox();
        telefon_label = new JLabel();
        telefon_edit = new JTextField();
        town_label = new JLabel();
        town_combo = new JComboBox();
        tip_label = new JLabel();
        tip_combo = new JComboBox();
        address_label = new JLabel();
        address_edit = new JTextField();
        number_label = new JLabel();
        number_edit = new JTextField();
        Dodaj = new JButton();
        apt_cb = new JCheckBox();
        apt_edit = new JTextField();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        var contentPane = getContentPane();
        contentPane.setLayout(null);

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
            panel1.add(Nazad, "cell 0 0");
            panel1.add(slika, "cell 5 1 2 3");

            //---- ime_label ----
            ime_label.setText("Ime:");
            panel1.add(ime_label, "cell 1 2");
            panel1.add(ime_edit, "cell 2 2 2 1");

            //---- prezime_label ----
            prezime_label.setText("Prezime:");
            panel1.add(prezime_label, "cell 1 3");
            panel1.add(prezime_edit, "cell 2 3 2 1");

            //---- email_label ----
            email_label.setText("Email:");
            panel1.add(email_label, "cell 1 4");
            panel1.add(email_edit, "cell 2 4 2 1");

            //---- dodaj_sliku_button ----
            dodaj_sliku_button.setText("Dodaj Sliku");
            panel1.add(dodaj_sliku_button, "cell 5 4");

            //---- sifra_label ----
            sifra_label.setText("Lozinka:");
            panel1.add(sifra_label, "cell 1 5");
            panel1.add(sifra_edit, "cell 2 5 2 1");

            //---- municipality_label ----
            municipality_label.setText("Opstina:");
            panel1.add(municipality_label, "cell 4 5");

            //---- municipality_combo ----
            municipality_combo.addItemListener(e -> municipality_comboItemStateChanged(e));
            panel1.add(municipality_combo, "cell 5 5 2 1");

            //---- telefon_label ----
            telefon_label.setText("Telefon:");
            panel1.add(telefon_label, "cell 1 6");
            panel1.add(telefon_edit, "cell 2 6 2 1");

            //---- town_label ----
            town_label.setText("Grad:");
            panel1.add(town_label, "cell 4 6");
            panel1.add(town_combo, "cell 5 6 2 1");

            //---- tip_label ----
            tip_label.setText("Tip Zaposlenog:");
            panel1.add(tip_label, "cell 1 7");
            panel1.add(tip_combo, "cell 2 7 2 1");

            //---- address_label ----
            address_label.setText("Adresa:");
            panel1.add(address_label, "cell 4 7");
            panel1.add(address_edit, "cell 5 7 2 1");

            //---- number_label ----
            number_label.setText("Broj:");
            panel1.add(number_label, "cell 4 8");
            panel1.add(number_edit, "cell 5 8");

            //---- Dodaj ----
            Dodaj.setText("Dodaj");
            Dodaj.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    DodajMouseClicked(e);
                }
            });
            panel1.add(Dodaj, "cell 2 9");

            //---- apt_cb ----
            apt_cb.setText("Stan:");
            apt_cb.addActionListener(e -> apt_cb(e));
            panel1.add(apt_cb, "cell 4 9");
            panel1.add(apt_edit, "cell 5 9");
        }
        contentPane.add(panel1);
        panel1.setBounds(0, 0, 800, 470);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
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
    private JLabel slika;
    private JLabel ime_label;
    private JTextField ime_edit;
    private JLabel prezime_label;
    private JTextField prezime_edit;
    private JLabel email_label;
    private JTextField email_edit;
    private JButton dodaj_sliku_button;
    private JLabel sifra_label;
    private JPasswordField sifra_edit;
    private JLabel municipality_label;
    private JComboBox municipality_combo;
    private JLabel telefon_label;
    private JTextField telefon_edit;
    private JLabel town_label;
    private JComboBox town_combo;
    private JLabel tip_label;
    private JComboBox tip_combo;
    private JLabel address_label;
    private JTextField address_edit;
    private JLabel number_label;
    private JTextField number_edit;
    private JButton Dodaj;
    private JCheckBox apt_cb;
    private JTextField apt_edit;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private File selectedImage;
}