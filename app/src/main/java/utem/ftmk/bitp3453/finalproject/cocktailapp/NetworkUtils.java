package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String COCKTAIL_BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/";
    private static final String SEARCH = "search.php";
    private static final String RANDOM = "random.php";
    private static final String NAME = "s";
    private static final String FIRST_LETTER = "f";
    private static final String[] LETTERS = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
            "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
            "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };
    private static final int LETTERS_LENGTH = LETTERS.length;
    private static int COUNT = 0;

    public static void init() {
        COUNT = 0;
    }

    public static String searchCocktailInfo() {
        if (COUNT == LETTERS_LENGTH) {
            return null;
        }

        Uri builtURI = Uri.parse(COCKTAIL_BASE_URL).buildUpon()
                .appendPath(SEARCH)
                .appendQueryParameter(FIRST_LETTER, LETTERS[COUNT++])
                .build();
        return getCocktailInfo(builtURI.toString());
    }

    public static String searchCocktailInfo(String query) {
        Uri builtURI = Uri.parse(COCKTAIL_BASE_URL).buildUpon()
                .appendPath(SEARCH)
                .appendQueryParameter(NAME, query)
                .build();
        return getCocktailInfo(builtURI.toString());
    }

    public static String randomCocktailInfo() {
        Uri builtURI = Uri.parse(COCKTAIL_BASE_URL).buildUpon()
                .appendPath(RANDOM)
                .build();
        return getCocktailInfo(builtURI.toString());
    }

    private static String getCocktailInfo(String uri) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String cocktailJSONString = null;

        try {
            URL requestURL = new URL(uri);

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            if (builder.length() == 0) {
                return null;
            }

            cocktailJSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return cocktailJSONString;
    }

}
