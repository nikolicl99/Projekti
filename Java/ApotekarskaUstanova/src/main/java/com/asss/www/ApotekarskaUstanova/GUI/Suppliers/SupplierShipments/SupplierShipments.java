/*
 * Created by JFormDesigner on Sun Feb 09 18:06:09 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierShipments;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Dto.Shipment_ItemsDto;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.InventoryBatch.InventoryBatch;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierInfo.SupplierInfo;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierList.SupplierList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.miginfocom.swing.MigLayout;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierInfo.SupplierInfo.getFormBefore;
import static com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierInfo.SupplierInfo.getSpecificShipment;


/**
 * @author lniko
 */
public class SupplierShipments extends JFrame {
    private final ObjectMapper objectMapper; // Dodajte ObjectMapper kao polje klase
    private int specificShipmentId; // Dodajte polje za čuvanje ID-a specifične dostave

    public SupplierShipments() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Registrujte JavaTimeModule
        initComponents();

        String[] columnNames = {"Naziv proizvoda", "Količina", "Cena"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        Items.setModel(model);
        customizeTable(Items, model);

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
            int shipmentId = Integer.parseInt(selectedItem.split(" - ")[0]); // Dobij ID iz selektovane dostave
            LoadShipmentItems(shipmentId); // Učitaj stavke za izabranu dostavu
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
            Back.setBackground(new Color(0xb3d8a8));
            Back.setForeground(Color.darkGray);
            Back.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BackMouseClicked(e);
                }
            });
            panel1.add(Back, "cell 1 1");

            //---- Shippment ----
            Shippment.setBackground(new Color(0xb3d8a8));
            Shippment.setForeground(Color.darkGray);
            Shippment.addActionListener(e -> Shippment(e));
            panel1.add(Shippment, "cell 3 1 4 1");

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(Color.darkGray);
                scrollPane1.setForeground(Color.darkGray);

                //---- Items ----
                Items.setBackground(new Color(0xfbffe4));
                Items.setForeground(Color.darkGray);
                Items.setGridColor(Color.darkGray);
                Items.setSelectionBackground(new Color(0xb3d8a8));
                Items.setSelectionForeground(Color.darkGray);
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

        try {
            URL url = new URL("http://localhost:8080/api/suppliers/" + supplierId + "/shipments");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora koristeći već kreirani ObjectMapper
                    List<ShipmentDto> shipments = objectMapper.readValue(response, new TypeReference<List<ShipmentDto>>() {});

                    // Sortiraj ID-jeve od najvećeg ka najmanjem
                    List<Integer> sortedShipmentIds = sortShipmentIds(shipments);

                    // Dodavanje u ComboBox
                    Shippment.removeAllItems();
                    for (Integer shipmentId : sortedShipmentIds) {
                        // Pronađi odgovarajuću pošiljku po ID-u
                        ShipmentDto shipment = shipments.stream()
                                .filter(s -> s.getId() == shipmentId)
                                .findFirst()
                                .orElse(null);

                        if (shipment != null) {
                            Shippment.addItem(shipment.getId() + " - " + shipment.getArrivalDate() + " - " + shipment.getArrivalTime());
                        }
                    }

                    // Ako je forma otvorena putem klika na određenu dostavu, prikaži tu dostavu
                    if (SupplierInfo.getFormPart() == 2) {
                        specificShipmentId = getSpecificShipment();
                        Shippment.setSelectedItem(specificShipmentId + " - " + shipments.stream()
                                .filter(s -> s.getId() == specificShipmentId)
                                .findFirst()
                                .map(s -> s.getArrivalDate() + " - " + s.getArrivalTime())
                                .orElse(""));
                        LoadShipmentItems(specificShipmentId);
                    } else if (SupplierInfo.getFormPart() == 1) {
                        // Ako je forma otvorena putem dugmeta "Prikaži sve dostave", prikaži poslednju dostavu (najveći ID)
                        if (!sortedShipmentIds.isEmpty()) {
                            int lastShipmentId = sortedShipmentIds.get(0); // Prvi u sortiranoj listi je najveći ID
                            ShipmentDto lastShipment = shipments.stream()
                                    .filter(s -> s.getId() == lastShipmentId)
                                    .findFirst()
                                    .orElse(null);

                            if (lastShipment != null) {
                                Shippment.setSelectedItem(lastShipment.getId() + " - " + lastShipment.getArrivalDate() + " - " + lastShipment.getArrivalTime());
                                LoadShipmentItems(lastShipment.getId());
                            }
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

    private List<Integer> sortShipmentIds(List<ShipmentDto> shipments) {
        return shipments.stream()
                .map(ShipmentDto::getId).sorted(Collections.reverseOrder()).collect(Collectors.toList());
    }

    private void LoadShipmentItems(int shipmentId) {
        DefaultTableModel model = (DefaultTableModel) Items.getModel();
        model.setRowCount(0); // Obriši sve postojeće redove

        try {
            URL url = new URL("http://localhost:8080/api/shipment-items/" + shipmentId + "/items");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream();
                     Scanner scanner = new Scanner(inputStream)) {

                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("JSON Response: " + response); // Ispis JSON odgovora

                    // Deserializacija JSON odgovora koristeći već kreirani ObjectMapper
                    List<Shipment_ItemsDto> items = objectMapper.readValue(response, new TypeReference<List<Shipment_ItemsDto>>() {});

                    for (Shipment_ItemsDto item : items) {
                        model.addRow(new Object[]{item.getProductBatch().getProduct().getName(), item.getQuantity(), item.getProductBatch().getProduct().getSellingPrice()});
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri učitavanju stavki dostave.", "Greška", JOptionPane.ERROR_MESSAGE);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri popunjavanju tabele.", "Greška", JOptionPane.ERROR_MESSAGE);
        } finally {
            customizeTable(Items, model);
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
}