package com.example.recepti;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class FirestoreHelper {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Context context;
    private ListenerRegistration realTimeListener;

    public FirestoreHelper(Context context) throws Exception {
        this.context = context;
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                throw new Exception("Firebase nije inicijalizovan");
            }
            db = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();
        } catch (Exception e) {
            throw new Exception("Firebase greška: " + e.getMessage());
        }
    }

    // ========== METODE ZA SLIKE ==========

    /**
     * Uploaduje i kompresuje sliku u thumbnail (300x300)
     */
    public void uploadRecipeImage(String localImagePath, String recipeId, OnCompleteListener listener) {
        if (localImagePath == null || localImagePath.isEmpty()) {
            listener.onSuccess(null);
            return;
        }

        try {
            File imageFile = new File(localImagePath);
            if (!imageFile.exists()) {
                Log.d("IMAGE_UPLOAD", "Fajl ne postoji: " + localImagePath);
                listener.onSuccess(null);
                return;
            }

            // 1. Učitaj i kompresuj sliku
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = calculateInSampleSize(localImagePath); // Auto sample

            Bitmap originalBitmap = BitmapFactory.decodeFile(localImagePath, options);

            if (originalBitmap == null) {
                Log.e("IMAGE_UPLOAD", "Ne mogu da dekodiram sliku");
                listener.onSuccess(null);
                return;
            }

            // 2. Kreiraj thumbnail (maks 300x300)
            int maxDimension = 300;
            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();

            float scale = Math.min((float) maxDimension / width, (float) maxDimension / height);
            int newWidth = (int) (width * scale);
            int newHeight = (int) (height * scale);

            Bitmap thumbnail = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

            // 3. Kompresuj u JPEG (70% kvaliteta)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageData = baos.toByteArray();

            Log.d("IMAGE_UPLOAD", "Thumbnail: " + newWidth + "x" + newHeight + " | Size: " + (imageData.length / 1024) + "KB");

            // 4. Upload na Firebase Storage
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("recipe_thumbs/" + recipeId + ".jpg");

            UploadTask uploadTask = imageRef.putBytes(imageData);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // 5. Dobavi download URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Log.d("IMAGE_UPLOAD", "Slika uploadovana: " + imageUrl);
                    listener.onSuccess(imageUrl);
                });
            }).addOnFailureListener(e -> {
                Log.e("IMAGE_UPLOAD", "Upload greška: " + e.getMessage());
                listener.onFailure("Upload greška: " + e.getMessage());
            });

            // Oslobodi memoriju
            originalBitmap.recycle();
            thumbnail.recycle();

        } catch (Exception e) {
            Log.e("IMAGE_UPLOAD", "Greška: " + e.getMessage());
            listener.onFailure("Greška: " + e.getMessage());
        }
    }

    /**
     * Briše sliku sa Storage-a
     */
    public void deleteRecipeImage(String recipeId, OnCompleteListener listener) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("recipe_thumbs/" + recipeId + ".jpg");

        imageRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("IMAGE_DELETE", "Slika obrisana: " + recipeId);
            listener.onSuccess(null);
        }).addOnFailureListener(e -> {
            Log.e("IMAGE_DELETE", "Greška: " + e.getMessage());
            listener.onFailure("Greška: " + e.getMessage());
        });
    }

    /**
     * Pomoćna metoda za izračunavanje inSampleSize za BitmapFactory
     */
    private int calculateInSampleSize(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        final int imageHeight = options.outHeight;
        final int imageWidth = options.outWidth;
        int inSampleSize = 1;

        if (imageHeight > 1200 || imageWidth > 1200) {
            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            while ((halfHeight / inSampleSize) >= 600 && (halfWidth / inSampleSize) >= 600) {
                inSampleSize *= 2;
            }
        }

        Log.d("IMAGE_SAMPLE", "Original: " + imageWidth + "x" + imageHeight + " | Sample: " + inSampleSize);
        return inSampleSize;
    }

    /**
     * Dodaje recept SA SLIKOM
     */
    public void addRecipeWithImage(Recipe recipe, OnCompleteListener listener) {
        // Prvo generiši ID ako ga nema
        if (recipe.getId() == null || recipe.getId().isEmpty()) {
            recipe.setId(db.collection("recipes").document().getId());
        }

        final String recipeId = recipe.getId();
        final String localImagePath = recipe.getImagePath();

        // Ako ima lokalnu sliku, uploaduj je
        if (localImagePath != null && !localImagePath.isEmpty() && !localImagePath.startsWith("http") && !localImagePath.startsWith("https")) {

            Log.d("ADD_RECIPE", "Uploadujem sliku za recept: " + recipe.getName());

            uploadRecipeImage(localImagePath, recipeId, new OnCompleteListener() {
                @Override
                public void onSuccess(Object result) {
                    String imageUrl = (String) result;
                    recipe.setImagePath(imageUrl); // Postavi Firebase URL

                    // Sada dodaj recept u Firestore
                    addRecipeToFirestore(recipe, listener);
                }

                @Override
                public void onFailure(String error) {
                    Log.e("ADD_RECIPE", "Greška pri upload-u slike: " + error);
                    // Dodaj recept BEZ slike
                    recipe.setImagePath(null);
                    addRecipeToFirestore(recipe, listener);
                }
            });
        } else {
            // Nema slike ili već ima URL
            addRecipeToFirestore(recipe, listener);
        }
    }

    /**
     * Ažuriraj recept SA SLIKOM
     */
    public void updateRecipeWithImage(Recipe recipe, String newLocalImagePath, OnCompleteListener listener) {
        final String recipeId = recipe.getId();

        // Ako ima NOVU lokalnu sliku
        if (newLocalImagePath != null && !newLocalImagePath.isEmpty() && !newLocalImagePath.startsWith("http") && !newLocalImagePath.startsWith("https")) {

            Log.d("UPDATE_RECIPE", "Uploadujem NOVU sliku za: " + recipe.getName());

            // Prvo uploaduj novu sliku
            uploadRecipeImage(newLocalImagePath, recipeId, new OnCompleteListener() {
                @Override
                public void onSuccess(Object result) {
                    String newImageUrl = (String) result;
                    recipe.setImagePath(newImageUrl);

                    // Ažuriraj recept sa novom slikom
                    updateRecipeInFirestore(recipe, listener);
                }

                @Override
                public void onFailure(String error) {
                    Log.e("UPDATE_RECIPE", "Greška pri upload-u nove slike: " + error);
                    // Ažuriraj bez promene slike
                    updateRecipeInFirestore(recipe, listener);
                }
            });
        } else {
            // Nema nove slike, samo ažuriraj podatke
            updateRecipeInFirestore(recipe, listener);
        }
    }

    /**
     * Briše recept i njegovu sliku
     */
    public void deleteRecipeWithImage(String recipeId, OnCompleteListener listener) {
        // Prvo obriši sliku
        deleteRecipeImage(recipeId, new OnCompleteListener() {
            @Override
            public void onSuccess(Object result) {
                // Onda obriši recept
                deleteRecipeFromFirestore(recipeId, listener);
            }

            @Override
            public void onFailure(String error) {
                Log.e("DELETE_RECIPE", "Greška pri brisanju slike: " + error);
                // Pokušaj da obrišeš recept makar
                deleteRecipeFromFirestore(recipeId, listener);
            }
        });
    }

    // ========== PRIVATE HELPER METODE ==========

    private void addRecipeToFirestore(Recipe recipe, OnCompleteListener listener) {
        Map<String, Object> recipeData = createRecipeMap(recipe);

        db.collection("recipes").document(recipe.getId()).set(recipeData).addOnSuccessListener(aVoid -> listener.onSuccess(recipe)).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    private void updateRecipeInFirestore(Recipe recipe, OnCompleteListener listener) {
        Map<String, Object> updates = createRecipeMap(recipe);
        updates.put("lastModified", System.currentTimeMillis());

        db.collection("recipes").document(recipe.getId()).update(updates).addOnSuccessListener(aVoid -> listener.onSuccess(recipe)).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    private void deleteRecipeFromFirestore(String recipeId, OnCompleteListener listener) {
        db.collection("recipes").document(recipeId).delete().addOnSuccessListener(aVoid -> listener.onSuccess(null)).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    private Map<String, Object> createRecipeMap(Recipe recipe) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", recipe.getName());
        map.put("imagePath", recipe.getImagePath());
        map.put("isBaked", recipe.isBaked());
        map.put("isSweet", recipe.isSweet());
        map.put("temperature", recipe.getTemperature());
        map.put("time", recipe.getTime());
        map.put("ingredients", recipe.getIngredients());
        map.put("instructions", recipe.getInstructions());
        map.put("createdAt", recipe.getCreatedAt());
        map.put("lastModified", recipe.getLastModified());
        map.put("firestoreUpdatedAt", FieldValue.serverTimestamp());
        return map;
    }

    // ... OSTALE POSTOJEĆE METODE (getAllRecipes, getRecipeById, itd.) OSTAJU ISTE ...


    // 1. Metoda za zaustavljanje real-time sync-a
    public void stopRealTimeSync() {
        if (realTimeListener != null) {
            realTimeListener.remove();
            realTimeListener = null;
            Log.d("FIRESTORE", "Real-time sync zaustavljen");
        }
    }

    // 2. Metoda za brisanje SVIH podataka iz Firestore-a
    public void deleteAllRecipesFromFirestore(OnCompleteListener listener) {
        db.collection("recipes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                WriteBatch batch = db.batch();
                int count = 0;

                for (DocumentSnapshot document : task.getResult()) {
                    batch.delete(document.getReference());
                    count++;
                }

                if (count > 0) {
                    int finalCount = count;
                    batch.commit().addOnSuccessListener(aVoid -> {
                        listener.onSuccess("Obrisano " + finalCount + " recepata iz Firestore-a");
                    }).addOnFailureListener(e -> {
                        listener.onFailure("Greška pri brisanju: " + e.getMessage());
                    });
                } else {
                    listener.onSuccess("Firestore već prazan (0 recepata)");
                }
            } else {
                listener.onFailure("Greška pri čitanju: " + task.getException().getMessage());
            }
        });
    }

    // 3. MODIFIKUJ postojeću real-time metodu da čuva listener
    public void getRecipesRealTime(OnRecipesLoadedListener listener) {
        // Prvo zaustavi stari listener ako postoji
        stopRealTimeSync();

        // Sačuvaj novi listener
        realTimeListener = db.collection("recipes").orderBy("lastModified", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("FIRESTORE", "Real-time greška: " + error.getMessage());
                listener.onError(error.getMessage());
                return;
            }

            List<Recipe> recipes = new ArrayList<>();
            if (value != null && !value.isEmpty()) {
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Recipe recipe = documentToRecipe(doc);
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                }
            }
            listener.onRecipesLoaded(recipes);
        });
    }

    // 4. Nova metoda koja sluša SAMO nove recepte (nakon određenog vremena)
    public void getRecipesRealTimeFromTime(long fromTime, OnRecipesLoadedListener listener) {
        stopRealTimeSync();

        realTimeListener = db.collection("recipes").whereGreaterThan("lastModified", fromTime).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("FIRESTORE", "Real-time greška: " + error.getMessage());
                listener.onError(error.getMessage());
                return;
            }

            if (value != null && !value.isEmpty()) {
                List<Recipe> newRecipes = new ArrayList<>();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Recipe recipe = documentToRecipe(doc);
                    if (recipe != null) {
                        newRecipes.add(recipe);
                    }
                }

                if (!newRecipes.isEmpty()) {
                    Log.d("FIRESTORE", "Novih recepata: " + newRecipes.size());
                    listener.onRecipesLoaded(newRecipes);
                }
            }
        });
    }

    // 5. Simple metoda za brisanje JEDNOG recepta (ako već nemaš)
    public void deleteRecipeById(String id, OnCompleteListener listener) {
        db.collection("recipes").document(id).delete().addOnSuccessListener(aVoid -> listener.onSuccess("Obrisano")).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getRecipesRealTimeFromNow(OnRecipesLoadedListener listener) {
        // Prvo zaustavi stari listener ako postoji
        stopRealTimeSync();

        // Postavi da slušaš samo NOVE recepte (nakon trenutnog vremena)
        final long startTime = System.currentTimeMillis();

        realTimeListener = db.collection("recipes").whereGreaterThan("lastModified", startTime - 1000) // -1s za sigurnost
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FIRESTORE", "Real-time greška: " + error.getMessage());
                        listener.onError(error.getMessage());
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        List<Recipe> newRecipes = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Recipe recipe = documentToRecipe(doc);
                            if (recipe != null) {
                                newRecipes.add(recipe);
                            }
                        }

                        if (!newRecipes.isEmpty()) {
                            Log.d("FIRESTORE", "Novih recepata: " + newRecipes.size());
                            listener.onRecipesLoaded(newRecipes);
                        }
                    }
                });
    }

    // Simple test metoda
    public void testConnection(OnTestCompleteListener listener) {
        Map<String, Object> test = new HashMap<>();
        test.put("test", true);
        test.put("timestamp", FieldValue.serverTimestamp());

        db.collection("test_connection").add(test).addOnSuccessListener(documentReference -> {
            listener.onSuccess("Firestore povezan: " + documentReference.getId());
        }).addOnFailureListener(e -> {
            listener.onFailure("Firestore greška: " + e.getMessage());
        });
    }

    // Dodaj recept
    public void addRecipe(Recipe recipe, OnCompleteListener listener) {
        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("name", recipe.getName());
        recipeData.put("imagePath", recipe.getImagePath());
        recipeData.put("isBaked", recipe.isBaked());
        recipeData.put("isSweet", recipe.isSweet());
        recipeData.put("temperature", recipe.getTemperature());
        recipeData.put("time", recipe.getTime());
        recipeData.put("ingredients", recipe.getIngredients());
        recipeData.put("instructions", recipe.getInstructions());
        recipeData.put("createdAt", recipe.getCreatedAt());
        recipeData.put("lastModified", recipe.getLastModified());
        recipeData.put("firestoreUpdatedAt", FieldValue.serverTimestamp());

        db.collection("recipes").add(recipeData).addOnSuccessListener(documentReference -> {
            recipe.setId(documentReference.getId());
            listener.onSuccess(recipe);
        }).addOnFailureListener(e -> {
            listener.onFailure(e.getMessage());
        });
    }

    // Ažuriraj recept
    public void updateRecipe(Recipe recipe, OnCompleteListener listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", recipe.getName());
        updates.put("imagePath", recipe.getImagePath());
        updates.put("isBaked", recipe.isBaked());
        updates.put("isSweet", recipe.isSweet());
        updates.put("temperature", recipe.getTemperature());
        updates.put("time", recipe.getTime());
        updates.put("ingredients", recipe.getIngredients());
        updates.put("instructions", recipe.getInstructions());
        updates.put("lastModified", recipe.getLastModified());
        updates.put("firestoreUpdatedAt", FieldValue.serverTimestamp());

        db.collection("recipes").document(recipe.getId()).update(updates).addOnSuccessListener(aVoid -> listener.onSuccess(recipe)).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Obriši recept
    public void deleteRecipe(String recipeId, OnCompleteListener listener) {
        db.collection("recipes").document(recipeId).delete().addOnSuccessListener(aVoid -> listener.onSuccess(null)).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Pomocna metoda za konvertovanje dokumenta u Recipe
    private Recipe documentToRecipe(DocumentSnapshot doc) {
        try {
            Recipe recipe = new Recipe();
            recipe.setId(doc.getId());
            recipe.setName(doc.getString("name"));
            recipe.setImagePath(doc.getString("imagePath"));

            Boolean isBaked = doc.getBoolean("isBaked");
            recipe.setBaked(isBaked != null ? isBaked : false);

            Boolean isSweet = doc.getBoolean("isSweet");
            recipe.setSweet(isSweet != null ? isSweet : false);

            recipe.setTemperature(doc.getString("temperature"));
            recipe.setTime(doc.getString("time"));
            recipe.setIngredients(doc.getString("ingredients"));
            recipe.setInstructions(doc.getString("instructions"));

            // Uzmi timestamp-ove
            Long createdAt = doc.getLong("createdAt");
            if (createdAt != null) {
                recipe.setCreatedAt(createdAt);
            }

            Long lastModified = doc.getLong("lastModified");
            if (lastModified != null) {
                recipe.setLastModified(lastModified);
            } else {
                // Ako nema lastModified, koristi trenutno vreme
                recipe.setLastModified(System.currentTimeMillis());
            }

            return recipe;
        } catch (Exception e) {
            Log.e("FIRESTORE", "Greška pri konvertovanju dokumenta: " + e.getMessage());
            return null;
        }
    }

    // Preuzmi sve recepte (jednom, ne real-time)
    public void getAllRecipes(OnRecipesLoadedListener listener) {
        db.collection("recipes").orderBy("lastModified", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Recipe> recipes = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Recipe recipe = documentToRecipe(document);
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                }
                listener.onRecipesLoaded(recipes);
            } else {
                listener.onError(task.getException().getMessage());
            }
        });
    }

    // Preuzmi recepte po tipu (slatko/slano) - ISPRAVLJENO
    public void getRecipesByType(boolean isSweet, OnRecipesLoadedListener listener) {
        db.collection("recipes").whereEqualTo("isSweet", isSweet) // ISPRAVLJENO: "isSweet" umesto "sweet"
                .orderBy("lastModified", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Recipe> recipes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = documentToRecipe(document);
                            if (recipe != null) {
                                recipes.add(recipe);
                            }
                        }
                        listener.onRecipesLoaded(recipes);
                    } else {
                        listener.onError(task.getException().getMessage());
                    }
                });
    }

    // Preuzmi recept po ID-u
    public void getRecipeById(String recipeId, OnCompleteListener listener) {
        db.collection("recipes").document(recipeId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Recipe recipe = documentToRecipe(documentSnapshot);
                if (recipe != null) {
                    listener.onSuccess(recipe);
                } else {
                    listener.onFailure("Greška pri konvertovanju recepta");
                }
            } else {
                listener.onFailure("Recept nije pronađen");
            }
        }).addOnFailureListener(e -> {
            listener.onFailure(e.getMessage());
        });
    }

    // Pretraži recepte po nazivu
    public void searchRecipesByName(String query, OnRecipesLoadedListener listener) {
        if (query == null || query.trim().isEmpty()) {
            // Ako je query prazan, vrati sve recepte
            getAllRecipes(listener);
            return;
        }

        String searchQuery = query.trim().toLowerCase();
        db.collection("recipes").orderBy("name").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Recipe> recipes = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Recipe recipe = documentToRecipe(document);
                    if (recipe != null && recipe.getName() != null) {
                        String recipeName = recipe.getName().toLowerCase();
                        if (recipeName.contains(searchQuery)) {
                            recipes.add(recipe);
                        }
                    }
                }
                listener.onRecipesLoaded(recipes);
            } else {
                listener.onError(task.getException().getMessage());
            }
        });
    }

    // Sinhronizuj lokalne promene na Firebase
    public void syncLocalRecipeToFirebase(Recipe localRecipe, OnCompleteListener listener) {
        // Prvo proveri da li recept postoji na Firebase-u
        getRecipeById(localRecipe.getId(), new OnCompleteListener() {
            @Override
            public void onSuccess(Object result) {
                Recipe firestoreRecipe = (Recipe) result;
                // Ako lokalna verzija je novija, ažuriraj Firebase
                if (localRecipe.getLastModified() > firestoreRecipe.getLastModified()) {
                    updateRecipe(localRecipe, listener);
                } else {
                    // Firebase verzija je novija ili ista
                    listener.onSuccess(firestoreRecipe);
                }
            }

            @Override
            public void onFailure(String error) {
                // Recept ne postoji na Firebase-u, dodaj ga
                addRecipe(localRecipe, listener);
            }
        });
    }

    // Interfejsi za callback
    public interface OnTestCompleteListener {
        void onSuccess(String message);

        void onFailure(String error);
    }

    public interface OnCompleteListener {
        void onSuccess(Object result);

        void onFailure(String error);
    }

    public interface OnRecipesLoadedListener {
        void onRecipesLoaded(List<Recipe> recipes);

        void onError(String error);
    }


}