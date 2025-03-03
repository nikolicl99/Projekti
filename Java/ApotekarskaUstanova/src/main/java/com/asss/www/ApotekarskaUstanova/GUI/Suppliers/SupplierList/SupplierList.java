/*
 * Created by JFormDesigner on Tue Feb 11 23:31:28 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierList;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;

import com.asss.www.ApotekarskaUstanova.Dto.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenu.MainMenu;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierInfo.SupplierInfo;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.AddSupplier.AddSupplier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

/**
 * @author lniko
 */
public class SupplierList extends JFrame {
    public SupplierList() {
        initComponents();
        LoadData();

    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            SupplierList frame = new SupplierList();
            frame.setTitle("Dobavljaci");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void BackMouseClicked(MouseEvent e) {
        dispose();
        MainMenu.start();
    }

    private void suppliersMouseClicked(MouseEvent e) {
        int selectedRow = suppliers.getSelectedRow(); // Dohvata indeks izabranog reda
        if (selectedRow >= 0) { // Proverava da li je red validan
            // Pretpostavlja se da je ID u prvoj koloni (kolona 0)
            SupplierInfo.setFormBefore(2);
            System.out.println("Postavljen formBefore: " + SupplierInfo.getFormBefore());

            int id = (int) suppliers.getValueAt(selectedRow, 0);
            System.out.println("Izabrani ID zaposlenog: " + id);


            // Skladišti ID u promenljivu za kasniju upotrebu
            setSelectedSupplierId(id);

            // Primer: otvaranje novog prozora za izmenu podataka
            if (e.getClickCount() == 2) { // Ako je dvoklik
//                JOptionPane.showMessageDialog(this, "Izabran je zaposleni: " + getSelectedSupplierId());

                System.out.println("Odabran supplier: " + getSelectedSupplierId());
                dispose();
                SupplierInfo.start();
            }
        }
    }

    private void AddSupplierMouseClicked(MouseEvent e) {
        dispose();
        AddSupplier.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        Back = new JButton();
        scrollPane1 = new JScrollPane();
        suppliers = new JTable();
        AddSupplier_btn = new JButton();

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

            //======== scrollPane1 ========
            {

                //---- suppliers ----
                suppliers.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        suppliersMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(suppliers);
            }
            panel1.add(scrollPane1, "cell 1 2 8 7");

            //---- AddSupplier_btn ----
            AddSupplier_btn.setText("Dodaj");
            AddSupplier_btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    AddSupplierMouseClicked(e);
                }
            });
            panel1.add(AddSupplier_btn, "cell 5 9");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    private void LoadData() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };

        model.addColumn("ID");
        model.addColumn("Ime i Prezime");
        model.addColumn("Adresa");
        model.addColumn("Telefon");
        model.addColumn("Email");

        try {
            // URL za API zaposlenih
            URL url = new URL("http://localhost:8080/api/suppliers");
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
                    List<Supplier> supplierList = objectMapper.readValue(response, new TypeReference<List<Supplier>>() {
                    });
//
                    for (Supplier supplier : supplierList) {
                        model.addRow(new Object[]{
                                supplier.getId(),
                                supplier.getName(),
                                supplier.getAddress(),
                                supplier.getPhone(),
                                supplier.getEmail()
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

        suppliers.setModel(model);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JButton Back;
    private JScrollPane scrollPane1;
    private JTable suppliers;
    private JButton AddSupplier_btn;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static int selectedSupplierId;

    public static void setSelectedSupplierId(int id) {
        selectedSupplierId = id;
    }

    public static int getSelectedSupplierId() {
        return selectedSupplierId;
    }

}
