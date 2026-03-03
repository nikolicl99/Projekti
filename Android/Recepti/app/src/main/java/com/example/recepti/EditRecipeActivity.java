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
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // Views
    private ImageView ivRecipeImage;
    private EditText etName, etTemperature, etTime, etIngredients, etInstructions;
    private Switch switchBaked, switchSweet;
    private Button btnSave;

    private String currentRecipeId;

    // Database
    private DatabaseHelper databaseHelper;
    private FirestoreHelper firestoreHelper;
    private String selectedImagePath;
    private Recipe currentRecipe;
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
        loadRecipeData();
        setupClickListeners();
    }

    private void initViews() {
        ivRecipeImage = findViewById(R.id.ivRecipeImage);
        etName = findViewById(R.id.editTextText);
        etTemperature = findViewById(R.id.editTextText2);
        etTime = findViewById(R.id.editTextText3);
        etIngredients = findViewById(R.id.editTextTextMultiLine);
        etInstructions = findViewById(R.id.editTextTextMultiLine2);
        switchBaked = findViewById(R.id.switch1);
        switchSweet = findViewById(R.id.switchSweet);
        btnSave = findViewById(R.id.add);

        // Promeni tekst dugmeta
        btnSave.setText("Ažuriraj recept");
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void initFirebase() throws Exception {
        firestoreHelper = new FirestoreHelper(this);
        firebaseAvailable = true;
    }

    private void loadRecipeData() {
        // Preuzmi ID recepta iz intenta - String
        currentRecipeId = getIntent().getStringExtra("recipe_id");

        if (currentRecipeId != null && !currentRecipeId.isEmpty()) {
            // Uzmi recept iz lokalne baze
            currentRecipe = databaseHelper.getRecipe(currentRecipeId);

            if (currentRecipe != null) {
                // Popuni formu sa postojećim podacima
                populateForm(currentRecipe);
                // Postavi početno stanje polja na osnovu switchBaked
                updateTemperatureTimeVisibility(currentRecipe.isBaked());
            } else {
                Toast.makeText(this, "Recept nije pronađen u lokalnoj bazi", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Nema ID recepta", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void populateForm(Recipe recipe) {
        etName.setText(recipe.getName());
        switchBaked.setChecked(recipe.isBaked());
        switchSweet.setChecked(recipe.isSweet());
        etTemperature.setText(recipe.getTemperature());
        etTime.setText(recipe.getTime());
        etIngredients.setText(recipe.getIngredients());
        etInstructions.setText(recipe.getInstructions());

        // Učitaj sliku ako postoji
        if (recipe.getImagePath() != null && !recipe.getImagePath().isEmpty()) {
            selectedImagePath = recipe.getImagePath();
            File imageFile = new File(recipe.getImagePath());
            if (imageFile.exists()) {
                ivRecipeImage.setImageURI(Uri.fromFile(imageFile));
                ivRecipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivRecipeImage.setBackgroundResource(0);
            }
        }
    }

    private void setupClickListeners() {
        // Klik na sliku za odabir iz galerije
        ivRecipeImage.setOnClickListener(v -> {
            openImagePicker();
        });

        // Klik na save dugme
        btnSave.setOnClickListener(v -> {
            updateRecipe();
        });

        // Listener za switch - KADA SE PEČE U RERNI
        switchBaked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateTemperatureTimeVisibility(isChecked);
        });
    }

    private void updateTemperatureTimeVisibility(boolean isBaked) {
        if (isBaked) {
            // Ako je switch ON (peče se) - prikaži polja
            etTemperature.setVisibility(View.VISIBLE);
            etTime.setVisibility(View.VISIBLE);
            etTemperature.setEnabled(true);
            etTime.setEnabled(true);

            // Postavi hintove
            etTemperature.setHint("(npr: 180°C)");
            etTime.setHint("(npr: 30 min)");
        } else {
            // Ako je switch OFF (ne peče se) - sakrij polja
            etTemperature.setVisibility(View.GONE);
            etTime.setVisibility(View.GONE);
            etTemperature.setEnabled(false);
            etTime.setEnabled(false);

            // Očisti polja
            etTemperature.setText("");
            etTime.setText("");
            etTemperature.setHint("");
            etTime.setHint("");
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        try {
            String fileName = "recipe_" + System.currentTimeMillis() + ".jpg";
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;

            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

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
                File imageFile = new File(selectedImagePath);
                if (imageFile.exists()) {
                    ivRecipeImage.setImageURI(Uri.fromFile(imageFile));
                    ivRecipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivRecipeImage.setBackgroundResource(0);
                }
            } else {
                Toast.makeText(this, "Greška pri učitavanju slike", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateRecipe() {
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

        // Validacija
        if (name.isEmpty()) {
            Toast.makeText(this, "Unesite naziv recepta", Toast.LENGTH_SHORT).show();
            etName.requestFocus();
            return;
        }

        if (ingredients.isEmpty()) {
            Toast.makeText(this, "Unesite sastojke", Toast.LENGTH_SHORT).show();
            etIngredients.requestFocus();
            return;
        }

        if (instructions.isEmpty()) {
            Toast.makeText(this, "Unesite pripremu", Toast.LENGTH_SHORT).show();
            etInstructions.requestFocus();
            return;
        }

        // Ako se peče, proveri da li su temperatura i vreme popunjeni
        if (isBaked && (temperature.isEmpty() || time.isEmpty())) {
            Toast.makeText(this, "Popunite temperaturu i vreme pečenja", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ažuriraj recept objekat
        currentRecipe.setName(name);
        currentRecipe.setBaked(isBaked);
        currentRecipe.setSweet(isSweet);
        currentRecipe.setTemperature(isBaked ? temperature : "");
        currentRecipe.setTime(isBaked ? time : "");
        currentRecipe.setIngredients(ingredients);
        currentRecipe.setInstructions(instructions);
        currentRecipe.setLastModified(System.currentTimeMillis());

        // Koristi NOVU sliku ako je izabrana
        String newImagePath = selectedImagePath != null ? selectedImagePath : currentRecipe.getImagePath();

        // Ažuriraj na Firebase SA SLIKOM
        if (firebaseAvailable && firestoreHelper != null) {
            firestoreHelper.updateRecipeWithImage(currentRecipe, selectedImagePath,
                    new FirestoreHelper.OnCompleteListener() {
                        @Override
                        public void onSuccess(Object result) {
                            Recipe updatedRecipe = (Recipe) result;

                            // Lokalno sačuvaj
                            if (selectedImagePath != null) {
                                updatedRecipe.setImagePath(selectedImagePath); // Lokalna putanja
                            }
                            databaseHelper.updateRecipe(updatedRecipe);

                            runOnUiThread(() -> {
                                Toast.makeText(EditRecipeActivity.this,
                                        "✅ Recept i slika ažurirani na serveru!",
                                        Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.e("UPDATE_RECIPE", "Firebase greška: " + error);

                            // Fallback: samo lokalno
                            if (selectedImagePath != null) {
                                currentRecipe.setImagePath(selectedImagePath);
                            }
                            databaseHelper.updateRecipe(currentRecipe);

                            runOnUiThread(() -> {
                                Toast.makeText(EditRecipeActivity.this,
                                        "📱 Recept ažuriran samo lokalno",
                                        Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                finish();
                            });
                        }
                    });
        } else {
            // Firebase nije dostupan
            if (selectedImagePath != null) {
                currentRecipe.setImagePath(selectedImagePath);
            }
            databaseHelper.updateRecipe(currentRecipe);
            Toast.makeText(this, "📱 Recept ažuriran samo lokalno", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
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