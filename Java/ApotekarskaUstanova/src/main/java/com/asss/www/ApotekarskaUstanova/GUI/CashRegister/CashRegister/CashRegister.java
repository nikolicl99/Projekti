/*
 * Created by JFormDesigner on Thu Feb 20 21:09:37 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.CashRegister.CashRegister;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.asss.www.ApotekarskaUstanova.Dto.EmployeeDto;
import com.asss.www.ApotekarskaUstanova.GUI.CashRegister.SalesHistory.SalesHistory;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin.MainMenuAdmin;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.miginfocom.swing.MigLayout;

import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Dto.ProductBatchDto;
import com.asss.www.ApotekarskaUstanova.Dto.ProductDto;
import com.asss.www.ApotekarskaUstanova.Entity.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;

/**
 * @author lniko
 */
public class CashRegister extends JFrame {
    private final ObjectMapper objectMapper; // Dodajte ObjectMapper kao polje klase


    public CashRegister() {
        initComponents();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Registrujte JavaTimeModule
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        System.out.println("Vreme: " + currentTime);
        System.out.println("Datum: " + currentDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        System.out.println("Formatted Time: " + formattedTime); // e.g., 00:02:30
        setupListeners();
        ammount.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        ammountPaper.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        // Kreiranje modela za tabelu
        String[] columnNames = {"Naziv proizvoda", "Ean13", "Recept", "Cena po jedinici", "Popust", "Količina", "Ukupna cena"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        items.setModel(model);
        customizeTable(items, model);

        String[] columnNamesPrescriptions = {"ID", "Proizvod", "Ean13", "Doza", "Kolicina", "Popust"};
        DefaultTableModel modelPrescription = new DefaultTableModel(columnNamesPrescriptions, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        usersPrescriptionstbl.setModel(modelPrescription);
        customizeTable(usersPrescriptionstbl, modelPrescription);

        String[] columnNamesPaperPrescriptions = {"Naziv proizvoda", "Ean13", "Doza", "Cena po jedinici", "Popust", "Kolicina", "Ukupna cena"};
        DefaultTableModel modelPaperPrescription = new DefaultTableModel(columnNamesPaperPrescriptions, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        paperPrescriptiontbl.setModel(modelPaperPrescription);
        customizeTable(paperPrescriptiontbl, modelPaperPrescription);


    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            CashRegister frame = new CashRegister();
            frame.setTitle("Kasa");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void addMouseClicked(MouseEvent e) {
        String ean13 = article.getText(); // EAN13 kod iz unosa
        int productAmount = (Integer) ammount.getValue(); // Količina kupljenog proizvoda

        try {
            // 1. Pronalazak productId na osnovu EAN13
            System.out.println("Izabrani EAN13 kod: " + ean13);
            URL batchUrl = new URL("http://localhost:8080/api/batches/product-id/" + ean13);
            HttpURLConnection batchConnection = (HttpURLConnection) batchUrl.openConnection();
            batchConnection.setRequestMethod("GET");
            batchConnection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int batchResponseCode = batchConnection.getResponseCode();
            if (batchResponseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(batchConnection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("Odgovor API-ja (productId): " + response);

                    // Parsiraj productId iz odgovora API-ja
                    int productId = Integer.parseInt(response.trim());

                    // Dohvati informacije o proizvodu na osnovu productId
                    fetchProductAndAddToTable(productId, ean13, productAmount, 0, 0);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška API-ja za productId: " + batchResponseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchProductAndAddToTable(int productId, String ean13, int productAmount, int prescriptionItem, int discount) {
        DefaultTableModel model = (DefaultTableModel) items.getModel();
        try {
            URL url = new URL("http://localhost:8080/api/products/" + productId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora u ProductDto
                    ProductDto product = objectMapper.readValue(response, ProductDto.class);

                    if (product != null) {
                        String productName = product.getName();
                        double unitPrice = product.getSellingPrice(); // Puna cena proizvoda
                        double discountAmount = unitPrice * (discount / 100.0);
                        double finalPrice = unitPrice - discountAmount;
                        double totalPrice = finalPrice * productAmount;

                        String prescriptionText;
                        switch (prescriptionItem) {
                            case 1:
                                prescriptionText = "E-Recept";
                                break;
                            case 2:
                                prescriptionText = "Papirni recept";
                                break;
                            default:
                                prescriptionText = "-";
                                break;
                        }


                        boolean found = false;
                        double updatedTotalAmount = 0;

                        for (int i = 0; i < model.getRowCount(); i++) {
                            String existingProductName = model.getValueAt(i, 0).toString();
                            String existingEan13 = model.getValueAt(i, 1).toString();
                            String existingPrescription = model.getValueAt(i, 2).toString();
                            String existingDiscount = model.getValueAt(i, 4).toString().replace("%", "");

                            double existingDiscountValue = Double.parseDouble(existingDiscount);

                            boolean sameDiscount = (prescriptionItem == 0) || (existingDiscountValue == discount);

                            if (existingProductName.equals(productName) &&
                                    existingEan13.equals(ean13) &&
                                    existingPrescription.equals(prescriptionText) &&
                                    sameDiscount) {

                                int existingAmount = (int) model.getValueAt(i, 5);
                                int newAmount = existingAmount + productAmount;
                                double newTotalPrice = newAmount * finalPrice;

                                model.setValueAt(newAmount, i, 5);
                                model.setValueAt(newTotalPrice, i, 6);

                                updatedTotalAmount = newTotalPrice - (existingAmount * finalPrice);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            model.addRow(new Object[]{productName, ean13, prescriptionText, unitPrice, discount + "%", productAmount, totalPrice});
                            updatedTotalAmount = totalPrice;
                        }

                        updateTotalPrice(updatedTotalAmount);
                    }
                }
            } else {
                System.out.println("Greška prilikom dobijanja proizvoda: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customizeTable(items, model);
        }
    }

    private void updateTotalPrice(double totalAmount) {
        try {
            double currentValue = Double.parseDouble(totalPrice.getText());
            totalPrice.setText(String.format("%.2f", currentValue + totalAmount));
        } catch (NumberFormatException e) {
            totalPrice.setText(String.format("%.2f", totalAmount));
        }
    }

    private void prescriptionsMouseClicked(MouseEvent e) {

        choosePrescription.setVisible(true);
    }

    private void nextMouseClicked(MouseEvent e) {
        choosePrescription.setVisible(false);
        if (electronic.isSelected()) {
            electronicPrescription.setVisible(true);
        }
        if (paper.isSelected()) {
            paperPrescription.setVisible(true);
        }
    }

    private void nextERMouseClicked(MouseEvent e) {
        String cardNumber = healthCard.getText().trim(); // Trim whitespace

        if (cardNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Molimo unesite broj zdravstvene kartice.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. Fetch user by health card number
            System.out.println("Izabrani broj kartice: " + cardNumber);
            URL userUrl = new URL("http://localhost:8080/api/users/health-card/" + cardNumber);
            HttpURLConnection userConnection = (HttpURLConnection) userUrl.openConnection();
            userConnection.setRequestMethod("GET");
            userConnection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int userResponseCode = userConnection.getResponseCode();
            if (userResponseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(userConnection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("Odgovor API-ja: " + response);
                    User user = objectMapper.readValue(response, User.class);

                    if (user != null) {
                        // User found, open the next dialog
                        System.out.println("User nije null");
                        setUserId(user.getId());

                        healthCard.setText("");
                        electronicPrescription.setVisible(false);
                        usersPrescriptions.setVisible(true);
                    } else {
                        // User not found
                        JOptionPane.showMessageDialog(this, "Korisnik sa ovim brojem kartice nije pronađen.", "Greška", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (userResponseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                // User not found
                JOptionPane.showMessageDialog(this, "Korisnik sa ovim brojem kartice nije pronađen.", "Greška", JOptionPane.ERROR_MESSAGE);
            } else {
                // Other API error
                JOptionPane.showMessageDialog(this, "Greška API-ja: " + userResponseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillPrescriptionTable() {
        System.out.println("otvorena fill prescription metoda");
//        int userId = user.getId();

        try {
            URL url = new URL("http://localhost:8080/api/prescriptions/user/" + getUserId());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("odgovor od servera je dobar");
                    // Parsiranje JSON odgovora
                    List<Prescription> prescriptions = objectMapper.readValue(response, new TypeReference<List<Prescription>>() {
                    });
                    if (prescriptions.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Ne postoji nijedan recept za ovog korisnika", "HTTP greška", JOptionPane.INFORMATION_MESSAGE);
                    }
                    // Filtriranje - samo nepreuzeti recepti (obtained == false)
                    prescriptionsCB.removeAllItems();
                    for (Prescription prescription : prescriptions) {
                        if (!prescription.getObtained()) {  // Filtriranje nepreuzetih recepata
                            prescriptionsCB.addItem(prescription.getId() + " - " + prescription.getIssueDate());
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška: " + responseCode, "HTTP greška", JOptionPane.ERROR_MESSAGE);
            }

            connection.disconnect(); // Zatvaranje konekcije
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri popunjavanju ComboBox-a.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prescriptionsCBItemStateChanged(ItemEvent e) {
        String selectedItem = (String) prescriptionsCB.getSelectedItem();
        int prescriptionId = Integer.parseInt(selectedItem.split(" - ")[0]);
        setSelectedPrescriptionId(prescriptionId);
        fillPrescriptionItemsTable(prescriptionId);
    }

    private void fillPrescriptionItemsTable(int prescriptionId) {
        DefaultTableModel model = (DefaultTableModel) usersPrescriptionstbl.getModel();
        try {
            URL url = new URL("http://localhost:8080/api/prescriptions/" + prescriptionId + "/items");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora
                    List<Prescription_Items> items = objectMapper.readValue(response, new TypeReference<List<Prescription_Items>>() {
                    });

                    // Resetovanje tabele

                    model.setRowCount(0);

                    usersPrescriptions.setVisible(true);

                    // Dodavanje podataka u tabelu
                    for (Prescription_Items item : items) {
                        // Pretraga batches za dati productId
                        List<ProductBatchDto> productBatches = getProductBatchesByProductId((int) item.getProduct().getId());

                        // Filtriranje i sortiranje batches
                        List<ProductBatchDto> filteredBatches = filterProductBatches(productBatches);

                        // Ako postoji bar jedan batch sa validnim EAN-13 brojem, dodaj EAN-13 u tabelu
                        // Dodavanje stavki u tabelu sa EAN-13 brojem
                        String ean13 = "";
                        if (!filteredBatches.isEmpty()) {
                            // Check if EAN13 is null before converting to string
                            Long ean13Value = filteredBatches.get(0).getEan13();
                            ean13 = (ean13Value != null) ? String.valueOf(ean13Value) : "";

                            // For debugging
                            System.out.println("Product: " + item.getProduct().getName() +
                                    ", EAN13: " + ean13 +
                                    ", Batch ID: " + filteredBatches.get(0).getId());
                        }

                        model.addRow(new Object[]{
                                item.getId(),
                                item.getProduct().getName(),
                                ean13,  // This should now display properly
                                item.getProduct().getDosage(),
                                item.getQuantity(),
                                item.getDiscount()
                        });
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška: " + responseCode, "HTTP greška", JOptionPane.ERROR_MESSAGE);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri popunjavanju tabele.", "Greška", JOptionPane.ERROR_MESSAGE);
        } finally {
            customizeTable(usersPrescriptionstbl, model);
        }
    }

    private List<ProductBatchDto> getProductBatchesByProductId(int productId) {
        try {
            URL url = new URL("http://localhost:8080/api/batches/product/" + productId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora u listu batch-eva
                    List<ProductBatchDto> productBatches = objectMapper.readValue(response, new TypeReference<List<ProductBatchDto>>() {
                    });

                    return productBatches;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška: " + responseCode, "HTTP greška", JOptionPane.ERROR_MESSAGE);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri preuzimanju batch-eva.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
        return Collections.emptyList();
    }

    private void selectPrescriptionMouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) usersPrescriptionstbl.getModel();
        int rowCount = model.getRowCount(); // Broj redova u tabeli

        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "Nema proizvoda na receptu za dodavanje.", "Upozorenje", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < rowCount; i++) {
            try {
                // Uzimanje podataka iz trenutnog reda
                int prescriptionItemId = (int) model.getValueAt(i, 0); // ID receptnog artikla
                String productName = (String) model.getValueAt(i, 1); // Naziv proizvoda
                String ean13 = (String) model.getValueAt(i, 2);
                int dosage = (int) model.getValueAt(i, 3); // Doza (ako je relevantno)
                int quantity = (int) model.getValueAt(i, 4); // Količina
                int discount = (int) model.getValueAt(i, 5); // Popust

                // Pronalazak productId na osnovu naziva proizvoda
                int productId = getProductIdByName(productName);
                if (productId != -1) {
                    // Poziv metode sa svim parametrima, prescriptionItem = 1 jer je sa recepta
                    fetchProductAndAddToTable(productId, ean13, quantity, 1, discount);
                } else {
                    JOptionPane.showMessageDialog(this, "Greška: Proizvod '" + productName + "' nije pronađen.", "Greška", JOptionPane.ERROR_MESSAGE);
                }

                usersPrescriptions.setVisible(false);
                updatePrescriptionStatus(getSelectedPrescriptionId());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Greška pri dodavanju proizvoda sa recepta.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updatePrescriptionStatus(int prescriptionId) {
        try {
            URL url = new URL("http://localhost:8080/api/prescriptions/" + prescriptionId + "/update-status");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Recept (ID: " + prescriptionId + ") uspešno ažuriran.");
            } else {
                System.out.println("Greška pri ažuriranju recepta: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getProductIdByName(String productName) {
        try {
            URL url = new URL("http://localhost:8080/api/products/name/" + URLEncoder.encode(productName, StandardCharsets.UTF_8));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    return Integer.parseInt(scanner.useDelimiter("\\A").next());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Ako ne pronađe proizvod
    }

    private void addPaperMouseClicked(MouseEvent e) {
        String ean13 = articlePaper.getText();
        String productName = getProductName();
        int quantity = (int) ammountPaper.getValue();
        String dose = dosage.getSelectedItem().toString();
        int discountText = Integer.parseInt(discount.getText());

        double unitPrice = getProductPrice();
        double discountAmount = unitPrice * (discountText / 100.0); // Iznos popusta
        double finalPrice = unitPrice - discountAmount; // Cena nakon popusta
        double totalPrice = finalPrice * quantity; // Ukupna cena

        DefaultTableModel model = (DefaultTableModel) paperPrescriptiontbl.getModel();
        boolean found = false;
        double updatedTotalAmount = 0;

        // Prolazimo kroz redove tabele da vidimo da li već postoji proizvod sa istim ean13 i popustom
        for (int i = 0; i < model.getRowCount(); i++) {
            String existingEan13 = (String) model.getValueAt(i, 1);
            String existingDiscount = (String) model.getValueAt(i, 4);

            // Proveravamo da li je ean13 i popust isti
            if (existingEan13.equals(ean13) && existingDiscount.equals(discountText + "%")) {
                // Ako proizvod postoji, ažuriramo količinu i ukupnu cenu
                int existingAmount = (int) model.getValueAt(i, 5);
                int newAmount = existingAmount + quantity;
                double newTotalPrice = newAmount * finalPrice;

                model.setValueAt(newAmount, i, 5);
                model.setValueAt(newTotalPrice, i, 6);  // Assuming the total price is in column 6

                updatedTotalAmount = newTotalPrice - (existingAmount * finalPrice);
                found = true;
                break;
            }
        }

        if (!found) {
            // Ako proizvod ne postoji, dodajemo ga kao novi red
            model.addRow(new Object[]{productName, ean13, dose, unitPrice, discountText + "%", quantity, totalPrice});
            updatedTotalAmount = totalPrice;
        }

        updateTotalPricePaper(updatedTotalAmount);
        customizeTable(paperPrescriptiontbl, model);
        resetFields();
    }

    private void updateTotalPricePaper(double totalAmount) {
        try {
            double currentValue = Double.parseDouble(totalPricePaper.getText());
            totalPricePaper.setText(String.format("%.2f", currentValue + totalAmount));
        } catch (NumberFormatException e) {
            totalPricePaper.setText(String.format("%.2f", totalAmount)); // Ako je prazan ili nevalidan unos
        }
    }

    private void articlePaperPropertyChange() {
        System.out.println("obrisano sve, krece trazenje proizvoda");
        String input = articlePaper.getText().trim();

        if (input.isEmpty()) {
            popupMenu.setVisible(false);
            return;
        }

        popupMenu.removeAll();
        System.out.println("opet obrisano sve");

        try {
            URL url = new URL("http://localhost:8080/api/batches/search?query=" + URLEncoder.encode(input, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String response = reader.lines().collect(Collectors.joining());

                    List<ProductBatchDto> productBatches = objectMapper.readValue(response, new TypeReference<List<ProductBatchDto>>() {
                    });

                    // Filtriraj listu pre nego što je proslediš dalje
                    List<ProductBatchDto> filteredBatches = filterProductBatches(productBatches);

                    if (filteredBatches.isEmpty()) {
                        popupMenu.setVisible(false);
                    } else {
                        showDropdown(filteredBatches);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showDropdown(List<ProductBatchDto> productBatches) {
        popupMenu.removeAll();

        for (ProductBatchDto pb : productBatches) {
            JMenuItem item = new JMenuItem(pb.getProduct().getName() + " - " + pb.getEan13());
            item.addActionListener(e -> {
                articlePaper.setText(String.valueOf(pb.getEan13()));
                updateDosageDropdown(pb.getProduct().getName()); // Pozovi metodu kada se odabere proizvod
            });
            popupMenu.add(item);
        }

        if (!productBatches.isEmpty()) {
            popupMenu.show(articlePaper, 0, articlePaper.getHeight());
            articlePaper.requestFocus();
        } else {
            popupMenu.setVisible(false);
        }

        int itemHeight = 25;
        int maxHeight = 200;
        int newHeight = Math.min(productBatches.size() * itemHeight, maxHeight);

        popupMenu.setPopupSize(new Dimension(200, newHeight));
        popupMenu.revalidate();
        popupMenu.repaint();
    }

    private List<ProductBatchDto> filterProductBatches(List<ProductBatchDto> productBatches) {
        // Map za čuvanje najskorijeg batch-a za svaki proizvod
        Map<String, ProductBatchDto> filteredMap = new HashMap<>();

        for (ProductBatchDto batch : productBatches) {
            // Preskoči proizvode koji nisu na stanju
            if (batch.getQuantityRemaining() <= 0) continue;

            // Ako već postoji proizvod u mapi, zadrži onaj sa NAJDALJIM rokom trajanja
            if (!filteredMap.containsKey(batch.getProduct().getName()) ||
                    batch.getExpirationDate().isAfter(filteredMap.get(batch.getProduct().getName()).getExpirationDate())) {
                filteredMap.put(batch.getProduct().getName(), batch);
            }
        }

        // Sortiramo po datumu isteka - prvo oni sa najdaljim rokom
        List<ProductBatchDto> result = new ArrayList<>(filteredMap.values());
        result.sort(Comparator.comparing(ProductBatchDto::getExpirationDate).reversed());

        return result;
    }

    private void setupListeners() {
        // Dodaj MouseListener za articlePaper
        articlePaper.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                articlePaper.requestFocus(); // Postavi fokus na articlePaper
                updateDropdown(); // Ažuriraj dropdown kad dobije fokus
            }
        });

        // Dodaj DocumentListener za articlePaper
        articlePaper.getDocument().addDocumentListener(new DocumentListener() {
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

        ((AbstractDocument) discount.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;

                try {
                    int value = Integer.parseInt(newText);
                    if (value >= 1 && value <= 100) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });

    }

    private void updateDropdown() {
        String text = articlePaper.getText().trim();

        // Ako je polje prazno, odmah sakrij popup
        if (text.isEmpty()) {
            popupMenu.setVisible(false);
            return;
        }

        articlePaperPropertyChange();
    }

    private void updateDosageDropdown(String productName) {
        dosage.removeAllItems(); // Očisti prethodne doze

        if (productName.isEmpty()) {
            dosage.removeAllItems();
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/products/dosages?name=" + URLEncoder.encode(productName, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String response = reader.lines().collect(Collectors.joining());

                    List<Integer> dosages = objectMapper.readValue(response, new TypeReference<List<Integer>>() {
                    });

                    for (Integer dose : dosages) {
                        dosage.addItem(dose.toString());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getProductName() {
        String ean13 = articlePaper.getText();

        if (ean13.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Unesite EAN13!", "Greška", JOptionPane.ERROR_MESSAGE);
            return "";
        }

        try {
            // 1. Pronalazak informacije o proizvodu na osnovu EAN13
            System.out.println("Izabrani EAN13 kod: " + ean13);
            URL batchUrl = new URL("http://localhost:8080/api/products/by-ean13/" + ean13); // Putanja sa PathVariable
            HttpURLConnection batchConnection = (HttpURLConnection) batchUrl.openConnection();
            batchConnection.setRequestMethod("GET");
            batchConnection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int batchResponseCode = batchConnection.getResponseCode();
            if (batchResponseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(batchConnection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("Odgovor API-ja: " + response);

                    // Pretpostavljamo da API vraća podatke u JSON formatu, uključujući ime proizvoda
                    Product product = objectMapper.readValue(response, Product.class);

                    setProductPrice(product.getSellingPrice()); // Postavi cenu proizvoda

                    // Vraćamo ime proizvoda direktno
                    return product.getName(); // Vraćamo ime proizvoda iz odgovora
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška API-ja za batch: " + batchResponseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }

        return ""; // Ako se dogodi greška, vraćamo praznu vrednost
    }

    private void resetFields() {
        articlePaper.setText("");
        ammountPaper.setValue(1); // Postavi količinu na podrazumevanu vrednost (npr. 1)
        discount.setText(""); // Reset popusta
        updateDosageDropdown(articlePaper.getText());
    }

    private void enterPaperMouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) paperPrescriptiontbl.getModel();
        int rowCount = model.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            String ean13 = model.getValueAt(i, 1).toString(); // EAN13 je u drugoj koloni
            int productId = getProductIdByEan13(ean13); // Dobijamo productId na osnovu ean13

            if (productId != -1) {
                int productAmount = Integer.parseInt(model.getValueAt(i, 5).toString());
                int discount = Integer.parseInt(model.getValueAt(i, 4).toString().replace("%", ""));
                int prescriptionItem = 2; // Možeš prilagoditi ako postoji logika za recept

                fetchProductAndAddToTable(productId, ean13, productAmount, prescriptionItem, discount);
            }
        }

        // Opcioni reset tabele nakon obrade
        model.setRowCount(0);
        paperPrescription.setVisible(false);
    }

    private int getProductIdByEan13(String ean13) {
        try {
            URL url = new URL("http://localhost:8080/api/products/by-ean13/" + ean13);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    ProductDto product = objectMapper.readValue(response, ProductDto.class);

                    return product.getId(); // Pretpostavljam da ProductDto ima getId()
                }
            } else {
                System.out.println("Proizvod sa EAN13 " + ean13 + " nije pronađen.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Ako nije pronađen proizvod
    }

    private void usersPrescriptionsComponentShown(ComponentEvent e) {
        fillPrescriptionTable();
    }

    private void paymentMouseClicked(MouseEvent e) {
        setFinalPrice(Double.parseDouble(totalPrice.getText()));

        // Reset lista ako je već korišćena ranije
        receiptList.clear();

        // Dobijanje modela tabele
        DefaultTableModel model = (DefaultTableModel) items.getModel();
        int rowCount = model.getRowCount();

        // Iteracija kroz redove i dodavanje u listu
        for (int i = 0; i < rowCount; i++) {
            Map<String, Object> stavka = new HashMap<>();
            stavka.put("Naziv proizvoda", model.getValueAt(i, 0));
            stavka.put("Ean13", model.getValueAt(i, 1));
            stavka.put("Recept", model.getValueAt(i, 2));
            stavka.put("Cena po jedinici", model.getValueAt(i, 3));
            stavka.put("Popust", model.getValueAt(i, 4));
            stavka.put("Količina", model.getValueAt(i, 5));
            stavka.put("Ukupna cena", model.getValueAt(i, 6));

            receiptList.add(stavka);
        }

        paymentType.setVisible(true);
    }

    private void nextPayingMouseClicked(MouseEvent e) {
        if (cash.isSelected()) {
            paymentType.setVisible(false);
            cashPayment.setVisible(true);
        }
        if (card.isSelected()) {
            paymentType.setVisible(false);
            cardPayment.setVisible(true);
        }
    }

    private void cashPaymentComponentShown(ComponentEvent e) {
        // Postavljamo ukupnu cenu u totalPriceFinal polje
        String finalPriceString = String.valueOf(getFinalPrice());
        totalPriceFinal.setText(finalPriceString);

        // Dodajemo listener na payed JTextField
        payed.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateChange();
            }
        });
    }

    private void calculateChange() {
        try {
            double finalPrice = Double.parseDouble(totalPriceFinal.getText());
            double amountPaid = Double.parseDouble(payed.getText());

            if (amountPaid >= finalPrice) {
                double changeAmount = amountPaid - finalPrice;
                change.setText(String.format("%.2f", changeAmount));
            } else {
                change.setText("");
            }
        } catch (NumberFormatException ex) {
            change.setText(""); // Ako se unesu nevalidni podaci (slova, prazno), brišemo kusur
        }
    }

    private void finishCashMouseClicked(MouseEvent e) {
        double changeAmount = 0;
        double finalPrice = 0;
        try {
            finalPrice = Double.parseDouble(totalPriceFinal.getText()); // Ukupna cena
            double amountPaid = Double.parseDouble(payed.getText()); // Plaćeni iznos
            changeAmount = Double.parseDouble(change.getText());

            String message = String.format(
                    "Ukupna cena: %.2f RSD\nPlaćeno: %.2f RSD\nVraćen kusur: %.2f RSD",
                    finalPrice, amountPaid, changeAmount
            );

            JOptionPane.showMessageDialog(this, message, "Račun", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Unesite validne iznose!", "Greška", JOptionPane.ERROR_MESSAGE);
        }

        change.setText("");
        cashPayment.setVisible(false);
        paymentProcess(finalPrice, changeAmount, 0);
        DefaultTableModel model = (DefaultTableModel) items.getModel();
        model.setRowCount(0);
        totalPrice.setText("");
    }

    private void finishCardMouseClicked(MouseEvent e) {
        double finalPrice = Double.parseDouble(totalPriceFinalCard.getText());
        String cardNum = cardNumber.getText();
        String pin = new String(cardPin.getPassword());
        if (cardNum.isEmpty() || pin.isEmpty() || !cardNum.matches("\\d{16}") || !pin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Molimo unesite ispravan broj kartice i PIN.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Provera kartice preko API-ja
        if (!validateCardWithAPI(cardNum, pin)) {
            JOptionPane.showMessageDialog(this, "Neispravna kartica ili PIN!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ako je sve u redu, prikazuje se potvrda plaćanja
        String message = String.format("Ukupna cena: %.2f RSD\nPlaćeno karticom: %s\nPlaćanje uspešno!",
                finalPrice, cardNum.substring(0, 4) + " **** **** " + cardNum.substring(cardNum.length() - 4));

        JOptionPane.showMessageDialog(this, message, "Plaćanje karticom", JOptionPane.INFORMATION_MESSAGE);

        // Reset tabele
        cardPayment.setVisible(false);
        paymentProcess(finalPrice, 0, 1);
        DefaultTableModel model = (DefaultTableModel) items.getModel();
        model.setRowCount(0);
        totalPrice.setText("");
    }

    private boolean validateCardWithAPI(String cardNum, String pin) {
        try {
            URL url = new URL("http://localhost:8080/api/cards/validate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"cardNumber\":\"%s\", \"pin\":\"%s\"}", cardNum, pin);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            return responseCode == 200;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void cardPaymentComponentShown(ComponentEvent e) {
        String finalPriceString = String.valueOf(getFinalPrice());
        totalPriceFinalCard.setText(finalPriceString);
    }

    private void itemsMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) { // Dupli klik
            JTable table = (JTable) e.getSource();
            int row = table.getSelectedRow();
            int column = table.getSelectedColumn();

            if (column == 5 && row != -1) { // Kolona "Količina"
                String requiresPrescription = table.getValueAt(row, 2).toString(); // Kolona "Recept"

                if (requiresPrescription.equalsIgnoreCase("E-Recept") || requiresPrescription.equalsIgnoreCase("Papirni recept")) {
                    JOptionPane.showMessageDialog(table, "Nije dozvoljeno menjanje količine za proizvode na recept!",
                            "Greška", JOptionPane.ERROR_MESSAGE);
                } else {
                    selectedRow = row; // Sačuvaj red koji se menja
                    setCurrentAmmount(table.getValueAt(row, column).toString());
                    newAmmount.setText(getCurrentAmmount()); // Prikaz u polju
                    changeAmmount.setVisible(true); // Prikaži modal
                }
            }
        }
    }

    private void finishAmmountMouseClicked(MouseEvent e) {
        if (selectedRow != -1) { // Provera da li je neki red izabran
            try {
                int newAmount = Integer.parseInt(newAmmount.getText());

                if (newAmount > 0) {
                    DefaultTableModel model = (DefaultTableModel) items.getModel();

                    // Uzimamo staru cenu kako bismo mogli da korigujemo ukupnu sumu
                    double oldTotalPrice = Double.parseDouble(model.getValueAt(selectedRow, 6).toString());

                    // Postavljanje nove količine
                    model.setValueAt(newAmount, selectedRow, 5);

                    // Preračunaj ukupnu cenu
                    double unitPrice = Double.parseDouble(model.getValueAt(selectedRow, 3).toString());
                    double discount = Double.parseDouble(model.getValueAt(selectedRow, 4).toString().replace("%", ""));
                    double finalPrice = unitPrice - (unitPrice * (discount / 100.0));
                    double totalPrice = newAmount * finalPrice;

                    // Postavi novu ukupnu cenu
                    model.setValueAt(totalPrice, selectedRow, 6);

                    // Ažuriraj ukupnu cenu (oduzmi staru i dodaj novu vrednost)
                    updateTotalPrice(totalPrice - oldTotalPrice);

                    // Zatvori modal
                    changeAmmount.dispose();
                } else {
                    JOptionPane.showMessageDialog(changeAmmount, "Unesite validnu količinu!", "Greška", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(changeAmmount, "Unesite broj!", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void paymentProcess(double finalPrice, double finalChange, int paymentType) {
        int salesId = addSalesData(finalPrice, finalChange, paymentType);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        article.setText("");
        if (salesId != -1) {
            addSalesItems(salesId);
        }
    }

    private void addSalesItems(int salesId) {
        for (Map<String, Object> stavka : receiptList) {
            try {
                // Dobijanje podataka iz stavke
                String ean13 = (String) stavka.get("Ean13");
                int productId = getProductIdByEan13(ean13);
                String receiptType = (String) stavka.get("Recept");
                int quantity = ((Number) stavka.get("Količina")).intValue();
                double totalPrice = ((Number) stavka.get("Ukupna cena")).doubleValue();

                if (productId != -1) {
                    // Kreiranje JSON objekta
                    String jsonInputString = String.format(
                            "{\"salesId\": %d, \"product_batch_id\": %d, \"receiptType\": \"%s\", \"quantity\": %d, \"totalPrice\": %.2f}",
                            salesId, productId, receiptType, quantity, totalPrice
                    );

                    // Slanje HTTP POST zahteva
                    URL url = new URL("http://localhost:8080/api/sales_items/add");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
                    connection.setDoOutput(true);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        System.out.println("Stavka uspešno dodata: " + jsonInputString);
                    } else {
                        System.out.println("Greška pri dodavanju stavke: " + jsonInputString);
                    }
                } else {
                    System.out.println("Nije pronađen proizvod sa EAN13: " + ean13);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Greška pri komunikaciji sa serverom.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int addSalesData(double totalPrice, double receipt, int paymentType) {
        int employeeId = JwtResponse.getUserId();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        String payment_Type = "";

        if (paymentType == 0) {
            payment_Type = "Cash";
        } else if (paymentType == 1) {
            payment_Type = "Card";
        }
        System.out.println("Change ammount: " + receipt);

        try {
            URL url = new URL("http://localhost:8080/api/sales/add");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
            connection.setDoOutput(true);

            // JSON telo zahteva
            String jsonInputString = String.format(
                    "{\"totalPrice\": %.2f, \"change\": %.2f, \"transactionDate\": \"%s\", \"transactionTime\": \"%s\", \"employeeId\": %d, \"paymentType\": \"%s\"}",
                    totalPrice, receipt, currentDate, formattedTime, employeeId, payment_Type
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
                    int salesId = jsonResponse.getInt("id");

                    String employeeName = fetchEmployeeName(employeeId);

                    // Generate PDF receipt
                    generateReceipt(
                            salesId,
                            currentDate,
                            currentTime,
                            payment_Type,
                            totalPrice,
                            receipt,
                            receiptList, // List of items purchased
                            employeeName // Employee name
                    );

                    return salesId;
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

    public static void generateReceipt(int salesId, LocalDate transactionDate, LocalTime transactionTime,
                                       String paymentType, double totalPrice, double receiptChange,
                                       List<Map<String, Object>> items, String employeeName) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Učitavanje fonta
                PDType0Font font = null;
                try (InputStream fontStream = new ClassPathResource("static/arial.ttf").getInputStream()) {
                    font = PDType0Font.load(document, fontStream);
                }

                contentStream.setFont(font, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);

                // Naslov računa
                contentStream.showText("===== FISKALNI RAČUN =====");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("ID: " + salesId);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Datum: " + transactionDate);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Vreme: " + transactionTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Blagajnik: " + employeeName);
                contentStream.newLineAtOffset(0, -20);
                if (paymentType.equals("Cash")) {
                    paymentType = "Gotovina";
                }
                if (paymentType.equals("Card")) {
                    paymentType = "Kreditna Kartica";
                }
                contentStream.showText("Način plaćanja: " + paymentType);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("==========================");
                contentStream.newLineAtOffset(0, -30);

                // Tabela proizvoda
                contentStream.showText(String.format("%-20s %-10s %-10s %-10s %-10s", "Proizvod", "Količina", "Cena", "Popust", "Ukupno"));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("------------------------------------------------");
                contentStream.newLineAtOffset(0, -15);

                for (Map<String, Object> item : items) {
                    String name = (String) item.get("Naziv proizvoda");
                    int quantity = (int) item.get("Količina");
                    double price = (double) item.get("Cena po jedinici");
                    double totalItemPrice = (double) item.get("Ukupna cena");

                    // Popust kao string sa %
                    String discount = item.get("Popust") + "%";

                    // Formatiran prikaz podataka
                    contentStream.showText(String.format("%-20s %-10d %-10.2f %-10s %-10.2f",
                            name, quantity, price, discount, totalItemPrice));
                    contentStream.newLineAtOffset(0, -15);
                }

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("==========================");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText(String.format("Ukupno: %.2f RSD", totalPrice));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText(String.format("Povraćaj: %.2f RSD", receiptChange));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("==========================");

                contentStream.endText();
            }

            // Kreiranje foldera za čuvanje računa
            String folderPath = "receipts/" + transactionDate;
            Files.createDirectories(Paths.get(folderPath));
            String filePath = folderPath + "/Receipt_" + salesId + ".pdf";

            // Čuvanje dokumenta
            document.save(new File(filePath));
            System.out.println("Račun sačuvan: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void salesMouseClicked(MouseEvent e) {
        dispose();
        SalesHistory.start();
    }

    private void backMouseClicked(MouseEvent e) {
        dispose();
        MainMenuAdmin.start();
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
        JViewport viewport1 = scrollPane1.getViewport();
        viewport1.setBackground(backgroundColor);
        scrollPane1.setBackground(backgroundColor);

        JViewport viewport2 = scrollPane2.getViewport();
        viewport2.setBackground(backgroundColor);
        scrollPane2.setBackground(backgroundColor);

        JViewport viewport3 = scrollPane3.getViewport();
        viewport3.setBackground(backgroundColor);
        scrollPane3.setBackground(backgroundColor);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        back = new JButton();
        scrollPane1 = new JScrollPane();
        items = new JTable();
        sales = new JButton();
        article_label = new JLabel();
        article = new JTextField();
        amm_label = new JLabel();
        ammount = new JSpinner();
        add = new JButton();
        prescriptions = new JButton();
        totalPrice = new JTextField();
        payment = new JButton();
        choosePrescription = new JDialog();
        panel4 = new JPanel();
        question1 = new JLabel();
        electronic = new JRadioButton();
        paper = new JRadioButton();
        next = new JButton();
        electronicPrescription = new JDialog();
        panel3 = new JPanel();
        back2 = new JButton();
        question2 = new JLabel();
        healthCard = new JTextField();
        nextER = new JButton();
        usersPrescriptions = new JDialog();
        panel5 = new JPanel();
        back3 = new JButton();
        prescriptionsCB = new JComboBox();
        scrollPane2 = new JScrollPane();
        usersPrescriptionstbl = new JTable();
        selectPrescription = new JButton();
        paperPrescription = new JDialog();
        panel2 = new JPanel();
        article_label2 = new JLabel();
        articlePaper = new JTextField();
        back4 = new JButton();
        scrollPane3 = new JScrollPane();
        paperPrescriptiontbl = new JTable();
        amm_label2 = new JLabel();
        ammountPaper = new JSpinner();
        dosagelbl = new JLabel();
        dosage = new JComboBox();
        label1 = new JLabel();
        discount = new JTextField();
        label2 = new JLabel();
        addPaper = new JButton();
        totalPricePaper = new JTextField();
        enterPaper = new JButton();
        popupMenu = new JPopupMenu();
        paymentType = new JDialog();
        panel6 = new JPanel();
        label3 = new JLabel();
        cash = new JRadioButton();
        card = new JRadioButton();
        nextPaying = new JButton();
        cashPayment = new JDialog();
        panel7 = new JPanel();
        label4 = new JLabel();
        totalPriceFinal = new JTextField();
        label5 = new JLabel();
        payed = new JTextField();
        label6 = new JLabel();
        change = new JTextField();
        finishCash = new JButton();
        cardPayment = new JDialog();
        panel8 = new JPanel();
        label7 = new JLabel();
        totalPriceFinalCard = new JTextField();
        label8 = new JLabel();
        cardNumber = new JTextField();
        label9 = new JLabel();
        cardPin = new JPasswordField();
        finishCard = new JButton();
        changeAmmount = new JDialog();
        panel9 = new JPanel();
        label10 = new JLabel();
        newAmmount = new JTextField();
        finishAmmount = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setPreferredSize(new Dimension(850, 909));
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
                "[fill]",
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
            panel1.add(back, "cell 7 0");

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
                items.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        itemsMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(items);
            }
            panel1.add(scrollPane1, "cell 0 1 5 7");

            //---- sales ----
            sales.setIcon(UIManager.getIcon("FileView.directoryIcon"));
            sales.setBackground(new Color(0xb3d8a8));
            sales.setForeground(Color.darkGray);
            sales.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    salesMouseClicked(e);
                }
            });
            panel1.add(sales, "cell 7 1");

            //---- article_label ----
            article_label.setText("Artikal:");
            article_label.setForeground(new Color(0xfbffe4));
            panel1.add(article_label, "cell 5 2");

            //---- article ----
            article.setBackground(new Color(0xb3d8a8));
            article.setForeground(Color.darkGray);
            panel1.add(article, "cell 5 3 2 1");

            //---- amm_label ----
            amm_label.setText("Kolicina:");
            amm_label.setForeground(new Color(0xfbffe4));
            panel1.add(amm_label, "cell 5 4");

            //---- ammount ----
            ammount.setBackground(new Color(0xb3d8a8));
            ammount.setForeground(Color.darkGray);
            panel1.add(ammount, "cell 6 4");

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
            panel1.add(add, "cell 5 6");

            //---- prescriptions ----
            prescriptions.setText("Recepti");
            prescriptions.setBackground(new Color(0xb3d8a8));
            prescriptions.setForeground(Color.darkGray);
            prescriptions.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    prescriptionsMouseClicked(e);
                }
            });
            panel1.add(prescriptions, "cell 6 6");

            //---- totalPrice ----
            totalPrice.setBackground(new Color(0xb3d8a8));
            totalPrice.setForeground(Color.darkGray);
            panel1.add(totalPrice, "cell 4 8");

            //---- payment ----
            payment.setText("Placanje");
            payment.setBackground(new Color(0xb3d8a8));
            payment.setForeground(Color.darkGray);
            payment.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    paymentMouseClicked(e);
                }
            });
            panel1.add(payment, "cell 6 9");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 853, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());

        //======== choosePrescription ========
        {
            choosePrescription.setModal(true);
            var choosePrescriptionContentPane = choosePrescription.getContentPane();

            //======== panel4 ========
            {
                panel4.setBackground(new Color(0x3d8d7a));
                panel4.setLayout(new MigLayout(
                    "insets 0,hidemode 3",
                    // columns
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]",
                    // rows
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]"));

                //---- question1 ----
                question1.setText("Odaberite vrstu recepta:");
                question1.setForeground(new Color(0xfbffe4));
                panel4.add(question1, "cell 1 1 2 1");

                //---- electronic ----
                electronic.setText("E-recept");
                electronic.setSelected(true);
                electronic.setForeground(Color.darkGray);
                electronic.setBackground(new Color(0x3d8d7a));
                panel4.add(electronic, "cell 1 2");

                //---- paper ----
                paper.setText("Papirni");
                paper.setForeground(Color.darkGray);
                paper.setBackground(new Color(0x3d8d7a));
                panel4.add(paper, "cell 1 3");

                //---- next ----
                next.setText("Dalje");
                next.setBackground(new Color(0xb3d8a8));
                next.setForeground(Color.darkGray);
                next.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        nextMouseClicked(e);
                    }
                });
                panel4.add(next, "cell 2 4");
            }

            GroupLayout choosePrescriptionContentPaneLayout = new GroupLayout(choosePrescriptionContentPane);
            choosePrescriptionContentPane.setLayout(choosePrescriptionContentPaneLayout);
            choosePrescriptionContentPaneLayout.setHorizontalGroup(
                choosePrescriptionContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, choosePrescriptionContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel4, GroupLayout.PREFERRED_SIZE, 390, GroupLayout.PREFERRED_SIZE))
            );
            choosePrescriptionContentPaneLayout.setVerticalGroup(
                choosePrescriptionContentPaneLayout.createParallelGroup()
                    .addGroup(choosePrescriptionContentPaneLayout.createSequentialGroup()
                        .addComponent(panel4, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
            );
            choosePrescription.pack();
            choosePrescription.setLocationRelativeTo(choosePrescription.getOwner());
        }

        //======== electronicPrescription ========
        {
            electronicPrescription.setModal(true);
            var electronicPrescriptionContentPane = electronicPrescription.getContentPane();

            //======== panel3 ========
            {
                panel3.setBackground(new Color(0x3d8d7a));
                panel3.setLayout(new MigLayout(
                    "insets 0,hidemode 3",
                    // columns
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]",
                    // rows
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]"));

                //---- back2 ----
                back2.setText("Nazad");
                back2.setBackground(new Color(0xb3d8a8));
                back2.setForeground(Color.darkGray);
                panel3.add(back2, "cell 0 0");

                //---- question2 ----
                question2.setText("Unesite broj zdravstvene kartice:");
                question2.setForeground(new Color(0xfbffe4));
                panel3.add(question2, "cell 1 1 2 1");

                //---- healthCard ----
                healthCard.setBackground(new Color(0xb3d8a8));
                healthCard.setForeground(Color.darkGray);
                panel3.add(healthCard, "cell 1 2 2 1");

                //---- nextER ----
                nextER.setText("Dalje");
                nextER.setBackground(new Color(0xb3d8a8));
                nextER.setForeground(Color.darkGray);
                nextER.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        nextERMouseClicked(e);
                    }
                });
                panel3.add(nextER, "cell 2 4");
            }

            GroupLayout electronicPrescriptionContentPaneLayout = new GroupLayout(electronicPrescriptionContentPane);
            electronicPrescriptionContentPane.setLayout(electronicPrescriptionContentPaneLayout);
            electronicPrescriptionContentPaneLayout.setHorizontalGroup(
                electronicPrescriptionContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, electronicPrescriptionContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel3, GroupLayout.PREFERRED_SIZE, 390, GroupLayout.PREFERRED_SIZE))
            );
            electronicPrescriptionContentPaneLayout.setVerticalGroup(
                electronicPrescriptionContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, electronicPrescriptionContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel3, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE))
            );
            electronicPrescription.pack();
            electronicPrescription.setLocationRelativeTo(electronicPrescription.getOwner());
        }

        //======== usersPrescriptions ========
        {
            usersPrescriptions.setModal(true);
            usersPrescriptions.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    usersPrescriptionsComponentShown(e);
                }
            });
            var usersPrescriptionsContentPane = usersPrescriptions.getContentPane();

            //======== panel5 ========
            {
                panel5.setBackground(new Color(0x3d8d7a));
                panel5.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]",
                    // rows
                    "[45]" +
                    "[45]" +
                    "[45]" +
                    "[45]" +
                    "[45]" +
                    "[45]" +
                    "[]" +
                    "[45]"));

                //---- back3 ----
                back3.setText("Nazad");
                back3.setBackground(new Color(0xb3d8a8));
                back3.setForeground(Color.darkGray);
                panel5.add(back3, "cell 0 0");

                //---- prescriptionsCB ----
                prescriptionsCB.setBackground(new Color(0xb3d8a8));
                prescriptionsCB.setForeground(Color.darkGray);
                prescriptionsCB.addItemListener(e -> prescriptionsCBItemStateChanged(e));
                panel5.add(prescriptionsCB, "cell 2 0 2 1");

                //======== scrollPane2 ========
                {
                    scrollPane2.setBackground(Color.darkGray);
                    scrollPane2.setForeground(Color.darkGray);

                    //---- usersPrescriptionstbl ----
                    usersPrescriptionstbl.setBackground(new Color(0xfbffe4));
                    usersPrescriptionstbl.setForeground(Color.darkGray);
                    usersPrescriptionstbl.setGridColor(Color.darkGray);
                    usersPrescriptionstbl.setSelectionBackground(new Color(0xb3d8a8));
                    usersPrescriptionstbl.setSelectionForeground(Color.darkGray);
                    scrollPane2.setViewportView(usersPrescriptionstbl);
                }
                panel5.add(scrollPane2, "cell 0 2 6 5");

                //---- selectPrescription ----
                selectPrescription.setText("Dodaj");
                selectPrescription.setBackground(new Color(0xb3d8a8));
                selectPrescription.setForeground(Color.darkGray);
                selectPrescription.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectPrescriptionMouseClicked(e);
                    }
                });
                panel5.add(selectPrescription, "cell 4 7");
            }

            GroupLayout usersPrescriptionsContentPaneLayout = new GroupLayout(usersPrescriptionsContentPane);
            usersPrescriptionsContentPane.setLayout(usersPrescriptionsContentPaneLayout);
            usersPrescriptionsContentPaneLayout.setHorizontalGroup(
                usersPrescriptionsContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, usersPrescriptionsContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel5, GroupLayout.PREFERRED_SIZE, 610, GroupLayout.PREFERRED_SIZE))
            );
            usersPrescriptionsContentPaneLayout.setVerticalGroup(
                usersPrescriptionsContentPaneLayout.createParallelGroup()
                    .addGroup(usersPrescriptionsContentPaneLayout.createSequentialGroup()
                        .addComponent(panel5, GroupLayout.PREFERRED_SIZE, 365, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
            );
            usersPrescriptions.pack();
            usersPrescriptions.setLocationRelativeTo(usersPrescriptions.getOwner());
        }

        //======== paperPrescription ========
        {
            paperPrescription.setModal(true);
            var paperPrescriptionContentPane = paperPrescription.getContentPane();

            //======== panel2 ========
            {
                panel2.setPreferredSize(new Dimension(850, 909));
                panel2.setBackground(new Color(0x3d8d7a));
                panel2.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[fill]",
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
                    "[75,fill]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]"));

                //---- article_label2 ----
                article_label2.setText("Artikal:");
                article_label2.setForeground(new Color(0xfbffe4));
                panel2.add(article_label2, "cell 1 0");

                //---- articlePaper ----
                articlePaper.setBackground(new Color(0xb3d8a8));
                articlePaper.setForeground(Color.darkGray);
                panel2.add(articlePaper, "cell 1 1 4 1");

                //---- back4 ----
                back4.setText("Nazad");
                back4.setBackground(new Color(0xb3d8a8));
                back4.setForeground(Color.darkGray);
                panel2.add(back4, "cell 7 1");

                //======== scrollPane3 ========
                {
                    scrollPane3.setBackground(Color.darkGray);
                    scrollPane3.setForeground(Color.darkGray);

                    //---- paperPrescriptiontbl ----
                    paperPrescriptiontbl.setBackground(new Color(0xfbffe4));
                    paperPrescriptiontbl.setForeground(Color.darkGray);
                    paperPrescriptiontbl.setGridColor(Color.darkGray);
                    paperPrescriptiontbl.setSelectionBackground(new Color(0xb3d8a8));
                    paperPrescriptiontbl.setSelectionForeground(Color.darkGray);
                    scrollPane3.setViewportView(paperPrescriptiontbl);
                }
                panel2.add(scrollPane3, "cell 0 3 5 7");

                //---- amm_label2 ----
                amm_label2.setText("Kolicina:");
                amm_label2.setForeground(new Color(0xfbffe4));
                panel2.add(amm_label2, "cell 5 4");

                //---- ammountPaper ----
                ammountPaper.setBackground(new Color(0xb3d8a8));
                ammountPaper.setForeground(Color.darkGray);
                panel2.add(ammountPaper, "cell 6 4");

                //---- dosagelbl ----
                dosagelbl.setText("Doza:");
                dosagelbl.setForeground(new Color(0xfbffe4));
                panel2.add(dosagelbl, "cell 5 5");

                //---- dosage ----
                dosage.setBackground(new Color(0xb3d8a8));
                dosage.setForeground(Color.darkGray);
                panel2.add(dosage, "cell 6 5");

                //---- label1 ----
                label1.setText("Popust:");
                label1.setForeground(new Color(0xfbffe4));
                panel2.add(label1, "cell 5 6");

                //---- discount ----
                discount.setBackground(new Color(0xb3d8a8));
                discount.setForeground(Color.darkGray);
                panel2.add(discount, "cell 6 6");

                //---- label2 ----
                label2.setText("%");
                label2.setForeground(new Color(0xfbffe4));
                panel2.add(label2, "cell 7 6");

                //---- addPaper ----
                addPaper.setText("Dodaj");
                addPaper.setBackground(new Color(0xb3d8a8));
                addPaper.setForeground(Color.darkGray);
                addPaper.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        addPaperMouseClicked(e);
                    }
                });
                panel2.add(addPaper, "cell 5 8");

                //---- totalPricePaper ----
                totalPricePaper.setBackground(new Color(0xb3d8a8));
                totalPricePaper.setForeground(Color.darkGray);
                panel2.add(totalPricePaper, "cell 4 10");

                //---- enterPaper ----
                enterPaper.setText("Unesi");
                enterPaper.setBackground(new Color(0xb3d8a8));
                enterPaper.setForeground(Color.darkGray);
                enterPaper.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        enterPaperMouseClicked(e);
                    }
                });
                panel2.add(enterPaper, "cell 6 10");
            }

            GroupLayout paperPrescriptionContentPaneLayout = new GroupLayout(paperPrescriptionContentPane);
            paperPrescriptionContentPane.setLayout(paperPrescriptionContentPaneLayout);
            paperPrescriptionContentPaneLayout.setHorizontalGroup(
                paperPrescriptionContentPaneLayout.createParallelGroup()
                    .addComponent(panel2, GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)
            );
            paperPrescriptionContentPaneLayout.setVerticalGroup(
                paperPrescriptionContentPaneLayout.createParallelGroup()
                    .addComponent(panel2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
            );
            paperPrescription.pack();
            paperPrescription.setLocationRelativeTo(paperPrescription.getOwner());
        }

        //======== popupMenu ========
        {
            popupMenu.setBackground(new Color(0xb3d8a8));
            popupMenu.setForeground(Color.darkGray);
        }

        //======== paymentType ========
        {
            paymentType.setModal(true);
            var paymentTypeContentPane = paymentType.getContentPane();

            //======== panel6 ========
            {
                panel6.setBackground(new Color(0x3d8d7a));
                panel6.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]",
                    // rows
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]"));

                //---- label3 ----
                label3.setText("Odaberite tip placanja:");
                label3.setForeground(new Color(0xfbffe4));
                panel6.add(label3, "cell 1 1 2 1");

                //---- cash ----
                cash.setText("Gotovina");
                cash.setSelected(true);
                cash.setBackground(new Color(0x3d8d7a));
                cash.setForeground(Color.darkGray);
                panel6.add(cash, "cell 1 2");

                //---- card ----
                card.setText("Kreditna kartica");
                card.setBackground(new Color(0x3d8d7a));
                card.setForeground(Color.darkGray);
                panel6.add(card, "cell 1 3 2 1");

                //---- nextPaying ----
                nextPaying.setText("Dalje");
                nextPaying.setBackground(new Color(0xb3d8a8));
                nextPaying.setForeground(Color.darkGray);
                nextPaying.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        nextPayingMouseClicked(e);
                    }
                });
                panel6.add(nextPaying, "cell 2 4");
            }

            GroupLayout paymentTypeContentPaneLayout = new GroupLayout(paymentTypeContentPane);
            paymentTypeContentPane.setLayout(paymentTypeContentPaneLayout);
            paymentTypeContentPaneLayout.setHorizontalGroup(
                paymentTypeContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, paymentTypeContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel6, GroupLayout.PREFERRED_SIZE, 395, GroupLayout.PREFERRED_SIZE))
            );
            paymentTypeContentPaneLayout.setVerticalGroup(
                paymentTypeContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, paymentTypeContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel6, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE))
            );
            paymentType.pack();
            paymentType.setLocationRelativeTo(paymentType.getOwner());
        }

        //======== cashPayment ========
        {
            cashPayment.setModal(true);
            cashPayment.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    cashPaymentComponentShown(e);
                }
            });
            var cashPaymentContentPane = cashPayment.getContentPane();

            //======== panel7 ========
            {
                panel7.setBackground(new Color(0x3d8d7a));
                panel7.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]",
                    // rows
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]"));

                //---- label4 ----
                label4.setText("Ukupna cena:");
                label4.setForeground(new Color(0xfbffe4));
                panel7.add(label4, "cell 1 1");

                //---- totalPriceFinal ----
                totalPriceFinal.setEditable(false);
                totalPriceFinal.setBackground(new Color(0xb3d8a8));
                totalPriceFinal.setForeground(Color.darkGray);
                panel7.add(totalPriceFinal, "cell 2 1");

                //---- label5 ----
                label5.setText("Placeno:");
                label5.setForeground(new Color(0xfbffe4));
                panel7.add(label5, "cell 1 2");

                //---- payed ----
                payed.setBackground(new Color(0xb3d8a8));
                payed.setForeground(Color.darkGray);
                panel7.add(payed, "cell 2 2");

                //---- label6 ----
                label6.setText("Kusur:");
                label6.setForeground(new Color(0xfbffe4));
                panel7.add(label6, "cell 1 3");

                //---- change ----
                change.setEditable(false);
                change.setBackground(new Color(0xb3d8a8));
                change.setForeground(Color.darkGray);
                panel7.add(change, "cell 2 3");

                //---- finishCash ----
                finishCash.setText("Zavrsi");
                finishCash.setBackground(new Color(0xb3d8a8));
                finishCash.setForeground(Color.darkGray);
                finishCash.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        finishCashMouseClicked(e);
                    }
                });
                panel7.add(finishCash, "cell 2 5");
            }

            GroupLayout cashPaymentContentPaneLayout = new GroupLayout(cashPaymentContentPane);
            cashPaymentContentPane.setLayout(cashPaymentContentPaneLayout);
            cashPaymentContentPaneLayout.setHorizontalGroup(
                cashPaymentContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, cashPaymentContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel7, GroupLayout.PREFERRED_SIZE, 395, GroupLayout.PREFERRED_SIZE))
            );
            cashPaymentContentPaneLayout.setVerticalGroup(
                cashPaymentContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, cashPaymentContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel7, GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE))
            );
            cashPayment.pack();
            cashPayment.setLocationRelativeTo(cashPayment.getOwner());
        }

        //======== cardPayment ========
        {
            cardPayment.setModal(true);
            cardPayment.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    cardPaymentComponentShown(e);
                }
            });
            var cardPaymentContentPane = cardPayment.getContentPane();

            //======== panel8 ========
            {
                panel8.setBackground(new Color(0x3d8d7a));
                panel8.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[fill]",
                    // rows
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]"));

                //---- label7 ----
                label7.setText("Ukupna cena:");
                label7.setForeground(new Color(0xfbffe4));
                panel8.add(label7, "cell 1 2");

                //---- totalPriceFinalCard ----
                totalPriceFinalCard.setEditable(false);
                totalPriceFinalCard.setBackground(new Color(0xb3d8a8));
                totalPriceFinalCard.setForeground(Color.darkGray);
                panel8.add(totalPriceFinalCard, "cell 2 2");

                //---- label8 ----
                label8.setText("Broj kartice:");
                label8.setForeground(new Color(0xfbffe4));
                panel8.add(label8, "cell 1 3");

                //---- cardNumber ----
                cardNumber.setBackground(new Color(0xb3d8a8));
                cardNumber.setForeground(Color.darkGray);
                panel8.add(cardNumber, "cell 2 3 3 1");

                //---- label9 ----
                label9.setText("PIN:");
                label9.setForeground(new Color(0xfbffe4));
                panel8.add(label9, "cell 1 4");

                //---- cardPin ----
                cardPin.setBackground(new Color(0xb3d8a8));
                cardPin.setForeground(Color.darkGray);
                panel8.add(cardPin, "cell 2 4");

                //---- finishCard ----
                finishCard.setText("Zavrsi");
                finishCard.setBackground(new Color(0xb3d8a8));
                finishCard.setForeground(Color.darkGray);
                finishCard.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        finishCardMouseClicked(e);
                    }
                });
                panel8.add(finishCard, "cell 4 5");
            }

            GroupLayout cardPaymentContentPaneLayout = new GroupLayout(cardPaymentContentPane);
            cardPaymentContentPane.setLayout(cardPaymentContentPaneLayout);
            cardPaymentContentPaneLayout.setHorizontalGroup(
                cardPaymentContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, cardPaymentContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel8, GroupLayout.PREFERRED_SIZE, 570, GroupLayout.PREFERRED_SIZE))
            );
            cardPaymentContentPaneLayout.setVerticalGroup(
                cardPaymentContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, cardPaymentContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel8, GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE))
            );
            cardPayment.pack();
            cardPayment.setLocationRelativeTo(cardPayment.getOwner());
        }

        //======== changeAmmount ========
        {
            changeAmmount.setModal(true);
            changeAmmount.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    cashPaymentComponentShown(e);
                }
            });
            var changeAmmountContentPane = changeAmmount.getContentPane();

            //======== panel9 ========
            {
                panel9.setBackground(new Color(0x3d8d7a));
                panel9.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]" +
                    "[100,fill]",
                    // rows
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]" +
                    "[40]"));

                //---- label10 ----
                label10.setText("Unesite novu kolicinu:");
                label10.setForeground(new Color(0xfbffe4));
                panel9.add(label10, "cell 1 1 2 1");

                //---- newAmmount ----
                newAmmount.setBackground(new Color(0xb3d8a8));
                newAmmount.setForeground(Color.darkGray);
                panel9.add(newAmmount, "cell 1 2 2 1");

                //---- finishAmmount ----
                finishAmmount.setText("Zavrsi");
                finishAmmount.setBackground(new Color(0xb3d8a8));
                finishAmmount.setForeground(Color.darkGray);
                finishAmmount.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        finishAmmountMouseClicked(e);
                    }
                });
                panel9.add(finishAmmount, "cell 2 4");
            }

            GroupLayout changeAmmountContentPaneLayout = new GroupLayout(changeAmmountContentPane);
            changeAmmountContentPane.setLayout(changeAmmountContentPaneLayout);
            changeAmmountContentPaneLayout.setHorizontalGroup(
                changeAmmountContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, changeAmmountContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel9, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE))
            );
            changeAmmountContentPaneLayout.setVerticalGroup(
                changeAmmountContentPaneLayout.createParallelGroup()
                    .addGroup(changeAmmountContentPaneLayout.createSequentialGroup()
                        .addComponent(panel9, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
            );
            changeAmmount.pack();
            changeAmmount.setLocationRelativeTo(changeAmmount.getOwner());
        }

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(electronic);
        buttonGroup1.add(paper);

        //---- buttonGroup2 ----
        var buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(cash);
        buttonGroup2.add(card);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JButton back;
    private JScrollPane scrollPane1;
    private JTable items;
    private JButton sales;
    private JLabel article_label;
    private JTextField article;
    private JLabel amm_label;
    private JSpinner ammount;
    private JButton add;
    private JButton prescriptions;
    private JTextField totalPrice;
    private JButton payment;
    private JDialog choosePrescription;
    private JPanel panel4;
    private JLabel question1;
    private JRadioButton electronic;
    private JRadioButton paper;
    private JButton next;
    private JDialog electronicPrescription;
    private JPanel panel3;
    private JButton back2;
    private JLabel question2;
    private JTextField healthCard;
    private JButton nextER;
    private JDialog usersPrescriptions;
    private JPanel panel5;
    private JButton back3;
    private JComboBox prescriptionsCB;
    private JScrollPane scrollPane2;
    private JTable usersPrescriptionstbl;
    private JButton selectPrescription;
    private JDialog paperPrescription;
    private JPanel panel2;
    private JLabel article_label2;
    private JTextField articlePaper;
    private JButton back4;
    private JScrollPane scrollPane3;
    private JTable paperPrescriptiontbl;
    private JLabel amm_label2;
    private JSpinner ammountPaper;
    private JLabel dosagelbl;
    private JComboBox dosage;
    private JLabel label1;
    private JTextField discount;
    private JLabel label2;
    private JButton addPaper;
    private JTextField totalPricePaper;
    private JButton enterPaper;
    private JPopupMenu popupMenu;
    private JDialog paymentType;
    private JPanel panel6;
    private JLabel label3;
    private JRadioButton cash;
    private JRadioButton card;
    private JButton nextPaying;
    private JDialog cashPayment;
    private JPanel panel7;
    private JLabel label4;
    private JTextField totalPriceFinal;
    private JLabel label5;
    private JTextField payed;
    private JLabel label6;
    private JTextField change;
    private JButton finishCash;
    private JDialog cardPayment;
    private JPanel panel8;
    private JLabel label7;
    private JTextField totalPriceFinalCard;
    private JLabel label8;
    private JTextField cardNumber;
    private JLabel label9;
    private JPasswordField cardPin;
    private JButton finishCard;
    private JDialog changeAmmount;
    private JPanel panel9;
    private JLabel label10;
    private JTextField newAmmount;
    private JButton finishAmmount;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private int selectedPrescriptionId;
    private double changeFinal;
    private double productPrice;
    private int userId;
    private double finalPrice;
    private List<Map<String, Object>> receiptList = new ArrayList<>();
    private String currentAmmount;
    private int selectedRow = -1;


    public int getSelectedPrescriptionId() {
        return selectedPrescriptionId;
    }

    public void setSelectedPrescriptionId(int selectedPrescriptionId) {
        this.selectedPrescriptionId = selectedPrescriptionId;
    }

    public double getChangeFinal() {
        return changeFinal;
    }

    public void setChangeFinal(double changeFinal) {
        this.changeFinal = changeFinal;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getCurrentAmmount() {
        return currentAmmount;
    }

    public void setCurrentAmmount(String currentAmmount) {
        this.currentAmmount = currentAmmount;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    public List<Map<String, Object>> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(List<Map<String, Object>> receiptList) {
        this.receiptList = receiptList;
    }
}