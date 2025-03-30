/*
 * Created by JFormDesigner on Sun Mar 16 02:39:33 CET 2025
 */

package com.asss.www.ApotekarskaUstanova.GUI.Finance.EmployeeSalary;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

import com.asss.www.ApotekarskaUstanova.Dto.SalaryDetailsDto;
import com.asss.www.ApotekarskaUstanova.GUI.Finance.EverySalary.EverySalary;
import com.asss.www.ApotekarskaUstanova.Security.JwtResponse;
import net.miginfocom.swing.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author lniko
 */
public class EmployeeSalary extends JFrame {
    public EmployeeSalary() {
        initComponents();
        loadSalaryDetails();
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            EmployeeSalary frame = new EmployeeSalary();
            frame.setTitle("Salary");
            frame.setSize(600, 400); // Adjusted size to match the preferred bounds
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

    private void loadSalaryDetails() {
        int salaryId = EverySalary.getSelectedSalaryId();

        String url = "http://localhost:8080/api/salary-details/" + salaryId;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + JwtResponse.getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<SalaryDetailsDto> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    SalaryDetailsDto.class
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                SalaryDetailsDto salaryDetails = responseEntity.getBody();
                if (salaryDetails != null) {
                    // Popuni JTextField komponente sa podacima
                    nrHours.setText(String.valueOf(salaryDetails.getHoursWorked()));
                    nrOvertime.setText(String.valueOf(salaryDetails.getOvertimeHours()));
                    bonus.setText(String.valueOf(salaryDetails.getBonus()));
                    deductions.setText(String.valueOf(salaryDetails.getDeductions()));
                    notes.setText(salaryDetails.getNotes());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Greška pri učitavanju podataka.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri učitavanju podataka.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void backMouseClicked(MouseEvent e) {
        dispose();
        EverySalary.start();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Luka Nikolic (office)
        panel1 = new JPanel();
        back = new JButton();
        label1 = new JLabel();
        nrHours = new JTextField();
        label2 = new JLabel();
        nrOvertime = new JTextField();
        label3 = new JLabel();
        bonus = new JTextField();
        label4 = new JLabel();
        deductions = new JTextField();
        label5 = new JLabel();
        notes = new JTextField();

        //======== this ========
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBackground(new Color(0x3d8d7a));
            panel1.setLayout(new MigLayout(
                "fill,hidemode 3",
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
                "[75]"));

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
            panel1.add(back, "cell 1 0 2 1");

            //---- label1 ----
            label1.setText("Broj sati:");
            label1.setForeground(new Color(0xfbffe4));
            panel1.add(label1, "cell 2 2 2 1");

            //---- nrHours ----
            nrHours.setBackground(new Color(0xb3d8a8));
            nrHours.setForeground(Color.darkGray);
            panel1.add(nrHours, "cell 4 2 2 1");

            //---- label2 ----
            label2.setText("Prekomerni sati:");
            label2.setForeground(new Color(0xfbffe4));
            panel1.add(label2, "cell 2 3 2 1");

            //---- nrOvertime ----
            nrOvertime.setBackground(new Color(0xb3d8a8));
            nrOvertime.setForeground(Color.darkGray);
            panel1.add(nrOvertime, "cell 4 3 2 1");

            //---- label3 ----
            label3.setText("Bonus:");
            label3.setForeground(new Color(0xfbffe4));
            panel1.add(label3, "cell 2 4 2 1");

            //---- bonus ----
            bonus.setBackground(new Color(0xb3d8a8));
            bonus.setForeground(Color.darkGray);
            panel1.add(bonus, "cell 4 4 2 1");

            //---- label4 ----
            label4.setText("Odbici:");
            label4.setForeground(new Color(0xfbffe4));
            panel1.add(label4, "cell 2 5 2 1");

            //---- deductions ----
            deductions.setBackground(new Color(0xb3d8a8));
            deductions.setForeground(Color.darkGray);
            panel1.add(deductions, "cell 4 5 2 1");

            //---- label5 ----
            label5.setText("Dodatno");
            label5.setForeground(new Color(0xfbffe4));
            panel1.add(label5, "cell 2 6 2 1");

            //---- notes ----
            notes.setBackground(new Color(0xb3d8a8));
            notes.setForeground(Color.darkGray);
            panel1.add(notes, "cell 4 6 2 1");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Luka Nikolic (office)
    private JPanel panel1;
    private JButton back;
    private JLabel label1;
    private JTextField nrHours;
    private JLabel label2;
    private JTextField nrOvertime;
    private JLabel label3;
    private JTextField bonus;
    private JLabel label4;
    private JTextField deductions;
    private JLabel label5;
    private JTextField notes;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
