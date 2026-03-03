package com.example.recepti;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import android.net.Uri;
import java.io.File;

public class FullscreenImageActivity extends AppCompatActivity {

    private PhotoView photoView;
    private ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        initViews();
        loadImage();
        setupClickListeners();
    }

    private void initViews() {
        photoView = findViewById(R.id.photoView);
        btnClose = findViewById(R.id.btnClose);
    }

    private void loadImage() {
        // Proveri oba parametra - URL i lokalnu putanju
        String imageUrl = getIntent().getStringExtra("image_url");
        String imagePath = getIntent().getStringExtra("image_path");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Ovo je Firebase URL slika
            loadImageFromUrl(imageUrl);
        }
        else if (imagePath != null && !imagePath.isEmpty()) {
            // Ovo je lokalna putanja
            loadImageFromFile(imagePath);
        }
        else {
            Toast.makeText(this, "Nema slike za prikaz", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadImageFromUrl(String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.addimage)  // Placeholder dok se učitava
                .error(R.drawable.addimage)        // Ako greška
                .into(photoView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        // Uvećaj zoom kada se slika učita
                        photoView.setMaximumScale(10f);  // Povećaj maksimalni zoom
                        photoView.setScale(1f, true);    // Resetuj na normalnu veličinu
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(FullscreenImageActivity.this,
                                "Greška pri učitavanju slike sa servera",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void loadImageFromFile(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            photoView.setImageURI(Uri.fromFile(imageFile));
            // Uvećaj zoom za lokalne slike
            photoView.setMaximumScale(10f);
            photoView.setScale(1f, true);
        } else {
            Toast.makeText(this, "Lokalna slika nije pronađena", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupClickListeners() {
        // Close dugme
        btnClose.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Klik na sliku takođe zatvara (opciono)
        photoView.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Dugme za download slike (opciono)
//        setupDownloadButton();
    }

    // Opciono: dodajte dugme za download slike ako je sa Firebase-a
//    private void setupDownloadButton() {
//        ImageButton btnDownload = findViewById(R.id.btnDownload);
//        if (btnDownload != null) {
//            btnDownload.setVisibility(View.VISIBLE);
//            btnDownload.setOnClickListener(v -> {
//                String imageUrl = getIntent().getStringExtra("image_url");
//                if (imageUrl != null && imageUrl.startsWith("http")) {
//                    downloadImage(imageUrl);
//                }
//            });
//        }
//    }

    private void downloadImage(String imageUrl) {
        // Ovo je samo primer - možete implementirati download
        Toast.makeText(this, "Download funkcija u izradi...", Toast.LENGTH_SHORT).show();
        // Možete koristiti DownloadManager ili Picasso za download
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        // Oslobodi resurse ako je potrebno
        super.onDestroy();
    }
}