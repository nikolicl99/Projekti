/*
 * Created by JFormDesigner on Tue Nov 26 21:54:48 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList;

import java.awt.*;
import java.awt.event.*;

import com.asss.www.ApotekarskaUstanova.GUI.Finance.AddSalary.AddSalary;
import com.asss.www.ApotekarskaUstanova.GUI.Finance.EmployeeMenu.EmployeeMenu;
import com.asss.www.ApotekarskaUstanova.GUI.Finance.EverySalary.EverySalary;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.AddEmployee.AddEmployee;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin.MainMenuAdmin;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeView.EmployeeView;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.fasterxml.jackson.core.type.TypeReference;  // Ovaj import je potreban za TypeReference
import net.miginfocom.swing.*;
/**
 * @author lniko
 */
public class EmployeeList extends JFrame {
    public EmployeeList() {
            initComponents(); // Komponente se kreiraju nakon što je stil primenjen
            PrikaziZaposlene();
            setSelectedEmployeeId(0);
        }

        public static void start() {
        SwingUtilities.invokeLater(() -> {
            EmployeeList frame = new EmployeeList();
            frame.setTitle("Zaposleni");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void ZaposleniMouseClicked(MouseEvent e) {
        int selectedRow = Zaposleni.getSelectedRow(); // Dohvata indeks izabranog reda
        if (selectedRow >= 0) { // Proverava da li je red validan
            // Pretpostavlja se da je ID u prvoj koloni (kolona 0)
            int id = (int) Zaposleni.getValueAt(selectedRow, 0);
            System.out.println("Izabrani ID zaposlenog: " + id);

            setSelectedEmployeeId(id);
            // Skladišti ID u promenljivu za kasniju upotrebu
            EmployeeView.setSelectedEmployeeId(id);

            // Primer: otvaranje novog prozora za izmenu podataka
            if (e.getClickCount() == 2) { // Ako je dvoklik
                dispose();
                EmployeeView.setPreviousForm(1);
                EmployeeView.start();
            }
        }
    }

    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        EmployeeMenu.start();
    }

    private void ObrisiMouseClicked(MouseEvent e) {
        if (getSelectedEmployeeId() != 0) {

            int response = JOptionPane.showConfirmDialog(this, "Da li ste sigurni da želite da otpustite zaposlenog?", "Potvrda", JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.YES_OPTION) return;

            // Slanje HTTP zahteva na server
            try {
                int id = getSelectedEmployeeId();
                URL url = new URL("http://localhost:8080/api/employees/fire/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());
                connection.setDoOutput(true);

                // Provera odgovora servera
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JOptionPane.showMessageDialog(this, "Zaposleni uspešno otpušten.", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                    PrikaziZaposlene(); // Osvežite tabelu nakon uspešnog zahteva
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

    private void PrikaziZaposlene() {
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

                        if (employees.getEmployed() == 1) {
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
        Zaposleni.setModel(model);
        customizeTable(Zaposleni, model);
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

    private void DodajMouseClicked(MouseEvent e) {
        dispose();
        AddEmployee.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel = new JPanel();
        Nazad = new JButton();
        scrollPane1 = new JScrollPane();
        Zaposleni = new JTable();
        Obrisi = new JButton();
        Dodaj = new JButton();

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
            panel.add(Nazad, "cell 1 0 2 1");

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(Color.darkGray);
                scrollPane1.setForeground(Color.darkGray);

                //---- Zaposleni ----
                Zaposleni.setBackground(new Color(0xfbffe4));
                Zaposleni.setForeground(Color.darkGray);
                Zaposleni.setGridColor(Color.darkGray);
                Zaposleni.setSelectionBackground(new Color(0xb3d8a8));
                Zaposleni.setSelectionForeground(Color.darkGray);
                Zaposleni.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ZaposleniMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(Zaposleni);
            }
            panel.add(scrollPane1, "cell 1 1 8 8");

            //---- Obrisi ----
            Obrisi.setText("Otpusti");
            Obrisi.setBackground(new Color(0xb3d8a8));
            Obrisi.setForeground(Color.darkGray);
            Obrisi.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ObrisiMouseClicked(e);
                }
            });
            panel.add(Obrisi, "cell 3 9 2 1");

            //---- Dodaj ----
            Dodaj.setText("Novi Zaposleni");
            Dodaj.setBackground(new Color(0xb3d8a8));
            Dodaj.setForeground(Color.darkGray);
            Dodaj.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    DodajMouseClicked(e);
                }
            });
            panel.add(Dodaj, "cell 5 9 3 1");
        }
        contentPane.add(panel);
        setSize(490, 500);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel;
    private JButton Nazad;
    private JScrollPane scrollPane1;
    private JTable Zaposleni;
    private JButton Obrisi;
    private JButton Dodaj;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    private static int selectedEmployeeId;

    public static int getSelectedEmployeeId() {
        return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(int selectedEmployeeId) {
        EmployeeList.selectedEmployeeId = selectedEmployeeId;
    }
}
