package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Cocktail.class}, version = 1, exportSchema = false)
@TypeConverters(Cocktail.Converter.class)
public abstract class CocktailRoomDatabase extends RoomDatabase {

    public abstract CocktailDao cocktailDao();

    private static CocktailRoomDatabase INSTANCE;

    public static CocktailRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CocktailRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        CocktailRoomDatabase.class, "cocktail_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }

}
