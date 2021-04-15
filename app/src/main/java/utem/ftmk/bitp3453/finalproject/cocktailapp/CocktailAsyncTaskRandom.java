package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CocktailAsyncTaskRandom extends AsyncTask<Void, Void, String> {

    private final OnTaskCompleteListener listener;

    public CocktailAsyncTaskRandom(OnTaskCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return NetworkUtils.randomCocktailInfo();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("drinks");
            JSONObject cocktailJSON = itemsArray.getJSONObject(0);

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
            Cocktail cocktail = new Cocktail(
                    cocktailJSON.getString("idDrink"),
                    cocktailJSON.getString("strDrink"),
                    cocktailJSON.getString("strCategory"),
                    cocktailJSON.getString("strAlcoholic"),
                    cocktailJSON.getString("strGlass"),
                    cocktailJSON.getString("strInstructions"),
                    ingredients,
                    measures,
                    cocktailJSON.getString("strDrinkThumb")
            );
            listener.onSuccess(cocktail);
        } catch (JSONException e) {
            listener.onFailure(e);
        }
    }

    public interface OnTaskCompleteListener {
        void onSuccess(Cocktail cocktail);

        void onFailure(Exception e);
    }
}
