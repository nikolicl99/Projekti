@extends('layout.app')

@php
$difficultyLabels = [
'easy' => 'Lako',
'medium' => 'Srednje',
'hard' => 'Teško'
];
@endphp

@section('content')
<div class="d-flex justify-content-between align-items-center mb-4 header-container">
    <h1>Inspiriši se
i pripremi nešto dobro!</h1>
    <a href="{{ route('recipes.create') }}" class="btn btn-success">Dodaj recept</a>
</div>

<div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
    @foreach($recipes as $recipe)
    <div class="col">
        <div class="card h-100">
            <div class="card-body">
                <h5 class="card-title">{{ $recipe->title }}</h5>
                <p class="card-text">{{ $recipe->description }}</p>
                <p><strong>Sastojci:</strong> {{ $recipe->ingredients }}</p>
                <p><strong>Vreme pripreme:</strong> {{ $recipe->prep_time }} min</p>
                <p><strong>Težina:</strong> {{ $difficultyLabels[$recipe->difficulty] ?? $recipe->difficulty }}</p>

                <a href="{{ route('recipes.show', $recipe->id) }}" class="btn btn-outline-primary mt-2">Pogledaj više</a>
            </div>
        </div>
    </div>
    @endforeach
</div>
@endsection

@section('content')
<div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
    @foreach($recipes as $recipe)
    <div class="col">
        <div class="card h-100">
            <div class="card-body">
                <h5 class="card-title">{{ $recipe->title }}</h5>
                <p class="card-text">{{ $recipe->description }}</p>
                <p><strong>Sastojci:</strong> {{ $recipe->ingredients }}</p>
                <p><strong>Vreme pripreme:</strong> {{ $recipe->prep_time }} min</p>
                <p><strong>Težina:</strong> {{ $difficultyLabels[$recipe->difficulty] ?? $recipe->difficulty }}</p>

                <a href="{{ route('recipes.show', $recipe->id) }}" class="btn btn-outline-primary mt-2">Pogledaj više</a>
            </div>
        </div>
    </div>
    @endforeach
</div>
@endsection

@if(session('success'))
    <div id="success-alert" class="alert alert-success alert-dismissible fade show mt-3" role="alert" style="transition: opacity 0.5s ease;">
        {{ session('success') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
@endif

<script>
    // Automatsko nestajanje poruke uz fade-out animaciju
    setTimeout(function() {
        let alert = document.getElementById('success-alert');
        if (alert) {
            alert.style.opacity = '0'; // smanjuje vidljivost
            setTimeout(() => alert.remove(), 500); // potpuno uklanja element posle 0.5s
        }
    }, 3000);
</script>


