package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CocktailRepository {

    private final CocktailDao cocktailDao;
    private final LiveData<List<Cocktail>> allCocktails;

    public CocktailRepository(Application application) {
        CocktailRoomDatabase db = CocktailRoomDatabase.getInstance(application);
        cocktailDao = db.cocktailDao();
        allCocktails = cocktailDao.getAllCocktail(FirebaseAuth.getInstance().getUid());
    }

    public LiveData<List<Cocktail>> getAllCocktails() {
        return allCocktails;
    }

    public void getCount(Cocktail cocktail, OnCountCompleteListener listener) {
        new CountAsyncTask(cocktailDao, listener).execute(cocktail);
    }

    public void insert(Cocktail cocktail) {
        new InsertAsyncTask(cocktailDao).execute(cocktail);
    }

    public void delete(Cocktail cocktail) {
        new DeleteAsyncTask(cocktailDao).execute(cocktail);
    }

    private static class CountAsyncTask extends AsyncTask<Cocktail, Void, Integer> {

        private final CocktailDao cocktailDao;
        private final OnCountCompleteListener listener;

        public CountAsyncTask(CocktailDao cocktailDao, OnCountCompleteListener listener) {
            this.cocktailDao = cocktailDao;
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Cocktail... cocktails) {
            return cocktailDao.getCount(cocktails[0].getId(), FirebaseAuth.getInstance().getUid());
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            listener.onComplete(integer);
        }
    }

    private static class InsertAsyncTask extends AsyncTask<Cocktail, Void, Void> {

        private final CocktailDao cocktailDao;

        public InsertAsyncTask(CocktailDao cocktailDao) {
            this.cocktailDao = cocktailDao;
        }

        @Override
        protected Void doInBackground(Cocktail... cocktails) {
            cocktailDao.insert(cocktails[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Cocktail, Void, Void> {

        private final CocktailDao cocktailDao;

        public DeleteAsyncTask(CocktailDao cocktailDao) {
            this.cocktailDao = cocktailDao;
        }

        @Override
        protected Void doInBackground(Cocktail... cocktails) {
            cocktailDao.delete(cocktails[0]);
            return null;
        }
    }

    public interface OnCountCompleteListener {
        void onComplete(int count);
    }
}
