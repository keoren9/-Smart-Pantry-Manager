package com.example.smartpantrymanager;

import java.util.List;
import java.util.Locale;

public class IngredientMatcher {

    public static String normalizeName(String name) {
        if (name == null) return "";
        String n = name.trim().toLowerCase(Locale.ROOT);
        if (n.endsWith("ies")) {
            n = n.substring(0, n.length() - 3) + "y";
        } else if (n.endsWith("es")) {
            n = n.substring(0, n.length() - 2);
        } else if (n.endsWith("s") && !n.endsWith("ss")) {
            n = n.substring(0, n.length() - 1);
        }
        return n;
    }

    public static String normalizeUnit(String unit) {
        if (unit == null) return "";
        String u = unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "gram":
            case "grams":
            case "g":
                return "g";
            case "millilitre":
            case "millilitres":
            case "ml":
                return "ml";
            case "unit":
            case "units":
            case "pcs":
            case "piece":
            case "pieces":
                return "unit";
            default:
                return u;
        }
    }

    public static boolean canMake(Recipe recipe, List<Ingredient> pantry) {
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!pantryHasEnough(required, pantry)) {
                return false;
            }
        }
        return true;
    }

    public static int countMissingIngredients(Recipe recipe, List<Ingredient> pantry) {
        int missing = 0;
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!pantryHasEnough(required, pantry)) {
                missing++;
            }
        }
        return missing;
    }

    private static boolean pantryHasEnough(RecipeIngredient required, List<Ingredient> pantry) {
        String requiredName = normalizeName(required.getName());
        String requiredUnit = normalizeUnit(required.getUnit());

        for (Ingredient owned : pantry) {
            String ownedName = normalizeName(owned.getName());
            String ownedUnit = normalizeUnit(owned.getUnit());

            if (ownedName.equals(requiredName) && ownedUnit.equals(requiredUnit)) {
                if (owned.getQuantity() >= required.getQuantity()) {
                    return true;
                }
            }
        }
        return false;
    }
}