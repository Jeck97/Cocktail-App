package utem.ftmk.bitp3453.finalproject.cocktailapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CocktailDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Cocktail cocktail);

    @Delete
    void delete(Cocktail cocktail);

    @Query("SELECT * FROM cocktail_table WHERE uid = :uid")
    LiveData<List<Cocktail>> getAllCocktail(String uid);

    @Query("SELECT COUNT(*) FROM cocktail_table WHERE id = :id AND uid = :uid")
    int getCount(String id, String uid);

}
