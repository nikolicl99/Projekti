/*
 * Created by JFormDesigner on Tue Nov 26 20:09:01 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenu;

import com.asss.www.ApotekarskaUstanova.GUI.CashRegister.CashRegister.CashRegister;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList.EmployeeList;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.InventoryMenu.InventoryMenu;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.RoleManagement.RoleManagement;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierList.SupplierList;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author lniko
 */
public class MainMenu extends JFrame {
    public MainMenu() {
        initComponents();
    }
    public static void start() {
        SwingUtilities.invokeLater(() -> {
            MainMenu frame = new MainMenu();
            frame.setTitle("Glavni Meni");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }


    private void ZaposleniMouseClicked(MouseEvent e) {
        dispose();
        EmployeeList.start();
        // TODO add your code here
        // error message
        // JOptionPane.showMessageDialog(this, "kliknuli na zaposlne", "failure", JOptionPane.ERROR_MESSAGE);
    }

//    private void UlogeMouseClicked(MouseEvent e) {
//        dispose();
//        RoleManagement.start();
//    }

    private void InventarMouseClicked(MouseEvent e) {
        dispose();
        InventoryMenu.start();
    }

    private void SuppliersMouseClicked(MouseEvent e) {
        dispose();
        SupplierList.start();
    }
    private void cashRegisterMouseClicked(MouseEvent e) {
        dispose();
        CashRegister.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel = new JPanel();
        Zaposleni = new JButton();
        Inventar = new JButton();
        Izvestaji = new JButton();
        Suppliers = new JButton();
        cashRegister = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== panel ========
        {
            panel.setLayout(null);

            //---- Zaposleni ----
            Zaposleni.setText("Zaposleni");
            Zaposleni.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ZaposleniMouseClicked(e);
                }
            });
            panel.add(Zaposleni);
            Zaposleni.setBounds(195, 150, 105, 40);

            //---- Inventar ----
            Inventar.setText("Inventar");
            Inventar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    InventarMouseClicked(e);
                }
            });
            panel.add(Inventar);
            Inventar.setBounds(195, 195, 105, 40);

            //---- Izvestaji ----
            Izvestaji.setText("Izvestaji");
            panel.add(Izvestaji);
            Izvestaji.setBounds(195, 240, 105, 45);

            //---- Suppliers ----
            Suppliers.setText("Dobavljaci");
            Suppliers.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    SuppliersMouseClicked(e);
                }
            });
            panel.add(Suppliers);
            Suppliers.setBounds(195, 290, 105, 45);

            //---- cashRegister ----
            cashRegister.setText("Kasa");
            cashRegister.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cashRegisterMouseClicked(e);
                }
            });
            panel.add(cashRegister);
            cashRegister.setBounds(195, 340, 105, 45);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel.getComponentCount(); i++) {
                    Rectangle bounds = panel.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel.setMinimumSize(preferredSize);
                panel.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(panel);
        panel.setBounds(0, 5, 500, 465);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel;
    private JButton Zaposleni;
    private JButton Inventar;
    private JButton Izvestaji;
    private JButton Suppliers;
    private JButton cashRegister;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
