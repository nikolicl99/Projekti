package com.example.recepti;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {
    private Context context;
    private List<Recipe> recipes;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, R.layout.item_recipe, recipes);
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        }

        Recipe recipe = recipes.get(position);

        // Referenca na view-ove
        ImageView ivRecipeImage = convertView.findViewById(R.id.ivRecipeImage);
        TextView tvRecipeName = convertView.findViewById(R.id.tvRecipeName);
        TextView tvRecipeType = convertView.findViewById(R.id.tvRecipeType);
        TextView tvBakingInfo = convertView.findViewById(R.id.tvBakingInfo);
        TextView tvIngredientsPreview = convertView.findViewById(R.id.tvIngredientsPreview);

        // Postavi podatke
        tvRecipeName.setText(recipe.getName());
        tvRecipeName.setTextColor(ContextCompat.getColor(context, R.color.list_item_text));

        // Tip recepta sa zaobljenim ivicama
        if (recipe.isSweet()) {
            tvRecipeType.setText("Slatko");
            // Koristite drawable za zaobljene ivice
            tvRecipeType.setBackgroundResource(R.drawable.rounded_slatko);
        } else {
            tvRecipeType.setText("Slano");
            // Koristite drawable za zaobljene ivice
            tvRecipeType.setBackgroundResource(R.drawable.rounded_slano);
        }
        tvRecipeType.setTextColor(ContextCompat.getColor(context, R.color.list_item_text));

        // Informacije o pečenju
        if (recipe.isBaked()) {
            String bakingInfo = "Peče se: " + recipe.getTemperature() + " - " + recipe.getTime();
            tvBakingInfo.setText(bakingInfo);
        } else {
            tvBakingInfo.setText("Ne peče se");
        }
        tvBakingInfo.setTextColor(ContextCompat.getColor(context, R.color.list_item_text));

        // Kratak prikaz sastojaka
        String ingredients = recipe.getIngredients();
        if (ingredients.length() > 50) {
            ingredients = ingredients.substring(0, 50) + "...";
        }
        tvIngredientsPreview.setText("Sastojci: " + ingredients);
        tvIngredientsPreview.setTextColor(ContextCompat.getColor(context, R.color.list_item_text));

        // Učitaj sliku ako postoji
        if (recipe.getImagePath() != null && !recipe.getImagePath().isEmpty()) {
            // Proveri da li je ovo URL (sa Firebase) ili lokalna putanja
            if (recipe.getImagePath().startsWith("http")) {
                // Ovo je Firebase URL - koristi Picasso
                Picasso.get()
                        .load(recipe.getImagePath())
                        .placeholder(R.drawable.addimage)
                        .error(R.drawable.addimage)
                        .fit()
                        .centerCrop()
                        .into(ivRecipeImage);
            } else {
                // Ovo je lokalna putanja
                File imageFile = new File(recipe.getImagePath());
                if (imageFile.exists()) {
                    ivRecipeImage.setImageURI(Uri.fromFile(imageFile));
                    ivRecipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    ivRecipeImage.setImageResource(R.drawable.addimage);
                }
            }
        } else {
            ivRecipeImage.setImageResource(R.drawable.addimage);
        }

        return convertView;
    }
}