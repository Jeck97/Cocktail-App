package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CocktailViewModel extends AndroidViewModel {

    private final CocktailRepository repository;
    private final LiveData<List<Cocktail>> allCocktails;

    public CocktailViewModel(@NonNull Application application) {
        super(application);
        repository = new CocktailRepository(application);
        allCocktails = repository.getAllCocktails();
    }

    public LiveData<List<Cocktail>> getAllCocktails() {
        return allCocktails;
    }

    public void getCount(Cocktail cocktail, CocktailRepository.OnCountCompleteListener listener) {
        repository.getCount(cocktail, listener);
    }

    public void insert(Cocktail cocktail) {
        repository.insert(cocktail);
    }

    public void delete(Cocktail cocktail) {
        repository.delete(cocktail);
    }

}
