/*
 * Created by JFormDesigner on Sun Mar 16 03:06:16 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Suppliers.OrderSupplies;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.Dto.ProductDto;
import com.asss.www.ApotekarskaUstanova.Dto.SupplierDto;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin.MainMenuAdmin;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.*;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author lniko
 */
public class OrderSupplies extends JFrame {
    public OrderSupplies() {
        initComponents();
        setupListeners();
        loadSupplierCB();

        String[] columnNames = {"Proizvod", "Količina"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        items.setModel(model);
        customizeTable(items, model);

        String[] columnNames1 = {"Izaberi", "ID", "Naziv", "Doza", "Preostala Količina", "Minimalna Količina"};
        DefaultTableModel model1 = new DefaultTableModel(columnNames1, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        table1.setModel(model1);
        customizeTable(table1, model1);
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            OrderSupplies frame = new OrderSupplies();
            frame.setTitle("Inventory");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
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
                    List<SupplierDto> suppliers = objectMapper.readValue(response, new TypeReference<List<SupplierDto>>() {});

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
        String selectedSupplier = supplier.getSelectedItem().toString(); // "Ime - Telefon"
        String supplierName = selectedSupplier.split(" - ")[0]; // "Ime"
        int supplierId = getSupplierId(supplierName);

        if (supplierId <= 0) {
            JOptionPane.showMessageDialog(null, "Nevažeći dobavljač!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int orderId = addOrder(supplierId);

        System.out.println("prva provera order id: " + orderId);

        if (orderId != -1) {
            System.out.println("druga provera order id: " + orderId);
            System.out.println("pali se druga metoda");
            addOrderItems(orderId);
            JOptionPane.showMessageDialog(this, "Narudžbina je uspešno kreirana!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
            DefaultTableModel model = (DefaultTableModel) items.getModel();
            // Opcioni reset tabele nakon obrade
            model.setRowCount(0);
        } else {
            JOptionPane.showMessageDialog(this, "Problem sa unosom Nabavke", "Greska", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int addOrder(int supplierId) {
        LocalDate selectedDate = LocalDate.now();
        LocalTime selectedTime = LocalTime.now();

        String jwtToken = JwtResponse.getToken();  // Pretpostavljam da imate metodu koja vraća token

        if (jwtToken == null || jwtToken.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Niste ulogovani! Token nije dostupan.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        String url = "http://localhost:8080/api/orders";

        JSONObject json = new JSONObject();
        json.put("supplierId", supplierId);
        json.put("selectedDate", selectedDate);
        json.put("selectedTime", selectedTime);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        try {
            // Koristi RestTemplate za slanje POST zahteva sa podacima
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Provera odgovora
            System.out.println("Statusni kod: " + responseEntity.getStatusCode()); // Dodajte za debagovanje
            System.out.println("Odgovor servera: " + responseEntity.getBody()); // Dodajte za debagovanje

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                try {
                    // Parsirajte odgovor kao broj
                    int orderId = Integer.parseInt(responseEntity.getBody());
                    return orderId;
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Neočekivani odgovor od servera.",
                            "Greška", JOptionPane.ERROR_MESSAGE);
                    return -1;
                }
            }
            else {
                JOptionPane.showMessageDialog(this,
                        "Greška prilikom dodavanja ordera. Status: " + responseEntity.getStatusCode(),
                        "Greška", JOptionPane.ERROR_MESSAGE);
                return -1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Greška pri unosu podataka u bazu.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private void addOrderItems(int orderId) {
        DefaultTableModel model = (DefaultTableModel) items.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String productName = (String) model.getValueAt(i, 0);
            int quantity;
            try {
                quantity = Integer.parseInt((String) model.getValueAt(i, 1));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Nevažeća količina za proizvod '" + productName + "'.",
                        "Greška", JOptionPane.ERROR_MESSAGE);
                continue; // Preskoči ovaj proizvod i nastavi sa sledećim
            }

            // Pretpostavka da imaš metodu za dobijanje productId na osnovu productName
            int productId = getProductIdByName(productName);
            if (productId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Proizvod '" + productName + "' nije pronađen.",
                        "Greška", JOptionPane.ERROR_MESSAGE);
                continue; // Preskoči ovaj proizvod i nastavi sa sledećim
            }

            String jwtToken = JwtResponse.getToken();  // Pretpostavljam da imate metodu koja vraća token

            if (jwtToken == null || jwtToken.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Niste ulogovani! Token nije dostupan.",
                        "Greška", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String apiUrl = "http://localhost:8080/api/order-items";

            // Kreiranje JSON objekta sa podacima zaposlenog
            JSONObject json = new JSONObject();
            json.put("orderId", orderId);
            json.put("productId", productId);
            json.put("quantity", quantity);

            // Postavljanje headera sa JWT tokenom
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

            try {
                // Koristi RestTemplate za slanje POST zahteva sa podacima
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        apiUrl,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

                // Provera odgovora
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    JOptionPane.showMessageDialog(this,
                            "Proizvod uspešno dodat.",
                            "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Greška prilikom dodavanja proizvod.",
                            "Greška", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Greška pri unosu podataka u bazu.",
                        "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lowAmmountMouseClicked(MouseEvent e) {
        lowAmmountDialog.setVisible(true);
    }

    private void lowAmmountDialogComponentShown(ComponentEvent e) {
        // Dobavi proizvode sa niskom količinom
        List<ProductDto> lowStockProducts = getLowStockProducts();

        // Kreiraj model tabele sa checkbox kolonom
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class; // Prva kolona je checkbox
                }
                return super.getColumnClass(columnIndex);
            }
        };

        // Dodaj kolone
        model.addColumn("Izaberi"); // Checkbox kolona
        model.addColumn("ID");
        model.addColumn("Naziv");
        model.addColumn("Doza");
        model.addColumn("Preostala Količina");
        model.addColumn("Minimalna Količina");

        // Popuni tabelu podacima
        for (ProductDto product : lowStockProducts) {
            model.addRow(new Object[]{
                    false, // Početna vrednost za checkbox (nije izabran)
                    product.getId(),
                    product.getName(),
                    product.getDosage(),
                    product.getStockQuantity(), // Ovo je suma preostalih količina iz batch-eva
                    product.getMinQuantity()
            });
        }

        // Postavi model u tabelu
        table1.setModel(model);
        customizeTable(table1, model);
    }

    private List<ProductDto> getLowStockProducts() {
        List<ProductDto> lowStockProducts = new ArrayList<>();

        try {
            URL url = new URL("http://localhost:8080/api/products/low-stock");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String response = reader.lines().collect(Collectors.joining());

                    ObjectMapper objectMapper = new ObjectMapper();
                    lowStockProducts = objectMapper.readValue(response, new TypeReference<List<ProductDto>>() {
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lowStockProducts;
    }

    private void addToTable(String productName, int quantityToAdd) {
        DefaultTableModel model = (DefaultTableModel) items.getModel();
        boolean sameProduct = false;
        int rowIndex = -1;

        // Prođi kroz sve redove u tabeli
        for (int i = 0; i < model.getRowCount(); i++) {
            String productInTable = (String) model.getValueAt(i, 0); // Proizvod u tabeli

            if (productName.equals(productInTable)) {
                sameProduct = true;
                rowIndex = i;
                break;
            }
        }

        // Ako postoji isti proizvod, ažuriraj količinu
        if (sameProduct) {
            int existingQuantity = Integer.parseInt((String) model.getValueAt(rowIndex, 1)); // Postojeća količina
            int newQuantity = existingQuantity + quantityToAdd; // Nova količina
            model.setValueAt(String.valueOf(newQuantity), rowIndex, 1); // Ažuriraj količinu u tabeli
        } else {
            // Dodavanje novog reda u tabelu
            model.addRow(new Object[]{productName, String.valueOf(quantityToAdd)});
            customizeTable(items, model);
        }
    }

    private void addMouseClicked(MouseEvent e) {
        // Dohvatanje podataka iz polja
        String selectedProduct = product.getText().trim();
        String selectedQuantity = quantity.getText().trim();

        // Validacija podataka
        if (selectedProduct.isEmpty() || selectedQuantity.isEmpty()) {
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

        // Dodaj proizvod u tabelu koristeći centralizovanu metodu
        addToTable(selectedProduct, quantityValue);

        // Čišćenje polja nakon dodavanja
        product.setText("");
        quantity.setText("");
    }

    private void add2MouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();

        // Prođi kroz sve redove u tabeli
        for (int i = 0; i < model.getRowCount(); i++) {
            // Proveri da li je checkbox označen
            boolean isSelected = (boolean) model.getValueAt(i, 0);

            if (isSelected) {
                // Dobavi podatke iz tabele
                int productId = (int) model.getValueAt(i, 1);
                String productName = (String) model.getValueAt(i, 2);
                int totalRemainingQuantity = (int) model.getValueAt(i, 4); // Preostala količina
                int minQuantity = (int) model.getValueAt(i, 5); // Minimalna količina

                // Izračunaj količinu koju treba dodati
                int quantityToAdd = minQuantity - totalRemainingQuantity;

                // Proveri da li je količina validna (veća od 0)
                if (quantityToAdd > 0) {
                    // Dodaj proizvod u glavnu tabelu koristeći centralizovanu metodu
                    addToTable(productName, quantityToAdd);
                }
            }
        }

        // Zatvori dijalog
        lowAmmountDialog.setVisible(false);
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
        JViewport viewport = scrollPane1.getViewport();
        viewport.setBackground(backgroundColor);
        scrollPane1.setBackground(backgroundColor);

        JViewport viewport2 = scrollPane2.getViewport();
        viewport2.setBackground(backgroundColor);
        scrollPane2.setBackground(backgroundColor);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        back = new JButton();
        label1 = new JLabel();
        supplier = new JComboBox();
        lowAmmount = new JButton();
        scrollPane1 = new JScrollPane();
        items = new JTable();
        label2 = new JLabel();
        product = new JTextField();
        label3 = new JLabel();
        quantity = new JTextField();
        add = new JButton();
        finnish = new JButton();
        popupMenu = new JPopupMenu();
        lowAmmountDialog = new JDialog();
        panel2 = new JPanel();
        back2 = new JButton();
        scrollPane2 = new JScrollPane();
        table1 = new JTable();
        add2 = new JButton();

        //======== this ========
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
            panel1.add(back, "cell 6 0");

            //---- label1 ----
            label1.setText("Dobavljac:");
            label1.setForeground(new Color(0xfbffe4));
            panel1.add(label1, "cell 1 1");

            //---- supplier ----
            supplier.setBackground(new Color(0xb3d8a8));
            supplier.setForeground(Color.darkGray);
            panel1.add(supplier, "cell 2 1 2 1");

            //---- lowAmmount ----
            lowAmmount.setSelectedIcon(null);
            lowAmmount.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
            lowAmmount.setBackground(new Color(0xb3d8a8));
            lowAmmount.setForeground(Color.darkGray);
            lowAmmount.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    lowAmmountMouseClicked(e);
                }
            });
            panel1.add(lowAmmount, "cell 6 1");

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

        //======== lowAmmountDialog ========
        {
            lowAmmountDialog.setModal(true);
            lowAmmountDialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    lowAmmountDialogComponentShown(e);
                }
            });
            var lowAmmountDialogContentPane = lowAmmountDialog.getContentPane();

            //======== panel2 ========
            {
                panel2.setBackground(new Color(0x3d8d7a));
                panel2.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]",
                    // rows
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]"));

                //---- back2 ----
                back2.setText("Nazad");
                back2.setBackground(new Color(0xb3d8a8));
                back2.setForeground(Color.darkGray);
                panel2.add(back2, "cell 0 0");

                //======== scrollPane2 ========
                {
                    scrollPane2.setBackground(Color.darkGray);
                    scrollPane2.setForeground(Color.darkGray);

                    //---- table1 ----
                    table1.setBackground(new Color(0xfbffe4));
                    table1.setForeground(Color.darkGray);
                    table1.setGridColor(Color.darkGray);
                    table1.setSelectionBackground(new Color(0xb3d8a8));
                    table1.setSelectionForeground(Color.darkGray);
                    scrollPane2.setViewportView(table1);
                }
                panel2.add(scrollPane2, "cell 0 1 2 1");

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
                panel2.add(add2, "cell 1 2");
            }

            GroupLayout lowAmmountDialogContentPaneLayout = new GroupLayout(lowAmmountDialogContentPane);
            lowAmmountDialogContentPane.setLayout(lowAmmountDialogContentPaneLayout);
            lowAmmountDialogContentPaneLayout.setHorizontalGroup(
                lowAmmountDialogContentPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, lowAmmountDialogContentPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel2, GroupLayout.PREFERRED_SIZE, 455, GroupLayout.PREFERRED_SIZE))
            );
            lowAmmountDialogContentPaneLayout.setVerticalGroup(
                lowAmmountDialogContentPaneLayout.createParallelGroup()
                    .addGroup(lowAmmountDialogContentPaneLayout.createSequentialGroup()
                        .addComponent(panel2, GroupLayout.PREFERRED_SIZE, 505, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
            );
            lowAmmountDialog.pack();
            lowAmmountDialog.setLocationRelativeTo(lowAmmountDialog.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JButton back;
    private JLabel label1;
    private JComboBox supplier;
    private JButton lowAmmount;
    private JScrollPane scrollPane1;
    private JTable items;
    private JLabel label2;
    private JTextField product;
    private JLabel label3;
    private JTextField quantity;
    private JButton add;
    private JButton finnish;
    private JPopupMenu popupMenu;
    private JDialog lowAmmountDialog;
    private JPanel panel2;
    private JButton back2;
    private JScrollPane scrollPane2;
    private JTable table1;
    private JButton add2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
