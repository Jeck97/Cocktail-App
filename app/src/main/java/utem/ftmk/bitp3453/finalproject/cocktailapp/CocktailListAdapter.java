package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static utem.ftmk.bitp3453.finalproject.cocktailapp.CocktailUtils.getAlcoholColor;

public class CocktailListAdapter extends RecyclerView.Adapter<CocktailListAdapter.CocktailHolder> {

    private final List<Cocktail> cocktails;
    private final int layout;
    private final CocktailUtils.OnCocktailClickListener listener;
    private boolean endless;

    public CocktailListAdapter(boolean favoured, CocktailUtils.OnCocktailClickListener listener) {
        this.cocktails = new ArrayList<>();
        this.listener = listener;
        this.endless = true;
        this.layout = favoured ?
                R.layout.card_view_cocktail_favourited :
                R.layout.card_view_cocktail_main;
    }

    @NonNull
    @Override
    public CocktailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CocktailHolder(LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailHolder holder, int position) {
        holder.bind(cocktails.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return cocktails.size();
    }

    public Cocktail getCocktailAtPosition(int position) {
        return cocktails.get(position);
    }

    public void resetCocktails(List<Cocktail> cocktails) {
        this.cocktails.clear();
        this.cocktails.addAll(cocktails);
        this.notifyDataSetChanged();
    }

    public void updateCocktails(List<Cocktail> cocktails) {
        this.cocktails.addAll(cocktails);
        this.notifyDataSetChanged();
    }

    public void clearCocktails() {
        this.cocktails.clear();
        this.notifyDataSetChanged();
    }

    public void setEndless(boolean endless) {
        this.endless = endless;
    }

    public boolean isEndless() {
        return this.endless;
    }

    public static class CocktailHolder extends RecyclerView.ViewHolder {

        private final ImageView ivImage;
        private final TextView tvName;
        private final TextView tvCategory;
        private final TextView tvAlcoholic;
        private final View itemView;

        public CocktailHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivImage = itemView.findViewById(R.id.iv_cocktail_image);
            this.tvName = itemView.findViewById(R.id.tv_cocktail_name);
            this.tvCategory = itemView.findViewById(R.id.tv_cocktail_category);
            this.tvAlcoholic = itemView.findViewById(R.id.tv_cocktail_alcoholic);
        }

        public void bind(Cocktail cocktail, CocktailUtils.OnCocktailClickListener listener) {
            Context context = itemView.getContext();
            CocktailUtils.setCocktailImage(context, cocktail.getImage(), ivImage);
            tvName.setText(cocktail.getName());
            tvCategory.setText(cocktail.getCategory());
            tvAlcoholic.setText(cocktail.getAlcoholic());
            tvAlcoholic.setTextColor(context.getColor(getAlcoholColor(cocktail.getAlcoholic())));

            itemView.setOnClickListener(v -> listener.onClick(cocktail));
        }

    }

}
