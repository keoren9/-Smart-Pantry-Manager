package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    public interface OnItemActionListener {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    private final List<Ingredient> ingredients;
    private final OnItemActionListener listener;

    public IngredientAdapter(List<Ingredient> ingredients, OnItemActionListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient_row, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.rowName.setText(ingredient.getName());
        holder.rowQuantityUnit.setText(ingredient.getQuantity() + " " + ingredient.getUnit());

        holder.editButton.setOnClickListener(v -> listener.onEdit(ingredient));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(ingredient));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView rowName, rowQuantityUnit;
        View editButton, deleteButton;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            rowName = itemView.findViewById(R.id.rowName);
            rowQuantityUnit = itemView.findViewById(R.id.rowQuantityUnit);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}