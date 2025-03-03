/*
 * Created by JFormDesigner on Sun Dec 22 18:56:41 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Employee.RoleManagement;

import java.awt.event.*;
import com.asss.www.ApotekarskaUstanova.Dto.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Employee_Type;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.AddRole.AddRole;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenu.MainMenu;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.RoleDetails.RoleDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

/**
 * @author lniko
 */
public class RoleManagement extends JFrame {
    public RoleManagement() {
        initComponents();
        showRoles();
    }
    public static void start() {
        SwingUtilities.invokeLater(() -> {
            RoleManagement frame = new RoleManagement();
            frame.setTitle("Role Management");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void RolesMouseClicked(MouseEvent e) {
        int selectedRow = Roles.getSelectedRow(); // Dohvata indeks izabranog reda
        if (selectedRow >= 0) { // Proverava da li je red validan
            // Pretpostavlja se da je ID u prvoj koloni (kolona 0)
            int id = (int) Roles.getValueAt(selectedRow, 0);
            System.out.println("Izabrani ID uloge: " + id);

            // Skladišti ID u promenljivu za kasniju upotrebu
            setSelectedRoleID(id);

            // Primer: otvaranje novog prozora za izmenu podataka
            if (e.getClickCount() == 2) { // Ako je dvoklik
//                EditZaposleni editWindow = new EditZaposleni(selectedEmployeeId);
//                editWindow.setVisible(true);
                JOptionPane.showMessageDialog(this, "Izabrana je uloga: " + getSelectedRoleID());
            }
        }
    }

    private void showRoles() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };

        model.addColumn("ID");
        model.addColumn("Naziv Uloge");

        try {
            // URL za API zaposlenih
            URL url = new URL("http://localhost:8080/api/employee-type");
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
                    List<Employee_Type> employeeTypeList = objectMapper.readValue(response, new TypeReference<List<Employee_Type>>() {
                    });

                    for (Employee_Type employeeType : employeeTypeList) {
                        model.addRow(new Object[]{
                                employeeType.getId(),
                                employeeType.getName()
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

        Roles.setModel(model);
    }

    private void ObrisiUlogu(long ID) {
        try {
            // URL za API za brisanje zaposlenog
            URL url = new URL("http://localhost:8080/api/employee-type/" + ID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            // Dodavanje Authorization header-a sa tokenom
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            // Provera odgovora API-ja
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(this, "Uloga je uspešno obrisana.", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                showRoles(); // Osvežavanje prikaza nakon brisanja
            } else {
                JOptionPane.showMessageDialog(this, "Greška prilikom brisanja uloga! Kod greške: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Došlo je do greške prilikom brisanja uloge!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ObrisiMouseClicked(MouseEvent e) {
        if (getSelectedRoleID() == 0) {
            JOptionPane.showMessageDialog(this, "Odaberite Ulogu", "Odabir Ulogu", JOptionPane.ERROR_MESSAGE);
            return; // Ne pokušavaj brisanje ako ID nije izabran
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da želite obrisati ulogu?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                ObrisiUlogu(getSelectedRoleID());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Došlo je do greške prilikom brisanja!", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void PrikaziMouseClicked(MouseEvent e) {
        dispose();
        RoleDetails.start();
    }

    private void DodajMouseClicked(MouseEvent e) {
        dispose();
        AddRole.start();
    }

    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        MainMenu.start();
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Luka Nikolić
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        Roles = new JTable();
        Dodaj = new JButton();
        Prikazi = new JButton();
        Obrisi = new JButton();
        Nazad = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new
            javax.swing.border.EmptyBorder(0,0,0,0), "JF\u006frmDesi\u0067ner Ev\u0061luatio\u006e",javax
            .swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM,new java
            .awt.Font("Dialo\u0067",java.awt.Font.BOLD,12),java.awt
            .Color.red),panel1. getBorder()));panel1. addPropertyChangeListener(new java.beans.
            PropertyChangeListener(){@Override public void propertyChange(java.beans.PropertyChangeEvent e){if("borde\u0072".
            equals(e.getPropertyName()))throw new RuntimeException();}});

            //======== scrollPane1 ========
            {

                //---- Roles ----
                Roles.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        RolesMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(Roles);
            }

            //---- Dodaj ----
            Dodaj.setText("Dodaj");
            Dodaj.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    DodajMouseClicked(e);
                }
            });

            //---- Prikazi ----
            Prikazi.setText("Prikazi");
            Prikazi.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PrikaziMouseClicked(e);
                }
            });

            //---- Obrisi ----
            Obrisi.setText("Obrisi");
            Obrisi.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ObrisiMouseClicked(e);
                }
            });

            //---- Nazad ----
            Nazad.setText("Nazad");
            Nazad.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NazadMouseClicked(e);
                }
            });

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(Dodaj, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Prikazi, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Obrisi, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Nazad, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(89, Short.MAX_VALUE))
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(Nazad, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 345, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(Prikazi, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                                .addComponent(Obrisi, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
                            .addComponent(Dodaj, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Luka Nikolić
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JTable Roles;
    private JButton Dodaj;
    private JButton Prikazi;
    private JButton Obrisi;
    private JButton Nazad;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static int SelectedRoleID;

    public static int getSelectedRoleID() {
        return SelectedRoleID;
    }

    public void setSelectedRoleID(int selectedRoleID) {
        SelectedRoleID = selectedRoleID;
    }
}
