package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static utem.ftmk.bitp3453.finalproject.cocktailapp.CocktailUtils.getAlcoholColor;

public class CocktailDialogFragment extends DialogFragment
        implements View.OnClickListener, CocktailUtils.OnCocktailClickListener {

    public static final String TAG = CocktailDialogFragment.class.getSimpleName();

    private View layoutLucky;
    private ImageView ivImage;
    private TextView tvName;
    private TextView tvCategory;
    private TextView tvAlcoholic;

    private View layoutPlaceholder;
    private ImageView ivPlaceholder;
    private TextView tvPrompt;

    private FloatingActionButton fabRetry;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isSensorAvailable;
    private ShakerEventListener shakerEventListener;

    private Vibrator vibrator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            isSensorAvailable = true;
        } else {
            dismiss();
            isSensorAvailable = false;
            Toast.makeText(requireActivity(), R.string.sensor_not_available,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.layout_cocktail_lucky, container, false);

        layoutLucky = root.findViewById(R.id.layout_cocktail_lucky);
        ivImage = root.findViewById(R.id.iv_cocktail_image_lucky);
        tvName = root.findViewById(R.id.tv_cocktail_name_lucky);
        tvCategory = root.findViewById(R.id.tv_cocktail_category_lucky);
        tvAlcoholic = root.findViewById(R.id.tv_cocktail_alcoholic_lucky);

        layoutPlaceholder = root.findViewById(R.id.layout_placeholder_lucky);
        tvPrompt = root.findViewById(R.id.tv_prompt_shake);
        ivPlaceholder = root.findViewById(R.id.iv_placeholder_lucky);

        FloatingActionButton fabClose = root.findViewById(R.id.fab_lucky_close);
        fabClose.setOnClickListener(this);
        fabRetry = root.findViewById(R.id.fab_lucky_retry);
        fabRetry.setOnClickListener(this);

        Objects.requireNonNull(getDialog()).getWindow()
                .setBackgroundDrawableResource(android.R.color.transparent);

        return root;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }

    private void onCloseClick() {
        dismiss();
    }

    private void onRetryClick() {
        shakerEventListener = new ShakerEventListener();
        sensorManager.registerListener(shakerEventListener, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        layoutLucky.setVisibility(View.INVISIBLE);
        layoutPlaceholder.setVisibility(View.VISIBLE);
        ivPlaceholder.setVisibility(View.VISIBLE);
        tvPrompt.setText(R.string.shake_your_lucky_cocktail);
        fabRetry.setVisibility(View.GONE);
    }

    @Override
    public void onClick(Cocktail cocktail) {
        cocktail.setKey(cocktail.getId() + FirebaseAuth.getInstance().getUid());
        cocktail.setUid(FirebaseAuth.getInstance().getUid());
        Intent intent = new Intent(requireActivity(), CocktailDetailActivity.class);
        intent.putExtra(CocktailUtils.EXTRA_COCKTAIL, cocktail);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_lucky_close:
                onCloseClick();
                break;
            case R.id.fab_lucky_retry:
                onRetryClick();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSensorAvailable && layoutLucky.getVisibility() == View.INVISIBLE) {
            shakerEventListener = new ShakerEventListener();
            sensorManager.registerListener(shakerEventListener, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isSensorAvailable) {
            sensorManager.unregisterListener(shakerEventListener, accelerometer);
        }
    }

    private void fetchRandomCocktail() {
        new CocktailAsyncTaskRandom(new CocktailAsyncTaskRandom.OnTaskCompleteListener() {
            @Override
            public void onSuccess(Cocktail cocktail) {
                CocktailUtils.setCocktailImage(requireActivity(), cocktail.getImage(), ivImage);
                tvName.setText(cocktail.getName());
                tvCategory.setText(cocktail.getCategory());
                tvAlcoholic.setText(cocktail.getAlcoholic());
                tvAlcoholic.setTextColor(requireActivity()
                        .getColor(getAlcoholColor(cocktail.getAlcoholic())));

                layoutLucky.setOnClickListener(v -> onClick(cocktail));

                layoutPlaceholder.setVisibility(View.GONE);
                fabRetry.setVisibility(View.VISIBLE);
                layoutLucky.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Exception e) {
                fabRetry.setVisibility(View.VISIBLE);
                ivPlaceholder.setVisibility(View.GONE);
                tvPrompt.setText(e.getMessage());
            }
        }).execute();
    }

    private class ShakerEventListener implements SensorEventListener {

        private boolean isFirstShake = true;
        private float lastX, lastY, lastZ;
        private static final float SHAKE_THRESHOLD = 10f;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float currentX = event.values[0];
            float currentY = event.values[1];
            float currentZ = event.values[2];

            if (!isFirstShake) {
                float differenceX = Math.abs(lastX - currentX);
                float differenceY = Math.abs(lastY - currentY);
                float differenceZ = Math.abs(lastZ - currentZ);

                if (differenceX > SHAKE_THRESHOLD && differenceY > SHAKE_THRESHOLD ||
                        differenceX > SHAKE_THRESHOLD && differenceZ > SHAKE_THRESHOLD ||
                        differenceY > SHAKE_THRESHOLD && differenceZ > SHAKE_THRESHOLD) {
                    sensorManager.unregisterListener(shakerEventListener, accelerometer);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500,
                                VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(500);
                    }
                    fetchRandomCocktail();
                }
            }

            lastX = currentX;
            lastY = currentY;
            lastZ = currentZ;
            isFirstShake = false;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

}
