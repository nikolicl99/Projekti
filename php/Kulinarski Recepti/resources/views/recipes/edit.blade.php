@extends('layout.app')

@section('content')
<div class="container">
    <h1 class="mt-4">Izmeni recept</h1>

    <form action="{{ route('recipes.update', $recipe->id) }}" method="POST">
        @csrf
        @method('PUT') <div class="mb-3">
            <label for="title" class="form-label">Naziv recepta</label>
            <input type="text" name="title" value="{{ $recipe->title }}" class="form-control"> </div>

        <div class="mb-3">
            <label for="description" class="form-label">Opis</label>
            <textarea name="description" id="description" class="form-control" rows="3" required>{{ $recipe->description }}</textarea> </div>

        <div class="mb-3">
            <label for="ingredients" class="form-label">Sastojci</label>
            <textarea name="ingredients" id="ingredients" class="form-control" rows="5" required>{{ $recipe->ingredients }}</textarea> </div>

        <div class="mb-3">
            <label for="steps" class="form-label">Koraci pripreme</label>
            <textarea name="steps" id="steps" class="form-control" rows="5" required>{{ $recipe->steps }}</textarea> </div>

        <div class="mb-3">
            <label for="prep_time" class="form-label">Vreme pripreme (u minutima)</label>
            <input type="number" name="prep_time" id="prep_time" class="form-control" value="{{ $recipe->prep_time }}" required> </div>

        <div class="mb-3">
            <label for="difficulty" class="form-label">Težina</label>
            <select name="difficulty" id="difficulty" class="form-select" required>
                <option value="easy" {{ $recipe->difficulty == 'easy' ? 'selected' : '' }}>Lako</option>
                <option value="medium" {{ $recipe->difficulty == 'medium' ? 'selected' : '' }}>Srednje</option>
                <option value="hard" {{ $recipe->difficulty == 'hard' ? 'selected' : '' }}>Teško</option>
            </select>
        </div>

        <button type="submit" class="btn btn-success">Sačuvaj izmene</button>
        <a href="{{ route('recipes.index') }}" class="btn btn-secondary">Nazad na listu</a>
    </form>
</div>
@endsection