@extends('layout.app')

@section('content')
<div class="container mt-5">
    <h1 class="mb-4">Dodaj novi recept</h1>

    <form action="{{ route('recipes.store') }}" method="POST" enctype="multipart/form-data">
        @csrf
        
        <div class="row">
            <div class="col-md-6">
                <div class="mb-4">
                    <label for="title" class="form-label">Naziv recepta</label>
                    <input type="text" name="title" class="form-control" required placeholder="Unesite naziv recepta">
                </div>

                <div class="mb-4">
                    <label for="description" class="form-label">Opis recepta</label>
                    <textarea name="description" class="form-control" rows="8" required placeholder="Unesite kratak opis recepta"></textarea>
                </div>
                
            </div>

            <div class="col-md-6">
                <div class="mb-4">
                    <label for="ingredients" class="form-label">Sastojci</label>
                    <textarea name="ingredients" class="form-control" rows="8" required placeholder="Unesite sastojke (jedan po jedan red)"></textarea>
                </div>

                <div class="mb-4">
                    <label for="steps" class="form-label">Koraci pripreme</label>
                    <textarea name="steps" class="form-control" rows="8" required placeholder="Unesite korake pripreme (jedan po jedan red)"></textarea>
                </div>
            </div>
        </div>

        <div class="row mt-4">
            <div class="col-md-4">
                <div class="mb-4">
                    <label for="prep_time" class="form-label">Vreme pripreme (u minutima)</label>
                    <input type="number" name="prep_time" class="form-control" required placeholder="Unesite vreme pripreme">
                </div>
            </div>
            <div class="col-md-4">
                <div class="mb-4">
                    <label for="difficulty" class="form-label">Težina</label>
                    <select name="difficulty" class="form-select" required>
                        <option value="easy">Lako</option>
                        <option value="medium">Srednje</option>
                        <option value="hard">Teško</option>
                    </select>
                </div>
            </div>
            <div class="col-md-4">
                </div>
        </div>

        <div class="d-flex justify-content-end mt-4">
            <a href="{{ route('recipes.index') }}" class="btn btn-secondary me-2">Nazad na listu</a>
            <button type="submit" class="btn btn-success">Dodaj recept</button>
        </div>
    </form>

</div>
@endsection