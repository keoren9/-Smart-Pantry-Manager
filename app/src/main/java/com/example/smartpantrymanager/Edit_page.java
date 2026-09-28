package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Edit_page extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private IngredientAdapter adapter;
    private final List<Ingredient> ingredientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new IngredientAdapter(ingredientList, new IngredientAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Ingredient ingredient) {
                showEditDialog(ingredient);
            }

            @Override
            public void onDelete(Ingredient ingredient) {
                deleteIngredient(ingredient);
            }
        });
        recyclerView.setAdapter(adapter);

        loadIngredients();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadIngredients() {
        firestore.collection("ingredients")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ingredientList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String id = doc.getId();
                        String name = doc.getString("Name");
                        String unit = doc.getString("unit");
                        Double quantity = doc.getDouble("quantity");
                        ingredientList.add(new Ingredient(id, name,
                                quantity != null ? quantity : 0, unit));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load ingredients", Toast.LENGTH_SHORT).show());
    }

    private void showEditDialog(Ingredient ingredient) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_item, null);
        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editQuantity = dialogView.findViewById(R.id.editQuantity);
        Spinner editUnitSpinner = dialogView.findViewById(R.id.editUnitSpinner);

        editName.setText(ingredient.getName());
        editQuantity.setText(String.valueOf(ingredient.getQuantity()));

        List<String> units = new ArrayList<>();
        units.add("g");
        units.add("ml");
        units.add("UNIT");
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editUnitSpinner.setAdapter(unitAdapter);
        int unitPosition = units.indexOf(ingredient.getUnit());
        if (unitPosition >= 0) {
            editUnitSpinner.setSelection(unitPosition);
        }

        new AlertDialog.Builder(this)
                .setTitle("Edit Ingredient")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = editName.getText().toString().trim();
                    String newQtyStr = editQuantity.getText().toString().trim();
                    String newUnit = editUnitSpinner.getSelectedItem().toString();

                    if (newName.isEmpty() || newQtyStr.isEmpty()) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double newQuantity;
                    try {
                        newQuantity = Double.parseDouble(newQtyStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    updateIngredient(ingredient.getId(), newName, newQuantity, newUnit);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateIngredient(String id, String name, double quantity, String unit) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("Name", name);
        updates.put("quantity", quantity);
        updates.put("unit", unit);

        firestore.collection("ingredients").document(id)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
                    loadIngredients();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    private void deleteIngredient(Ingredient ingredient) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Remove \"" + ingredient.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    firestore.collection("ingredients").document(ingredient.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                                loadIngredients();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}