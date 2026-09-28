package com.example.smartpantrymanager;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeSeeder {

    public static void seedIfEmpty(android.content.Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("recipes").limit(1).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.isEmpty()) {
                Log.d("RecipeSeeder", "Recipes already exist, skipping seed.");
                return;
            }

            List<Recipe> recipes = buildRecipeList();
            for (Recipe recipe : recipes) {
                Map<String, Object> doc = new HashMap<>();
                doc.put("name", recipe.getName());
                doc.put("steps", recipe.getSteps());

                List<Map<String, Object>> ingredientMaps = new ArrayList<>();
                for (RecipeIngredient ri : recipe.getIngredients()) {
                    Map<String, Object> im = new HashMap<>();
                    im.put("name", ri.getName());
                    im.put("quantity", ri.getQuantity());
                    im.put("unit", ri.getUnit());
                    ingredientMaps.add(im);
                }
                doc.put("ingredients", ingredientMaps);

                db.collection("recipes").add(doc);
            }

            Toast.makeText(context, "Seeded " + recipes.size() + " recipes", Toast.LENGTH_SHORT).show();
            Log.d("RecipeSeeder", "Seeded " + recipes.size() + " recipes");
        }).addOnFailureListener(e ->
                Log.e("RecipeSeeder", "Failed to check/seed recipes", e));
    }

    private static List<Recipe> buildRecipeList() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(new Recipe(null, "Scrambled Eggs", list(
                new RecipeIngredient("egg", 2, "UNIT"),
                new RecipeIngredient("milk", 30, "ml"),
                new RecipeIngredient("butter", 10, "g")
        ), "1. Whisk eggs and milk together.\n2. Melt butter in a pan over low heat.\n3. Pour in egg mixture and stir gently until set.\n4. Season and serve."));

        recipes.add(new Recipe(null, "Tomato Pasta", list(
                new RecipeIngredient("pasta", 200, "g"),
                new RecipeIngredient("tomato", 300, "g"),
                new RecipeIngredient("garlic", 10, "g"),
                new RecipeIngredient("olive oil", 20, "ml")
        ), "1. Boil pasta until al dente.\n2. Sauté garlic in olive oil.\n3. Add chopped tomato and simmer 10 min.\n4. Toss pasta through sauce."));

        recipes.add(new Recipe(null, "Vegetable Stir Fry", list(
                new RecipeIngredient("carrot", 100, "g"),
                new RecipeIngredient("broccoli", 150, "g"),
                new RecipeIngredient("soy sauce", 30, "ml"),
                new RecipeIngredient("garlic", 5, "g")
        ), "1. Chop all vegetables.\n2. Stir fry garlic briefly.\n3. Add vegetables, cook 5-7 min.\n4. Add soy sauce and toss."));

        recipes.add(new Recipe(null, "Cheese Omelette", list(
                new RecipeIngredient("egg", 3, "UNIT"),
                new RecipeIngredient("cheese", 50, "g"),
                new RecipeIngredient("butter", 10, "g")
        ), "1. Whisk eggs.\n2. Melt butter in pan.\n3. Pour eggs, cook until nearly set.\n4. Add cheese, fold, and serve."));

        recipes.add(new Recipe(null, "Chicken Rice Bowl", list(
                new RecipeIngredient("chicken breast", 200, "g"),
                new RecipeIngredient("rice", 150, "g"),
                new RecipeIngredient("soy sauce", 20, "ml")
        ), "1. Cook rice according to package.\n2. Pan-fry chicken until cooked through.\n3. Slice chicken, serve over rice with soy sauce."));

        recipes.add(new Recipe(null, "Pancakes", list(
                new RecipeIngredient("flour", 200, "g"),
                new RecipeIngredient("egg", 2, "UNIT"),
                new RecipeIngredient("milk", 250, "ml"),
                new RecipeIngredient("butter", 20, "g")
        ), "1. Mix flour, egg, and milk into a batter.\n2. Melt butter in a pan.\n3. Pour batter and cook until bubbles form.\n4. Flip and cook other side."));

        recipes.add(new Recipe(null, "Grilled Cheese Sandwich", list(
                new RecipeIngredient("bread", 2, "UNIT"),
                new RecipeIngredient("cheese", 60, "g"),
                new RecipeIngredient("butter", 10, "g")
        ), "1. Butter the outer sides of the bread.\n2. Place cheese between slices.\n3. Grill in a pan until golden and cheese melts."));

        recipes.add(new Recipe(null, "Vegetable Soup", list(
                new RecipeIngredient("carrot", 100, "g"),
                new RecipeIngredient("onion", 100, "g"),
                new RecipeIngredient("potato", 200, "g"),
                new RecipeIngredient("vegetable stock", 500, "ml")
        ), "1. Chop all vegetables.\n2. Sauté onion until soft.\n3. Add remaining vegetables and stock.\n4. Simmer 20 min until tender."));

        recipes.add(new Recipe(null, "Garlic Butter Rice", list(
                new RecipeIngredient("rice", 200, "g"),
                new RecipeIngredient("garlic", 15, "g"),
                new RecipeIngredient("butter", 30, "g")
        ), "1. Cook rice according to package.\n2. Melt butter, sauté garlic until fragrant.\n3. Mix through cooked rice."));

        recipes.add(new Recipe(null, "Banana Smoothie", list(
                new RecipeIngredient("banana", 2, "UNIT"),
                new RecipeIngredient("milk", 250, "ml"),
                new RecipeIngredient("honey", 20, "ml")
        ), "1. Peel and slice bananas.\n2. Blend with milk and honey until smooth."));

        recipes.add(new Recipe(null, "Egg Fried Rice", list(
                new RecipeIngredient("rice", 250, "g"),
                new RecipeIngredient("egg", 2, "UNIT"),
                new RecipeIngredient("soy sauce", 20, "ml"),
                new RecipeIngredient("carrot", 50, "g")
        ), "1. Scramble eggs in a hot pan, set aside.\n2. Stir fry carrot briefly.\n3. Add rice and soy sauce, mix well.\n4. Fold in scrambled egg."));

        recipes.add(new Recipe(null, "Tomato Soup", list(
                new RecipeIngredient("tomato", 400, "g"),
                new RecipeIngredient("onion", 80, "g"),
                new RecipeIngredient("vegetable stock", 300, "ml")
        ), "1. Sauté onion until soft.\n2. Add chopped tomato and stock.\n3. Simmer 15 min.\n4. Blend until smooth."));

        recipes.add(new Recipe(null, "Potato Salad", list(
                new RecipeIngredient("potato", 400, "g"),
                new RecipeIngredient("egg", 2, "UNIT"),
                new RecipeIngredient("mayonnaise", 60, "ml")
        ), "1. Boil potatoes until tender, cool and cube.\n2. Boil eggs, chop.\n3. Mix potato, egg, and mayonnaise together."));

        recipes.add(new Recipe(null, "Cheesy Baked Pasta", list(
                new RecipeIngredient("pasta", 200, "g"),
                new RecipeIngredient("cheese", 100, "g"),
                new RecipeIngredient("tomato", 200, "g")
        ), "1. Boil pasta until al dente.\n2. Mix with chopped tomato in a baking dish.\n3. Top with cheese and bake until golden."));

        recipes.add(new Recipe(null, "Fruit Yogurt Bowl", list(
                new RecipeIngredient("yogurt", 200, "g"),
                new RecipeIngredient("banana", 1, "UNIT"),
                new RecipeIngredient("honey", 15, "ml")
        ), "1. Slice banana.\n2. Spoon yogurt into a bowl.\n3. Top with banana and honey."));

        recipes.add(new Recipe(null, "Chicken Soup", list(
                new RecipeIngredient("chicken breast", 200, "g"),
                new RecipeIngredient("carrot", 100, "g"),
                new RecipeIngredient("onion", 80, "g"),
                new RecipeIngredient("vegetable stock", 500, "ml")
        ), "1. Sauté onion and carrot.\n2. Add diced chicken and stock.\n3. Simmer 20 min until chicken is cooked through."));

        return recipes;
    }

    private static List<RecipeIngredient> list(RecipeIngredient... items) {
        List<RecipeIngredient> l = new ArrayList<>();
        for (RecipeIngredient i : items) l.add(i);
        return l;
    }
}