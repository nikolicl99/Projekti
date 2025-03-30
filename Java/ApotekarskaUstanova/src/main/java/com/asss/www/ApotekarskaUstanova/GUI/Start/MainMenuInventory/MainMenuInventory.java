/*
 * Created by JFormDesigner on Tue Mar 18 21:52:42 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuInventory;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

import com.asss.www.ApotekarskaUstanova.GUI.InventoryGUI.Inventory.Inventory;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenuAdmin.MainMenuAdmin;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.NewShipment.NewShipment;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.OrderSupplies.OrderSupplies;
import com.asss.www.ApotekarskaUstanova.GUI.Suppliers.SupplierList.SupplierList;
import net.miginfocom.swing.*;

/**
 * @author lniko
 */
public class MainMenuInventory extends JFrame {
    public MainMenuInventory() {
        initComponents();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            MainMenuInventory frame = new MainMenuInventory();
            frame.setTitle("Glavni Meni");
            frame.setSize(500, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void InventarMouseClicked(MouseEvent e) {
        dispose();
        Inventory.start();
    }

    private void newDeliveryMouseClicked(MouseEvent e) {
        dispose();
        NewShipment.start();
    }

    private void SuppliersMouseClicked(MouseEvent e) {
        dispose();
        SupplierList.start();
    }

    private void orderMouseClicked(MouseEvent e) {
        dispose();
        OrderSupplies.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel = new JPanel();
        order = new JButton();
        Inventar = new JButton();
        newDelivery = new JButton();
        Suppliers = new JButton();
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
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]",
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

            //---- order ----
            order.setText("Nabavka");
            order.setForeground(Color.darkGray);
            order.setBackground(new Color(0xb3d8a8));
            order.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    orderMouseClicked(e);
                }
            });
            panel.add(order, "cell 2 2 2 1");

            //---- Inventar ----
            Inventar.setText("Inventar");
            Inventar.setForeground(Color.darkGray);
            Inventar.setBackground(new Color(0xb3d8a8));
            Inventar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    InventarMouseClicked(e);
                }
            });
            panel.add(Inventar, "cell 2 3 2 1");

            //---- newDelivery ----
            newDelivery.setText("Nova Dostava");
            newDelivery.setForeground(Color.darkGray);
            newDelivery.setBackground(new Color(0xb3d8a8));
            newDelivery.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    newDeliveryMouseClicked(e);
                }
            });
            panel.add(newDelivery, "cell 2 4 2 1");

            //---- Suppliers ----
            Suppliers.setText("Dobavljaci");
            Suppliers.setForeground(Color.darkGray);
            Suppliers.setBackground(new Color(0xb3d8a8));
            Suppliers.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    SuppliersMouseClicked(e);
                }
            });
            panel.add(Suppliers, "cell 2 5 2 1");

            //---- logOut ----
            logOut.setText("Odjavi Se");
            logOut.setBackground(new Color(0xb3d8a8));
            logOut.setForeground(Color.darkGray);
            panel.add(logOut, "cell 3 7");
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
    private JButton order;
    private JButton Inventar;
    private JButton newDelivery;
    private JButton Suppliers;
    private JButton logOut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
