/*
 * Created by JFormDesigner on Mon Mar 10 23:20:16 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.CashRegister.SalesItems;

import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.Dto.EmployeeDto;
import com.asss.www.ApotekarskaUstanova.Dto.SalesItemDto;
import com.asss.www.ApotekarskaUstanova.Entity.Sales;
import com.asss.www.ApotekarskaUstanova.GUI.CashRegister.SalesHistory.SalesHistory;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.miginfocom.swing.*;

/**
 * @author lniko
 */
public class SalesItems extends JFrame {

    public SalesItems() {
        initComponents();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Registrujte JavaTimeModule
        int saleId = SalesHistory.getSelectedSale();

        String[] columnNames = {"Proizvod", "Ean13", "Recept", "Cena po komadu", "Kolicina", "Ukupna cena"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        customizeTable(Items, model);

        LoadSale(saleId);
        LoadSalesItems(saleId);
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            SalesItems frame = new SalesItems();
            frame.setTitle("Prodaja");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void LoadSale(int saleId) {
        try {
            URL url = new URL("http://localhost:8080/api/sales/" + saleId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora u objekat Employees
                    Sales sales = objectMapper.readValue(response, Sales.class);

                    date.setText(sales.getTransactionDate().toString() + " / " + sales.getTransactionTime().toString());
                    employee.setText(sales.getCashier().getName() + " " + sales.getCashier().getSurname());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri učitavanju detalja prodaje.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri dobavljanju detalja prodaje.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void LoadSalesItems(int selectedSale) {
        DefaultTableModel model = (DefaultTableModel) Items.getModel();
        System.out.println("selectedSale: " + selectedSale);
        try {
            // Dohvati sve prodaje za dati datum
            URL url = new URL("http://localhost:8080/api/sales_items/" + selectedSale + "/items");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream();
                     Scanner scanner = new Scanner(inputStream)) {

                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("JSON Response: " + response);

                    List<SalesItemDto> items = objectMapper.readValue(response, new TypeReference<List<SalesItemDto>>() {});
                    for (int i = 0; i < items.size(); i++) {
                        SalesItemDto item = items.get(i);
//                        String employeeName = fetchEmployeeName(item.getEmployeeId().getId());
                        String data0 = item.getProductBatch().getProduct().getName();
                        String data1 = String.valueOf(item.getProductBatch().getEan13());
                        String data2 = item.getReceiptType();
                        String data3 = String.valueOf(item.getProductBatch().getProduct().getSellingPrice());
                        String data4 = String.valueOf(item.getQuantity());
                        String data5 = String.valueOf(item.getTotalPrice());

                        model.addRow(new Object[]{data0, data1, data2, data3, data4, data5});
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri učitavanju stavki prodaje.", "Greška", JOptionPane.ERROR_MESSAGE);
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
        JViewport viewport = scrollPane.getViewport();
        viewport.setBackground(backgroundColor);
        scrollPane.setBackground(backgroundColor);
    }


    private String fetchEmployeeName(long employeeId) {
        try {
            URL url = new URL("http://localhost:8080/api/employees/" + employeeId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    ObjectMapper objectMapper = new ObjectMapper();
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

    private void BackMouseClicked(MouseEvent e) {
        dispose();
        SalesHistory.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel = new JPanel();
        label1 = new JLabel();
        date = new JTextField();
        Back = new JButton();
        label2 = new JLabel();
        employee = new JTextField();
        scrollPane = new JScrollPane();
        Items = new JTable();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel ========
        {
            panel.setBackground(new Color(0x3d8d7a));
            panel.setLayout(new MigLayout(
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

            //---- label1 ----
            label1.setText("Datum:");
            label1.setForeground(new Color(0xfbffe4));
            panel.add(label1, "cell 6 0");

            //---- date ----
            date.setBackground(new Color(0xb3d8a8));
            date.setForeground(Color.darkGray);
            date.setEditable(false);
            panel.add(date, "cell 7 0 2 1");

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
            panel.add(Back, "cell 1 1");

            //---- label2 ----
            label2.setText("Zaposleni:");
            label2.setForeground(new Color(0xfbffe4));
            panel.add(label2, "cell 6 1");

            //---- employee ----
            employee.setBackground(new Color(0xb3d8a8));
            employee.setForeground(Color.darkGray);
            employee.setEditable(false);
            panel.add(employee, "cell 7 1 2 1");

            //======== scrollPane ========
            {
                scrollPane.setBackground(Color.darkGray);
                scrollPane.setForeground(Color.darkGray);

                //---- Items ----
                Items.setBackground(new Color(0xfbffe4));
                Items.setForeground(Color.darkGray);
                Items.setGridColor(Color.darkGray);
                Items.setSelectionBackground(new Color(0xb3d8a8));
                Items.setSelectionForeground(Color.darkGray);
                scrollPane.setViewportView(Items);
            }
            panel.add(scrollPane, "cell 2 3 6 7");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel;
    private JLabel label1;
    private JTextField date;
    private JButton Back;
    private JLabel label2;
    private JTextField employee;
    private JScrollPane scrollPane;
    private JTable Items;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private final ObjectMapper objectMapper; // Dodajte ObjectMapper kao polje klase
}
