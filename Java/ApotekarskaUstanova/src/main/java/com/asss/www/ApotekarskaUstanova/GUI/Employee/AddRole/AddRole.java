/*
 * Created by JFormDesigner on Mon Dec 23 00:50:46 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Employee.AddRole;

import com.asss.www.ApotekarskaUstanova.Dto.JwtResponse;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.RoleManagement.RoleManagement;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author lniko
 */
public class AddRole extends JFrame {
    public AddRole() {
        initComponents();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            AddRole frame = new AddRole();
            frame.setTitle("Add Role");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void AddMouseClicked(MouseEvent e) {
        String name = Uloga.getText().trim();

        if (name.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "Uloga mora imati naziv.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int addEmployeeValue = isCheckboxChecked(addEmployee);
        int editEmployeeValue = isCheckboxChecked(editEmployee);
        int manageInvValue = isCheckboxChecked(manageInv);
        int viewReportValue = isCheckboxChecked(viewReport);
        int generateReportValue = isCheckboxChecked(generateReport);
        int manageRolesValue = isCheckboxChecked(manageRoles);

        // Provera JWT tokena
        String jwtToken = JwtResponse.getToken();  // Pretpostavljam da imate metodu koja vraća token

        if (jwtToken == null || jwtToken.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Niste ulogovani! Token nije dostupan.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // URL za API za dodavanje zaposlenog
        String apiUrl = "http://localhost:8080/api/employee-type";

        // Kreiranje JSON objekta sa podacima zaposlenog
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("canAddEmployee", addEmployeeValue);
        json.put("canEditEmployee", editEmployeeValue);
        json.put("canManageInventory", manageInvValue);
        json.put("canViewReports", viewReportValue);
        json.put("canGenerateReports", generateReportValue);
        json.put("canManageRoles", manageRolesValue);

        // Postavljanje headera sa JWT tokenom
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        try {
            // Koristi RestTemplate za slanje POST zahteva sa podacima
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Provera odgovora
            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                JOptionPane.showMessageDialog(this,
                        "Uloga uspešno dodata.",
                        "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                resetFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Greška prilikom dodavanja uloge.",
                        "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Greška pri unosu podataka u bazu.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        Uloga.setText("");
        addEmployee.setSelected(false);
        editEmployee.setSelected(false);
        manageInv.setSelected(false);
        viewReport.setSelected(false);
        generateReport.setSelected(false);
        manageRoles.setSelected(false);
    }

    private int isCheckboxChecked(JCheckBox checkbox) {
        if (checkbox.isSelected()) {
            return 1;
        } else {
            return 0;
        }
    }

    private void BackMouseClicked(MouseEvent e) {
        dispose();
        RoleManagement.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Luka Nikolić
        panel1 = new JPanel();
        addEmployee = new JCheckBox();
        editEmployee = new JCheckBox();
        manageInv = new JCheckBox();
        manageRoles = new JCheckBox();
        viewReport = new JCheckBox();
        generateReport = new JCheckBox();
        Uloga = new JTextField();
        roleName = new JLabel();
        Add = new JButton();
        Back = new JButton();

        //======== this ========
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new
            javax.swing.border.EmptyBorder(0,0,0,0), "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn",javax
            .swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM,new java
            .awt.Font("Dia\u006cog",java.awt.Font.BOLD,12),java.awt
            .Color.red),panel1. getBorder()));panel1. addPropertyChangeListener(new java.beans.
            PropertyChangeListener(){@Override public void propertyChange(java.beans.PropertyChangeEvent e){if("\u0062ord\u0065r".
            equals(e.getPropertyName()))throw new RuntimeException();}});

            //---- addEmployee ----
            addEmployee.setText("Dodavanje Zaposlenih");

            //---- editEmployee ----
            editEmployee.setText("Editovanje Zaposlenih");

            //---- manageInv ----
            manageInv.setText("Upravljanje Inventarom");

            //---- manageRoles ----
            manageRoles.setText("Upravljanje Ulogama");

            //---- viewReport ----
            viewReport.setText("Pregled Izvestaja");

            //---- generateReport ----
            generateReport.setText("Generisanje Izvestaja");

            //---- roleName ----
            roleName.setText("Naziv Uloge:");

            //---- Add ----
            Add.setText("Dodaj");
            Add.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    AddMouseClicked(e);
                }
            });

            //---- Back ----
            Back.setText("Nazad");
            Back.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BackMouseClicked(e);
                }
            });

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(153, 153, 153)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(Uloga, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(manageRoles, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(generateReport, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(viewReport, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(manageInv, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(editEmployee, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(addEmployee, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))
                                    .addComponent(roleName, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(Add, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Back, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(157, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(Back, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(roleName)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Uloga, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(addEmployee)
                        .addGap(18, 18, 18)
                        .addComponent(editEmployee)
                        .addGap(18, 18, 18)
                        .addComponent(manageInv)
                        .addGap(18, 18, 18)
                        .addComponent(viewReport)
                        .addGap(18, 18, 18)
                        .addComponent(generateReport)
                        .addGap(18, 18, 18)
                        .addComponent(manageRoles)
                        .addGap(36, 36, 36)
                        .addComponent(Add, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(10, Short.MAX_VALUE))
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
    private JCheckBox addEmployee;
    private JCheckBox editEmployee;
    private JCheckBox manageInv;
    private JCheckBox manageRoles;
    private JCheckBox viewReport;
    private JCheckBox generateReport;
    private JTextField Uloga;
    private JLabel roleName;
    private JButton Add;
    private JButton Back;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on


}
