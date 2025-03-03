/*
 * Created by JFormDesigner on Sun Dec 22 22:01:44 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Employee.RoleDetails;

import java.awt.event.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.asss.www.ApotekarskaUstanova.Dto.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Employee_Type;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.RoleManagement.RoleManagement;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author lniko
 */
public class RoleDetails extends JFrame {
    public RoleDetails() {
        initComponents();
        fetchRoleDetails();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            RoleDetails frame = new RoleDetails();
            frame.setTitle("Role Details");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void fetchRoleDetails() {
        int roleID = RoleManagement.getSelectedRoleID();
        String urlString = "http://localhost:8080/api/employee-type/" + roleID;
        try {
            // Priprema URL-a
            URL url = new URL(urlString);

            // Otvaranje konekcije
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Dodavanje Authorization header-a sa JWT tokenom
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            // Provera odgovora servera
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Čitanje odgovora servera
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    // Parsiranje JSON odgovora u objekat Employees
                    ObjectMapper objectMapper = new ObjectMapper();
                    Employee_Type employeeType = objectMapper.readValue(response, Employee_Type.class);

                    // Popunjavanje podataka u UI komponentama
                    if (employeeType != null) {
                        SelectedRole.setText(employeeType.getName());
                        AddEmployee.setText(String.valueOf(employeeType.isCanAddEmployee()));
                        EditEmployee.setText(String.valueOf(employeeType.isCanEditEmployee()));
                        InventoryManag.setText(String.valueOf(employeeType.isCanManageInventory()));
                        ReportView.setText(String.valueOf(employeeType.isCanViewReports()));
                        ReportGenerate.setText(String.valueOf(employeeType.isCanGenerateReports()));
                        RoleManag.setText(String.valueOf(employeeType.isCanManageRoles()));
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // Token nije validan
                JOptionPane.showMessageDialog(this, "Nevažeći token. Prijavite se ponovo.", "Greška", JOptionPane.WARNING_MESSAGE);
            } else {
                // Drugi statusni kodovi
                JOptionPane.showMessageDialog(this, "Greška pri dohvatu podataka: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        RoleManagement.start();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Luka Nikolić
        panel1 = new JPanel();
        SelectedRole = new JTextField();
        AddEmployee_Label = new JLabel();
        AddEmployee = new JTextField();
        EditEmployee_Label = new JLabel();
        EditEmployee = new JTextField();
        InventoryManag_Label = new JLabel();
        InventoryManag = new JTextField();
        ReportView_Label = new JLabel();
        ReportView = new JTextField();
        ReportGenerate_Label = new JLabel();
        ReportGenerate = new JTextField();
        RoleManag_Label = new JLabel();
        RoleManag = new JTextField();
        Nazad = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing.
            border. EmptyBorder( 0, 0, 0, 0) , "JF\u006frmDesi\u0067ner Ev\u0061luatio\u006e", javax. swing. border. TitledBorder. CENTER
            , javax. swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dialo\u0067" ,java .awt .Font
            .BOLD ,12 ), java. awt. Color. red) ,panel1. getBorder( )) ); panel1. addPropertyChangeListener (
            new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("borde\u0072"
            .equals (e .getPropertyName () )) throw new RuntimeException( ); }} );

            //---- AddEmployee_Label ----
            AddEmployee_Label.setText("Dodavanje Zaposlenog:");

            //---- EditEmployee_Label ----
            EditEmployee_Label.setText("Editovanje Zaposlenog:");

            //---- InventoryManag_Label ----
            InventoryManag_Label.setText("Upravljanje Inventarom:");

            //---- ReportView_Label ----
            ReportView_Label.setText("Pregled Izvestaja:");

            //---- ReportGenerate_Label ----
            ReportGenerate_Label.setText("Generisanje Izvestaja:");

            //---- RoleManag_Label ----
            RoleManag_Label.setText("Menadzment Uloga:");

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
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap(82, Short.MAX_VALUE)
                                .addComponent(EditEmployee_Label, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42)
                                .addComponent(EditEmployee, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(82, 82, 82)
                                .addComponent(AddEmployee_Label, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                                .addComponent(AddEmployee, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(0, 82, Short.MAX_VALUE)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(RoleManag_Label, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
                                        .addGap(42, 42, 42)
                                        .addComponent(RoleManag, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(panel1Layout.createSequentialGroup()
                                            .addComponent(ReportGenerate_Label, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
                                            .addGap(42, 42, 42)
                                            .addComponent(ReportGenerate, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panel1Layout.createSequentialGroup()
                                            .addComponent(ReportView_Label, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
                                            .addGap(42, 42, 42)
                                            .addComponent(ReportView, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panel1Layout.createSequentialGroup()
                                            .addComponent(InventoryManag_Label, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
                                            .addGap(42, 42, 42)
                                            .addComponent(InventoryManag, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))))))
                        .addGap(102, 102, 102))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(154, 154, 154)
                                .addComponent(SelectedRole, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(Nazad, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(163, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(SelectedRole, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(AddEmployee, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(AddEmployee_Label, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(EditEmployee_Label, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                            .addComponent(EditEmployee, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(InventoryManag_Label, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                            .addComponent(InventoryManag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(ReportView_Label, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                            .addComponent(ReportView, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(ReportGenerate_Label, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                            .addComponent(ReportGenerate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(RoleManag_Label, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                            .addComponent(RoleManag, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                        .addComponent(Nazad, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))
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
    private JTextField SelectedRole;
    private JLabel AddEmployee_Label;
    private JTextField AddEmployee;
    private JLabel EditEmployee_Label;
    private JTextField EditEmployee;
    private JLabel InventoryManag_Label;
    private JTextField InventoryManag;
    private JLabel ReportView_Label;
    private JTextField ReportView;
    private JLabel ReportGenerate_Label;
    private JTextField ReportGenerate;
    private JLabel RoleManag_Label;
    private JTextField RoleManag;
    private JButton Nazad;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
