package utem.ftmk.bitp3453.finalproject.cocktailapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "cocktail_table")
public class Cocktail implements Serializable {

    public static final String ALCOHOLIC = "Alcoholic";
    public static final String NON_ALCOHOLIC = "Non alcoholic";
    public static final String OPTIONAL_ALCOHOL = "Optional alcohol";
    public static final String NO_INGREDIENT = "null";
    public static final String NO_MEASURE = "null";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "key")
    private String key;

    @ColumnInfo(name = "id")
    private final String id;

    @ColumnInfo(name = "name")
    private final String name;

    @ColumnInfo(name = "category")
    private final String category;

    @ColumnInfo(name = "alcoholic")
    private final String alcoholic;

    @ColumnInfo(name = "glass")
    private final String glass;

    @ColumnInfo(name = "instruction")
    private final String instruction;

    @ColumnInfo(name = "ingredients")
    @TypeConverters(Converter.class)
    private final List<String> ingredients;

    @ColumnInfo(name = "measures")
    @TypeConverters(Converter.class)
    private final List<String> measures;

    @ColumnInfo(name = "image")
    private final String image;

    @ColumnInfo(name = "uid")
    private String uid;

    public Cocktail(String id, String name, String category, String alcoholic,
                    String glass, String instruction, List<String> ingredients,
                    List<String> measures, String image) {
        this.key = "";
        this.id = id;
        this.name = name;
        this.category = category;
        this.alcoholic = alcoholic;
        this.glass = glass;
        this.instruction = instruction;
        this.ingredients = ingredients;
        this.measures = measures;
        this.image = image;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAlcoholic() {
        return alcoholic;
    }

    public String getGlass() {
        return glass;
    }

    public String getInstruction() {
        return instruction;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> getMeasures() {
        return measures;
    }

    public String getImage() {
        return image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static class Converter {
        @TypeConverter
        public String get_string(List<String> str) {
            if (str == null)
                return null;
            StringBuilder pictures = new StringBuilder();
            for (String s : str) pictures.append(s).append(",");
            return pictures.toString();
        }

        @TypeConverter
        public List<String> set_string(String str) {
            if (str == null)
                return null;
            return new ArrayList<>(Arrays.asList(str.split(",")));
        }
    }

}
