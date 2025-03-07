/*
 * Created by JFormDesigner on Sun Feb 09 18:06:09 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierShipments;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;

import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Dto.Shipment_ItemsDto;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.InventoryBatch.InventoryBatch;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierInfo.SupplierInfo;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierList.SupplierList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.MigLayout;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import static com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierInfo.SupplierInfo.getFormBefore;


/**
 * @author lniko
 */
public class SupplierShipments extends JFrame {
    public SupplierShipments() {
        initComponents();
        LoadData();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            SupplierShipments frame = new SupplierShipments();
            frame.setTitle("Dostave");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void BackMouseClicked(MouseEvent e) {
        dispose();
        SupplierInfo.start();
    }

    private void Shippment(ActionEvent e) {
        String selectedItem = (String) Shippment.getSelectedItem();
        if (selectedItem != null) {
            long shipmentId = Long.parseLong(selectedItem.split(" - ")[0]); // Dobij ID iz selektovane dostave
            LoadShipmentItems(shipmentId);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        Back = new JButton();
        Shippment = new JComboBox();
        scrollPane1 = new JScrollPane();
        Items = new JTable();

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
            panel1.add(Back, "cell 1 1");

            //---- Shippment ----
            Shippment.addActionListener(e -> Shippment(e));
            panel1.add(Shippment, "cell 3 1 4 1");

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(Items);
            }
            panel1.add(scrollPane1, "cell 2 3 6 7");
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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JButton Back;
    private JComboBox Shippment;
    private JScrollPane scrollPane1;
    private JTable Items;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private void LoadData() {
        int supplierId = 0;

        if (getFormBefore() == 1) {
            supplierId = InventoryBatch.getSelectedSupplierId();
        }
        if (getFormBefore() == 2) {
            supplierId = SupplierList.getSelectedSupplierId();
        }
//        int supplierId = InventoryBatch.getSelectedSupplierId();  // Dobij ID kurira
        try {
            URL url = new URL("http://localhost:8080/api/suppliers/" + supplierId + "/shipments");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Shipment> shipments = objectMapper.readValue(response, new TypeReference<List<Shipment>>() {});

                    // Dodavanje u ComboBox
                    Shippment.removeAllItems();
                    for (Shipment shipment : shipments) {
                        Shippment.addItem(shipment.getId() + " - " + shipment.getArrivalTime());
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
    private void LoadShipmentItems(long shipmentId) {
        try {
            URL url = new URL("http://localhost:8080/api/shipments/" + shipmentId + "/items");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream();
                     Scanner scanner = new Scanner(inputStream)) {

                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("JSON Response: " + response); // Ispis JSON odgovora

                    // Deserializacija JSON odgovora u listu Shipment_ItemsDto objekata
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Shipment_ItemsDto> items = objectMapper.readValue(response, new TypeReference<List<Shipment_ItemsDto>>() {});

                    // Postavljanje podataka u tabelu
                    String[] columnNames = {"Naziv proizvoda", "Količina", "Cena"};
                    Object[][] data = new Object[items.size()][3];

                    for (int i = 0; i < items.size(); i++) {
                        Shipment_ItemsDto item = items.get(i);
                        data[i][0] = item.getName();
                        data[i][1] = item.getQuantity();
                        data[i][2] = item.getPurchasePrice();

                        // Ispis za debagovanje
                        System.out.println("kolona: " + i + ", ime: " + item.getName() + ", kolicina: " + item.getQuantity() + ", cena: " + item.getPurchasePrice());
                    }

                    Items.setModel(new DefaultTableModel(data, columnNames));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri učitavanju stavki dostave.", "Greška", JOptionPane.ERROR_MESSAGE);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri popunjavanju tabele.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }



}
