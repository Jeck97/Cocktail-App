package utem.ftmk.bitp3453.finalproject.cocktailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CocktailMainActivity extends AppCompatActivity
        implements CocktailUtils.OnCocktailClickListener {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(
                SettingsActivity.PREFERENCE_NAME, MODE_PRIVATE);
        int theme = sharedPreferences.getInt(
                SettingsActivity.KEY_THEME, SettingsActivity.MODE_DEFAULT);
        AppCompatDelegate.setDefaultNightMode(theme);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_cocktail_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        String email = user.getEmail();
        if (email != null && getSupportActionBar() != null) {
            int index = email.indexOf('@');
            getSupportActionBar().setTitle(getString(
                    R.string.hello_user, email.substring(0, index)));
        }

        ProgressBar pbLoad = findViewById(R.id.pb_load);
        TextView tvNoResult = findViewById(R.id.tv_no_result);
        ImageView ivPlaceholder = findViewById(R.id.iv_placeholder);

        CocktailListAdapter adapter = new CocktailListAdapter(false, this);

        NetworkUtils.init();
        new CocktailAsyncTask(adapter, ivPlaceholder, tvNoResult).execute();

        GridLayoutManager lm = new GridLayoutManager(this, 2);
        RecyclerView rv = findViewById(R.id.rv_cocktail);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int cocktailCount = adapter.getItemCount();
                if (lm.findLastCompletelyVisibleItemPosition() == cocktailCount - 1
                        && cocktailCount != 0 && adapter.isEndless()) {
                    new CocktailAsyncTask(adapter, pbLoad, tvNoResult).execute();
                }
            }
        });

        SearchView sv = findViewById(R.id.sv_main);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sv.clearFocus();
                adapter.setEndless(false);
                adapter.clearCocktails();
                new CocktailAsyncTask(adapter, ivPlaceholder, tvNoResult).execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        sv.setOnCloseListener(() -> {
            if (!adapter.isEndless()) {
                NetworkUtils.init();
                adapter.setEndless(true);
                adapter.clearCocktails();
                new CocktailAsyncTask(adapter, ivPlaceholder, tvNoResult).execute();
            }
            return false;
        });
    }

    @Override
    public void onClick(Cocktail cocktail) {
        cocktail.setKey(cocktail.getId() + auth.getUid());
        cocktail.setUid(auth.getUid());
        Intent intent = new Intent(this, CocktailDetailActivity.class);
        intent.putExtra(CocktailUtils.EXTRA_COCKTAIL, cocktail);
        startActivity(intent);
    }

    public void onLuckyClick(View view) {
        new CocktailDialogFragment().show(getSupportFragmentManager(), CocktailDialogFragment.TAG);
    }

    public void onFavouriteClick() {
        startActivity(new Intent(this, CocktailFavouriteActivity.class));
    }

    private void onSettingsClick() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void onLogoutClick() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.message_logout)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    auth.signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_favourite:
                onFavouriteClick();
                return true;
            case R.id.menu_item_setting:
                onSettingsClick();
                return true;
            case R.id.menu_item_logout:
                onLogoutClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}