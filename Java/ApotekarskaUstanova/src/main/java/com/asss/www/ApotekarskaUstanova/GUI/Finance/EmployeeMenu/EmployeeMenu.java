/*
 * Created by JFormDesigner on Sun Dec 22 18:34:22 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Finance.EmployeeMenu;

import java.awt.*;
import java.awt.event.*;

import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList.EmployeeList;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.ExEmployeeList.ExEmployeeList;
import com.asss.www.ApotekarskaUstanova.GUI.Finance.EverySalary.EverySalary;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.Inventory.Inventory;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin.MainMenuAdmin;

import javax.swing.*;
import javax.swing.GroupLayout;
import net.miginfocom.swing.*;

/**
 * @author lniko
 */
public class EmployeeMenu extends JFrame {
    public EmployeeMenu() {
        initComponents();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            EmployeeMenu frame = new EmployeeMenu();
            frame.setTitle("Zaposleni");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void backMouseClicked(MouseEvent e) {
        dispose();
        MainMenuAdmin.start();
    }

    private void paychecksMouseClicked(MouseEvent e) {
        dispose();
        EverySalary.start();
    }

    private void employeesMouseClicked(MouseEvent e) {
        dispose();
        EmployeeList.start();
    }

    private void exEmployeesMouseClicked(MouseEvent e) {
        dispose();
        ExEmployeeList.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        employees = new JButton();
        exEmployees = new JButton();
        paychecks = new JButton();
        back = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0x3d8d7a));
            panel1.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]",
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
                "[75]"));

            //---- employees ----
            employees.setText("Zaposleni");
            employees.setBackground(new Color(0xb3d8a8));
            employees.setForeground(Color.darkGray);
            employees.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    employeesMouseClicked(e);
                }
            });
            panel1.add(employees, "cell 3 2 2 1");

            //---- exEmployees ----
            exEmployees.setText("Bivsi Zaposleni");
            exEmployees.setBackground(new Color(0xb3d8a8));
            exEmployees.setFont(exEmployees.getFont().deriveFont(exEmployees.getFont().getStyle() & ~Font.ITALIC));
            exEmployees.setForeground(Color.darkGray);
            exEmployees.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    exEmployeesMouseClicked(e);
                }
            });
            panel1.add(exEmployees, "cell 3 3 2 1");

            //---- paychecks ----
            paychecks.setText("Plate Zaposlenih");
            paychecks.setBackground(new Color(0xb3d8a8));
            paychecks.setForeground(Color.darkGray);
            paychecks.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    paychecksMouseClicked(e);
                }
            });
            panel1.add(paychecks, "cell 3 4 2 1");

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
            panel1.add(back, "cell 5 7");
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
    private JButton employees;
    private JButton exEmployees;
    private JButton paychecks;
    private JButton back;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
