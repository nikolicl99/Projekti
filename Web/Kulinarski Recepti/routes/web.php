<?php

use App\Http\Controllers\RecipeController;
use Illuminate\Support\Facades\Route;

// Ruta za početnu stranicu
Route::get('/', [RecipeController::class, 'index'])->name('recipes.index');

// Grupisanje resursnih ruta za recepte
Route::resource('recipes', RecipeController::class);