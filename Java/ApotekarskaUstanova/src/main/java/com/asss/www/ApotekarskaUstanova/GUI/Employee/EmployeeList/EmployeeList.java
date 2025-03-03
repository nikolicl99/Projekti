/*
 * Created by JFormDesigner on Tue Nov 26 21:54:48 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList;

import java.awt.event.*;

import com.asss.www.ApotekarskaUstanova.Dto.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.AddEmployee.AddEmployee;
import com.asss.www.ApotekarskaUstanova.GUI.Start.MainMenu.MainMenu;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeView.EmployeeView;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.fasterxml.jackson.core.type.TypeReference;  // Ovaj import je potreban za TypeReference
import net.miginfocom.swing.*;
/**
 * @author lniko
 */
public class EmployeeList extends JFrame {
    public EmployeeList() {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

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
//            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            frame.setVisible(true);
        });


    }

    private void ZaposleniMouseClicked(MouseEvent e) {
        int selectedRow = Zaposleni.getSelectedRow(); // Dohvata indeks izabranog reda
        if (selectedRow >= 0) { // Proverava da li je red validan
            // Pretpostavlja se da je ID u prvoj koloni (kolona 0)
            int id = (int) Zaposleni.getValueAt(selectedRow, 0);
            System.out.println("Izabrani ID zaposlenog: " + id);

            // Skladišti ID u promenljivu za kasniju upotrebu
            setSelectedEmployeeId(id);

            // Primer: otvaranje novog prozora za izmenu podataka
            if (e.getClickCount() == 2) { // Ako je dvoklik
//                EditZaposleni editWindow = new EditZaposleni(selectedEmployeeId);
//                editWindow.setVisible(true);
                JOptionPane.showMessageDialog(this, "Izabran je zaposleni: " + getSelectedEmployeeId());
            }
        }
    }

    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        MainMenu.start();
    }

    private void ObrisiMouseClicked(MouseEvent e) {
        if (getSelectedEmployeeId() == 0) {
            JOptionPane.showMessageDialog(this, "Odaberite Zaposlenog", "Odabir Zaposlenog", JOptionPane.ERROR_MESSAGE);
            return; // Ne pokušavaj brisanje ako ID nije izabran
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da želite obrisati zaposlenog?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                ObrisiZaposlenog(getSelectedEmployeeId());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Došlo je do greške prilikom brisanja!", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void DodajMouseClicked(MouseEvent e) {
        dispose();
        AddEmployee.start();
    }

    private void PregledMouseClicked(MouseEvent e) {
        if (getSelectedEmployeeId() == 0) {
            JOptionPane.showMessageDialog(this, "Odaberite Zaposlenog", "Odabir Zaposlenog", JOptionPane.ERROR_MESSAGE);
        } else {
            dispose();
            EmployeeView.start();
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Luka Nikolić
        panel = new JPanel();
        Nazad = new JButton();
        scrollPane1 = new JScrollPane();
        Zaposleni = new JTable();
        Obrisi = new JButton();
        Dodaj = new JButton();
        Pregled = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        var contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== panel ========
        {
            panel.setPreferredSize(null);
            panel.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border. EmptyBorder(
            0, 0, 0, 0) , "JFor\u006dDesi\u0067ner \u0045valu\u0061tion", javax. swing. border. TitledBorder. CENTER, javax. swing. border. TitledBorder
            . BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,12 ), java. awt. Color.
            red) ,panel. getBorder( )) ); panel. addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .
            beans .PropertyChangeEvent e) {if ("bord\u0065r" .equals (e .getPropertyName () )) throw new RuntimeException( ); }} );
            panel.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[]" +
                "[fill]" +
                "[]"));

            //---- Nazad ----
            Nazad.setText("Nazad");
            Nazad.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NazadMouseClicked(e);
                }
            });
            panel.add(Nazad, "cell 1 0");

            //======== scrollPane1 ========
            {

                //---- Zaposleni ----
                Zaposleni.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ZaposleniMouseClicked(e);
                    }
                });
                scrollPane1.setViewportView(Zaposleni);
            }
            panel.add(scrollPane1, "cell 1 1 5 1");

            //---- Obrisi ----
            Obrisi.setText("Obrisi");
            Obrisi.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ObrisiMouseClicked(e);
                }
            });
            panel.add(Obrisi, "cell 2 2");

            //---- Dodaj ----
            Dodaj.setText("Dodaj");
            Dodaj.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    DodajMouseClicked(e);
                }
            });
            panel.add(Dodaj, "cell 2 2");

            //---- Pregled ----
            Pregled.setText("Pregled");
            Pregled.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    PregledMouseClicked(e);
                }
            });
            panel.add(Pregled, "cell 2 2");
        }
        contentPane.add(panel);
        setSize(490, 500);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Luka Nikolić
    private JPanel panel;
    private JButton Nazad;
    private JScrollPane scrollPane1;
    private JTable Zaposleni;
    private JButton Obrisi;
    private JButton Dodaj;
    private JButton Pregled;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    private static int selectedEmployeeId;

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

                        model.addRow(new Object[]{
                                employees.getId(),
                                employees.getName() + " " + employees.getSurname(),
                                typeName
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

        Zaposleni.setModel(model);
    }


    private void ObrisiZaposlenog(long ID) {
        try {
            // URL za API za brisanje zaposlenog
            URL url = new URL("http://localhost:8080/api/employees/" + ID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            // Dodavanje Authorization header-a sa tokenom
            connection.setRequestProperty("Authorization", "Bearer " + JwtResponse.getToken());

            // Provera odgovora API-ja
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(this, "Zaposleni je uspešno obrisan.", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                PrikaziZaposlene(); // Osvežavanje prikaza nakon brisanja
            } else {
                JOptionPane.showMessageDialog(this, "Greška prilikom brisanja zaposlenog! Kod greške: " + responseCode, "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Došlo je do greške prilikom brisanja zaposlenog!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }



    public static int getSelectedEmployeeId() {
        return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(int selectedEmployeeId) {
        EmployeeList.selectedEmployeeId = selectedEmployeeId;
    }
}
