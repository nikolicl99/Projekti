/*
 * Created by JFormDesigner on Mon Feb 24 05:19:20 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Suppliers.NewShipment;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.Repository.ProductBatchRepository;
import com.asss.www.ApotekarskaUstanova.Dto.ProductBatchDto;
import com.asss.www.ApotekarskaUstanova.Dto.ProductDto;
import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Dto.SupplierDto;
import com.asss.www.ApotekarskaUstanova.Entity.Location;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin.MainMenuAdmin;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Service.ProductBatchService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.lgooddatepicker.components.*;
import net.miginfocom.swing.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author lniko
 */
public class NewShipment extends JFrame {
    public NewShipment() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule
        initComponents();
        loadSupplierCB();
        loadLocationCB();
        setupListeners();

        calendarDesign(datePicker);
        datePicker.setDate(LocalDate.now());

        String[] columnNames = {"Naziv proizvoda", "Količina", "Rok Trajanja", "Lokacija"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        items.setModel(model);
        customizeTable(items, model);
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            NewShipment frame = new NewShipment();
            frame.setTitle("Inventory");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    public void customizeTable(JTable table, TableModel model) {
        // Set background color for the table header
        JTableHeader header = table.getTableHeader();
        Color headerBackgroundColor = new Color(0xb3, 0xd8, 0xa8); // Hex code #b3d8a8
        header.setBackground(headerBackgroundColor);

        // Optional: Set foreground (text) color for the header
        Color headerForegroundColor = Color.DARK_GRAY; // Example: Dark gray text
        header.setForeground(headerForegroundColor);

        // Set font for the header
        Font headerFont = new Font("Inter", Font.BOLD, 13);
        header.setFont(headerFont);

        // Set the model for the table
        table.setModel(model);

        // Set the background color for the viewport and scroll pane
        Color backgroundColor = new Color(0xfb, 0xff, 0xe4); // Hex code #fbffe4
        JViewport viewport = scrollPane1.getViewport();
        viewport.setBackground(backgroundColor);
        scrollPane1.setBackground(backgroundColor);
    }

    private static void calendarDesign(DatePicker datePicker) {
        // Create a new instance of DatePickerSettings
        DatePickerSettings settings = new DatePickerSettings();

        settings.setFirstDayOfWeek(DayOfWeek.MONDAY);

        // Customize the text field
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate, new Color(0xfb, 0xff, 0xe4)); // Background color
        settings.setColor(DatePickerSettings.DateArea.DatePickerTextValidDate, Color.DARK_GRAY); // Text color

        // Customize the calendar popup
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates, new Color(0xfb, 0xff, 0xe4)); // Background color
        settings.setColor(DatePickerSettings.DateArea.CalendarTextNormalDates, Color.DARK_GRAY); // Text color
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundSelectedDate, new Color(0xfb, 0xff, 0xe4)); // Background color
        settings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, new Color(0xfb, 0xff, 0xe4)); // Text color
        settings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels, new Color(0xfb, 0xff, 0xe4)); //Pozadina gornjeg teksta
        settings.setColor(DatePickerSettings.DateArea.BackgroundTodayLabel, new Color(0xfb, 0xff, 0xe4));
        settings.setColor(DatePickerSettings.DateArea.BackgroundClearLabel, new Color(0xfb, 0xff, 0xe4));

        // Apply the settings to the DatePicker
        datePicker.setSettings(settings);
    }

    private void loadSupplierCB() {
        String urlString = "http://localhost:8080/api/suppliers";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora u listu SupplierDto objekata
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<SupplierDto> suppliers = objectMapper.readValue(response, new TypeReference<List<SupplierDto>>() {
                    });

                    // Čišćenje postojećih stavki u JComboBox-u
                    supplier.removeAllItems();

                    // Dodavanje imena dobavljača u JComboBox
                    for (SupplierDto s : suppliers) {
                        supplier.addItem(s.getName() + " - " + s.getPhone()); // Pretpostavimo da klasa SupplierDto ima metodu getName()
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

    private void loadLocationCB() {
        String urlString = "http://localhost:8080/api/locations";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora u listu lokacija
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Location> locations = objectMapper.readValue(response, new TypeReference<List<Location>>() {
                    });

                    // Čišćenje postojećih stavki u JComboBox-u
                    location.removeAllItems();

                    // Dodavanje kombinacije polja u JComboBox
                    for (Location l : locations) {
                        String comboBoxItem = String.format("%s - %s - %s - %s", l.getSection(), l.getShelf(), l.getRow(), l.getDescription());
                        location.addItem(comboBoxItem);
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

    private void addMouseClicked(MouseEvent e) {
        // Dohvatanje podataka iz polja
        String selectedProduct = product.getText().trim();
        String selectedQuantity = quantity.getText().trim();
        LocalDate selectedDate = datePicker.getDate();
        String selectedLocation = (String) location.getSelectedItem();

        // Validacija podataka
        if (selectedProduct.isEmpty() || selectedQuantity.isEmpty() || selectedDate == null || selectedLocation == null) {
            JOptionPane.showMessageDialog(this, "Sva polja su obavezna!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Provera da li je količina validan broj
        int quantityValue;
        try {
            quantityValue = Integer.parseInt(selectedQuantity);
            if (quantityValue <= 0) {
                JOptionPane.showMessageDialog(this, "Količina mora biti pozitivan broj!", "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Količina mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Provera da li je datum nakon trenutnog datuma
        if (selectedDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Datum mora biti nakon trenutnog datuma!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Formatiranje datuma
        String formattedDate = selectedDate.toString();

        // Provera da li postoji isti proizvod sa istim rokom trajanja na drugoj lokaciji
        DefaultTableModel model = (DefaultTableModel) items.getModel();
        boolean sameProductDifferentLocation = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            String productInTable = (String) model.getValueAt(i, 0);
            String dateInTable = (String) model.getValueAt(i, 2);
            String locationInTable = (String) model.getValueAt(i, 3);

            if (selectedProduct.equals(productInTable) && formattedDate.equals(dateInTable)) {
                if (!selectedLocation.equals(locationInTable)) {
                    sameProductDifferentLocation = true;
                    break;
                }
            }
        }

        // Ako postoji isti proizvod na drugoj lokaciji, prikaži dijalog
        if (sameProductDifferentLocation) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Postoji isti proizvod sa istim rokom trajanja na drugoj lokaciji. Da li želite da nastavite sa unosom?",
                    "Upozorenje",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (option == JOptionPane.NO_OPTION) {
                return; // Vrati se na formu bez dodavanja
            }
        }

        // Provera da li postoji isti proizvod, rok trajanja i lokacija u tabeli
        boolean sameProductSameLocation = false;
        int rowIndex = -1;

        for (int i = 0; i < model.getRowCount(); i++) {
            String productInTable = (String) model.getValueAt(i, 0);
            String dateInTable = (String) model.getValueAt(i, 2);
            String locationInTable = (String) model.getValueAt(i, 3);

            if (selectedProduct.equals(productInTable) && formattedDate.equals(dateInTable) && selectedLocation.equals(locationInTable)) {
                sameProductSameLocation = true;
                rowIndex = i;
                break;
            }
        }

        // Ako postoji isti proizvod, rok trajanja i lokacija, ažuriraj količinu
        if (sameProductSameLocation) {
            int existingQuantity = Integer.parseInt((String) model.getValueAt(rowIndex, 1));
            int newQuantity = existingQuantity + quantityValue;
            model.setValueAt(String.valueOf(newQuantity), rowIndex, 1);
        } else {
            // Dodavanje novog reda u tabelu
            model.addRow(new Object[]{selectedProduct, selectedQuantity, formattedDate, selectedLocation});
            customizeTable(items, model);
        }

        // Čišćenje polja nakon dodavanja
        product.setText("");
        quantity.setText("");
        datePicker.setDate(LocalDate.now());
        location.setSelectedIndex(0);
    }

    private void setupListeners() {
        // Dodaj MouseListener za articlePaper
        product.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                product.requestFocus(); // Postavi fokus na articlePaper
                updateDropdown(); // Ažuriraj dropdown kad dobije fokus
            }
        });

        // Dodaj DocumentListener za articlePaper
        product.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDropdown();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateDropdown();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateDropdown();
            }
        });
    }

    private void updateDropdown() {
        String text = product.getText().trim();

        // Ako je polje prazno, odmah sakrij popup
        if (text.isEmpty()) {
            popupMenu.setVisible(false);
            return;
        }

        productPropertyChange();
    }

    private void productPropertyChange() {
        System.out.println("obrisano sve, krece trazenje proizvoda");
        String input = product.getText().trim();

        if (input.isEmpty()) {
            popupMenu.setVisible(false);
            return;
        }

        popupMenu.removeAll();
        System.out.println("opet obrisano sve");

        try {
            URL url = new URL("http://localhost:8080/api/products/search?query=" + URLEncoder.encode(input, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String response = reader.lines().collect(Collectors.joining());

                    ObjectMapper objectMapper = new ObjectMapper();
                    List<ProductDto> products = objectMapper.readValue(response, new TypeReference<List<ProductDto>>() {
                    });


                    if (products.isEmpty()) {
                        popupMenu.setVisible(false);
                    } else {
                        showDropdown(products);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showDropdown(List<ProductDto> products) {
        popupMenu.removeAll();

        for (ProductDto pb : products) {
            JMenuItem item = new JMenuItem(pb.getName() + " - " + pb.getDosage());
            item.addActionListener(e -> {
                product.setText(pb.getName());
//                updateDosageDropdown(pb.getProduct().getName()); // Pozovi metodu kada se odabere proizvod
            });
            popupMenu.add(item);
        }

        if (!products.isEmpty()) {
            popupMenu.show(product, 0, product.getHeight());
            product.requestFocus();
        } else {
            popupMenu.setVisible(false);
        }

        int itemHeight = 25;
        int maxHeight = 200;
        int newHeight = Math.min(products.size() * itemHeight, maxHeight);

        popupMenu.setPopupSize(new Dimension(200, newHeight));
        popupMenu.revalidate();
        popupMenu.repaint();
    }

    private void finnishMouseClicked(MouseEvent e) {
        fillList();
        String selectedSupplier = supplier.getSelectedItem().toString(); // "Ime - Telefon"
        String supplierName = selectedSupplier.split(" - ")[0]; // "Ime"
        int supplierId = getSupplierId(supplierName);

        if (supplierId <= 0) {
            JOptionPane.showMessageDialog(null, "Nevažeći dobavljač!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        int shipmentId = addShipment(supplierId, currentDate, formattedTime);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        if (shipmentId != -1) {
            addShipmentItems(shipmentId);
        }
        DefaultTableModel model = (DefaultTableModel) items.getModel();
        model.setRowCount(0);
    }

    private void fillList() {
        itemsList.clear();

        DefaultTableModel model = (DefaultTableModel) items.getModel();
        int rowCount = model.getRowCount();

        // Iteracija kroz redove i dodavanje u listu
        for (int i = 0; i < rowCount; i++) {
            Map<String, Object> stavka = new HashMap<>();
            stavka.put("Naziv proizvoda", model.getValueAt(i, 0));
            stavka.put("Kolicina", model.getValueAt(i, 1));
            stavka.put("Rok trajanja", model.getValueAt(i, 2));
            stavka.put("Lokacija", model.getValueAt(i, 3));

            itemsList.add(stavka);
        }
    }

    private void addShipmentItems(int shipmentId) {
        for (Map<String, Object> item : itemsList) {
            try {
                String productName = (String) item.get("Naziv proizvoda");
                int quantity = Integer.parseInt(item.get("Kolicina").toString());
                LocalDate expirationDate = LocalDate.parse(item.get("Rok trajanja").toString());
                int locationId = getLocationIdFromItem(item);

                int productId = getProductIdByName(productName);
                if (productId == -1) {
                    JOptionPane.showMessageDialog(null, "Proizvod: " + productName + " nije pronadjen", "Greška", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                String batchNumber = generateBatchNumber(productId);
                long ean13 = generateEAN13(productId, batchNumber);

                // Dodajte proizvod u product_batches i dobijte ID
                int productBatchId = addProductBatch(
                        new ProductBatchDto(productId, ean13, batchNumber, expirationDate, quantity, quantity, shipmentId, locationId)
                );

                if (productBatchId == -1) {
                    JOptionPane.showMessageDialog(null, "Greška pri dodavanju proizvoda u product_batches.", "Greška", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                // Dodajte stavku u shipment_items koristeći productBatchId
                addShipmentItem(shipmentId, productBatchId, quantity);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Greška pri obradi stavke: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getLocationIdFromItem(Map<String, Object> item) {
        String selectedLocation = (String) item.get("Lokacija");
        if (selectedLocation != null) {
            String[] parts = selectedLocation.split(" - ");
            if (parts.length == 4) {
                return getLocationIdByName(parts[0], parts[1], parts[2], parts[3]);
            }
        }
        return -1;
    }

    private int getProductIdByName(String productName) {
        try {
            URL url = new URL("http://localhost:8080/api/products/name/" + URLEncoder.encode(productName, "UTF-8"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Deserialize the response as an integer (product ID)
                    ObjectMapper objectMapper = new ObjectMapper();
                    int productId = objectMapper.readValue(response, Integer.class);

                    return productId;
                }
            } else {
                System.out.println("Proizvod pod imenom: " + productName + " nije pronađen.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Ako nije pronađen proizvod
    }

    private int getLocationIdByName(String section, String shelf, String row, String description) {
        try {
            String urlString = String.format(
                    "http://localhost:8080/api/locations/find?section=%s&shelf=%s&row=%s&description=%s",
                    URLEncoder.encode(section, "UTF-8"),
                    URLEncoder.encode(shelf, "UTF-8"),
                    URLEncoder.encode(row, "UTF-8"),
                    URLEncoder.encode(description, "UTF-8")
            );

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    return Integer.parseInt(response); // Pretpostavljamo da server vraća samo ID kao broj
                }
            } else {
                System.out.println("Lokacija nije pronađena za unete parametre.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Ako nije pronađena lokacija
    }

    private long generateEAN13(int productId, String batchNumber) {
        // Prve 3 cifre: 860 (Srbija)
        String ean13Prefix = "860";

        // Sledeće 2 cifre: productId (formatirano kao 2-cifreni broj)
        String productIdPart = String.format("%02d", productId);

        // Sledeće 3 cifre: batchNumber (tri cifre, npr. "001" za "BATCH001")
        String batchNumberPart = batchNumber.substring(5); // Uzima "001" iz "BATCH001"

        // Poslednjih 5 cifara: nasumično generisan broj
        String randomPart = String.format("%05d", ThreadLocalRandom.current().nextInt(0, 100000));

        // Kombinacija svih delova
        String ean13String = ean13Prefix + productIdPart + batchNumberPart + randomPart;

        // Konvertujemo u long
        return Long.parseLong(ean13String);
    }

    private String generateBatchNumber(int productId) {
        // Dobijamo trenutni najveći batch broj za dati proizvod
        int nextBatchNumber = getNextBatchNumberForProduct(productId);

        // Formatiranje batch broja (npr. "BATCH001")
        return String.format("BATCH%03d", nextBatchNumber);
    }

    private int getNextBatchNumberForProduct(int productId) {
        try {
            URL url = new URL("http://localhost:8080/api/batches/next-batch-number/" + productId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    return Integer.parseInt(response.trim());
                }
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if an error occurs
    }

    private int getSupplierId(String name) {
        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
            encodedName = encodedName.replace("+", "%20");
            URL url = new URL("http://localhost:8080/api/suppliers/name/" + encodedName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parse the response as an integer
                    return Integer.parseInt(response.trim());
                }
            } else {
                System.out.println("Dostavljac pod imenom: " + name + " nije pronađen.");
                return -1; // Return -1 if the supplier is not found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 if an exception occurs
        }
    }

    public int addProductBatch(ProductBatchDto productBatchDto) {
        try {
            URL url = new URL("http://localhost:8080/api/batches/add");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            connection.setDoOutput(true);

            // Convert ProductBatchDto to JSON
            String jsonInputString = objectMapper.writeValueAsString(productBatchDto);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parse JSON response into ProductBatchDto
                    ProductBatchDto createdProductBatch = objectMapper.readValue(response, ProductBatchDto.class);

                    // Return the ID of the created ProductBatch
                    return createdProductBatch.getId();
                }
            } else {
                System.out.println("Greška pri dodavanju proizvoda u product_batches: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Greška pri komunikaciji sa serverom.");
        }
        return -1; // Return -1 if an error occurs
    }

    public int addShipment(int supplierId, LocalDate arrivalDate, String arrivalTime) {
        try {
            URL url = new URL("http://localhost:8080/api/shipment/add");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            connection.setDoOutput(true);

            String arrivalDateString = String.valueOf(arrivalDate);
            System.out.println("Arrival Date: " + arrivalDateString);

            // JSON request body
            String jsonInputString = String.format(
                    "{\"supplier_id\": %d, \"arrivalDate\": \"%s\", \"arrivalTime\": \"%s\"}",
                    supplierId, arrivalDateString, arrivalTime
            );

            System.out.println("jsonInputString: " + jsonInputString);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parse JSON response into ShipmentDto
                    ShipmentDto shipmentDto = objectMapper.readValue(response, ShipmentDto.class);

                    // Return the ID of the created Shipment
                    return shipmentDto.getId();
                }
            } else {
                System.out.println("Greška pri dodavanju pošiljke: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if an error occurs
    }

    public void addShipmentItem(int shipmentId, int productId, int quantity) {
        try {
            URL url = new URL("http://localhost:8080/api/shipment-items/add");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            connection.setDoOutput(true);

            // JSON request body
            String jsonInputString = String.format(
                    "{\"shipment_id\": %d, \"product_id\": %d, \"quantity\": %d}",
                    shipmentId, productId, quantity
            );

            System.out.println("jsonInputString: " + jsonInputString);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Stavka uspešno dodata u shipment_items.");
            } else {
                System.out.println("Greška pri dodavanju stavke u shipment_items: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Greška pri komunikaciji sa serverom.");
        }
    }

    private void datePickerMouseClicked(MouseEvent e) {
        if (datePicker.getDate().isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Datum mora biti nakon trenutnog datuma!", "Greška", JOptionPane.ERROR_MESSAGE);
            datePicker.setDate(LocalDate.now());
        }
    }

    private void backMouseClicked(MouseEvent e) {
        dispose();
        MainMenuAdmin.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        label1 = new JLabel();
        supplier = new JComboBox();
        back = new JButton();
        scrollPane1 = new JScrollPane();
        items = new JTable();
        label2 = new JLabel();
        product = new JTextField();
        label3 = new JLabel();
        quantity = new JTextField();
        label4 = new JLabel();
        datePicker = new DatePicker();
        label5 = new JLabel();
        location = new JComboBox();
        add = new JButton();
        finnish = new JButton();
        popupMenu = new JPopupMenu();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0x3d8d7a));
            panel1.setLayout(new MigLayout(
                "fill,hidemode 3",
                // columns
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]",
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
                "[75]" +
                "[75]"));

            //---- label1 ----
            label1.setText("Dobavljac:");
            label1.setForeground(new Color(0xfbffe4));
            panel1.add(label1, "cell 1 1");

            //---- supplier ----
            supplier.setBackground(new Color(0xb3d8a8));
            supplier.setForeground(Color.darkGray);
            panel1.add(supplier, "cell 2 1 2 1");

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
            panel1.add(back, "cell 8 1");

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(Color.darkGray);
                scrollPane1.setForeground(Color.darkGray);

                //---- items ----
                items.setBackground(new Color(0xfbffe4));
                items.setForeground(Color.darkGray);
                items.setGridColor(Color.darkGray);
                items.setSelectionBackground(new Color(0xb3d8a8));
                items.setSelectionForeground(Color.darkGray);
                scrollPane1.setViewportView(items);
            }
            panel1.add(scrollPane1, "cell 0 3 5 5");

            //---- label2 ----
            label2.setText("Proizvod:");
            label2.setForeground(new Color(0xfbffe4));
            panel1.add(label2, "cell 5 3");

            //---- product ----
            product.setBackground(new Color(0xb3d8a8));
            product.setForeground(Color.darkGray);
            panel1.add(product, "cell 6 3 2 1");

            //---- label3 ----
            label3.setText("Kolicina:");
            label3.setForeground(new Color(0xfbffe4));
            panel1.add(label3, "cell 5 4");

            //---- quantity ----
            quantity.setBackground(new Color(0xb3d8a8));
            quantity.setForeground(Color.darkGray);
            panel1.add(quantity, "cell 6 4");

            //---- label4 ----
            label4.setText("Rok Trajanja:");
            label4.setForeground(new Color(0xfbffe4));
            panel1.add(label4, "cell 5 5");

            //---- datePicker ----
            datePicker.setBackground(new Color(0xb3d8a8));
            datePicker.setForeground(Color.darkGray);
            datePicker.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    datePickerMouseClicked(e);
                }
            });
            panel1.add(datePicker, "cell 6 5 2 1");

            //---- label5 ----
            label5.setText("Lokacija");
            label5.setForeground(new Color(0xfbffe4));
            panel1.add(label5, "cell 5 6");

            //---- location ----
            location.setBackground(new Color(0xb3d8a8));
            location.setForeground(Color.darkGray);
            panel1.add(location, "cell 6 6 3 1");

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
            panel1.add(add, "cell 5 7");

            //---- finnish ----
            finnish.setText("Zavrsi");
            finnish.setBackground(new Color(0xb3d8a8));
            finnish.setForeground(Color.darkGray);
            finnish.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    finnishMouseClicked(e);
                }
            });
            panel1.add(finnish, "cell 3 9");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());

        //======== popupMenu ========
        {
            popupMenu.setBackground(new Color(0xb3d8a8));
            popupMenu.setForeground(Color.darkGray);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JLabel label1;
    private JComboBox supplier;
    private JButton back;
    private JScrollPane scrollPane1;
    private JTable items;
    private JLabel label2;
    private JTextField product;
    private JLabel label3;
    private JTextField quantity;
    private JLabel label4;
    private DatePicker datePicker;
    private JLabel label5;
    private JComboBox location;
    private JButton add;
    private JButton finnish;
    private JPopupMenu popupMenu;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    @Autowired
    private ProductBatchRepository productBatchRepository;
    @Autowired
    private ProductBatchService productBatchService;
    private List<Map<String, Object>> itemsList = new ArrayList<>();
    private final ObjectMapper objectMapper;

    public List<Map<String, Object>> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<Map<String, Object>> itemsList) {
        this.itemsList = itemsList;
    }
}
