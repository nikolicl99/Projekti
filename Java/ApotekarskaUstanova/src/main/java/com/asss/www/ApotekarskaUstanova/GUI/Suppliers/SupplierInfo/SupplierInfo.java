/*
 * Created by JFormDesigner on Wed Jan 22 23:49:20 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierInfo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.Dto.Shipment_ItemsDto;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeView.EmployeeView;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.InventoryBatch.InventoryBatch;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierList.SupplierList;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierShipments.SupplierShipments;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.miginfocom.swing.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * @author lniko
 */
public class SupplierInfo extends JFrame {
    private final ObjectMapper objectMapper; // Dodajte ObjectMapper kao polje klase

    public SupplierInfo() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Registrujte JavaTimeModule
        initComponents();
        System.out.println("Iz koje je forme krenulo: " + getFormBefore());
        String[] columnNames = {"ID Dostave", "Datum", "Količina Stavki"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        shipments.setModel(model);
        customizeTable(shipments, model);

        UcitavanjePodataka();
        loadDataTable();
    }


    public static void start() {
        SwingUtilities.invokeLater(() -> {
            SupplierInfo frame = new SupplierInfo();
            frame.setTitle("Dobavljaci");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void BackMouseClicked(MouseEvent e) {
        dispose();
        if (getFormBefore() == 1) {
            InventoryBatch.start();
        }
        if (getFormBefore() == 2) {
            SupplierList.start();
        }
    }

    private void allShipmentsMouseClicked(MouseEvent e) {
        dispose();
        setFormPart(1);
        SupplierShipments.start();
    }

    private void UcitavanjePodataka() {
        int id = 0;

        if (getFormBefore() == 1) {
            id = InventoryBatch.getSelectedSupplierId();
        }
        if (getFormBefore() == 2) {
            id = SupplierList.getSelectedSupplierId();
        }

        System.out.println("ID u prvoj metodi: " + id);
        String urlString = "http://localhost:8080/api/suppliers/" + id;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora koristeći već kreirani ObjectMapper
                    Supplier supplier = objectMapper.readValue(response, Supplier.class);

                    // Popunjavanje podataka u UI komponentama
                    nameSurname.setText(supplier.getName());
                    email.setText(supplier.getEmail());
                    phone.setText(supplier.getPhone());
                    adress.setText(supplier.getAddress().getAddress());
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

    private void loadDataTable() {
        DefaultTableModel model = (DefaultTableModel) shipments.getModel();
        model.setRowCount(0); // Obriši sve postojeće redove

        int id = 0;
        if (getFormBefore() == 1) {
            id = InventoryBatch.getSelectedSupplierId();
        } else if (getFormBefore() == 2) {
            id = SupplierList.getSelectedSupplierId();
        }

        System.out.println("ID u drugoj metodi: " + id);
        String urlString = "http://localhost:8080/api/suppliers/" + id + "/recent-shipments";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("JSON Response: " + response);

                    Shipment[] shipmentsArray = objectMapper.readValue(response, Shipment[].class);
                    if (shipmentsArray == null || shipmentsArray.length == 0) {
                        model.addRow(new Object[]{"Ovaj dobavljač nije napravio nijednu pošiljku", "", ""});
                        return;
                    }

                    for (Shipment shipment : shipmentsArray) {
                        String shipmentId = String.valueOf(shipment.getId());
                        String arrivalTime = shipment.getArrivalTime().toString();
                        int itemCount = getShipmentItemCount(shipment.getId());
                        model.addRow(new Object[]{shipmentId, arrivalTime, String.valueOf(itemCount)});
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                model.addRow(new Object[]{"Ovaj dobavljač nije napravio nijednu pošiljku", "", ""});
                allShipments.setVisible(false);
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                JOptionPane.showMessageDialog(this, "Nevažeći token. Prijavite se ponovo.", "Greška", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri dohvatu podataka: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka!", "Greška", JOptionPane.ERROR_MESSAGE);
        } finally {
            customizeTable(shipments, model); // Prilagodi tabelu nakon učitavanja podataka
        }
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

    private int getShipmentItemCount(int shipmentId) {
        try {
            String shipmentItemsUrl = "http://localhost:8080/api/shipment-items/" + shipmentId + "/items";
            URL itemsUrl = new URL(shipmentItemsUrl);
            HttpURLConnection itemsConnection = (HttpURLConnection) itemsUrl.openConnection();
            itemsConnection.setRequestMethod("GET");
            itemsConnection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int itemsResponseCode = itemsConnection.getResponseCode();
            if (itemsResponseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner itemsScanner = new Scanner(itemsConnection.getInputStream())) {
                    String itemsResponse = itemsScanner.useDelimiter("\\A").next();

                    // Parsiranje odgovora koristeći već kreirani ObjectMapper
                    Shipment_ItemsDto[] shipmentItems = objectMapper.readValue(itemsResponse, Shipment_ItemsDto[].class);
                    return shipmentItems.length; // Broj stavki u dostavi
                }
            } else {
                System.out.println("Greška pri dohvatu stavki za pošiljku " + shipmentId + ": " + itemsResponseCode);
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Greška prilikom učitavanja stavki za pošiljku " + shipmentId);
            return 0;
        }
    }

    private void shipmentsMouseClicked(MouseEvent e) {
        int selectedRow = shipments.getSelectedRow(); // Dohvata indeks izabranog reda
        if (selectedRow >= 0) { // Proverava da li je red validan
            // Pretpostavlja se da je ID u prvoj koloni (kolona 0)
            String idString = (String) shipments.getValueAt(selectedRow, 0); // Uzmi vrednost kao String
            int id = Integer.parseInt(idString); // Pretvori String u int
            System.out.println("Izabrani ID zaposlenog: " + id);

            setSpecificShipment(id);
            setFormPart(2);
            // Skladišti ID u promenljivu za kasniju upotrebu
            EmployeeView.setSelectedEmployeeId(id);

            // Primer: otvaranje novog prozora za izmenu podataka
            if (e.getClickCount() == 2) { // Ako je dvoklik
                dispose();
                SupplierShipments.start();
            }
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        Back = new JButton();
        image = new JLabel();
        nameSurname_Label = new JLabel();
        nameSurname = new JTextField();
        Email_Label = new JLabel();
        email = new JTextField();
        telefon_Label = new JLabel();
        phone = new JTextField();
        adress_Label = new JLabel();
        adress = new JTextField();
        scrollPane1 = new JScrollPane();
        shipments = new JTable();
        allShipments = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0x3d8d7a));
            panel1.setLayout(new MigLayout(
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
                "[100,fill]" +
                "[100,fill]",
                // rows
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]"));

            //---- Back ----
            Back.setText("Nazad");
            Back.setBackground(new Color(0xb3d8a8));
            Back.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BackMouseClicked(e);
                }
            });
            panel1.add(Back, "cell 0 0");
            panel1.add(image, "cell 5 1 2 3");

            //---- nameSurname_Label ----
            nameSurname_Label.setText("Ime i Prezime:");
            nameSurname_Label.setBackground(new Color(0xfbffe4));
            panel1.add(nameSurname_Label, "cell 1 2");

            //---- nameSurname ----
            nameSurname.setBackground(new Color(0xb3d8a8));
            nameSurname.setForeground(Color.darkGray);
            panel1.add(nameSurname, "cell 2 2 2 1");

            //---- Email_Label ----
            Email_Label.setText("Email:");
            Email_Label.setBackground(new Color(0xfbffe4));
            panel1.add(Email_Label, "cell 1 3");

            //---- email ----
            email.setBackground(new Color(0xb3d8a8));
            email.setForeground(Color.darkGray);
            panel1.add(email, "cell 2 3 2 1");

            //---- telefon_Label ----
            telefon_Label.setText("Telefon:");
            telefon_Label.setBackground(new Color(0xfbffe4));
            panel1.add(telefon_Label, "cell 1 4");

            //---- phone ----
            phone.setBackground(new Color(0xb3d8a8));
            phone.setForeground(Color.darkGray);
            panel1.add(phone, "cell 2 4 2 1");

            //---- adress_Label ----
            adress_Label.setText("Adresa:");
            adress_Label.setBackground(new Color(0xfbffe4));
            panel1.add(adress_Label, "cell 1 5");

            //---- adress ----
            adress.setBackground(new Color(0xb3d8a8));
            adress.setForeground(Color.darkGray);
            panel1.add(adress, "cell 2 5 2 1");

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(Color.darkGray);
                scrollPane1.setForeground(Color.darkGray);

                //---- shipments ----
                shipments.setBackground(new Color(0xfbffe4));
                shipments.setForeground(Color.darkGray);
                shipments.setGridColor(Color.darkGray);
                shipments.setSelectionBackground(new Color(0xb3d8a8));
                shipments.setSelectionForeground(Color.darkGray);
                shipments.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        shipmentsMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(shipments);
            }
            panel1.add(scrollPane1, "cell 6 4 2 5");

            //---- allShipments ----
            allShipments.setText("Prikazi sve dobavke");
            allShipments.setBackground(new Color(0xb3d8a8));
            allShipments.setForeground(Color.darkGray);
            allShipments.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    allShipmentsMouseClicked(e);
                }
            });
            panel1.add(allShipments, "cell 6 9 2 1");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 959, Short.MAX_VALUE)
                    .addGap(33, 33, 33))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JButton Back;
    private JLabel image;
    private JLabel nameSurname_Label;
    private JTextField nameSurname;
    private JLabel Email_Label;
    private JTextField email;
    private JLabel telefon_Label;
    private JTextField phone;
    private JLabel adress_Label;
    private JTextField adress;
    private JScrollPane scrollPane1;
    private JTable shipments;
    private JButton allShipments;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private static int formBefore;
    private static int specificShipment;
    private static int formPart;

    public static void setFormBefore(int formBefore) {
        SupplierInfo.formBefore = formBefore;
    }

    public static int getFormBefore() {
        return SupplierInfo.formBefore;
    }

    public static int getSpecificShipment() {
        return specificShipment;
    }

    public static void setSpecificShipment(int specificShipment) {
        SupplierInfo.specificShipment = specificShipment;
    }

    public static int getFormPart() {
        return formPart;
    }

    public static void setFormPart(int formPart) {
        SupplierInfo.formPart = formPart;
    }
}
