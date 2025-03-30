/*
 * Created by JFormDesigner on Sun Mar 09 22:23:07 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.CashRegister.SalesHistory;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.asss.www.ApotekarskaUstanova.Dto.EmployeeDto;
import com.asss.www.ApotekarskaUstanova.Dto.SalesDto;
import com.asss.www.ApotekarskaUstanova.GUI.CashRegister.CashRegister.CashRegister;
import com.asss.www.ApotekarskaUstanova.GUI.CashRegister.SalesItems.SalesItems;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.lgooddatepicker.components.*;
import net.miginfocom.swing.*;

/**
 * @author lniko
 */
public class SalesHistory extends JFrame {


    public SalesHistory() {
        initComponents();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Registrujte JavaTimeModule

        String[] columnNames = {"ID Prodaje", "Ukupna Cena", "Kusur", "Tip Plaćanja", "Zaposleni"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogući uređivanje ćelija
            }
        };
        Items.setModel(model);
        customizeTable(Items, model);
        calendarDesign(datePicker);

        datePicker.setDate(LocalDate.now());
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            SalesHistory frame = new SalesHistory();
            frame.setTitle("Prodaja");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void LoadSalesItems(String selectedDate) {
        DefaultTableModel model = (DefaultTableModel) Items.getModel();
        model.setRowCount(0); // Očisti tabelu pre dodavanja novih podataka
        System.out.println("selectedDate: " + selectedDate);
        try {
            // Dohvati sve prodaje za dati datum
            URL url = new URL("http://localhost:8080/api/sales/date/" + selectedDate);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream();
                     Scanner scanner = new Scanner(inputStream)) {

                    String response = scanner.useDelimiter("\\A").next();
                    System.out.println("JSON Response: " + response);
                    List<SalesDto> items = objectMapper.readValue(response, new TypeReference<List<SalesDto>>() {});

                    for (SalesDto item : items) {
                        String employeeName = fetchEmployeeName(item.getEmployeeDto().getId());
                        String data0 = String.valueOf(item.getId());
                        String data1 = String.valueOf(item.getTotalPrice());
                        String data2 = String.valueOf(item.getReceiptChange());
                        String data3 = item.getPaymentType();

                        model.addRow(new Object[]{data0, data1, data2, data3, employeeName});
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri učitavanju stavki prodaje.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri popunjavanju tabele.", "Greška", JOptionPane.ERROR_MESSAGE);
        } finally {
            customizeTable(Items, model);
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
        JViewport viewport = scrollPane.getViewport();
        viewport.setBackground(backgroundColor);
        scrollPane.setBackground(backgroundColor);
    }

    private static void calendarDesign(DatePicker datePicker) {

        // Create a new instance of DatePickerSettings
        DatePickerSettings settings = new DatePickerSettings();

        settings.setFirstDayOfWeek(DayOfWeek.MONDAY);

        // Customize the text field
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate, new Color(0xfb, 0xff, 0xe4)); // Background color
        settings.setColor(DatePickerSettings.DateArea.DatePickerTextValidDate, Color.DARK_GRAY); // Text color

        // Customize the calendar popup
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates, new Color(0xfb, 0xff, 0xe4)); // Background color
        settings.setColor(DatePickerSettings.DateArea.CalendarTextNormalDates, Color.DARK_GRAY); // Text color
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundSelectedDate, new Color(0xfb, 0xff, 0xe4)); // Background color
        settings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, new Color(0xfb, 0xff, 0xe4)); // Text color
        settings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels, new Color(0xfb, 0xff, 0xe4)); //Pozadina gornjeg teksta
        settings.setColor(DatePickerSettings.DateArea.BackgroundTodayLabel, new Color(0xfb, 0xff, 0xe4));
        settings.setColor(DatePickerSettings.DateArea.BackgroundClearLabel, new Color(0xfb, 0xff, 0xe4));

        // Apply the settings to the DatePicker
        datePicker.setSettings(settings);
    }



    private String fetchEmployeeName(long employeeId) {
        try {
            URL url = new URL("http://localhost:8080/api/employees/" + employeeId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    EmployeeDto employee = objectMapper.readValue(response, EmployeeDto.class);

                    return employee.getName() + " " + employee.getSurname();
                }
            } else {
                return "Nepoznati zaposleni";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Greška pri učitavanju zaposlenog";
        }
    }

    private void BackMouseClicked(MouseEvent e) {
        dispose();
        CashRegister.start();
    }

    private void ItemsMouseClicked(MouseEvent e) {
        int selectedRow = Items.getSelectedRow();
        if (selectedRow >= 0) { // Proverava da li je red validan
            try {
                // Pretpostavlja se da je ID u prvoj koloni (kolona 0)
                String idString = (String) Items.getValueAt(selectedRow, 0); // Uzmi vrednost kao String
                int id = Integer.parseInt(idString); // Pretvori String u int
                System.out.println("Izabrani ID prodaje: " + id);

                setSelectedSale(id);
                if (e.getClickCount() == 2) { // Ako je dvoklik
                    dispose();
                    SalesItems.start();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nevalidan ID u tabeli!", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void datePickerPropertyChange(PropertyChangeEvent e) {
        LocalDate selectedDate = datePicker.getDate();
        if (selectedDate != null) {
            LoadSalesItems(selectedDate.toString());
        } else {
            System.out.println("Datum nije odabran.");
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel = new JPanel();
        Back = new JButton();
        datePicker = new DatePicker();
        scrollPane = new JScrollPane();
        Items = new JTable();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //======== panel ========
        {
            panel.setBackground(new Color(0x3d8d7a));
            panel.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]",
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
                "[75]" +
                "[75]"));

            //---- Back ----
            Back.setText("Nazad");
            Back.setBackground(new Color(0xb3d8a8));
            Back.setForeground(Color.darkGray);
            Back.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BackMouseClicked(e);
                }
            });
            panel.add(Back, "cell 1 1");

            //---- datePicker ----
            datePicker.setBackground(new Color(0xb3d8a8));
            datePicker.setForeground(Color.darkGray);
            datePicker.addPropertyChangeListener(e -> datePickerPropertyChange(e));
            panel.add(datePicker, "cell 3 1");

            //======== scrollPane ========
            {
                scrollPane.setBackground(Color.darkGray);
                scrollPane.setForeground(Color.darkGray);

                //---- Items ----
                Items.setBackground(new Color(0xfbffe4));
                Items.setForeground(Color.darkGray);
                Items.setGridColor(Color.darkGray);
                Items.setSelectionBackground(new Color(0xb3d8a8));
                Items.setSelectionForeground(Color.darkGray);
                Items.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ItemsMouseClicked(e);
                    }
                });
                scrollPane.setViewportView(Items);
            }
            panel.add(scrollPane, "cell 2 3 6 7");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel;
    private JButton Back;
    private DatePicker datePicker;
    private JScrollPane scrollPane;
    private JTable Items;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static int selectedSale;
    private final ObjectMapper objectMapper; // Dodajte ObjectMapper kao polje klase

    public static int getSelectedSale() {
        return selectedSale;
    }

    public static void setSelectedSale(int selectedSale) {
        SalesHistory.selectedSale = selectedSale;
    }
}
