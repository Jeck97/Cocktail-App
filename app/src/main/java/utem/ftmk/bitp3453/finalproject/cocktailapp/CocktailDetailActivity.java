package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CocktailDetailActivity extends AppCompatActivity {

    private CocktailViewModel viewModel;
    private Cocktail cocktail;

    private FloatingActionButton fabFavourite;
    private boolean favoured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_detail);

        viewModel = ViewModelProviders.of(this).get(CocktailViewModel.class);

        cocktail = (Cocktail) getIntent()
                .getSerializableExtra(CocktailUtils.EXTRA_COCKTAIL);
        assert cocktail != null;

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(cocktail.getName());

        fabFavourite = findViewById(R.id.fab_favourite);

        viewModel.getCount(cocktail, count -> {
            if (count != 0) {
                fabFavourite.setColorFilter(getColor(R.color.favoured));
                favoured = true;
            }
        });

        ImageView ivImage = findViewById(R.id.iv_cocktail_image_detail);
        TextView tvCategory = findViewById(R.id.tv_cocktail_category_detail);
        TextView tvAlcoholic = findViewById(R.id.tv_cocktail_alcoholic_detail);
        TextView tvGlass = findViewById(R.id.tv_cocktail_glass_detail);
        TextView tvInstruction = findViewById(R.id.tv_cocktail_instruction_detail);

        CocktailUtils.setCocktailImage(this, cocktail.getImage(), ivImage);
        tvCategory.setText(cocktail.getCategory());
        tvAlcoholic.setText(cocktail.getAlcoholic());
        tvAlcoholic.setTextColor(getColor(CocktailUtils.getAlcoholColor(cocktail.getAlcoholic())));
        tvGlass.setText(cocktail.getGlass().equals("") ? "-" : cocktail.getGlass());
        tvInstruction.setText(cocktail.getInstruction());

        LayoutInflater inflater = LayoutInflater.from(this);
        TableLayout layoutIngredients = findViewById(R.id.layout_cocktail_ingredients);

        int ingredientCount = cocktail.getIngredients().size();
        for (int index = 0; index < ingredientCount; index++) {

            TableRow tableRow = (TableRow) inflater.inflate(R.layout.table_row_ingredients,
                    layoutIngredients, false);

            TextView tvIngredient = tableRow.findViewById(R.id.tv_cocktail_ingredient);
            tvIngredient.setText(cocktail.getIngredients().get(index));

            if (cocktail.getMeasures().get(index).equals(Cocktail.NO_MEASURE)) {
                TextView tvColon = tableRow.findViewById(R.id.tv_colon);
                tvColon.setVisibility(View.INVISIBLE);
            } else {
                TextView tvMeasure = tableRow.findViewById(R.id.tv_cocktail_measure);
                tvMeasure.setText(cocktail.getMeasures().get(index));
            }
            layoutIngredients.addView(tableRow);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFavouriteClick(View view) {
        fabFavourite.setClickable(false);
        if (favoured) {
            viewModel.delete(cocktail);
            fabFavourite.setColorFilter(getColor(R.color.white));
            favoured = false;
        } else {
            viewModel.insert(cocktail);
            fabFavourite.setColorFilter(getColor(R.color.favoured));
            favoured = true;
        }
        fabFavourite.setClickable(true);
    }
}