/*
 * Created by JFormDesigner on Sun Dec 22 17:06:36 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.Inventory;

import java.awt.*;
import java.awt.event.*;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Product;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.InventoryBatch.InventoryBatch;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.InventoryMenu.InventoryMenu;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
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
            long id = (long) Lekovi.getValueAt(selectedRow, 0);
            System.out.println("Izabrani ID predmeta: " + id);

            // Skladišti ID u promenljivu za kasniju upotrebu
            setSelectedItemId(id);

            // Primer: otvaranje novog prozora za izmenu podataka
            if (e.getClickCount() == 2) { // Ako je dvoklik
//                EditZaposleni editWindow = new EditZaposleni(selectedEmployeeId);
//                editWindow.setVisible(true);
                JOptionPane.showMessageDialog(this, "Izabran je predmet: " + getSelectedItemId());
            }
        }

    }

    private void DodajMouseClicked(MouseEvent e) {
        // TODO add your code here
    }

    private void PregledMouseClicked(MouseEvent e) {
        dispose();
        InventoryBatch.start();
    }

    private void ObrisiMouseClicked(MouseEvent e) {
        // TODO add your code here
    }

    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        InventoryMenu.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Luka Nikolić
        panel1 = new JPanel();
        Nazad = new JButton();
        textField1 = new JTextField();
        Search = new JButton();
        scrollPane1 = new JScrollPane();
        Lekovi = new JTable();
        Pregled = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setPreferredSize(new Dimension(1000, 505));
            panel1.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new javax . swing
            . border .EmptyBorder ( 0, 0 ,0 , 0) ,  "JFor\u006dDesi\u0067ner \u0045valu\u0061tion" , javax. swing .border . TitledBorder
            . CENTER ,javax . swing. border .TitledBorder . BOTTOM, new java. awt .Font ( "Dia\u006cog", java .
            awt . Font. BOLD ,12 ) ,java . awt. Color .red ) ,panel1. getBorder () ) )
            ; panel1. addPropertyChangeListener( new java. beans .PropertyChangeListener ( ){ @Override public void propertyChange (java . beans. PropertyChangeEvent e
            ) { if( "bord\u0065r" .equals ( e. getPropertyName () ) )throw new RuntimeException( ) ;} } )
            ;
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
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]"));

            //---- Nazad ----
            Nazad.setText("Nazad");
            Nazad.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NazadMouseClicked(e);
                    NazadMouseClicked(e);
                }
            });
            panel1.add(Nazad, "cell 0 0");
            panel1.add(textField1, "cell 1 1 2 1");

            //---- Search ----
            Search.setText("Pretraga");
            panel1.add(Search, "cell 3 1");

            //======== scrollPane1 ========
            {

                //---- Lekovi ----
                Lekovi.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        LekoviMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(Lekovi);
            }
            panel1.add(scrollPane1, "pad 0 5 0 5,cell 0 2 10 1");

            //---- Pregled ----
            Pregled.setText("Pregled");
            Pregled.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PregledMouseClicked(e);
                }
            });
            panel1.add(Pregled, "cell 3 3");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
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
    // Generated using JFormDesigner Evaluation license - Luka Nikolić
    private JPanel panel1;
    private JButton Nazad;
    private JTextField textField1;
    private JButton Search;
    private JScrollPane scrollPane1;
    private JTable Lekovi;
    private JButton Pregled;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static long SelectedItemId;

    public static long getSelectedItemId() {
        return SelectedItemId;
    }

    public void setSelectedItemId(long selectedItemId) {
        SelectedItemId = selectedItemId;
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
        model.addColumn("SKU");
        model.addColumn("Opis");
        model.addColumn("Kupovna Cena");
        model.addColumn("Prodajna Cena");
        model.addColumn("Stanje na lageru");
        model.addColumn("Lokacija");


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
                    List<Product> productList = objectMapper.readValue(response, new TypeReference<List<Product>>() {
                    });

                    // Popunjavanje modela podacima o lekovima
                    for (Product product : productList) {
                        model.addRow(new Object[]{
                                product.getId(),
                                product.getName(),
                                product.getDescription(),
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
    }
}
