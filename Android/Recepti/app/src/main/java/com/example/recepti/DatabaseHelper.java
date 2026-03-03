package com.example.recepti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Recipes.db";
    private static final int DATABASE_VERSION = 3;

    private Context context;

    // Naziv tabele i kolone
    private static final String TABLE_RECIPES = "recipes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_IS_BAKED = "is_baked";
    private static final String COLUMN_IS_SWEET = "is_sweet";
    private static final String COLUMN_TEMPERATURE = "temperature";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_INSTRUCTIONS = "instructions";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_LAST_MODIFIED = "last_modified";

    // SQL za kreiranje tabele
    private static final String CREATE_TABLE_RECIPES_V3 =
            "CREATE TABLE " + TABLE_RECIPES + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_IMAGE_PATH + " TEXT," +
                    COLUMN_IS_BAKED + " INTEGER," +
                    COLUMN_IS_SWEET + " INTEGER," +
                    COLUMN_TEMPERATURE + " TEXT," +
                    COLUMN_TIME + " TEXT," +
                    COLUMN_INGREDIENTS + " TEXT," +
                    COLUMN_INSTRUCTIONS + " TEXT," +
                    COLUMN_CREATED_AT + " INTEGER," +
                    COLUMN_LAST_MODIFIED + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RECIPES_V3);
        Log.d("DATABASE", "Tabela kreirana: " + TABLE_RECIPES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_RECIPES + " ADD COLUMN " + COLUMN_IS_SWEET + " INTEGER DEFAULT 0");
        }
        if (oldVersion < 3) {
            migrateToV3(db);
        }
        Log.d("DATABASE", "Baza nadogradjena sa verzije " + oldVersion + " na " + newVersion);
    }

    private void migrateToV3(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE recipes_temp AS SELECT * FROM " + TABLE_RECIPES);
        db.execSQL("DROP TABLE " + TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_RECIPES_V3);

        long currentTime = System.currentTimeMillis();
        db.execSQL("INSERT INTO " + TABLE_RECIPES + " (" +
                COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_IMAGE_PATH + ", " +
                COLUMN_IS_BAKED + ", " + COLUMN_IS_SWEET + ", " + COLUMN_TEMPERATURE + ", " +
                COLUMN_TIME + ", " + COLUMN_INGREDIENTS + ", " + COLUMN_INSTRUCTIONS + ", " +
                COLUMN_CREATED_AT + ", " + COLUMN_LAST_MODIFIED + ") " +
                "SELECT CAST(" + COLUMN_ID + " AS TEXT), " + COLUMN_NAME + ", " +
                COLUMN_IMAGE_PATH + ", " + COLUMN_IS_BAKED + ", " + COLUMN_IS_SWEET + ", " +
                COLUMN_TEMPERATURE + ", " + COLUMN_TIME + ", " + COLUMN_INGREDIENTS + ", " +
                COLUMN_INSTRUCTIONS + ", " + currentTime + ", " + currentTime +
                " FROM recipes_temp");

        db.execSQL("DROP TABLE recipes_temp");
    }

    // ========== DODAJ OVE METODE ZA ČIŠĆENJE ==========

    // 1. Obriši SVE recepte iz baze
    public void deleteAllRecipes() {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_RECIPES, null, null);
        db.close();

        Log.d("DATABASE", "Obrisano " + count + " recepata iz SQLite baze");

        // Očisti i slike
        deleteAllRecipeImages();
    }

    // 2. Vrati broj recepata u bazi
    public int getRecipeCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECIPES, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DATABASE", "Greška pri brojanju recepata: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return count;
    }

    // 3. Obriši sve slike recepata
    private void deleteAllRecipeImages() {
        try {
            File internalDir = context.getFilesDir();
            File[] files = internalDir.listFiles();
            int deletedCount = 0;

            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("recipe_") &&
                            (file.getName().endsWith(".jpg") ||
                                    file.getName().endsWith(".png") ||
                                    file.getName().endsWith(".jpeg"))) {
                        if (file.delete()) {
                            deletedCount++;
                        }
                    }
                }
            }

            Log.d("DATABASE", "Obrisano " + deletedCount + " slika recepata");
        } catch (Exception e) {
            Log.e("DATABASE", "Greška pri brisanju slika: " + e.getMessage());
        }
    }

    // 4. Resetuj bazu (kompletno obriši i ponovo kreiraj)
    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Obriši tabelu
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);

        // Ponovo kreiraj
        onCreate(db);

        db.close();

        Log.d("DATABASE", "Baza kompletno resetovana");
    }

    // ========== POSTOJEĆE METODE ==========

    public long addRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (recipe.getId() == null || recipe.getId().isEmpty()) {
            recipe.setId(UUID.randomUUID().toString());
        }
        if (recipe.getCreatedAt() == 0) {
            recipe.setCreatedAt(System.currentTimeMillis());
        }
        if (recipe.getLastModified() == 0) {
            recipe.setLastModified(System.currentTimeMillis());
        }

        values.put(COLUMN_ID, recipe.getId());
        values.put(COLUMN_NAME, recipe.getName());
        values.put(COLUMN_IMAGE_PATH, recipe.getImagePath());
        values.put(COLUMN_IS_BAKED, recipe.isBaked() ? 1 : 0);
        values.put(COLUMN_IS_SWEET, recipe.isSweet() ? 1 : 0);
        values.put(COLUMN_TEMPERATURE, recipe.getTemperature());
        values.put(COLUMN_TIME, recipe.getTime());
        values.put(COLUMN_INGREDIENTS, recipe.getIngredients());
        values.put(COLUMN_INSTRUCTIONS, recipe.getInstructions());
        values.put(COLUMN_CREATED_AT, recipe.getCreatedAt());
        values.put(COLUMN_LAST_MODIFIED, recipe.getLastModified());

        long result = db.insertWithOnConflict(TABLE_RECIPES, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Log.d("DATABASE", "Recept dodat: " + recipe.getName() + " (ID: " + recipe.getId() + ")");
        return result;
    }

    public long addRecipeFromFirestore(Recipe recipe) {
        return addRecipe(recipe);
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_RECIPES +
                    " ORDER BY " + COLUMN_LAST_MODIFIED + " DESC";
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    recipes.add(cursorToRecipe(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DATABASE", "Greška pri učitavanju recepata: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        Log.d("DATABASE", "Učitano " + recipes.size() + " recepata");
        return recipes;
    }

    public List<Recipe> getRecipesByType(boolean isSweet) {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_RECIPES +
                    " WHERE " + COLUMN_IS_SWEET + " = " + (isSweet ? 1 : 0) +
                    " ORDER BY " + COLUMN_LAST_MODIFIED + " DESC";
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    recipes.add(cursorToRecipe(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DATABASE", "Greška pri filtriranju recepata: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return recipes;
    }

    public Recipe getRecipe(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Recipe recipe = null;

        try {
            cursor = db.query(TABLE_RECIPES,
                    null,
                    COLUMN_ID + "=?",
                    new String[]{id},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                recipe = cursorToRecipe(cursor);
            }
        } catch (Exception e) {
            Log.e("DATABASE", "Greška pri preuzimanju recepta: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return recipe;
    }

    public Recipe getRecipeByFirestoreId(String firestoreId) {
        return getRecipe(firestoreId);
    }

    public void deleteRecipe(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_RECIPES, COLUMN_ID + " = ?", new String[]{id});
        db.close();

        Log.d("DATABASE", "Recept obrisan (ID: " + id + "), obrisano redova: " + rows);
    }

    public int updateRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        recipe.setLastModified(System.currentTimeMillis());

        values.put(COLUMN_NAME, recipe.getName());
        values.put(COLUMN_IMAGE_PATH, recipe.getImagePath());
        values.put(COLUMN_IS_BAKED, recipe.isBaked() ? 1 : 0);
        values.put(COLUMN_IS_SWEET, recipe.isSweet() ? 1 : 0);
        values.put(COLUMN_TEMPERATURE, recipe.getTemperature());
        values.put(COLUMN_TIME, recipe.getTime());
        values.put(COLUMN_INGREDIENTS, recipe.getIngredients());
        values.put(COLUMN_INSTRUCTIONS, recipe.getInstructions());
        values.put(COLUMN_LAST_MODIFIED, recipe.getLastModified());

        int rowsAffected = db.update(TABLE_RECIPES, values, COLUMN_ID + " = ?",
                new String[]{recipe.getId()});
        db.close();

        Log.d("DATABASE", "Recept ažuriran: " + recipe.getName() + ", promenjeno redova: " + rowsAffected);
        return rowsAffected;
    }

    public int updateRecipeFromFirestore(Recipe recipe) {
        return updateRecipe(recipe);
    }

    public void syncRecipeFromFirestore(Recipe firestoreRecipe) {
        Recipe localRecipe = getRecipe(firestoreRecipe.getId());

        if (localRecipe == null) {
            addRecipe(firestoreRecipe);
            Log.d("DATABASE", "Sinhronizovano: Dodat novi recept iz Firestore-a");
        } else if (firestoreRecipe.getLastModified() > localRecipe.getLastModified()) {
            updateRecipe(firestoreRecipe);
            Log.d("DATABASE", "Sinhronizovano: Ažuriran stari recept iz Firestore-a");
        } else {
            Log.d("DATABASE", "Sinhronizacija: Lokalna verzija je novija ili ista");
        }
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        Recipe recipe = new Recipe();

        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
        int imagePathIndex = cursor.getColumnIndex(COLUMN_IMAGE_PATH);
        int isBakedIndex = cursor.getColumnIndex(COLUMN_IS_BAKED);
        int isSweetIndex = cursor.getColumnIndex(COLUMN_IS_SWEET);
        int temperatureIndex = cursor.getColumnIndex(COLUMN_TEMPERATURE);
        int timeIndex = cursor.getColumnIndex(COLUMN_TIME);
        int ingredientsIndex = cursor.getColumnIndex(COLUMN_INGREDIENTS);
        int instructionsIndex = cursor.getColumnIndex(COLUMN_INSTRUCTIONS);
        int createdAtIndex = cursor.getColumnIndex(COLUMN_CREATED_AT);
        int lastModifiedIndex = cursor.getColumnIndex(COLUMN_LAST_MODIFIED);

        if (idIndex != -1) recipe.setId(cursor.getString(idIndex));
        if (nameIndex != -1) recipe.setName(cursor.getString(nameIndex));
        if (imagePathIndex != -1) recipe.setImagePath(cursor.getString(imagePathIndex));
        if (isBakedIndex != -1) recipe.setBaked(cursor.getInt(isBakedIndex) == 1);
        if (isSweetIndex != -1) recipe.setSweet(cursor.getInt(isSweetIndex) == 1);
        if (temperatureIndex != -1) recipe.setTemperature(cursor.getString(temperatureIndex));
        if (timeIndex != -1) recipe.setTime(cursor.getString(timeIndex));
        if (ingredientsIndex != -1) recipe.setIngredients(cursor.getString(ingredientsIndex));
        if (instructionsIndex != -1) recipe.setInstructions(cursor.getString(instructionsIndex));
        if (createdAtIndex != -1) recipe.setCreatedAt(cursor.getLong(createdAtIndex));
        if (lastModifiedIndex != -1) recipe.setLastModified(cursor.getLong(lastModifiedIndex));

        return recipe;
    }

    // 5. Dodatna metoda za proveru da li tabela postoji
    public boolean isTableExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_RECIPES + "'",
                null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}