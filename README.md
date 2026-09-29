Smart Pantry Manager

A Android app (Java) that helps users reduce food waste by tracking the
ingredients they have at home and suggesting recipes they can cook using
*strictly* what's already in their pantry — no shopping trip required.

Concept

The core value of the app is the **strict-matching rule**: a recipe is only
suggested if the user's pantry currently contains *every* ingredient the
recipe requires, in at least the required quantity. Partial matches are
excluded from the main suggestions list.

Tech Stack

- Language: Java
- Platform: Android (native, ConstraintLayout XML)
- Database: Firebase Firestore (cloud, NoSQL)
- Lists: RecyclerView + custom adapters


Home  `Page1`  Welcome screen with navigation to Add Item, Edit Items, and Recipes 
Add Item  `MainActivity`  Form to add a new pantry ingredient (name, quantity, unit) 
Edit Items  `Edit_page`  Lists all pantry ingredients (RecyclerView); supports edit and delete 
Suggested Recipes  `Recipe_page`  Runs the strict-matching algorithm against the current pantry and lists only recipes the user can make right now 
Recipe Detail  `RecipeDetailActivity`  Shows a selected recipe's full ingredient list and preparation steps 

Each screen (other than Home) has a back arrow in the top-left (via
`onSupportNavigateUp()`) that returns to the Home screen.

Data Model

`ingredients` collection (Firestore) — the user's pantry

{
  Name: string,
  quantity: number,
  unit: string
}


`recipes` collection (Firestore) — seeded on first run with 17 recipes

{
  name: string,
  steps: string,
  ingredients: [
    { name: string, quantity: number, unit: string },
    ...
  ]
}


Recipes are seeded automatically the first time the app runs on a fresh
database (`RecipeSeeder.seedIfEmpty()`, called from `Page1.onCreate()`). The
seeder checks whether the `recipes` collection is empty before writing, so it
never creates duplicates on subsequent launches.

The Strict-Matching Algorithm

Implemented in `IngredientMatcher.java`.

- `canMake(Recipe, pantry)` — loops through every ingredient a recipe
  requires. If even one required ingredient is missing or insufficient in
  the pantry, the recipe is excluded — no partial matches.
- `normalizeName()` — lowercases and strips trailing "s"/"es"/"ies" so names
  like "tomato" and "tomatoes" are treated as equivalent, without needing a
  full NLP solution.
- `normalizeUnit()` — maps common unit variants (e.g. "gram"/"grams"/"g") to
  a single canonical form before comparing.
- `countMissingIngredients()` — counts how many required ingredients are
  missing; not currently used in the main flow, but available for an
  "Almost There" (missing-one-ingredient) list as a future enhancement.

If no recipes match the current pantry, the Suggested Recipes screen shows:
*"No recipes match your pantry yet - add more ingredients"* instead of a
blank screen.

Key Classes

- `Ingredient` / `RecipeIngredient` — data models for a pantry item and a
  recipe's required ingredient, respectively.
- `Recipe` — data model for a full recipe (name, ingredient list, steps).
- `IngredientAdapter` / `SuggestedRecipeAdapter` — RecyclerView adapters
  binding Firestore data to the pantry list and suggested-recipes list.
- `IngredientMatcher` — the strict-matching business logic (see above).
- `RecipeSeeder` — one-time seed of the initial recipe catalog into
  Firestore.

Setup

1. Clone the repository and open it in Android Studio.
2. Add your own `google-services.json` to the `app/` directory (not
   committed to the repo) — from Firebase Console → Project Settings → your
   Android app.
3. Ensure Firestore is enabled on the linked Firebase project.
4. For development only: set Firestore security rules to
   `allow read, write: if true;` so the app can read/write without
   authentication. Tighten this before any real deployment.
5. Run the app. On first launch, the home screen will seed the `recipes`
   collection automatically.

Notes / Restrictions

- This app does **not** use Google Maps, any mapping SDK, or device
  location/GPS services, per the assignment brief — its scope is limited to
  the user's own pantry and recipe matching.
- Firestore security rules are currently open for development/testing and
  are **not** production-ready.

Possible Future Enhancements

- "Almost There" list for recipes missing exactly one ingredient (uses
  `IngredientMatcher.countMissingIngredients()`, already implemented but
  not yet wired into a screen).
- Settings/profile screen for unit preferences or expiring-soon alerts.
- Expiry date tracking per pantry ingredient.
- Deduct pantry quantities automatically when a recipe is marked as cooked.
