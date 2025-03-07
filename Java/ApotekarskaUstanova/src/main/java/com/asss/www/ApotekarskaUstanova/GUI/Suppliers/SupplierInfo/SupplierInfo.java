/*
 * Created by JFormDesigner on Wed Jan 22 23:49:20 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierInfo;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment_Items;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.InventoryBatch.InventoryBatch;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierList.SupplierList;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierShipments.SupplierShipments;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * @author lniko
 */
public class SupplierInfo extends JFrame {
    public SupplierInfo() {
        initComponents();
        System.out.println("Iz koje je forme krenulo: " + getFormBefore());
        UcitavanjePodataka();
        UcitavanjePodatakaTabela();
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
        SupplierShipments.start();
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

            //---- Back ----
            Back.setText("Nazad");
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
            panel1.add(nameSurname_Label, "cell 1 2");
            panel1.add(nameSurname, "cell 2 2 2 1");

            //---- Email_Label ----
            Email_Label.setText("Email:");
            panel1.add(Email_Label, "cell 1 3");
            panel1.add(email, "cell 2 3 2 1");

            //---- telefon_Label ----
            telefon_Label.setText("Telefon:");
            panel1.add(telefon_Label, "cell 1 4");
            panel1.add(phone, "cell 2 4 2 1");

            //---- adress_Label ----
            adress_Label.setText("Adresa:");
            panel1.add(adress_Label, "cell 1 5");
            panel1.add(adress, "cell 2 5 2 1");

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(shipments);
            }
            panel1.add(scrollPane1, "cell 6 6 2 2");

            //---- allShipments ----
            allShipments.setText("Prikazi sve dobavke");
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

    public static void setFormBefore(int formBefore) {
        SupplierInfo.formBefore = formBefore;
    }

    public static int getFormBefore() {
        return SupplierInfo.formBefore;
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
         // Dohvata ID izabrane stavke
        String urlString = "http://localhost:8080/api/suppliers/" + id;

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
                    Supplier supplier = objectMapper.readValue(response, Supplier.class);

                    // Popunjavanje podataka u UI komponentama
                    nameSurname.setText(supplier.getName());
                    email.setText(supplier.getEmail());
                    phone.setText(supplier.getPhone());
                    adress.setText(supplier.getAddress().getAddress());

//                    if (employees.getScaledProfileImage() != null) {
//                        byte[] imageBytes = Base64.getDecoder().decode(employees.getScaledProfileImage());
//                        ImageIcon scaledIcon = scaleImage(imageBytes, 200, 150);
//                        picture.setIcon(scaledIcon);
//                    } else {
//                        picture.setIcon(null);
//                    }

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

    private void UcitavanjePodatakaTabela() {
        int id = 0;

        if (getFormBefore() == 1) {
            id = InventoryBatch.getSelectedSupplierId();
        }
        if (getFormBefore() == 2) {
            id = SupplierList.getSelectedSupplierId();
        }
        System.out.println("ID u drugoj metodi: " + id);
        String urlString = "http://localhost:8080/api/suppliers/" + id + "/recent-shipments"; // URL za poslednje 4-5 dostava

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

                    System.out.println("JSON Response: " + response);
                    // Parsiranje JSON odgovora u listu dostava
                    ObjectMapper objectMapper = new ObjectMapper();
                    Shipment[] shipmentsArray = objectMapper.readValue(response, Shipment[].class);

                    if (shipmentsArray.length == 0) {
                        // Ako nema pošiljki, prikazujemo poruku
                        String[][] data = {{"Ovaj dobavljač nije napravio nijednu pošiljku", "", ""}};
                        String[] columnNames = {"ID Dostave", "Datum", "Količina Stavki"};
                        shipments.setModel(new DefaultTableModel(data, columnNames));
                        return;
                    }

                    // Kreiranje modela tabele sa potrebnim podacima
                    String[][] data = new String[shipmentsArray.length][3]; // ID dostave, Datum, Količina
                    for (int i = 0; i < shipmentsArray.length; i++) {
                        String shipmentId = String.valueOf(shipmentsArray[i].getId()); // ID dostave
                        String arrivalTime = String.valueOf(shipmentsArray[i].getArrivalTime()); // Datum dostave

                        // Pozivanje endpoint-a za shipment items i brojanje stavki
                        String shipmentItemsUrl = "http://localhost:8080/api/shipments/" + shipmentId + "/items";
                        URL itemsUrl = new URL(shipmentItemsUrl);
                        HttpURLConnection itemsConnection = (HttpURLConnection) itemsUrl.openConnection();
                        itemsConnection.setRequestMethod("GET");
                        itemsConnection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

                        int itemsResponseCode = itemsConnection.getResponseCode();
                        int itemCount = 0;
                        if (itemsResponseCode == HttpURLConnection.HTTP_OK) {
                            try (Scanner itemsScanner = new Scanner(itemsConnection.getInputStream())) {
                                String itemsResponse = itemsScanner.useDelimiter("\\A").next();
                                // Parsiranje odgovora i brojanje stavki
                                Shipment_Items[] shipmentItems = objectMapper.readValue(itemsResponse, Shipment_Items[].class);
                                itemCount = shipmentItems.length; // Broj stavki u dostavi
                            }
                        }

                        // Dodavanje podataka u tabelu
                        data[i][0] = shipmentId; // ID dostave
                        data[i][1] = arrivalTime; // Datum dostave
                        data[i][2] = String.valueOf(itemCount); // Količina stavki u dostavi
                    }

                    // Kreiranje tabele sa podacima
                    String[] columnNames = {"ID Dostave", "Datum", "Količina Stavki"};
                    shipments.setModel(new DefaultTableModel(data, columnNames));

                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                // Ako nema podataka (404), prikaži poruku u tabeli
                String[][] data = {{"Ovaj dobavljač nije napravio nijednu pošiljku"}};
                String[] columnNames = {"Dobavljac"};
                allShipments.setVisible(false);
                shipments.setModel(new DefaultTableModel(data, columnNames));
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




}
