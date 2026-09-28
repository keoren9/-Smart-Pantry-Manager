package com.example.smartpantrymanager;

import java.util.List;

public class Recipe {
    private String id;
    private String name;
    private List<RecipeIngredient> ingredients;
    private String steps;

    public Recipe() {
    }

    public Recipe(String id, String name, List<RecipeIngredient> ingredients, String steps) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public String getSteps() { return steps; }
}