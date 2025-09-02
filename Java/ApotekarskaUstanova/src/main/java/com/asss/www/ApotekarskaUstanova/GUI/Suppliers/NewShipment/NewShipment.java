/*
 * Created by JFormDesigner on Mon Feb 24 05:19:20 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Suppliers.NewShipment;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.Dto.*;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuInventory.MainMenuInventory;
import com.asss.www.ApotekarskaUstanova.Repository.ProductBatchRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Location;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin.MainMenuAdmin;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Service.ProductBatchService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.lgooddatepicker.components.*;
import net.miginfocom.swing.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.awt.event.ComponentEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        loadSupplierCB(3);
        loadLocationCB();
        loadLocationCB2();
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

        String[] columnNames2 = {"ID", "Naziv", "Količina"};
        DefaultTableModel model2 = new DefaultTableModel(columnNames2, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        orderItems.setModel(model2);
        customizeTable(orderItems, model2);
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

        JViewport viewport2 = scrollPane2.getViewport();
        viewport2.setBackground(backgroundColor);
        scrollPane2.setBackground(backgroundColor);

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

    private void loadSupplierCB(int preselectedId) {
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

                    ObjectMapper objectMapper = new ObjectMapper();
                    List<SupplierDto> suppliers = objectMapper.readValue(response, new TypeReference<List<SupplierDto>>() {});

                    supplier.removeAllItems();

                    for (SupplierDto s : suppliers) {
                        supplier.addItem(s.getId() + " - " + s.getName() + " - " + s.getPhone());
                    }

                    // Ovde sigurno postoji combo box i stavke — sada postavi selekciju
                    setSupplierComboBoxById(preselectedId);
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

    private void loadLocationCB2() {
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
                    locationCB2.removeAllItems();

                    // Dodavanje kombinacije polja u JComboBox
                    for (Location l : locations) {
                        String comboBoxItem = String.format("%s - %s - %s - %s", l.getSection(), l.getShelf(), l.getRow(), l.getDescription());
                        locationCB2.addItem(comboBoxItem);
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

        if (selectedDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Datum mora biti nakon trenutnog datuma!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String formattedDate = selectedDate.toString();

        // Poziv metode za unos u tabelu
        addOrUpdateItemInTable(selectedProduct, quantityValue, formattedDate, selectedLocation);

        // Čišćenje polja nakon dodavanja
        product.setText("");
        quantity.setText("");
        datePicker.setDate(LocalDate.now());
        location.setSelectedIndex(0);
    }

    private void addOrUpdateItemInTable(String selectedProduct, int quantityValue, String formattedDate, String selectedLocation) {
        DefaultTableModel model = (DefaultTableModel) items.getModel();

        // Provera da li postoji isti proizvod sa istim rokom trajanja na drugoj lokaciji
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

        if (sameProductDifferentLocation) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Postoji isti proizvod sa istim rokom trajanja na drugoj lokaciji. Da li želite da nastavite sa unosom?",
                    "Upozorenje",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (option == JOptionPane.NO_OPTION) {
                return;
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

        if (sameProductSameLocation) {
            int existingQuantity = Integer.parseInt((String) model.getValueAt(rowIndex, 1));
            int newQuantity = existingQuantity + quantityValue;
            model.setValueAt(String.valueOf(newQuantity), rowIndex, 1);
        } else {
            model.addRow(new Object[]{selectedProduct, String.valueOf(quantityValue), formattedDate, selectedLocation});
            customizeTable(items, model);
        }
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
            String encodedName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
            encodedName = encodedName.replace("+", "%20");
            URL url = new URL("http://localhost:8080/api/products/name/" + encodedName);
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

    private int addShipment(int supplierId, LocalDate arrivalDate, String arrivalTime) {
        int employeeId = JwtResponse.getUserId();
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

                    // Generate receipt
                    String supplierName = getSupplierNameById(supplierId);
                    LocalTime time = LocalTime.parse(arrivalTime);
                    String employeeName = fetchEmployeeName(employeeId);
                    generateReceipt(
                            shipmentDto.getId(),
                            arrivalDate,
                            time,
                            supplierName,
                            employeeName,
                            itemsList
                    );

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

    private String fetchEmployeeName(int employeeId) {
        try {
            URL url = new URL("http://localhost:8080/api/employees/" + employeeId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    EmployeeDto employee = objectMapper.readValue(response, EmployeeDto.class);

                    return employee.getName() + " " + employee.getSurname();
                }
            } else {
                return "Nepoznati zaposleni";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Greška pri učitavanju zaposlenog";
        }
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

    public static void generateReceipt(int shipmentId, LocalDate arrivalDate, LocalTime arrivalTime,
                                       String supplierName, String employeeName, List<Map<String, Object>> items) {
        try (PDDocument document = new PDDocument()) {
            // 1. Izračunavanje dimenzija koristeći metode
            int maxLineLength = calculateMaxLineLength(shipmentId, arrivalDate, arrivalTime, supplierName, items);
            int totalLines = calculateTotalLines(items);

            // 2. Dinamičko određivanje širina kolona
            int maxNameLength = items.stream()
                    .mapToInt(item -> ((String)item.get("Naziv proizvoda")).length())
                    .max()
                    .orElse(15);
            maxNameLength = Math.min(Math.max(maxNameLength, 10), 25);

            int col1Width = maxNameLength + 4;
            int col2Width = 8;
            int col3Width = 12;
            int tableWidth = col1Width + col2Width + col3Width + 4;

            // 3. Postavke stranice
            int lineHeight = 15;
            int margin = 15;
            int pageWidth = Math.min(Math.max(maxLineLength + 2 * margin, 300), 595);
            int pageHeight = Math.min(totalLines * lineHeight + 2 * margin, 842);

            PDPage page = new PDPage(new PDRectangle(pageWidth, pageHeight));
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDType0Font font = PDType0Font.load(document,
                        new ClassPathResource("static/arial.ttf").getInputStream());

                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, pageHeight - margin - 10);

                // 4. Zaglavlje
                String[] headerLines = {
                        "===== PRIJEMNICA =====",
                        "ID: " + shipmentId,
                        "Datum: " + arrivalDate,
                        "Vreme: " + arrivalTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        "Blagajnik: " + employeeName,
                        "Dobavljač: " + supplierName
                };

                for (String line : headerLines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -lineHeight);
                }

                // Linija ispod zaglavlja (dužina naslova)
                String headerSeparator = new String(new char[headerLines[0].length()]).replace('\0', '=');
                contentStream.showText(headerSeparator);
                contentStream.newLineAtOffset(0, -lineHeight);

                // 5. Tabela proizvoda
                String headerFormat = "%-" + col1Width + "s %" + col2Width + "s %" + col3Width + "s";
                contentStream.showText(String.format(headerFormat, "Proizvod", "Količina", "Rok trajanja"));
                contentStream.newLineAtOffset(0, -lineHeight);

                // Linija ispod naslova tabele (tačna širina tabele)
                String tableSeparator = new String(new char[tableWidth]).replace('\0', '-');
                contentStream.showText(tableSeparator);
                contentStream.newLineAtOffset(0, -lineHeight);

                // Stavke
                String itemFormat = "%-" + col1Width + "s %" + col2Width + "s %" + col3Width + "s";
                for (Map<String, Object> item : items) {
                    String productName = shorten(((String) item.get("Naziv proizvoda")), maxNameLength);
                    contentStream.showText(String.format(itemFormat,
                            productName,
                            item.get("Kolicina").toString(),
                            ((String) item.get("Rok trajanja")).substring(2)));
                    contentStream.newLineAtOffset(0, -lineHeight);
                }
                contentStream.endText();
            }

            // 7. Čuvanje dokumenta
            String folderPath = "receipts/shipments/" + arrivalDate;
            Files.createDirectories(Paths.get(folderPath));
            document.save(new File(folderPath + "/Shipment_" + shipmentId + ".pdf"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int calculateMaxLineLength(int shipmentId, LocalDate arrivalDate,
                                              LocalTime arrivalTime, String supplierName,
                                              List<Map<String, Object>> items) {
        int maxLength = 0;
        String[] headerLines = {
                "===== PRIJEMNICA =====",
                "ID: " + shipmentId,
                "Datum: " + arrivalDate,
                "Vreme: " + arrivalTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                "Blagajnik: ...",
                "Dobavljač: " + supplierName
        };

        for (String line : headerLines) {
            maxLength = Math.max(maxLength, line.length() * 6);
        }

        // Izračunaj širinu za tabelu
        int maxNameLength = items.stream()
                .mapToInt(item -> ((String)item.get("Naziv proizvoda")).length())
                .max()
                .orElse(15);
        maxNameLength = Math.min(Math.max(maxNameLength, 10), 25);

        int col1Width = maxNameLength + 4;
        int col2Width = 8;
        int col3Width = 12;
        int tableWidth = col1Width + col2Width + col3Width + 4;

        maxLength = Math.max(maxLength, tableWidth * 6);

        String footer = "Ukupno: " + items.size() + " proizvoda";
        return Math.max(maxLength, footer.length() * 6);
    }

    private static int calculateTotalLines(List<Map<String, Object>> items) {
        // Zaglavlje: 7 linija (naslov + 5 podataka + linija razdvajanja)
        // Tabela: 2 linije (header + separator)
        // Stavke: items.size()
        return 7 + 2 + items.size();
    }

    private static String shorten(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }

    private String getSupplierNameById(int supplierId) {
        try {
            URL url = new URL("http://localhost:8080/api/suppliers/" + supplierId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    ObjectMapper mapper = new ObjectMapper();
                    SupplierDto supplier = mapper.readValue(response, SupplierDto.class);
                    return supplier.getName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Nepoznat dobavljač";
    }

    private void backMouseClicked(MouseEvent e) {
        dispose();
        MainMenuInventory.start();
    }

    private void add2MouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) orderItems.getModel();
        int rowCount = model.getRowCount();

        itemsToProcess.clear();
        currentItemIndex = 0;

        for (int i = 0; i < rowCount; i++) {
            String name = model.getValueAt(i, 1).toString();          // naziv proizvoda je kolona 1 (jer 0 je ID)
            int quantity = Integer.parseInt(model.getValueAt(i, 2).toString()); // kolona 2 = količina

            // Pretpostavljam da OrderItemsDto ima konstruktor sa nazivom i količinom
            itemsToProcess.add(new OrderItemsDto(name, quantity));
        }

//        if (!itemsToProcess.isEmpty()) {
//            showItemInputDialog(itemsToProcess.get(currentItemIndex));
//        } else {
//            JOptionPane.showMessageDialog(this, "Nema stavki za unos.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
//        }
        if (!itemsToProcess.isEmpty()) {
            showItemInputDialog();
        }
    }

    private void showItemInputDialog() {
        if (currentItemIndex >= itemsToProcess.size()) {
            orderedSuppliesDialog2.setVisible(false);  // zatvori modal kad je kraj
            return;
        }

        OrderItemsDto currentItem = itemsToProcess.get(currentItemIndex);

        // Postavi podatke u modal: npr. naziv proizvoda u labelu, resetuj polja za rok i lokaciju
        productName.setText(currentItem.getProduct().getName());
        datePicker1.setText("");
        locationCB2.setSelectedIndex(0);

        orderedSuppliesDialog2.setVisible(true);
    }

    private void orderedSuppliesMouseClicked(MouseEvent e) {
        orderedSuppliesDialog.setVisible(true);
    }

    private void orderedSuppliesDialogComponentShown(ComponentEvent e) {
        List<OrderDto> nonReceivedOrders = getNonReceivedOrders();

        orderCB.removeAllItems();
        for (OrderDto orderItem : nonReceivedOrders) {
            String itemText = orderItem.getId() + " - " +
                    orderItem.getSupplier().getName() + " - " +
                    orderItem.getSelectedDate() + " - " +
                    orderItem.getSelectedTime();
            orderCB.addItem(itemText);
        }

        if (orderCB.getItemCount() > 0) {
            orderCB.setSelectedIndex(0);

            // Ručno pokreni akciju da bi se popunila tabela
            orderCB(new ActionEvent(orderCB, ActionEvent.ACTION_PERFORMED, ""));
        } else {
            System.out.println("Nema narudžbina za prikaz");
        }
    }


    private List<OrderDto> getNonReceivedOrders() {
        List<OrderDto> nonReceivedOrders = new ArrayList<>();

        try {
            URL url = new URL("http://localhost:8080/api/orders/not-received");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String response = reader.lines().collect(Collectors.joining());
                    nonReceivedOrders = objectMapper.readValue(response, new TypeReference<List<OrderDto>>() {});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return nonReceivedOrders;
    }

    private void fillOrderItemsTable(int orderId) {
        DefaultTableModel model = (DefaultTableModel) orderItems.getModel();
        model.setRowCount(0);
        try {
            URL url = new URL("http://localhost:8080/api/order-items/order/" + orderId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    List<OrderItemsDto> orderItems = objectMapper.readValue(response, new TypeReference<List<OrderItemsDto>>() {});

                    for (OrderItemsDto item : orderItems) {
                        model.addRow(new Object[]{
                                item.getId(),
                                item.getProduct().getName(),
                                item.getQuantity()
                        });
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja stavki narudžbine.", "HTTP greška", JOptionPane.ERROR_MESSAGE);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri komunikaciji sa serverom.", "Greška", JOptionPane.ERROR_MESSAGE);
        }

        customizeTable(orderItems, model);
    }

    private void add3MouseClicked(MouseEvent e) {
        String selectedProduct = productName.getText();
        int quantityValue = itemsToProcess.get(currentItemIndex).getQuantity();
        String formattedDate = datePicker1.getText();
        String selectedLocation = (String) locationCB2.getSelectedItem();

        addOrUpdateItemInTable(selectedProduct, quantityValue, formattedDate, selectedLocation);

        currentItemIndex++;
        if (currentItemIndex < itemsToProcess.size()) {
            showItemInputDialog();  // idi na sledeću stavku
        } else {
            orderedSuppliesDialog2.setVisible(false);
            orderedSuppliesDialog.setVisible(false);

            // Dodaj u listu unetih narudžbina
            processedOrderIds.add(selectedOrderId);

            // Ažuriraj status na acquired = 1
            markOrderAsAcquired(selectedOrderId);

            loadSupplierCB(selectedSupplierId);

        }
    }

    private void markOrderAsAcquired(int orderId) {
        try {
            URL url = new URL("http://localhost:8080/api/orders/mark-acquired/" + orderId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            conn.setDoOutput(true); // jer je PUT

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Narudžbina označena kao primljena.");
            } else {
                System.err.println("Greška pri označavanju narudžbine: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void orderCBPropertyChange(PropertyChangeEvent e) {

    }

    private void orderCBItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void orderCB(ActionEvent e) {
        String selectedItem = (String) orderCB.getSelectedItem();

        if (selectedItem == null || selectedItem.isEmpty()) {
            System.out.println("Nijedna stavka nije selektovana u combo boxu.");
            return;
        }

        try {
            int orderId = Integer.parseInt(selectedItem.split(" - ")[0]);

            OrderDto selectedOrder = getNonReceivedOrders()
                    .stream()
                    .filter(o -> o.getId() == orderId)
                    .findFirst()
                    .orElse(null);

            if (selectedOrder != null) {
                selectedSupplierId = selectedOrder.getSupplier().getId();
                loadSupplierCB(selectedSupplierId);
            }

            setSelectedOrderId(orderId);
            fillOrderItemsTable(orderId);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri obradi selektovane narudžbine.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void setSupplierComboBoxById(int supplierId) {
        for (int i = 0; i < supplier.getItemCount(); i++) {
            String item = (String) supplier.getItemAt(i);
            String idPart = item.split(" - ")[0].trim();

            try {
                int id = Integer.parseInt(idPart);
                if (id == supplierId) {
                    supplier.setSelectedIndex(i);
                    break;
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        label1 = new JLabel();
        supplier = new JComboBox();
        orderedSupplies = new JButton();
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
        orderedSuppliesDialog = new JDialog();
        panel2 = new JPanel();
        back2 = new JButton();
        orderCB = new JComboBox();
        scrollPane2 = new JScrollPane();
        orderItems = new JTable();
        add2 = new JButton();
        orderedSuppliesDialog2 = new JDialog();
        panel3 = new JPanel();
        back3 = new JButton();
        productName = new JTextField();
        label6 = new JLabel();
        locationCB2 = new JComboBox();
        label7 = new JLabel();
        datePicker1 = new DatePicker();
        add3 = new JButton();

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

            //---- orderedSupplies ----
            orderedSupplies.setSelectedIcon(null);
            orderedSupplies.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
            orderedSupplies.setBackground(new Color(0xb3d8a8));
            orderedSupplies.setForeground(Color.darkGray);
            orderedSupplies.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    orderedSuppliesMouseClicked(e);
                }
            });
            panel1.add(orderedSupplies, "cell 6 1");

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

        //======== orderedSuppliesDialog ========
        {
            orderedSuppliesDialog.setModal(true);
            orderedSuppliesDialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    orderedSuppliesDialogComponentShown(e);
                }
            });
            var orderedSuppliesDialogContentPane = orderedSuppliesDialog.getContentPane();

            //======== panel2 ========
            {
                panel2.setBackground(new Color(0x3d8d7a));
                panel2.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]",
                    // rows
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[]" +
                    "[fill]" +
                    "[fill]"));

                //---- back2 ----
                back2.setText("Nazad");
                back2.setBackground(new Color(0xb3d8a8));
                back2.setForeground(Color.darkGray);
                panel2.add(back2, "cell 0 0");

                //---- orderCB ----
                orderCB.setBackground(new Color(0xb3d8a8));
                orderCB.setForeground(Color.darkGray);
                orderCB.addItemListener(e -> orderCBItemStateChanged(e));
                orderCB.addPropertyChangeListener(e -> orderCBPropertyChange(e));
                orderCB.addActionListener(e -> orderCB(e));
                panel2.add(orderCB, "cell 1 1 6 1");

                //======== scrollPane2 ========
                {
                    scrollPane2.setBackground(Color.darkGray);
                    scrollPane2.setForeground(Color.darkGray);

                    //---- orderItems ----
                    orderItems.setBackground(new Color(0xfbffe4));
                    orderItems.setForeground(Color.darkGray);
                    orderItems.setGridColor(Color.darkGray);
                    orderItems.setSelectionBackground(new Color(0xb3d8a8));
                    orderItems.setSelectionForeground(Color.darkGray);
                    scrollPane2.setViewportView(orderItems);
                }
                panel2.add(scrollPane2, "cell 0 2 7 4,growy");

                //---- add2 ----
                add2.setText("Dodaj");
                add2.setBackground(new Color(0xb3d8a8));
                add2.setForeground(Color.darkGray);
                add2.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        add2MouseClicked(e);
                    }
                });
                panel2.add(add2, "cell 1 6");
            }

            GroupLayout orderedSuppliesDialogContentPaneLayout = new GroupLayout(orderedSuppliesDialogContentPane);
            orderedSuppliesDialogContentPane.setLayout(orderedSuppliesDialogContentPaneLayout);
            orderedSuppliesDialogContentPaneLayout.setHorizontalGroup(
                orderedSuppliesDialogContentPaneLayout.createParallelGroup()
                    .addComponent(panel2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
            );
            orderedSuppliesDialogContentPaneLayout.setVerticalGroup(
                orderedSuppliesDialogContentPaneLayout.createParallelGroup()
                    .addGroup(orderedSuppliesDialogContentPaneLayout.createSequentialGroup()
                        .addComponent(panel2, GroupLayout.PREFERRED_SIZE, 505, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
            );
            orderedSuppliesDialog.pack();
            orderedSuppliesDialog.setLocationRelativeTo(orderedSuppliesDialog.getOwner());
        }

        //======== orderedSuppliesDialog2 ========
        {
            orderedSuppliesDialog2.setModal(true);
            var orderedSuppliesDialog2ContentPane = orderedSuppliesDialog2.getContentPane();

            //======== panel3 ========
            {
                panel3.setBackground(new Color(0x3d8d7a));
                panel3.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]",
                    // rows
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]"));

                //---- back3 ----
                back3.setText("Nazad");
                back3.setBackground(new Color(0xb3d8a8));
                back3.setForeground(Color.darkGray);
                panel3.add(back3, "cell 1 1,align center center,grow 0 0");

                //---- productName ----
                productName.setBackground(new Color(0xb3d8a8));
                productName.setForeground(Color.darkGray);
                productName.setEditable(false);
                panel3.add(productName, "cell 1 2 4 1,aligny center,growy 0");

                //---- label6 ----
                label6.setText("Lokacija:");
                label6.setForeground(new Color(0xfbffe4));
                panel3.add(label6, "cell 1 3 2 1");

                //---- locationCB2 ----
                locationCB2.setBackground(new Color(0xb3d8a8));
                locationCB2.setForeground(Color.darkGray);
                locationCB2.addItemListener(e -> orderCBItemStateChanged(e));
                panel3.add(locationCB2, "cell 1 4 6 1,aligny center,growy 0");

                //---- label7 ----
                label7.setText("Rok trajanja:");
                label7.setForeground(new Color(0xfbffe4));
                panel3.add(label7, "cell 1 5 2 1");

                //---- datePicker1 ----
                datePicker1.setBackground(new Color(0xb3d8a8));
                datePicker1.setForeground(Color.darkGray);
                panel3.add(datePicker1, "cell 1 6 6 1,aligny center,growy 0");

                //---- add3 ----
                add3.setText("Dodaj");
                add3.setBackground(new Color(0xb3d8a8));
                add3.setForeground(Color.darkGray);
                add3.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        add3MouseClicked(e);
                    }
                });
                panel3.add(add3, "cell 1 8,align center center,grow 0 0");
            }

            GroupLayout orderedSuppliesDialog2ContentPaneLayout = new GroupLayout(orderedSuppliesDialog2ContentPane);
            orderedSuppliesDialog2ContentPane.setLayout(orderedSuppliesDialog2ContentPaneLayout);
            orderedSuppliesDialog2ContentPaneLayout.setHorizontalGroup(
                orderedSuppliesDialog2ContentPaneLayout.createParallelGroup()
                    .addComponent(panel3, GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
            );
            orderedSuppliesDialog2ContentPaneLayout.setVerticalGroup(
                orderedSuppliesDialog2ContentPaneLayout.createParallelGroup()
                    .addComponent(panel3, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
            );
            orderedSuppliesDialog2.pack();
            orderedSuppliesDialog2.setLocationRelativeTo(orderedSuppliesDialog2.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JLabel label1;
    private JComboBox supplier;
    private JButton orderedSupplies;
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
    private JDialog orderedSuppliesDialog;
    private JPanel panel2;
    private JButton back2;
    private JComboBox orderCB;
    private JScrollPane scrollPane2;
    private JTable orderItems;
    private JButton add2;
    private JDialog orderedSuppliesDialog2;
    private JPanel panel3;
    private JButton back3;
    private JTextField productName;
    private JLabel label6;
    private JComboBox locationCB2;
    private JLabel label7;
    private DatePicker datePicker1;
    private JButton add3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    @Autowired
    private ProductBatchRepository productBatchRepository;
    @Autowired
    private ProductBatchService productBatchService;
    private List<Map<String, Object>> itemsList = new ArrayList<>();
    private final ObjectMapper objectMapper;
    private int selectedOrderId;
    private List<OrderItemsDto> itemsToProcess = new ArrayList<>();
    private int currentItemIndex = 0;
    private int selectedSupplierId;
    private List<Integer> processedOrderIds = new ArrayList<>();



    public List<Map<String, Object>> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<Map<String, Object>> itemsList) {
        
        this.itemsList = itemsList;
    }

    public int getSelectedOrderId() {
        return selectedOrderId;
    }

    public void setSelectedOrderId(int selectedOrderId) {
        this.selectedOrderId = selectedOrderId;
    }

    public int getSelectedSupplierId() {
        return selectedSupplierId;
    }

    public void setSelectedSupplierId(int selectedSupplierId) {
        this.selectedSupplierId = selectedSupplierId;
    }

    public List<Integer> getProcessedOrderIds() {
        return processedOrderIds;
    }

    public void setProcessedOrderIds(List<Integer> processedOrderIds) {
        this.processedOrderIds = processedOrderIds;
    }
}
