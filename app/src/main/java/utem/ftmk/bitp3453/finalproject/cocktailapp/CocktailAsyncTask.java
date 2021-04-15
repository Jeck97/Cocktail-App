package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static utem.ftmk.bitp3453.finalproject.cocktailapp.NetworkUtils.searchCocktailInfo;

public class CocktailAsyncTask extends AsyncTask<String, Void, String> {

    private final CocktailListAdapter adapter;
    private final WeakReference<View> placeholder;
    private final WeakReference<View> noResult;

    public CocktailAsyncTask(CocktailListAdapter adapter, View placeholder, View noResult) {
        this.adapter = adapter;
        this.placeholder = new WeakReference<>(placeholder);
        this.noResult = new WeakReference<>(noResult);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        noResult.get().setVisibility(View.GONE);
        placeholder.get().setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        return strings.length == 0 ? searchCocktailInfo() : searchCocktailInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s == null) {
            placeholder.get().setVisibility(View.GONE);
            return;
        }

        List<Cocktail> cocktails = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("drinks");

            for (int i = 0; i < itemsArray.length(); i++) {

                JSONObject cocktailJSON = itemsArray.getJSONObject(i);

                List<String> ingredients = new ArrayList<>();
                List<String> measures = new ArrayList<>();
                for (int j = 1; j <= 15; j++) {
                    if (cocktailJSON.getString("strIngredient" + j)
                            .equals(Cocktail.NO_INGREDIENT)) {
                        break;
                    }
                    ingredients.add(cocktailJSON.getString("strIngredient" + j));
                    measures.add(cocktailJSON.getString("strMeasure" + j));
                }
                cocktails.add(new Cocktail(
                        cocktailJSON.getString("idDrink"),
                        cocktailJSON.getString("strDrink"),
                        cocktailJSON.getString("strCategory"),
                        cocktailJSON.getString("strAlcoholic"),
                        cocktailJSON.getString("strGlass"),
                        cocktailJSON.getString("strInstructions"),
                        ingredients,
                        measures,
                        cocktailJSON.getString("strDrinkThumb")
                ));
            }
            adapter.updateCocktails(cocktails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        placeholder.get().setVisibility(View.GONE);
        if (cocktails.size() == 0) {
            noResult.get().setVisibility(View.VISIBLE);
        }
    }
}
