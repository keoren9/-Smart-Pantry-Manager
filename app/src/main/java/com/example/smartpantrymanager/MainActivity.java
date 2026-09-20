package com.example.smartpantrymanager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    private Spinner spinner;
    private EditText nameField;
    private EditText qtyField;
    private ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseFirestore.getInstance();

        nameField = findViewById(R.id.editTextName);
        qtyField = findViewById(R.id.Number);
        spinner = findViewById(R.id.spinner3);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, "Selected Item: " + item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // optional
            }
        });

        arrayList.add("g");
        arrayList.add("ml");
        arrayList.add("UNIT");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        findViewById(R.id.Submitbutton).setOnClickListener(this::handleText);
    }

    public void handleText(View v) {
        String name = nameField.getText().toString().trim();
        String qtyStr = qtyField.getText().toString().trim();
        String unit = spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : "";

        if (name.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("MainActivity", name + " " + quantity + " " + unit);

        Map<String, Object> ingredient = new HashMap<>();
        ingredient.put("Name", name);
        ingredient.put("quantity", quantity);
        ingredient.put("unit", unit);

        firestore.collection("ingredients")
                .add(ingredient)
                .addOnSuccessListener(documentReference -> {
                    Log.d("MainActivity", "Saved with ID: " + documentReference.getId());
                    Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
                    nameField.setText("");
                    qtyField.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error saving ingredient", e);
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
                });
    }
}