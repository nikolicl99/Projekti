<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void {
        Schema::create('recipes', function (Blueprint $table) {
            $table->id();
            $table->string('title');
            $table->text('description');
            $table->text('ingredients');
            $table->text('steps');
            $table->integer('prep_time');
            $table->string('difficulty', 50);
            $table->timestamps();
        });
    }

    public function down(): void {
        Schema::dropIfExists('recipes');
    }
};
