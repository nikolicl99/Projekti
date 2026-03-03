package com.example.recepti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecipeList extends AppCompatActivity {
    private Button addNew, btnAll, btnSweet, btnSalty;
    private ListView listViewRecipes;
    private DatabaseHelper databaseHelper;
    private FirestoreHelper firestoreHelper;
    private RecipeAdapter adapter;
    private List<Recipe> allRecipes;
    private boolean firebaseConnected = false;
    private String currentFilter = "all";
    private static final String TAG = "RecipeList";
    private void checkDatabaseState() {
        int count = databaseHelper.getRecipeCount();
        Log.d(TAG, "SQLite ima " + count + " recepata");

        if (firestoreHelper != null) {
            firestoreHelper.getAllRecipes(new FirestoreHelper.OnRecipesLoadedListener() {
                @Override
                public void onRecipesLoaded(List<Recipe> recipes) {
                    Log.d(TAG, "Firestore ima " + recipes.size() + " recepata");
                    for (Recipe r : recipes) {
                        Log.d(TAG, "Firestore recept: " + r.getName() + " (ID: " + r.getId() + ")");
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Firestore error: " + error);
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initViews();
        initDatabase();
        setupClickListeners();

        Log.d(TAG, "=== ONCREATE RecipeList ===");
        checkDatabaseState();

        // Prvo učitaj iz SQLite
        loadAllRecipesFromSQLite();

        // Zatim pokušaj Firebase u pozadini
        initFirebaseInBackground();

        // Nakon 3 sekunde proveri Firebase
        new Handler().postDelayed(() -> {
            if (firestoreHelper != null) {
                Log.d("FIREBASE_CHECK", "Proveravam Firebase...");
                firestoreHelper.getAllRecipes(new FirestoreHelper.OnRecipesLoadedListener() {
                    @Override
                    public void onRecipesLoaded(List<Recipe> recipes) {
                        Log.d("FIREBASE_CHECK", "Pronađeno " + recipes.size() + " recepata:");
                        for (Recipe r : recipes) {
                            Log.d("FIREBASE_CHECK", "- " + r.getName() + " (ID: " + r.getId() + ")");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("FIREBASE_CHECK", "Greška: " + error);
                    }
                });
            }
        }, 3000);
    }

    private void initFirebaseInBackground() {
        new Thread(() -> {
            try {
                firestoreHelper = new FirestoreHelper(RecipeList.this);
                firebaseConnected = true;

                runOnUiThread(() -> {
                    Toast.makeText(RecipeList.this,
                            "Firebase povezan",
                            Toast.LENGTH_SHORT).show();
                    applyCurrentFilter();
                    startRealTimeSync();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(RecipeList.this,
                            "Firebase nije dostupan - koristim lokalne podatke",
                            Toast.LENGTH_LONG).show();
                    firebaseConnected = false;
                    applyCurrentFilter();
                });
            }
        }).start();
    }

    private void loadAllRecipesFromSQLite() {
        allRecipes = databaseHelper.getAllRecipes();
        if (allRecipes.isEmpty()) {
            Toast.makeText(this, "Nema sačuvanih recepata", Toast.LENGTH_SHORT).show();
        }
        adapter = new RecipeAdapter(this, allRecipes);
        listViewRecipes.setAdapter(adapter);
    }

    private void startRealTimeSync() {
        if (firestoreHelper != null && firebaseConnected) {
            // Koristi NOVU metodu koja ne učitava stare podatke
            firestoreHelper.getRecipesRealTimeFromNow(new FirestoreHelper.OnRecipesLoadedListener() {
                @Override
                public void onRecipesLoaded(List<Recipe> recipes) {
                    // SAMO za NOVE recepte
                    if (!recipes.isEmpty()) {
                        syncWithLocalDatabase(recipes);
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e("FIREBASE_SYNC", "Real-time greška: " + error);
                }
            });
        }
    }

    private void syncWithLocalDatabase(List<Recipe> firestoreRecipes) {
        Log.d("SYNC", "=== POČINJEM SINHRONIZACIJU ===");
        Log.d("SYNC", "Firestore ima: " + firestoreRecipes.size() + " recepata");

        // 1. MAPA Firebase recepata za brzu proveru
        Map<String, Recipe> firestoreMap = new HashMap<>();
        for (Recipe r : firestoreRecipes) {
            firestoreMap.put(r.getId(), r);
            Log.d("SYNC", "🔥 Firebase: " + r.getName() + " (ID: " + r.getId() + ")");
        }

        // 2. Uzmi SVE lokalne recepte
        List<Recipe> localRecipes = databaseHelper.getAllRecipes();
        Log.d("SYNC", "📱 SQLite ima: " + localRecipes.size() + " recepata");

        Set<String> localIdsToKeep = new HashSet<>();
        int added = 0;
        int updated = 0;
        int deleted = 0;

        // 3. SINHRONIZACIJA: Firebase → SQLite
        for (Recipe firestoreRecipe : firestoreRecipes) {
            Recipe localRecipe = databaseHelper.getRecipe(firestoreRecipe.getId());

            if (localRecipe == null) {
                // NOVI recept - dodaj u SQLite
                databaseHelper.addRecipeFromFirestore(firestoreRecipe);
                added++;
                Log.d("SYNC", "➕ DODAT: " + firestoreRecipe.getName());
            } else {
                // Postojeći - ažuriraj ako je novija verzija
                if (firestoreRecipe.getLastModified() > localRecipe.getLastModified()) {
                    databaseHelper.updateRecipeFromFirestore(firestoreRecipe);
                    updated++;
                    Log.d("SYNC", "🔄 AŽURIRAN: " + firestoreRecipe.getName());
                }
            }

            // Dodaj ID u set recepata koji treba da ostanu
            localIdsToKeep.add(firestoreRecipe.getId());
        }

        // 4. OBRISI recepte koji VIŠE NEMAJU u Firebase-u
        for (Recipe localRecipe : localRecipes) {
            String localId = localRecipe.getId();

            // Ako lokalni recept NIJE u Firebase mapi, OBRISI ga
            if (!firestoreMap.containsKey(localId)) {
                databaseHelper.deleteRecipe(localId);
                deleted++;
                Log.d("SYNC", "🗑️ OBRISAN: " + localRecipe.getName() + " (ID: " + localId + ")");
            }
        }

        // 5. Log rezultat
        Log.d("SYNC", "📊 REZULTAT: +" + added + " / ↻" + updated + " / -" + deleted);
        Log.d("SYNC", "📱 SQLite sada ima: " + databaseHelper.getRecipeCount() + " recepata");

        // 6. Obavesti korisnika i osveži
        if (added > 0 || updated > 0 || deleted > 0) {
            int finalAdded = added;
            int finalUpdated = updated;
            int finalDeleted = deleted;
            runOnUiThread(() -> {
                String message = "Sinhronizovano: ";
                if (finalAdded > 0) message += "+" + finalAdded + " ";
                if (finalUpdated > 0) message += "↻" + finalUpdated + " ";
                if (finalDeleted > 0) message += "-" + finalDeleted;

                Toast.makeText(RecipeList.this, message, Toast.LENGTH_SHORT).show();
                applyCurrentFilter(); // Osveži prikaz
            });
        }
    }

    private void initViews() {
        addNew = findViewById(R.id.add);
        btnAll = findViewById(R.id.btnAll);
        btnSweet = findViewById(R.id.btnSweet);
        btnSalty = findViewById(R.id.btnSalty);
        listViewRecipes = findViewById(R.id.listViewRecipes);

        allRecipes = new ArrayList<>();
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        addNew.setOnClickListener(v -> addFood());

        // Početno stanje - SVI su selektovani (tamna siva)
        btnAll.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.buttonBackground)));
        btnSweet.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.buttonBackgroundLight)));
        btnSalty.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.buttonBackgroundLight)));

        btnAll.setOnClickListener(v -> {
            resetFilterButtons();
            btnAll.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.buttonBackground)));
            currentFilter = "all";
            loadAllRecipes();
        });

        btnSweet.setOnClickListener(v -> {
            resetFilterButtons();
            btnSweet.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.buttonBackground)));
            currentFilter = "sweet";
            loadSweetRecipes();
        });

        btnSalty.setOnClickListener(v -> {
            resetFilterButtons();
            btnSalty.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.buttonBackground)));
            currentFilter = "salty";
            loadSaltyRecipes();
        });

        // ISPRAVLJENO: Klik na stavku u listi
        listViewRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // VAŽNO: Uzmi recept iz adaptera (ovo sigurno postoji)
                    Recipe selectedRecipe = adapter.getItem(position);

                    if (selectedRecipe == null) {
                        Toast.makeText(RecipeList.this,
                                "Recept nije pronađen u listi",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d(TAG, "Kliknut recept: " + selectedRecipe.getName() +
                            " (ID: " + selectedRecipe.getId() + ")");

                    // Proveri da li ID postoji
                    if (selectedRecipe.getId() == null || selectedRecipe.getId().isEmpty()) {
                        Log.e(TAG, "Recept nema ID!");
                        Toast.makeText(RecipeList.this,
                                "Recept nema validan ID",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Prvo proveri lokalnu bazu
                    Recipe localRecipe = databaseHelper.getRecipe(selectedRecipe.getId());

                    if (localRecipe != null) {
                        Log.d(TAG, "Pronađen u lokalnoj bazi: " + localRecipe.getName());
                        openRecipeDetail(localRecipe);
                    } else {
                        Log.d(TAG, "Nije pronađen u lokalnoj bazi, proveravam Firebase...");

                        // Ako nije u lokalnoj, proveri Firebase
                        if (firestoreHelper != null && firebaseConnected) {
                            firestoreHelper.getRecipeById(selectedRecipe.getId(),
                                    new FirestoreHelper.OnCompleteListener() {
                                        @Override
                                        public void onSuccess(Object result) {
                                            Recipe firestoreRecipe = (Recipe) result;
                                            Log.d(TAG, "Pronađen u Firestore: " + firestoreRecipe.getName());

                                            // Sačuvaj u lokalnu bazu za ubuduće
                                            databaseHelper.addRecipeFromFirestore(firestoreRecipe);

                                            runOnUiThread(() -> {
                                                openRecipeDetail(firestoreRecipe);
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            Log.e(TAG, "Nije pronađen ni u Firestore: " + error);

                                            runOnUiThread(() -> {
                                                // Ako nije ni u Firestore-u, pokušaj sa podacima iz liste
                                                if (selectedRecipe.getName() != null &&
                                                        !selectedRecipe.getName().isEmpty()) {
                                                    Log.d(TAG, "Koristim podatke iz liste");
                                                    openRecipeDetail(selectedRecipe);
                                                } else {
                                                    Toast.makeText(RecipeList.this,
                                                            "Recept nije pronađen u bazi",
                                                            Toast.LENGTH_LONG).show();

                                                    // Refresh listu
                                                    applyCurrentFilter();
                                                }
                                            });
                                        }
                                    });
                        } else {
                            // Ako Firebase nije dostupan, pokušaj sa podacima iz liste
                            if (selectedRecipe.getName() != null && !selectedRecipe.getName().isEmpty()) {
                                Log.d(TAG, "Firebase nije dostupan, koristim podatke iz liste");
                                openRecipeDetail(selectedRecipe);
                            } else {
                                Toast.makeText(RecipeList.this,
                                        "Nema podataka o receptu",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Greška pri kliku: " + e.getMessage(), e);
                    Toast.makeText(RecipeList.this,
                            "Greška: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetFilterButtons() {
        // Sva dugmad postavi na transparentnu sivu
        int deselectedColor = getResources().getColor(R.color.buttonBackgroundLight);
        btnAll.setBackgroundTintList(ColorStateList.valueOf(deselectedColor));
        btnSweet.setBackgroundTintList(ColorStateList.valueOf(deselectedColor));
        btnSalty.setBackgroundTintList(ColorStateList.valueOf(deselectedColor));
    }

    private void loadAllRecipes() {
        Log.d("LOAD", "Učitavam SVE recepte...");

        if (firebaseConnected && firestoreHelper != null) {
            Log.d("LOAD", "Koristim Firebase...");

            firestoreHelper.getAllRecipes(new FirestoreHelper.OnRecipesLoadedListener() {
                @Override
                public void onRecipesLoaded(List<Recipe> recipes) {
                    Log.d("LOAD", "Firestore vratio: " + recipes.size() + " recepata");

                    // VAŽNO: Sinhronizuj sa lokalnom bazom!
                    syncWithLocalDatabase(recipes);

                    runOnUiThread(() -> {
                        allRecipes = recipes;
                        updateAdapter(allRecipes);
                        Toast.makeText(RecipeList.this,
                                "Prikazano " + recipes.size() + " recepata sa Firebase",
                                Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e("LOAD", "Firestore greška: " + error);

                    runOnUiThread(() -> {
                        // Fallback na SQLite
                        allRecipes = databaseHelper.getAllRecipes();
                        Log.d("LOAD", "SQLite fallback: " + allRecipes.size() + " recepata");
                        updateAdapter(allRecipes);
                        Toast.makeText(RecipeList.this,
                                "Prikazano " + allRecipes.size() + " recepata iz lokalne baze",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            Log.d("LOAD", "Firebase nije povezan, koristim SQLite");
            allRecipes = databaseHelper.getAllRecipes();
            updateAdapter(allRecipes);
            Toast.makeText(this,
                    "Prikazano " + allRecipes.size() + " recepata iz lokalne baze",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSweetRecipes() {
        Log.d("FILTER", "Učitavam SLATKE recepte...");

        if (firebaseConnected && firestoreHelper != null) {
            firestoreHelper.getRecipesByType(true, new FirestoreHelper.OnRecipesLoadedListener() {
                @Override
                public void onRecipesLoaded(List<Recipe> recipes) {
                    Log.d("FILTER", "Firestore slatki: " + recipes.size());

                    runOnUiThread(() -> {
                        // Ažuriraj allRecipes za konzistentnost
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            allRecipes.removeIf(r -> r.isSweet());
                        }
                        allRecipes.addAll(recipes);

                        updateAdapter(recipes);
                        Toast.makeText(RecipeList.this,
                                "Prikazano " + recipes.size() + " slatkih recepata sa Firebase",
                                Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e("FILTER", "Firestore slatki greška: " + error);

                    runOnUiThread(() -> {
                        List<Recipe> sweetRecipes = databaseHelper.getRecipesByType(true);
                        Log.d("FILTER", "SQLite slatki: " + sweetRecipes.size());

                        updateAdapter(sweetRecipes);
                        Toast.makeText(RecipeList.this,
                                "Prikazano " + sweetRecipes.size() + " slatkih recepata iz lokalne baze",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            List<Recipe> sweetRecipes = databaseHelper.getRecipesByType(true);
            updateAdapter(sweetRecipes);
            Toast.makeText(this,
                    "Prikazano " + sweetRecipes.size() + " slatkih recepata iz lokalne baze",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSaltyRecipes() {
        Log.d("FILTER", "Učitavam SLANE recepte...");

        if (firebaseConnected && firestoreHelper != null) {
            firestoreHelper.getRecipesByType(false, new FirestoreHelper.OnRecipesLoadedListener() {
                @Override
                public void onRecipesLoaded(List<Recipe> recipes) {
                    Log.d("FILTER", "Firestore slani: " + recipes.size());

                    runOnUiThread(() -> {
                        // Ažuriraj allRecipes za konzistentnost
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            allRecipes.removeIf(r -> !r.isSweet());
                        }
                        allRecipes.addAll(recipes);

                        updateAdapter(recipes);
                        Toast.makeText(RecipeList.this,
                                "Prikazano " + recipes.size() + " slanih recepata sa Firebase",
                                Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e("FILTER", "Firestore slani greška: " + error);

                    runOnUiThread(() -> {
                        List<Recipe> saltyRecipes = databaseHelper.getRecipesByType(false);
                        Log.d("FILTER", "SQLite slani: " + saltyRecipes.size());

                        updateAdapter(saltyRecipes);
                        Toast.makeText(RecipeList.this,
                                "Prikazano " + saltyRecipes.size() + " slanih recepata iz lokalne baze",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            List<Recipe> saltyRecipes = databaseHelper.getRecipesByType(false);
            updateAdapter(saltyRecipes);
            Toast.makeText(this,
                    "Prikazano " + saltyRecipes.size() + " slanih recepata iz lokalne baze",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAdapter(List<Recipe> recipes) {
        if (recipes.isEmpty()) {
            Toast.makeText(this, "Nema recepata za prikaz", Toast.LENGTH_SHORT).show();
        }
        adapter = new RecipeAdapter(this, recipes);
        listViewRecipes.setAdapter(adapter);
    }

    private void applyCurrentFilter() {
        switch (currentFilter) {
            case "all":
                loadAllRecipes();
                break;
            case "sweet":
                loadSweetRecipes();
                break;
            case "salty":
                loadSaltyRecipes();
                break;
        }
    }

    public void addFood() {
        Intent intent = new Intent(this, AddNew.class);
        startActivity(intent);
    }

    // ISPRAVLJENA metoda za otvaranje detalja
    private void openRecipeDetail(Recipe recipe) {
        try {
            Intent intent = new Intent(this, RecipeDetailActivity.class);

            // Pošalji CELE podatke o receptu
            intent.putExtra("recipe_id", recipe.getId());
            intent.putExtra("recipe_name", recipe.getName());
            intent.putExtra("recipe_image", recipe.getImagePath());
            intent.putExtra("recipe_is_sweet", recipe.isSweet());
            intent.putExtra("recipe_is_baked", recipe.isBaked());
            intent.putExtra("recipe_temperature", recipe.getTemperature());
            intent.putExtra("recipe_time", recipe.getTime());
            intent.putExtra("recipe_ingredients", recipe.getIngredients());
            intent.putExtra("recipe_instructions", recipe.getInstructions());

            Log.d("OPEN_RECIPE", "Otvaram recept: " + recipe.getName() +
                    " (ID: " + recipe.getId() + ")");

            startActivity(intent);
        } catch (Exception e) {
            Log.e("OPEN_RECIPE", "Greška pri otvaranju detalja: " + e.getMessage());
            Toast.makeText(this, "Greška pri otvaranju recepta", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyCurrentFilter();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}