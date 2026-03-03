package com.example.recepti;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.*;
import java.util.*;

public class FirebaseTestActivity extends AppCompatActivity {

    private TextView tvStatus, tvResult;
    private Button btnTestConnection, btnTestWrite, btnTestRead, btnBack;
    private FirebaseFirestore db;
    private boolean isFirebaseInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);

        initViews();
        setupClickListeners();
        initializeFirebase();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvResult = findViewById(R.id.tvResult);
        btnTestConnection = findViewById(R.id.btnTestConnection);
        btnTestWrite = findViewById(R.id.btnTestWrite);
        btnTestRead = findViewById(R.id.btnTestRead);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnTestConnection.setOnClickListener(v -> testConnection());
        btnTestWrite.setOnClickListener(v -> testWriteToFirestore());
        btnTestRead.setOnClickListener(v -> testReadFromFirestore());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeFirebase() {
        tvStatus.setText("Status: Inicijalizacija Firebase...");

        try {
            db = FirebaseFirestore.getInstance();
            isFirebaseInitialized = true;
            tvStatus.setText("Status: ✓ Firebase inicijalizovan");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        } catch (Exception e) {
            isFirebaseInitialized = false;
            tvStatus.setText("Status: ✗ Firebase greška: " + e.getMessage());
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            e.printStackTrace();
        }
    }

    private void testConnection() {
        if (!isFirebaseInitialized) {
            tvResult.setText("Firebase nije inicijalizovan!\nProveri google-services.json");
            return;
        }

        tvResult.setText("Testiram konekciju...");

        // Simple ping test
        db.collection("test")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String result = "✓ Konekcija USPELA!\n" +
                            "Firestore je dostupan.\n" +
                            "Projekat: " + db.getApp().getName();
                    tvResult.setText(result);
                })
                .addOnFailureListener(e -> {
                    String result = "✗ Konekcija NEUSPELA!\n" +
                            "Greška: " + e.getMessage() + "\n\n" +
                            "Proveri:\n" +
                            "1. Internet konekciju\n" +
                            "2. Firebase projekat\n" +
                            "3. google-services.json";
                    tvResult.setText(result);
                });
    }

    private void testWriteToFirestore() {
        if (!isFirebaseInitialized) {
            tvResult.setText("Firebase nije inicijalizovan!");
            return;
        }

        tvResult.setText("Upisujem test podatak...");

        Map<String, Object> testData = new HashMap<>();
        testData.put("message", "Test iz Recepti aplikacije");
        testData.put("timestamp", new Date());
        testData.put("device", "Android");
        testData.put("testId", UUID.randomUUID().toString());

        db.collection("test_logs")
                .add(testData)
                .addOnSuccessListener(documentReference -> {
                    String result = "✓ Upis USPEO!\n\n" +
                            "Document ID: " + documentReference.getId() + "\n" +
                            "Collection: test_logs\n" +
                            "Možeš videti u Firebase Console.";
                    tvResult.setText(result);

                    // Sačuvaj ID za test čitanja
                    testDocumentId = documentReference.getId();
                })
                .addOnFailureListener(e -> {
                    String result = "✗ Upis NEUSPEO!\n\n" +
                            "Greška: " + e.getMessage() + "\n\n" +
                            "Proveri Firestore pravila:\n" +
                            "Pravila moraju dozvoliti write.";
                    tvResult.setText(result);
                });
    }

    private String testDocumentId = "";

    private void testReadFromFirestore() {
        if (!isFirebaseInitialized) {
            tvResult.setText("Firebase nije inicijalizovan!");
            return;
        }

        tvResult.setText("Čitam test podatke...");

        if (testDocumentId.isEmpty()) {
            // Ako nema novog dokumenta, čitaj sve test_logs
            db.collection("test_logs")
                    .limit(5)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        StringBuilder result = new StringBuilder();
                        result.append("✓ Čitanje USPELO!\n\n");
                        result.append("Pronađeno dokumenata: ").append(queryDocumentSnapshots.size()).append("\n\n");

                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            result.append("ID: ").append(doc.getId()).append("\n");
                            result.append("Poruka: ").append(doc.getString("message")).append("\n");
                            result.append("---\n");
                        }

                        tvResult.setText(result.toString());
                    })
                    .addOnFailureListener(e -> {
                        tvResult.setText("✗ Čitanje NEUSPELO!\nGreška: " + e.getMessage());
                    });
        } else {
            // Čitaj specifični dokument
            db.collection("test_logs")
                    .document(testDocumentId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String result = "✓ Dokument pročitan!\n\n" +
                                    "ID: " + documentSnapshot.getId() + "\n" +
                                    "Poruka: " + documentSnapshot.getString("message") + "\n" +
                                    "Datum: " + documentSnapshot.getDate("timestamp");
                            tvResult.setText(result);
                        } else {
                            tvResult.setText("✗ Dokument ne postoji!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        tvResult.setText("✗ Greška pri čitanju: " + e.getMessage());
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup ako je potrebno
    }
}