package com.asss.www.ApotekarskaUstanova;

import com.asss.www.ApotekarskaUstanova.GUI.Start.StartPage.StartPage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class ApotekarskaUstanovaApplication {

	public static void main(String[] args) {
		// Pokretanje Spring Boot aplikacije
		System.setProperty("java.awt.headless", "false");
		ApplicationContext context = SpringApplication.run(ApotekarskaUstanovaApplication.class, args);

		// Pokretanje Swing GUI-a na Event Dispatch Thread
		SwingUtilities.invokeLater(() -> {
			JFrame pocetnaStrana = context.getBean(StartPage.class);
			pocetnaStrana.setVisible(true);
		});
	}
}
