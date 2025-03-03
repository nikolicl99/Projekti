/*
 * Created by JFormDesigner on Tue Dec 03 19:29:36 CET 2024
 */

package com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeView;

import java.awt.event.*;

import com.asss.www.ApotekarskaUstanova.Dto.JwtResponse;
import com.asss.www.ApotekarskaUstanova.Entity.Employees;
import com.asss.www.ApotekarskaUstanova.GUI.Employee.EmployeeList.EmployeeList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author lniko
 */
public class EmployeeView extends JFrame {
    public EmployeeView() {
        initComponents();
        UcitavanjePodataka();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            EmployeeView frame = new EmployeeView();
            frame.setTitle("Zaposleni");
            frame.setSize(1000, 500); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void NazadMouseClicked(MouseEvent e) {
        dispose();
        EmployeeList.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Luka Nikolić
        panel = new JPanel();
        Nazad = new JButton();
        picture = new JLabel();
        label1 = new JLabel();
        imePrezime = new JTextField();
        tip_Label = new JLabel();
        tip = new JTextField();
        telefon_Label = new JLabel();
        telefon_edit = new JTextField();
        email_Label = new JLabel();
        email_edit = new JTextField();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== panel ========
        {
            panel.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border
            . EmptyBorder( 0, 0, 0, 0) , "JFor\u006dDesi\u0067ner \u0045valu\u0061tion", javax. swing. border. TitledBorder. CENTER, javax
            . swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,
            12 ), java. awt. Color. red) ,panel. getBorder( )) ); panel. addPropertyChangeListener (new java. beans
            . PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("bord\u0065r" .equals (e .
            getPropertyName () )) throw new RuntimeException( ); }} );
            panel.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]" +
                "[100,fill]",
                // rows
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]" +
                "[75,fill]"));

            //---- Nazad ----
            Nazad.setText("Nazad");
            Nazad.setPreferredSize(new Dimension(90, 40));
            Nazad.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NazadMouseClicked(e);
                }
            });
            panel.add(Nazad, "cell 0 0");
            panel.add(picture, "cell 4 1 2 3");

            //---- label1 ----
            label1.setText("Ime i Prezime:");
            panel.add(label1, "cell 1 3");

            //---- imePrezime ----
            imePrezime.setEditable(false);
            panel.add(imePrezime, "cell 2 3");

            //---- tip_Label ----
            tip_Label.setText("Tip Zaposlenog:");
            panel.add(tip_Label, "cell 1 4");

            //---- tip ----
            tip.setEditable(false);
            panel.add(tip, "cell 2 4");

            //---- telefon_Label ----
            telefon_Label.setText("Telefon:");
            panel.add(telefon_Label, "cell 1 5");

            //---- telefon_edit ----
            telefon_edit.setEditable(false);
            panel.add(telefon_edit, "cell 2 5");

            //---- email_Label ----
            email_Label.setText("Email:");
            panel.add(email_Label, "cell 1 6");

            //---- email_edit ----
            email_edit.setEditable(false);
            panel.add(email_edit, "cell 2 6");
        }
        contentPane.add(panel);
        panel.setBounds(0, 0, 800, 470);

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
        setSize(800, 500);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    private void UcitavanjePodataka() {
        int id = EmployeeList.getSelectedEmployeeId(); // Dohvata ID izabrane stavke
        String urlString = "http://localhost:8080/api/employees/" + id;

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
                    Employees employees = objectMapper.readValue(response, Employees.class);

                    // Popunjavanje podataka u UI komponentama
                    imePrezime.setText(employees.getName() + " " + employees.getSurname());
                    tip.setText(employees.getEmployeeType().getName());
                    telefon_edit.setText(employees.getMobile());
                    email_edit.setText(employees.getEmail());

                    if (employees.getScaledProfileImage() != null) {
                        byte[] imageBytes = Base64.getDecoder().decode(employees.getScaledProfileImage());
                        ImageIcon scaledIcon = scaleImage(imageBytes, 200, 150);
                        picture.setIcon(scaledIcon);
                    } else {
                        picture.setIcon(null);
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

    public static ImageIcon scaleImage(byte[] imageBytes, int width, int height) {
        ImageIcon imageIcon = new ImageIcon(imageBytes);
        Image originalImage = imageIcon.getImage();

        // Kreiranje BufferedImage u novoj veličini
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Podešavanje kvaliteta
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();

        return new ImageIcon(resizedImage);
    }





    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Luka Nikolić
    private JPanel panel;
    private JButton Nazad;
    private JLabel picture;
    private JLabel label1;
    private JTextField imePrezime;
    private JLabel tip_Label;
    private JTextField tip;
    private JLabel telefon_Label;
    private JTextField telefon_edit;
    private JLabel email_Label;
    private JTextField email_edit;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
