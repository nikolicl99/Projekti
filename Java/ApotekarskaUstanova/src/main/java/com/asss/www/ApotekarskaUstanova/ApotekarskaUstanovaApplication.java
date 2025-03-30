package com.asss.www.ApotekarskaUstanova;

import com.asss.www.ApotekarskaUstanova.GUI.Start.StartPage.StartPage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class ApotekarskaUstanovaApplication {

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");
		ApplicationContext context = SpringApplication.run(ApotekarskaUstanovaApplication.class, args);
        SwingUtilities.invokeLater(StartPage::start);
	}
}
