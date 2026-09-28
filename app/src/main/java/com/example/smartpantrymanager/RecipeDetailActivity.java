package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class RecipeDetailActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firestore = FirebaseFirestore.getInstance();

        String recipeId = getIntent().getStringExtra("recipeId");
        if (recipeId == null) {
            Toast.makeText(this, "No recipe selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipe(recipeId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @SuppressWarnings("unchecked")
    private void loadRecipe(String recipeId) {
        firestore.collection("recipes").document(recipeId).get()
                .addOnSuccessListener(this::displayRecipe)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load recipe", Toast.LENGTH_SHORT).show());
    }

    @SuppressWarnings("unchecked")
    private void displayRecipe(DocumentSnapshot doc) {
        TextView nameView = findViewById(R.id.detailRecipeName);
        TextView ingredientsView = findViewById(R.id.detailIngredientsList);
        TextView stepsView = findViewById(R.id.detailSteps);

        String name = doc.getString("name");
        String steps = doc.getString("steps");
        nameView.setText(name);
        stepsView.setText(steps);

        List<Map<String, Object>> rawIngredients =
                (List<Map<String, Object>>) doc.get("ingredients");

        StringBuilder sb = new StringBuilder();
        if (rawIngredients != null) {
            for (Map<String, Object> ri : rawIngredients) {
                String ingName = (String) ri.get("name");
                String ingUnit = (String) ri.get("unit");
                Object qtyObj = ri.get("quantity");
                double ingQty = qtyObj instanceof Number ? ((Number) qtyObj).doubleValue() : 0;
                sb.append("- ").append(ingQty).append(" ").append(ingUnit)
                        .append(" ").append(ingName).append("\n");
            }
        }
        ingredientsView.setText(sb.toString().trim());
    }
}