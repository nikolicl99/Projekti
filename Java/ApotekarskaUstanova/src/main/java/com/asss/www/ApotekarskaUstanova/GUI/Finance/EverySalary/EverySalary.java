/*
 * Created by JFormDesigner on Sun Mar 16 22:58:02 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Finance.EverySalary;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.Dto.SalaryDetailsDto;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList.EmployeeList;
import com.asss.www.ApotekarskaUstanova.GUI.Finance.AddSalary.AddSalary;
import com.asss.www.ApotekarskaUstanova.GUI.Finance.EmployeeSalary.EmployeeSalary;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import net.miginfocom.swing.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Objects;

/**
 * @author lniko
 */
public class EverySalary extends JFrame {
    public EverySalary() {
        initComponents();
        loadMonthsAndYears(); // Učitaj mesece i godine prilikom otvaranja prozora

        // Inicijalizacija tabele
        String[] columnNames = {"ID Plate", "Ime zaposlenog", "Ukupna plata"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        items.setModel(model);
        customizeTable(items, model);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        loadSalariesForMonthAndYear();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            EverySalary frame = new EverySalary();
            frame.setTitle("Salary");
            frame.setSize(800, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void loadMonthsAndYears() {
        String url = "http://localhost:8080/api/salaries/months-and-years";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + JwtResponse.getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<String>>() {}
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                List<String> monthsAndYears = responseEntity.getBody();
                months.removeAllItems();
                for (String monthAndYear : monthsAndYears) {
                    months.addItem(monthAndYear);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri učitavanju meseci i godina.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri učitavanju podataka.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSalariesForMonthAndYear() {
        DefaultTableModel model = (DefaultTableModel) items.getModel();
        String monthAndYear = Objects.requireNonNull(months.getSelectedItem()).toString();

        String url = "http://localhost:8080/api/salaries?monthAndYear=" + monthAndYear;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + JwtResponse.getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<SalaryDetailsDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<SalaryDetailsDto>>() {}
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                List<SalaryDetailsDto> salaries = responseEntity.getBody();
                model.setRowCount(0); // Očisti postojeće redove

                for (SalaryDetailsDto salary : salaries) {
                    model.addRow(new Object[]{
                            salary.getSalaryId(),
                            salary.getEmployeeName(),
                            salary.getTotalAmount()
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri učitavanju plata.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri učitavanju podataka.", "Greška", JOptionPane.ERROR_MESSAGE);
        } finally {
            customizeTable(items, model);
        }
    }

    private void openSalaryDetails(int salaryId) {
        // Otvori novi prozor ili prikaži detalje plate
        JOptionPane.showMessageDialog(this, "Otvaranje detalja za platu sa ID: " + salaryId, "Detalji plate", JOptionPane.INFORMATION_MESSAGE);
    }

    private void monthsItemStateChanged(ItemEvent e) {
        loadSalariesForMonthAndYear();
    }

    private void itemsMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) { // Dupli klik
            int selectedRow = items.getSelectedRow();
            if (selectedRow != -1) {
                // Uzmi ID plate iz prve kolone
                int salaryId = (int) items.getValueAt(selectedRow, 0);

                setSelectedSalaryId(salaryId);

                // Otvori klasu EmployeeSalary sa ID plate
                dispose();
                EmployeeSalary.start();
            }
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
        JViewport viewport = scrollPane1.getViewport();
        viewport.setBackground(backgroundColor);
        scrollPane1.setBackground(backgroundColor);
    }


    private void addMouseClicked(MouseEvent e) {
        dispose();
        AddSalary.start();
    }

    private void backMouseClicked(MouseEvent e) {
        dispose();
        EmployeeList.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        back = new JButton();
        months = new JComboBox();
        scrollPane1 = new JScrollPane();
        items = new JTable();
        add = new JButton();

        //======== this ========
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0x3d8d7a));
            panel1.setLayout(new MigLayout(
                "fill,hidemode 3",
                // columns
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
                "[100]" +
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
            panel1.add(back, "cell 1 1");

            //---- months ----
            months.setBackground(new Color(0xb3d8a8));
            months.setForeground(Color.darkGray);
            months.addItemListener(e -> monthsItemStateChanged(e));
            panel1.add(months, "cell 3 1 4 1,growx");

            //======== scrollPane1 ========
            {
                scrollPane1.setBackground(Color.darkGray);
                scrollPane1.setForeground(Color.darkGray);

                //---- items ----
                items.setBackground(new Color(0xfbffe4));
                items.setForeground(Color.darkGray);
                items.setGridColor(Color.darkGray);
                items.setSelectionBackground(new Color(0xb3d8a8));
                items.setSelectionForeground(Color.darkGray);
                items.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        itemsMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(items);
            }
            panel1.add(scrollPane1, "cell 2 2 6 8,grow");

            //---- add ----
            add.setText("Dodaj");
            add.setBackground(new Color(0xb3d8a8));
            add.setForeground(Color.darkGray);
            add.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    addMouseClicked(e);
                }
            });
            panel1.add(add, "cell 8 5");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
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
    private JButton back;
    private JComboBox months;
    private JScrollPane scrollPane1;
    private JTable items;
    private JButton add;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static int selectedSalaryId;

    public static int getSelectedSalaryId() {
        return selectedSalaryId;
    }

    public static void setSelectedSalaryId(int selectedSalaryId) {
        EverySalary.selectedSalaryId = selectedSalaryId;
    }
}
