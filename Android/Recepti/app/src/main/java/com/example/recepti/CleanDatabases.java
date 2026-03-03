package com.example.recepti;

import android.content.Context;
import android.util.Log;

public class CleanDatabases {

    // GLAVNA METODA koja će sve očistiti
    public static void cleanAllDatabases(Context context) {
        Log.d("CLEANER", "Počinjem čišćenje baza...");

        // 1. Očisti SQLite
        cleanSQLiteDatabase(context);

        // 2. Očisti Firebase
        cleanFirestoreDatabase(context);

        Log.d("CLEANER", "Sve baze očišćene!");
    }

    private static void cleanSQLiteDatabase(Context context) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            int countBefore = dbHelper.getRecipeCount();

            dbHelper.deleteAllRecipes();

            int countAfter = dbHelper.getRecipeCount();
            dbHelper.close();

            Log.d("CLEANER", "SQLite: " + countBefore + " -> " + countAfter + " recepata");

        } catch (Exception e) {
            Log.e("CLEANER", "SQLite greška: " + e.getMessage());
        }
    }

    private static void cleanFirestoreDatabase(Context context) {
        try {
            FirestoreHelper firestoreHelper = new FirestoreHelper(context);

            // Prvo zaustavi sve real-time listener-e
            firestoreHelper.stopRealTimeSync();

            // Onda obriši sve podatke
            firestoreHelper.deleteAllRecipesFromFirestore(new FirestoreHelper.OnCompleteListener() {
                @Override
                public void onSuccess(Object result) {
                    Log.d("CLEANER", "Firestore: " + result);
                }

                @Override
                public void onFailure(String error) {
                    Log.e("CLEANER", "Firestore brisanje greška: " + error);
                }
            });

        } catch (Exception e) {
            Log.e("CLEANER", "Firestore inicijalizacija greška: " + e.getMessage());
        }
    }
}