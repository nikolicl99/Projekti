/*
 * Created by JFormDesigner on Fri Jan 17 22:15:28 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.InventoryBatch;

import java.awt.*;
import java.awt.event.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierInfo.SupplierInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Dto.ProductBatchDto;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.Inventory.Inventory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.miginfocom.swing.*;
import org.json.JSONObject;

/**
 * @author lniko
 */
public class InventoryBatch extends JFrame {
    private final ObjectMapper objectMapper; // Dodajte ObjectMapper kao polje klase

    public InventoryBatch() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Registrujte JavaTimeModule
        initComponents();
        prikaziPodatke();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            InventoryBatch frame = new InventoryBatch();
            frame.setTitle("Inventory");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void BackMouseClicked(MouseEvent e) {
        dispose();
        Inventory.start();
    }

    private void BatchMouseClicked(MouseEvent e) {
        int selectedRow = Batch.getSelectedRow(); // Dohvata indeks izabranog reda
        int selectedColumn = Batch.getSelectedColumn(); // Dohvata indeks izabrane kolone

        if (selectedRow >= 0 && selectedColumn == 5) { // Proverava da li je kliknuta šesta kolona
            // Pretpostavlja se da je ID dobavljača u šestoj koloni (indeks 5)
            int supplierId = dobijanjeIDDobavljaca((String) Batch.getValueAt(selectedRow, 5));
            System.out.println("Izabrani ID dobavljača: " + supplierId);

            // Skladišti ID u promenljivu za kasniju upotrebu
            setSelectedSupplierId(supplierId);

            // Ako je dvoklik, otvara se forma za dobavljača
            if (e.getClickCount() == 2) {
                JOptionPane.showMessageDialog(this, "Izabran dobavljač ID: " + getSelectedSupplierId());
                SupplierInfo.setFormBefore(1);
                dispose();
                SupplierInfo.start();
            }

        }
    }

    private void prikaziPodatke() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        model.addColumn("Batch ID");
        model.addColumn("Naziv Isporuke");
        model.addColumn("Datum Isporuke");
        model.addColumn("Datum isteka");
        model.addColumn("Količina");
        model.addColumn("Dobavljac");

        try {
            // URL REST API-ja
            long selectedProductId = Inventory.getSelectedItemId(); // Pretpostavljam da vraća ID proizvoda
            System.out.println("izabrani id je: " + selectedProductId);
            URL url = new URL("http://localhost:8080/api/batches/product/" + selectedProductId);

            // Konfigurisanje HTTP zahteva
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            // Provera odgovora API-ja
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    System.out.println(response);

                    // Parsiranje JSON odgovora koristeći već kreirani ObjectMapper
                    List<ProductBatchDto> batchList = objectMapper.readValue(response, new TypeReference<List<ProductBatchDto>>() {});

                    for (ProductBatchDto batch : batchList) {
                        model.addRow(new Object[]{
                                batch.getId(),
                                batch.getBatchNumber(),
                                batch.getShipmentDto().getArrivalDate(),
                                batch.getExpirationDate(),
                                batch.getQuantityRemaining(),
                                batch.getShipmentDto().getSupplier().getName() != null ? batch.getShipmentDto().getSupplier().getName() : "Nepoznat dobavljac"
                        });
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška API-ja: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }

        Batch.setModel(model);
        customizeTable(Batch, model);
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


    private int dobijanjeIDDobavljaca(String naziv_Dobavljaca) {
        System.out.println("Naziv dobavljača: " + naziv_Dobavljaca);
        try {
            String encodedNazivDobavljaca = URLEncoder.encode(naziv_Dobavljaca, StandardCharsets.UTF_8);

            // Zameni sve '+' znakove sa '%20'
            encodedNazivDobavljaca = encodedNazivDobavljaca.replace("+", "%20");

            System.out.println("Encoded naziv: " + encodedNazivDobavljaca);
            URL url = new URL("http://localhost:8080/api/suppliers/name/" + encodedNazivDobavljaca);

            System.out.println("Slanje HTTP zahteva na: " + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            System.out.println("Zaglavlje zahteva: " + connection.getRequestProperties());

            int responseCode = connection.getResponseCode();
            System.out.println("HTTP odgovor sa statusom: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("Odgovor servera: " + response);

                    // Parsiranje odgovora kao broja (umesto JSON objekta)
                    return Integer.parseInt(response.trim()); // Pretvaramo odgovor u broj
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Greška: API je vratio status " + responseCode,
                        "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Greška pri komunikaciji sa serverom.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
        }

        return -1; // Vraćamo -1 ako dođe do greške
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        Back = new JButton();
        scrollPane1 = new JScrollPane();
        Batch = new JTable();

        //======== this ========
        setAutoRequestFocus(false);
        setPreferredSize(new Dimension(500, 500));
        setMinimumSize(new Dimension(500, 500));
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
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]"));

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
            panel1.add(Back, "cell 0 0");

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(Color.darkGray);
                scrollPane1.setForeground(Color.darkGray);

                //---- Batch ----
                Batch.setBackground(new Color(0xfbffe4));
                Batch.setForeground(Color.darkGray);
                Batch.setGridColor(Color.darkGray);
                Batch.setSelectionBackground(new Color(0xb3d8a8));
                Batch.setSelectionForeground(Color.darkGray);
                Batch.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        BatchMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(Batch);
            }
            panel1.add(scrollPane1, "cell 0 2 10 1");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
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
    private JScrollPane scrollPane1;
    private JTable Batch;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static int SelectedSupplierId;


    public static int getSelectedSupplierId() {
        return SelectedSupplierId;
    }

    public static void setSelectedSupplierId(int selectedSupplierId) {
        SelectedSupplierId = selectedSupplierId;
    }


}
