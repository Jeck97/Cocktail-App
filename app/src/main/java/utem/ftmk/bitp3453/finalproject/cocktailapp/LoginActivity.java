package utem.ftmk.bitp3453.finalproject.cocktailapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private String email;
    private String password;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.login));
        window.setNavigationBarColor(getColor(R.color.login));

        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);

        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);

    }

    public void onLoginClick(View view) {
        if (validateForm()) {
            signInUser();
        }
    }

    public void onRegisterClick(View view) {

        @SuppressLint("InflateParams") View dialogView = getLayoutInflater()
                .inflate(R.layout.layout_registration_dialog, null);
        EditText etEmail = dialogView.findViewById(R.id.et_register_email);
        EditText etPassword = dialogView.findViewById(R.id.et_register_password);
        EditText etConfirm = dialogView.findViewById(R.id.et_register_password_confirm);

        AlertDialog alertDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle(R.string.registration)
                .setView(dialogView)
                .setPositiveButton(R.string.register, null)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (validateForm(etEmail, etPassword, etConfirm)) {
                createUser(alertDialog);
            }
        });

    }

    private void signInUser() {
        showProgressDialog(R.string.progress_login);
        auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(onFailureListener)
                .addOnSuccessListener(authResult -> {
                    dismissProgressDialog();
                    startActivity(new Intent(this,
                            CocktailMainActivity.class));
                    finish();
                });
    }

    private void createUser(AlertDialog alertDialog) {
        showProgressDialog(R.string.progress_register);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(onFailureListener)
                .addOnSuccessListener(authResult -> {
                    dismissProgressDialog();
                    alertDialog.dismiss();
                    Toast.makeText(this, R.string.register_successful,
                            Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateForm() {
        boolean validEmail = false;
        Editable editableEmail = etEmail.getText();
        if (editableEmail == null || TextUtils.isEmpty(email = editableEmail.toString().trim())) {
            tilEmail.setError(getString(R.string.error_field_required));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_format_email));
        } else {
            tilEmail.setError(null);
            validEmail = true;
        }

        boolean validPassword = false;
        Editable editablePassword = etPassword.getText();
        if (editablePassword == null || TextUtils.isEmpty(password = editablePassword.toString())) {
            tilPassword.setError(getString(R.string.error_field_required));
        } else {
            tilPassword.setError(null);
            validPassword = true;
        }

        return validEmail && validPassword;
    }

    private boolean validateForm(EditText etEmail, EditText etPassword, EditText etConfirm) {
        boolean validEmail = false;
        email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_format_email));
        } else {
            etEmail.setError(null);
            validEmail = true;
        }

        boolean validPassword = false;
        password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
        } else if (password.length() < 8) {
            etPassword.setError(getString(R.string.error_minimum_password));
        } else {
            etPassword.setError(null);
            validPassword = true;
        }

        boolean validConfirm = false;
        String confirm = etConfirm.getText().toString();
        if (TextUtils.isEmpty(confirm)) {
            etConfirm.setError(getString(R.string.error_field_required));
        } else if (!password.equals(confirm)) {
            etConfirm.setError(getString(R.string.error_not_match_confirm_password));
        } else {
            etConfirm.setError(null);
            validConfirm = true;
        }

        return validEmail && validPassword && validConfirm;
    }

    public void showProgressDialog(@StringRes int message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(message));
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private final OnFailureListener onFailureListener = e -> {
        dismissProgressDialog();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    };
}