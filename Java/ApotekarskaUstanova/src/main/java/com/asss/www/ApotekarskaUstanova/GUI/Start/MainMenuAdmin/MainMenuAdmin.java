/*
 * Created by JFormDesigner on Tue Nov 26 20:09:01 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin;

import com.asss.www.ApotekarskaUstanova.GUI.CashRegister.CashRegister.CashRegister;
import com.asss.www.ApotekarskaUstanova.GUI.CashRegister.SalesHistory.SalesHistory;
import com.asss.www.ApotekarskaUstanova.GUI.CashRegister.SuppliesHistory.SuppliesHistory;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList.EmployeeList;
import com.asss.www.ApotekarskaUstanova.GUI.Finance.EmployeeMenu.EmployeeMenu;
import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.Inventory.Inventory;
import com.asss.www.ApotekarskaUstanova.GUI.Start.StartPage.StartPage;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.NewShipment.NewShipment;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.OrderSupplies.OrderSupplies;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierList.SupplierList;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author lniko
 */
public class MainMenuAdmin extends JFrame {
    public MainMenuAdmin() {
        initComponents();
    }
    public static void start() {
        SwingUtilities.invokeLater(() -> {
            MainMenuAdmin frame = new MainMenuAdmin();
            frame.setTitle("Apotekarska Ustanova");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }


    private void ZaposleniMouseClicked(MouseEvent e) {
        dispose();
        EmployeeMenu.start();
    }

    private void InventarMouseClicked(MouseEvent e) {
        dispose();
        Inventory.start();
    }

    private void SuppliersMouseClicked(MouseEvent e) {
        dispose();
        SupplierList.start();
    }

    private void cashRegistryHistoryMouseClicked(MouseEvent e) {
        dispose();
        SalesHistory.start();
    }

    private void shipmentsHistoryMouseClicked(MouseEvent e) {
        dispose();
        SuppliesHistory.start();
    }

    private void logOutMouseClicked(MouseEvent e) {
        dispose();
        StartPage.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel = new JPanel();
        Zaposleni = new JButton();
        Inventar = new JButton();
        Suppliers = new JButton();
        cashRegistryHistory = new JButton();
        shipmentsHistory = new JButton();
        logOut = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== panel ========
        {
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

            //---- Zaposleni ----
            Zaposleni.setText("Zaposleni");
            Zaposleni.setBackground(new Color(0xb3d8a8));
            Zaposleni.setForeground(Color.darkGray);
            Zaposleni.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ZaposleniMouseClicked(e);
                }
            });
            panel.add(Zaposleni, "cell 3 2 2 1");

            //---- Inventar ----
            Inventar.setText("Inventar");
            Inventar.setBackground(new Color(0xb3d8a8));
            Inventar.setForeground(Color.darkGray);
            Inventar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    InventarMouseClicked(e);
                }
            });
            panel.add(Inventar, "cell 3 3 2 1");

            //---- Suppliers ----
            Suppliers.setText("Dobavljaci");
            Suppliers.setBackground(new Color(0xb3d8a8));
            Suppliers.setForeground(Color.darkGray);
            Suppliers.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    SuppliersMouseClicked(e);
                }
            });
            panel.add(Suppliers, "cell 3 4 2 1");

            //---- cashRegistryHistory ----
            cashRegistryHistory.setText("Istorija Prodaje");
            cashRegistryHistory.setBackground(new Color(0xb3d8a8));
            cashRegistryHistory.setForeground(Color.darkGray);
            cashRegistryHistory.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cashRegistryHistoryMouseClicked(e);
                }
            });
            panel.add(cashRegistryHistory, "cell 3 5 3 1");

            //---- shipmentsHistory ----
            shipmentsHistory.setText("Istorija Nabavke");
            shipmentsHistory.setBackground(new Color(0xb3d8a8));
            shipmentsHistory.setForeground(Color.darkGray);
            shipmentsHistory.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    shipmentsHistoryMouseClicked(e);
                }
            });
            panel.add(shipmentsHistory, "cell 3 6 3 1");

            //---- logOut ----
            logOut.setText("Odjavi Se");
            logOut.setBackground(new Color(0xb3d8a8));
            logOut.setForeground(Color.darkGray);
            logOut.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    logOutMouseClicked(e);
                }
            });
            panel.add(logOut, "cell 6 8 2 1");
        }
        contentPane.add(panel);
        panel.setBounds(0, 0, 500, 470);

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
    private JButton Suppliers;
    private JButton cashRegistryHistory;
    private JButton shipmentsHistory;
    private JButton logOut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
