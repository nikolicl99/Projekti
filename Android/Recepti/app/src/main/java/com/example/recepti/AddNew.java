package com.example.recepti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddNew extends AppCompatActivity {

    private ScrollView scrollView;
    private static final int PICK_IMAGE_REQUEST = 1;

    // Views
    private ImageView ivRecipeImage;
    private EditText etName, etTemperature, etTime, etIngredients, etInstructions;
    private Switch switchBaked, switchSweet;
    private Button btnSave;

    // Database
    private DatabaseHelper databaseHelper;
    private String selectedImagePath;

    private FirestoreHelper firestoreHelper;
    private boolean firebaseAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        initViews();
        initDatabase();
        try {
            initFirebase();
        } catch (Exception e) {
            Toast.makeText(this, "Firebase nije dostupan, čuvam samo lokalno", Toast.LENGTH_LONG).show();
            firebaseAvailable = false;
        }
        setupClickListeners();
        setupTextListeners();
    }

    private void initFirebase() throws Exception {
        firestoreHelper = new FirestoreHelper(this);
        firebaseAvailable = true;
    }

    private void initViews() {
        // Image view za sliku
        ivRecipeImage = findViewById(R.id.ivRecipeImage);

        // EditText polja
        etName = findViewById(R.id.editTextText);
        etTemperature = findViewById(R.id.editTextText2);
        etTime = findViewById(R.id.editTextText3);
        etIngredients = findViewById(R.id.editTextTextMultiLine);
        etInstructions = findViewById(R.id.editTextTextMultiLine2);

        // Switch elementi
        switchBaked = findViewById(R.id.switch1);
        switchSweet = findViewById(R.id.switchSweet);

        // Button
        btnSave = findViewById(R.id.add);
        scrollView = findViewById(R.id.scrollView);
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        // Klik na sliku za odabir iz galerije
        ivRecipeImage.setOnClickListener(v -> {
            openImagePicker();
        });

        // Klik na save dugme
        btnSave.setOnClickListener(v -> {
            saveRecipe();
        });

        switchBaked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Ako je switch ON (peče se) - prikaži polja
                etTemperature.setVisibility(View.VISIBLE);
                etTime.setVisibility(View.VISIBLE);
                etTemperature.setEnabled(true);
                etTime.setEnabled(true);

                // Očisti hintove ako treba
                etTemperature.setHint("npr: 180°C");
                etTime.setHint("npr: 30 min");
            } else {
                // Ako je switch OFF (ne peče se) - sakrij/onemogući polja
                etTemperature.setVisibility(View.GONE); // Ili View.INVISIBLE
                etTime.setVisibility(View.GONE);
                etTemperature.setEnabled(false);
                etTime.setEnabled(false);

                // Očisti polja
                etTemperature.setText("");
                etTime.setText("");

                // Postavi default vrednosti ili ostavi prazno
                etTemperature.setHint("");
                etTime.setHint("");
            }
        });

        // Postavi početno stanje
        switchBaked.setChecked(false); // Po defaultu ne peče se
        etTemperature.setVisibility(View.GONE);
        etTime.setVisibility(View.GONE);
        etTemperature.setEnabled(false);
        etTime.setEnabled(false);
    }

    private void setupTextListeners() {
        // Automatski scroll kada kucaš u multiline poljima
        etIngredients.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });

        etInstructions.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });

        // Fokusiraj se na polje kada ga dodirneš
        etIngredients.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.post(() -> scrollView.smoothScrollTo(0, v.getTop()));
            }
        });

        etInstructions.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.post(() -> scrollView.smoothScrollTo(0, v.getTop()));
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        try {
            // Kreiraj jedinstveno ime fajla
            String fileName = "recipe_" + System.currentTimeMillis() + ".jpg";

            // Otvori input stream za originalnu sliku
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;

            // Kreiraj output stream za internu memoriju
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);

            // Kopiraj podatke
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Zatvori streamove
            inputStream.close();
            outputStream.close();

            // Vrati putanju do interne slike
            return getFilesDir().getAbsolutePath() + "/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Kopiraj sliku u internu memoriju
            selectedImagePath = copyImageToInternalStorage(selectedImageUri);

            if (selectedImagePath != null) {
                // Prikaži sliku iz interne memorije
                File imageFile = new File(selectedImagePath);
                if (imageFile.exists()) {
                    ivRecipeImage.setImageURI(Uri.fromFile(imageFile));
                    ivRecipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    // Ukloni placeholder background
                    ivRecipeImage.setBackgroundResource(0);
                }
            } else {
                Toast.makeText(this, "Greška pri učitavanju slike", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveRecipe() {
        // Preuzmi vrednosti iz polja
        String name = etName.getText().toString().trim();
        String temperature = "";
        String time = "";

        // Samo uzmi temperaturu i vreme ako se peče
        if (switchBaked.isChecked()) {
            temperature = etTemperature.getText().toString().trim();
            time = etTime.getText().toString().trim();
        }

        String ingredients = etIngredients.getText().toString().trim();
        String instructions = etInstructions.getText().toString().trim();
        boolean isBaked = switchBaked.isChecked();
        boolean isSweet = switchSweet.isChecked();

        // Validacija - temperature i time samo ako se peče
        if (name.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(this, "Popunite sva obavezna polja", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ako se peče, proveri da li su temperatura i vime popunjeni
        if (isBaked && (temperature.isEmpty() || time.isEmpty())) {
            Toast.makeText(this, "Popunite temperaturu i vreme pečenja", Toast.LENGTH_SHORT).show();
            return;
        }

        // Postavi default vrednosti ako se ne peče
        if (!isBaked) {
            temperature = "";
            time = "";
        }

        // Kreiraj novi recept
        Recipe recipe = new Recipe(name, selectedImagePath, isBaked, isSweet,
                temperature.isEmpty() ? "180°C" : temperature,
                time.isEmpty() ? "30 min" : time,
                ingredients, instructions);

        recipe.setCreatedAt(System.currentTimeMillis());
        recipe.setLastModified(System.currentTimeMillis());

        Log.d("SAVE_RECIPE", "Kreiran recept: " + name + " | Slika: " + selectedImagePath);

        // VAŽNO: Koristi addRecipeWithImage() umesto addRecipe()
        if (firebaseAvailable && firestoreHelper != null) {
            firestoreHelper.addRecipeWithImage(recipe, new FirestoreHelper.OnCompleteListener() {
                @Override
                public void onSuccess(Object result) {
                    Recipe savedRecipe = (Recipe) result;
                    Log.d("SAVE_RECIPE", "Firebase uspeh, ID: " + savedRecipe.getId());

                    // VAŽNO: Lokalno sačuvaj sa URL-om (ne lokalnom putanjom)
                    savedRecipe.setImagePath(selectedImagePath); // Lokalna putanja za ovaj uređaj
                    long localId = databaseHelper.addRecipe(savedRecipe);

                    runOnUiThread(() -> {
                        if (localId != -1) {
                            Toast.makeText(AddNew.this,
                                    "✅ Recept i slika sačuvani na serveru!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        clearForm();
                        finish();
                    });
                }

                @Override
                public void onFailure(String error) {
                    Log.e("SAVE_RECIPE", "Firebase greška: " + error);

                    // Fallback: samo lokalno sačuvaj
                    long localId = databaseHelper.addRecipe(recipe);

                    runOnUiThread(() -> {
                        if (localId != -1) {
                            Toast.makeText(AddNew.this,
                                    "📱 Recept sačuvan samo lokalno",
                                    Toast.LENGTH_LONG).show();
                        }
                        clearForm();
                        finish();
                    });
                }
            });
        } else {
            // Firebase nije dostupan - samo lokalno
            long localId = databaseHelper.addRecipe(recipe);
            Toast.makeText(this, "📱 Recept sačuvan samo lokalno", Toast.LENGTH_SHORT).show();
            finish();
            clearForm();
        }
    }
    private void clearForm() {
        // Očisti sva polja nakon uspešnog čuvanja
        runOnUiThread(() -> {
            etName.setText("");
            etTemperature.setText("");
            etTime.setText("");
            etIngredients.setText("");
            etInstructions.setText("");
            switchBaked.setChecked(false);
            switchSweet.setChecked(false);

            // Resetuj sliku na placeholder
            ivRecipeImage.setImageResource(R.drawable.addimage);
            ivRecipeImage.setScaleType(ImageView.ScaleType.CENTER);
            // Vrati placeholder background ako postoji
            try {
                ivRecipeImage.setBackgroundResource(R.drawable.image_placeholder_bg);
            } catch (Exception e) {
                // Ignoriši ako drawable ne postoji
            }
            selectedImagePath = null;
        });
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}