/*
 * Created by JFormDesigner on Thu Mar 20 18:09:18 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Employee.ExEmployeeList;

import java.awt.*;
import java.awt.event.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList.EmployeeList;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeView.EmployeeView;
import com.asss.www.ApotekarskaUstanova.GUI.Finance.EmployeeMenu.EmployeeMenu;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.miginfocom.swing.*;

/**
 * @author lniko
 */
public class ExEmployeeList extends JFrame {
    public ExEmployeeList() {
        initComponents();
        LoadData();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            ExEmployeeList frame = new ExEmployeeList();
            frame.setTitle("Zaposleni");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void backMouseClicked(MouseEvent e) {
        dispose();
        EmployeeMenu.start();
    }

    private void ZaposleniMouseClicked(MouseEvent e) {
        int selectedRow = employees.getSelectedRow(); // Dohvata indeks izabranog reda
        if (selectedRow >= 0) { // Proverava da li je red validan
            // Pretpostavlja se da je ID u prvoj koloni (kolona 0)
            int id = (int) employees.getValueAt(selectedRow, 0);
            System.out.println("Izabrani ID zaposlenog: " + id);

            setSelectedEmployeeId(id);
            System.out.println(getSelectedEmployeeId());
            // Skladišti ID u promenljivu za kasniju upotrebu
            EmployeeView.setSelectedEmployeeId(id);

            // Primer: otvaranje novog prozora za izmenu podataka
            if (e.getClickCount() == 2) { // Ako je dvoklik
                dispose();
                EmployeeView.setPreviousForm(2);
                EmployeeView.start();
            }
        }
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
        model.addColumn("Tip Zaposlenog");

        try {
            // URL za API zaposlenih
            URL url = new URL("http://localhost:8080/api/employees");
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
                    List<Employees> employeeList = objectMapper.readValue(response, new TypeReference<List<Employees>>() {
                    });

                    // Popunjavanje modela podacima zaposlenih
                    for (Employees employees : employeeList) {
                        String typeName = (employees.getEmployeeType() != null)
                                ? employees.getEmployeeType().getName()
                                : "Nedefinisan tip";

                        if (employees.getEmployed() == 0) {
                            model.addRow(new Object[]{
                                    employees.getId(),
                                    employees.getName() + " " + employees.getSurname(),
                                    typeName
                            });
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "API greška: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka iz baze!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
        employees.setModel(model);
        customizeTable(employees, model);
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


    private void rehireMouseClicked(MouseEvent e) {
        if (getSelectedEmployeeId() != 0) {

            int response = JOptionPane.showConfirmDialog(this, "Da li ste sigurni da želite da ponovo zaposlite zaposlenog?", "Potvrda", JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.YES_OPTION) return;

            // Slanje HTTP zahteva na server
            try {
                int id = getSelectedEmployeeId();
                URL url = new URL("http://localhost:8080/api/employees/rehire/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
                connection.setDoOutput(true);

                // Provera odgovora servera
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JOptionPane.showMessageDialog(this, "Zaposleni uspešno ponovo zaposlen.", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                    LoadData(); // Osvežite tabelu nakon uspešnog zahteva
                } else {
                    JOptionPane.showMessageDialog(this, "Greška prilikom otpuštanja zaposlenog. Status: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Greška pri komunikaciji sa serverom.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Izaberite zaposlenog iz tabele.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel = new JPanel();
        back = new JButton();
        scrollPane1 = new JScrollPane();
        employees = new JTable();
        rehire = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== panel ========
        {
            panel.setPreferredSize(null);
            panel.setBackground(new Color(0x3d8d7a));
            panel.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]" +
                "[50]",
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
            panel.add(back, "cell 1 0 2 1");

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(Color.darkGray);
                scrollPane1.setForeground(Color.darkGray);

                //---- employees ----
                employees.setBackground(new Color(0xfbffe4));
                employees.setForeground(Color.darkGray);
                employees.setGridColor(Color.darkGray);
                employees.setSelectionBackground(new Color(0xb3d8a8));
                employees.setSelectionForeground(Color.darkGray);
                employees.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ZaposleniMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(employees);
            }
            panel.add(scrollPane1, "cell 1 1 8 8");

            //---- rehire ----
            rehire.setText("Ponovno zaposlenje");
            rehire.setBackground(new Color(0xb3d8a8));
            rehire.setForeground(Color.darkGray);
            rehire.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    rehireMouseClicked(e);
                }
            });
            panel.add(rehire, "cell 3 9 4 1,alignx center,growx 0");
        }
        contentPane.add(panel);
        setSize(490, 500);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel;
    private JButton back;
    private JScrollPane scrollPane1;
    private JTable employees;
    private JButton rehire;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static int selectedEmployeeId;

    public static int getSelectedEmployeeId() {
        return selectedEmployeeId;
    }

    public static void setSelectedEmployeeId(int selectedEmployeeId) {
        ExEmployeeList.selectedEmployeeId = selectedEmployeeId;
    }
}
