@extends('layout.app')

@php
    $difficultyLabels = [
        'easy' => 'Lako',
        'medium' => 'Srednje',
        'hard' => 'Teško'
    ];
@endphp

@section('content')
<div class="container my-5">
    <div class="card shadow-lg p-4 border-0 rounded-3">

        <!-- Gornji deo: Naziv sa leve, vreme i težina sa desne -->
        <div class="position-relative text-center mb-3">
    <div>
        <h1 class="mb-1" style="color:#ff6f61;">{{ $recipe->title }}</h1>
        <p class="text-muted">{{ $recipe->description }}</p>
    </div>

    <div class="position-absolute end-0 top-0 text-end">
        <p class="mb-1"><strong>Vreme pripreme:</strong> {{ $recipe->prep_time }} min</p>
        <p class="mb-0"><strong>Težina:</strong> {{ $difficultyLabels[$recipe->difficulty] ?? $recipe->difficulty }}</p>
    </div>
</div>


        <!-- Sastojci i koraci -->
        <div class="row">
            <div class="col-md-6 mb-4">
                <h4 class="mb-3" style="color:#333;">Sastojci</h4>
                <div class="border rounded p-3 bg-light">
                    {!! nl2br(e($recipe->ingredients)) !!}
                </div>
            </div>
            <div class="col-md-6 mb-4">
                <h4 class="mb-3" style="color:#333;">Koraci pripreme</h4>
                <div class="border rounded p-3 bg-light">
                    {!! nl2br(e($recipe->steps)) !!}
                </div>
            </div>
        </div>

        <!-- Dugmad -->
        <div class="mt-4 text-end">
            <a href="{{ route('recipes.edit', $recipe->id) }}" class="btn btn-primary">Izmeni</a>
            <form action="{{ route('recipes.destroy', $recipe->id) }}" method="POST" class="d-inline">
                @csrf
                @method('DELETE')
                <button type="submit" class="btn btn-danger">Obriši</button>
            </form>
            <a href="{{ route('recipes.index') }}" class="btn btn-secondary">Nazad na listu</a>
        </div>
    </div>
</div>
@endsection
