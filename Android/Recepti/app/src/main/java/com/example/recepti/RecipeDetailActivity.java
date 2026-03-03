package com.example.recepti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView ivRecipeImage;
    private TextView tvName, tvBaked, tvSweet, tvTemperature, tvTime, tvIngredients, tvInstructions;
    private Button btnEdit, btnDelete;
    private String currentRecipeId;
    private Recipe currentRecipe;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        initViews();
        initDatabase();
        loadRecipeData();
        setupClickListeners();
    }

    private void initViews() {
        // PhotoView za sliku
        ivRecipeImage = findViewById(R.id.ivRecipeImage);

        // TextView elementi
        tvName = findViewById(R.id.tvName);
        tvBaked = findViewById(R.id.tvBaked);
        tvSweet = findViewById(R.id.tvSweet);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvTime = findViewById(R.id.tvTime);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvInstructions = findViewById(R.id.tvInstructions);

        // Dugmad
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // Preuzmi ID recepta
        currentRecipeId = getIntent().getStringExtra("recipe_id");
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void loadRecipeData() {
        Log.d("RECIPE_DETAIL", "Trazim recept sa ID: " + currentRecipeId);

        if (currentRecipeId != null && !currentRecipeId.isEmpty()) {
            // 1. Prvo pokušaj iz lokalne baze
            currentRecipe = databaseHelper.getRecipe(currentRecipeId);

            if (currentRecipe != null) {
                Log.d("RECIPE_DETAIL", "Pronađen u lokalnoj bazi: " + currentRecipe.getName());
                displayRecipe(currentRecipe);
            } else {
                Log.d("RECIPE_DETAIL", "Nije pronađen u lokalnoj bazi, proveravam Firebase...");

                // 2. Ako nije u lokalnoj, pokušaj sa Firebase
                try {
                    FirestoreHelper firestoreHelper = new FirestoreHelper(this);
                    firestoreHelper.getRecipeById(currentRecipeId,
                            new FirestoreHelper.OnCompleteListener() {
                                @Override
                                public void onSuccess(Object result) {
                                    Recipe firestoreRecipe = (Recipe) result;
                                    currentRecipe = firestoreRecipe;

                                    runOnUiThread(() -> {
                                        if (currentRecipe != null) {
                                            Log.d("RECIPE_DETAIL", "Pronađen u Firestore: " + currentRecipe.getName());
                                            displayRecipe(currentRecipe);

                                            // Sačuvaj u lokalnu bazu za ubuduće
                                            databaseHelper.addRecipeFromFirestore(currentRecipe);
                                        } else {
                                            showError("Recept nije pronađen");
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e("RECIPE_DETAIL", "Firestore greška: " + error);
                                    runOnUiThread(() -> {
                                        showError("Recept nije pronađen: " + error);
                                    });
                                }
                            });

                } catch (Exception e) {
                    Log.e("RECIPE_DETAIL", "Firestore init greška: " + e.getMessage());
                    showError("Firebase nije dostupan");
                }
            }
        } else {
            showError("Nema ID recepta");
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e("RECIPE_DETAIL", message);

        // Zatvori aktivnost nakon 2 sekunde
        new Handler().postDelayed(() -> {
            finish();
        }, 2000);
    }

    private void displayRecipe(Recipe recipe) {
        try {
            // Naziv
            tvName.setText(recipe.getName());

            // PRIKAZ SLIKE - podrška za Firebase URL i lokalne slike
            if (recipe.getImagePath() != null && !recipe.getImagePath().isEmpty()) {
                // Proveri da li je ovo Firebase URL ili lokalna putanja
                if (recipe.getImagePath().startsWith("http")) {
                    // Ovo je Firebase URL - koristi Picasso
                    Picasso.get()
                            .load(recipe.getImagePath())
                            .placeholder(R.drawable.addimage)  // Placeholder dok se učitava
                            .error(R.drawable.addimage)        // Ako greška
                            .fit()
                            .centerCrop()
                            .into(ivRecipeImage);

                    Log.d("RECIPE_IMAGE", "Učitavam Firebase URL: " + recipe.getImagePath());
                } else {
                    // Ovo je lokalna putanja
                    File imageFile = new File(recipe.getImagePath());
                    if (imageFile.exists()) {
                        ivRecipeImage.setImageURI(Uri.fromFile(imageFile));
                        ivRecipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Log.d("RECIPE_IMAGE", "Učitavam lokalnu sliku: " + recipe.getImagePath());
                    } else {
                        showPlaceholderImage();
                        Log.w("RECIPE_IMAGE", "Lokalna slika ne postoji: " + recipe.getImagePath());
                    }
                }
            } else {
                showPlaceholderImage();
                Log.d("RECIPE_IMAGE", "Nema slike za recept");
            }

            // Ostali podaci
            tvBaked.setText(recipe.isBaked() ? "Da" : "Ne");
            tvSweet.setText(recipe.isSweet() ? "Da" : "Ne");

            // Temperatura i vreme - prikaži samo ako se peče
            if (recipe.isBaked()) {
                tvTemperature.setText(recipe.getTemperature());
                tvTime.setText(recipe.getTime());
                tvTemperature.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.VISIBLE);
            } else {
                // Sakrij temperaturu i vreme ako se ne peče
                tvTemperature.setText("");
                tvTime.setText("");
                tvTemperature.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
            }

            // Sastojci
            String ingredients = formatWithBulletPoints(recipe.getIngredients());
            tvIngredients.setText(ingredients);

            // Priprema
            String instructions = formatInstructions(recipe.getInstructions());
            tvInstructions.setText(instructions);

            // Debug log
            Log.d("RECIPE_DETAIL", "Recept prikazan: " + recipe.getName() +
                    " | Image: " + recipe.getImagePath() +
                    " | Sweet: " + recipe.isSweet());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Greška pri prikazu recepta", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPlaceholderImage() {
        ivRecipeImage.setImageResource(R.drawable.addimage);
        ivRecipeImage.setScaleType(ImageView.ScaleType.CENTER);
    }

    private String formatWithBulletPoints(String text) {
        if (text == null || text.isEmpty()) return "";

        if (text.contains("•") || text.contains("-")) {
            return text;
        }

        if (text.contains("\n")) {
            String[] lines = text.split("\n");
            StringBuilder formatted = new StringBuilder();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    formatted.append("• ").append(line.trim()).append("\n");
                }
            }
            return formatted.toString().trim();
        }

        return "• " + text;
    }

    private String formatInstructions(String text) {
        if (text == null || text.isEmpty()) return "";
        return text;
    }

    private void setupClickListeners() {
        // Edit dugme
        btnEdit.setOnClickListener(v -> {
            editRecipe();
        });

        // Delete dugme
        btnDelete.setOnClickListener(v -> {
            showDeleteConfirmation();
        });

        ivRecipeImage.setOnClickListener(v -> {
            openFullscreenImage();
        });
    }

    private void openFullscreenImage() {
        if (currentRecipe != null && currentRecipe.getImagePath() != null &&
                !currentRecipe.getImagePath().isEmpty()) {

            // Proveri tip slike
            if (currentRecipe.getImagePath().startsWith("http")) {
                // Firebase URL slika - otvori sa URL-om
                Intent intent = new Intent(this, FullscreenImageActivity.class);
                intent.putExtra("image_url", currentRecipe.getImagePath());  // Promenjeno u image_url
                startActivity(intent);
            } else {
                // Lokalna slika
                File imageFile = new File(currentRecipe.getImagePath());
                if (imageFile.exists()) {
                    Intent intent = new Intent(this, FullscreenImageActivity.class);
                    intent.putExtra("image_path", currentRecipe.getImagePath());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Slika nije pronađena", Toast.LENGTH_SHORT).show();
                }
            }

            // Opciono: dodaj animaciju
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            Toast.makeText(this, "Nema slike za prikaz", Toast.LENGTH_SHORT).show();
        }
    }


    private void editRecipe() {
        Intent intent = new Intent(this, EditRecipeActivity.class);
        intent.putExtra("recipe_id", currentRecipeId);
        startActivity(intent);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Brisanje recepta")
                .setMessage("Da li želite da obrišete recept:\n\n" +
                        "• SAMO sa ovog uređaja\n" +
                        "• SA SVIH uređaja (sa servera)")
                .setPositiveButton("Sa svih uređaja", (dialog, which) -> {
                    deleteFromBoth();
                })
                .setNegativeButton("Samo sa ovog uređaja", (dialog, which) -> {
                    deleteLocalOnly();
                })
                .setNeutralButton("Otkaži", null)
                .show();
    }

    private void deleteLocalOnly() {
        // Obriši samo lokalno
        databaseHelper.deleteRecipe(currentRecipeId);
        Toast.makeText(this, "Recept obrisan samo sa ovog uređaja", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deleteFromBoth() {
        // Prvo obriši lokalno
        databaseHelper.deleteRecipe(currentRecipeId);

        // Pokušaj da obrišeš i sa Firebase (SA SLIKOM)
        try {
            FirestoreHelper firestoreHelper = new FirestoreHelper(this);

            // Koristi NOVU metodu koja briše i recept i sliku
            firestoreHelper.deleteRecipeWithImage(currentRecipeId,
                    new FirestoreHelper.OnCompleteListener() {
                        @Override
                        public void onSuccess(Object result) {
                            runOnUiThread(() -> {
                                Toast.makeText(RecipeDetailActivity.this,
                                        "✅ Recept i slika obrisani sa svih uređaja",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            runOnUiThread(() -> {
                                Log.e("DELETE", "Firebase greška: " + error);
                                Toast.makeText(RecipeDetailActivity.this,
                                        "⚠ Recept obrisan lokalno, ali greška sa serverom: " + error,
                                        Toast.LENGTH_LONG).show();
                                finish();
                            });
                        }
                    });

        } catch (Exception e) {
            Log.e("DELETE", "FirestoreHelper greška: " + e.getMessage());
            runOnUiThread(() -> {
                Toast.makeText(this,
                        "📱 Recept obrisan samo lokalno (Firebase greška)",
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}