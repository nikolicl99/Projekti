<?php

namespace App\Http\Controllers;

use App\Models\Recipe;
use Illuminate\Http\Request;

class RecipeController extends Controller
{
    /**
     * Prikazuje listu svih recepata.
     */
    public function index()
    {
        // Dohvati sve recepte iz baze
        $recipes = Recipe::all();

        // Prosledi recepte view-u
        return view('recipes.index', compact('recipes'));
    }


    /**
     * Prikazuje formu za kreiranje novog recepta.
     */
    public function create()
    {
        return view('recipes.create');
    }

    /**
     * Čuva novi recept u bazi podataka.
     */
    public function store(Request $request)
    {
        $validatedData = $request->validate([
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'ingredients' => 'required|string',
            'steps' => 'required|string',
            'prep_time' => 'required|integer',
            'difficulty' => 'required|string',
        ]);

        Recipe::create($validatedData);

        return redirect()->route('recipes.index')
                         ->with('success', 'Recept je uspešno dodat!');
    }

    /**
     * Prikazuje detalje o određenom receptu.
     */
    public function show(Recipe $recipe)
    {
        return view('recipes.show', compact('recipe'));
    }

    /**
     * Prikazuje formu za izmenu postojećeg recepta.
     */
    public function edit(Recipe $recipe)
    {
        return view('recipes.edit', compact('recipe'));
    }

    /**
     * Ažurira postojeći recept u bazi podataka.
     */
    public function update(Request $request, Recipe $recipe)
    {
        $validatedData = $request->validate([
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'ingredients' => 'required|string',
            'steps' => 'required|string',
            'prep_time' => 'required|integer',
            'difficulty' => 'required|string',
        ]);

        $recipe->update($validatedData);

        return redirect()->route('recipes.index')
                         ->with('success', 'Recept je uspešno ažuriran!');
    }

    /**
     * Briše recept iz baze podataka.
     */
    public function destroy(Recipe $recipe)
    {
        $recipe->delete();

        return redirect()->route('recipes.index')
                         ->with('success', 'Recept je uspešno obrisan!');
    }
}