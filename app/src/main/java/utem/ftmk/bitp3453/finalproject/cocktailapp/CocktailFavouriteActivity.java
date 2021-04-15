package utem.ftmk.bitp3453.finalproject.cocktailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class CocktailFavouriteActivity extends AppCompatActivity
        implements CocktailUtils.OnCocktailClickListener {

    private CocktailViewModel viewModel;
    private CocktailListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_favourite);

        viewModel = ViewModelProviders.of(this)
                .get(CocktailViewModel.class);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        TextView tvNoResult = findViewById(R.id.tv_no_result);
        tvNoResult.setText(R.string.no_favourite);
        ImageView ivPlaceholder = findViewById(R.id.iv_placeholder);
        Glide.with(this).load(R.drawable.cocktail_shaker).into(ivPlaceholder);

        adapter = new CocktailListAdapter(true, this);

        RecyclerView rv = findViewById(R.id.rv_cocktail);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Cocktail cocktail = adapter.getCocktailAtPosition(position);
                showConfirmDialog(cocktail, position);
            }
        });
        helper.attachToRecyclerView(rv);

        viewModel.getAllCocktails().observe(this, cocktails -> {
            adapter.resetCocktails(cocktails);
            if (cocktails.size() != 0) {
                tvNoResult.setVisibility(View.GONE);
            } else {
                tvNoResult.setVisibility(View.VISIBLE);
            }
        });

    }

    private void showConfirmDialog(Cocktail cocktail, int position) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_cocktail, cocktail.getName()))
                .setPositiveButton(R.string.yes, (d, w) -> viewModel.delete(cocktail))
                .setNegativeButton(R.string.no, (d, w) -> adapter.notifyItemChanged(position))
                .show();
    }

    @Override
    public void onClick(Cocktail cocktail) {
        Intent intent = new Intent(this, CocktailDetailActivity.class);
        intent.putExtra(CocktailUtils.EXTRA_COCKTAIL, cocktail);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}