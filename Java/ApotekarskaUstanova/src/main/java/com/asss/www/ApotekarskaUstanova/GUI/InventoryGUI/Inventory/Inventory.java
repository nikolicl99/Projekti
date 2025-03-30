/*
 * Created by JFormDesigner on Sun Dec 22 17:06:36 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.Inventory;

import java.awt.*;
import java.awt.event.*;

import com.asss.www.ApotekarskaUstanova.Dto.ProductDto;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin.MainMenuAdmin;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.InventoryBatch.InventoryBatch;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import net.miginfocom.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lniko
 */
public class Inventory extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(Inventory.class);

    public Inventory() {
        initComponents();
        PrikaziLekove();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            Inventory frame = new Inventory();
            frame.setTitle("Inventory");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void LekoviMouseClicked(MouseEvent e) {
        int selectedRow = Lekovi.getSelectedRow(); // Dohvata indeks izabranog reda
        int selectedColumn = Lekovi.getSelectedColumn();
        if (selectedRow >= 0) { // Proverava da li je red validan
            // Pretpostavlja se da je ID u prvoj koloni (kolona 0)
            int id = (int) Lekovi.getValueAt(selectedRow, 0);
            System.out.println("Izabrani ID predmeta: " + id);

            // Skladišti ID u promenljivu za kasniju upotrebu
            setSelectedItemId(id);

            // Primer: otvaranje novog prozora za izmenu podataka
            if (e.getClickCount() == 2) { // Ako je dvoklik
                dispose();
                InventoryBatch.start();
            }
        }

    }

    private void PrikaziLekove() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Onemogući uređivanje ćelija
            }
        };

        // Definisanje kolona za osnovne informacije o lekovima
        model.addColumn("ID");
        model.addColumn("Naziv leka");
        model.addColumn("Opis");
        model.addColumn("Kategorija");
        model.addColumn("Kupovna Cena");
        model.addColumn("Prodajna Cena");
        model.addColumn("Stanje na lageru");
        try {
            // URL za API lekova
            URL url = new URL("http://localhost:8080/api/products");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Dodavanje Authorization header-a sa tokenom
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            // Provera odgovora API-ja
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<ProductDto> productList = objectMapper.readValue(response, new TypeReference<List<ProductDto>>() {
                    });

                    // Popunjavanje modela podacima o lekovima
                    for (ProductDto product : productList) {
                        String subcategoryName = "N/A"; // Default value if subcategoryDto is null
                        if (product.getSubcategoryDto() != null) {
                            subcategoryName = product.getSubcategoryDto().getName();
                        }

                        model.addRow(new Object[]{
                                product.getId(),
                                product.getName(),
                                product.getDescription(),
                                subcategoryName, // Use the subcategory name (or "N/A" if null)
                                product.getPurchasePrice(),
                                product.getSellingPrice(),
                                product.getStockQuantity(),
                        });
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "API greška: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka iz baze!", "Greška", JOptionPane.ERROR_MESSAGE);
        }

        // Postavljanje modela na JTable
        Lekovi.setModel(model);
        customizeTable(Lekovi, model);
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


    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        MainMenuAdmin.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        Nazad = new JButton();
        scrollPane1 = new JScrollPane();
        Lekovi = new JTable();
        textField1 = new JTextField();
        Search = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setPreferredSize(new Dimension(1000, 505));
            panel1.setBackground(new Color(0x3d8d7a));
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

            //---- Nazad ----
            Nazad.setText("Nazad");
            Nazad.setBackground(new Color(0xb3d8a8));
            Nazad.setForeground(Color.darkGray);
            Nazad.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NazadMouseClicked(e);
                }
            });
            panel1.add(Nazad, "cell 1 0");

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(Color.darkGray);
                scrollPane1.setForeground(Color.darkGray);

                //---- Lekovi ----
                Lekovi.setBackground(new Color(0xfbffe4));
                Lekovi.setForeground(Color.darkGray);
                Lekovi.setGridColor(Color.darkGray);
                Lekovi.setSelectionBackground(new Color(0xb3d8a8));
                Lekovi.setSelectionForeground(Color.darkGray);
                Lekovi.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        LekoviMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(Lekovi);
            }
            panel1.add(scrollPane1, "pad 0 5 0 5,cell 0 1 10 9");

            //---- textField1 ----
            textField1.setVisible(false);
            panel1.add(textField1, "cell 1 1 2 1");

            //---- Search ----
            Search.setText("Pretraga");
            Search.setVisible(false);
            panel1.add(Search, "cell 3 1");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
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
    private JButton Nazad;
    private JScrollPane scrollPane1;
    private JTable Lekovi;
    private JTextField textField1;
    private JButton Search;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static long SelectedItemId;

    public static long getSelectedItemId() {
        return SelectedItemId;
    }

    public void setSelectedItemId(long selectedItemId) {
        SelectedItemId = selectedItemId;
    }


}
