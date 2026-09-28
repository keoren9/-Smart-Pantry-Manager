package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Recipe_page extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private SuggestedRecipeAdapter adapter;
    private final List<Recipe> suggestedRecipes = new ArrayList<>();

    private final List<Ingredient> pantry = new ArrayList<>();
    private final List<Recipe> allRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recipeRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SuggestedRecipeAdapter(suggestedRecipes, recipe -> {
            // Opens the detail screen (we'll build this next)
            android.content.Intent intent = new android.content.Intent(Recipe_page.this, RecipeDetailActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadPantryThenRecipes();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadPantryThenRecipes() {
        firestore.collection("ingredients").get().addOnSuccessListener(pantrySnapshot -> {
            pantry.clear();
            for (QueryDocumentSnapshot doc : pantrySnapshot) {
                String name = doc.getString("Name");
                String unit = doc.getString("unit");
                Double quantity = doc.getDouble("quantity");
                pantry.add(new Ingredient(doc.getId(), name, quantity != null ? quantity : 0, unit));
            }
            loadRecipesAndMatch();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load pantry", Toast.LENGTH_SHORT).show());
    }

    @SuppressWarnings("unchecked")
    private void loadRecipesAndMatch() {
        firestore.collection("recipes").get().addOnSuccessListener(recipeSnapshot -> {
            allRecipes.clear();
            suggestedRecipes.clear();

            for (QueryDocumentSnapshot doc : recipeSnapshot) {
                String name = doc.getString("name");
                String steps = doc.getString("steps");
                List<RecipeIngredient> ingredients = new ArrayList<>();

                List<Map<String, Object>> rawIngredients =
                        (List<Map<String, Object>>) doc.get("ingredients");

                if (rawIngredients != null) {
                    for (Map<String, Object> ri : rawIngredients) {
                        String ingName = (String) ri.get("name");
                        String ingUnit = (String) ri.get("unit");
                        Object qtyObj = ri.get("quantity");
                        double ingQty = qtyObj instanceof Number ? ((Number) qtyObj).doubleValue() : 0;
                        ingredients.add(new RecipeIngredient(ingName, ingQty, ingUnit));
                    }
                }

                Recipe recipe = new Recipe(doc.getId(), name, ingredients, steps);
                allRecipes.add(recipe);

                if (IngredientMatcher.canMake(recipe, pantry)) {
                    suggestedRecipes.add(recipe);
                }
            }

            adapter.notifyDataSetChanged();
            updateEmptyState();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load recipes", Toast.LENGTH_SHORT).show());
    }

    private void updateEmptyState() {
        if (suggestedRecipes.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}