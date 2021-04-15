package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;

public class CocktailUtils {

    public static final String EXTRA_COCKTAIL = "extra_cocktail";

    public interface OnCocktailClickListener {
        void onClick(Cocktail cocktail);
    }

    public static void setCocktailImage(Context context, String image, ImageView imageView) {
        try {
            Uri imageUri = Uri.parse(image);
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(getProgressDrawable(context))
                    .error(R.drawable.ic_no_image)
                    .into(imageView);
        } catch (NullPointerException e) {
            Glide.with(context)
                    .load(R.drawable.ic_error)
                    .into(imageView);
        }
    }

    public static CircularProgressDrawable getProgressDrawable(Context context) {
        CircularProgressDrawable circularProgressDrawable
                = new CircularProgressDrawable(context);
        circularProgressDrawable.setStyle(CircularProgressDrawable.LARGE);
        circularProgressDrawable.setColorSchemeColors(context.getColor(R.color.teal_200));
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }

    public static int getAlcoholColor(String alcoholic) {
        switch (alcoholic) {
            case Cocktail.ALCOHOLIC:
                return R.color.alcoholic;
            case Cocktail.NON_ALCOHOLIC:
                return R.color.non_alcoholic;
            case Cocktail.OPTIONAL_ALCOHOL:
                return R.color.optional_alcohol;
        }
        return R.color.black;
    }
}
