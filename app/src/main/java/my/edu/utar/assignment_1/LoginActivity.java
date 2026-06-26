package my.edu.utar.assignment_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private String savedPassword;
    private SharedPreferences prefs;
    private SharedPreferences vaultPrefs;
    private boolean isPasswordVisible = false;
    
    private enum Mode { SIGN_IN, SIGN_UP, FORGOT_PASSWORD }
    private Mode currentMode = Mode.SIGN_IN;

    private TextView tvWelcome, tvInstruction, tvBottomLabel, tvBottomAction, tvForgotPassword;
    private View llFullName, llPassword, clOptions, llSocialLogin, llPinFields;
    private MaterialButton btnLogin;
    private EditText etPassword, etEmail, etFullName, etPin, etConfirmPin;
    private ImageView ivTogglePass, ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Prevent screenshots and hide content in Recent Apps
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, 
                             android.view.WindowManager.LayoutParams.FLAG_SECURE);

        prefs = getSharedPreferences("VaultPassPrefs", MODE_PRIVATE);
        vaultPrefs = getSharedPreferences("VaultPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            startActivity(new Intent(this, LockActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        View mainLayout = findViewById(R.id.login_main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        savedPassword = prefs.getString("master_password", "");

        initViews();
        setupLogic();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcomeLogin);
        tvInstruction = findViewById(R.id.tvInstructionLogin);
        tvBottomLabel = findViewById(R.id.tvBottomLabel);
        tvBottomAction = findViewById(R.id.tvBottomAction);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        
        llFullName = findViewById(R.id.llFullName);
        llPassword = findViewById(R.id.llPassword);
        llPinFields = findViewById(R.id.llPinFields);
        clOptions = findViewById(R.id.clOptions);
        llSocialLogin = findViewById(R.id.llSocialLogin);
        
        btnLogin = findViewById(R.id.btnLogin);
        etPassword = findViewById(R.id.etMasterPasswordLogin);
        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullName);
        etPin = findViewById(R.id.etPinLogin);
        etConfirmPin = findViewById(R.id.etConfirmPinLogin);
        
        ivTogglePass = findViewById(R.id.ivTogglePass);
        ivBack = findViewById(R.id.ivBack);

        // Initially check if user exists
        if (savedPassword.trim().isEmpty()) {
            currentMode = Mode.SIGN_UP;
        } else {
            currentMode = Mode.SIGN_IN;
            etEmail.setText(prefs.getString("saved_email", ""));
            // Move focus to password if email is already filled
            if (!etEmail.getText().toString().isEmpty()) {
                etPassword.requestFocus();
            }
        }
        updateUI();
    }

    private void updateUI() {
        switch (currentMode) {
            case SIGN_UP:
                tvWelcome.setText("Create Your Account?");
                tvInstruction.setText("Create your account to start securing privacy.");
                btnLogin.setText("Register");
                btnLogin.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_person));
                
                llFullName.setVisibility(View.VISIBLE);
                llPassword.setVisibility(View.VISIBLE);
                llPinFields.setVisibility(View.VISIBLE);
                clOptions.setVisibility(View.GONE);
                llSocialLogin.setVisibility(View.VISIBLE);
                
                tvBottomLabel.setText("Already have an account? ");
                tvBottomAction.setText("Sign In");
                ivBack.setVisibility(View.VISIBLE);
                break;
                
            case SIGN_IN:
                tvWelcome.setText("Welcome Back!");
                tvInstruction.setText("Sign in to protect your password.");
                btnLogin.setText("Sign in");
                btnLogin.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_key));
                
                llFullName.setVisibility(View.GONE);
                llPassword.setVisibility(View.VISIBLE);
                llPinFields.setVisibility(View.GONE);
                clOptions.setVisibility(View.VISIBLE);
                llSocialLogin.setVisibility(View.VISIBLE);
                
                tvBottomLabel.setText("Don't have an account? ");
                tvBottomAction.setText("Sign up");
                ivBack.setVisibility(View.GONE);
                
                setupBiometric();
                break;
                
            case FORGOT_PASSWORD:
                tvWelcome.setText("Forgot Password?");
                tvInstruction.setText("Enter your email and we'll send a 5-digit verification code instantly.");
                btnLogin.setText("Send Code");
                btnLogin.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_mail));
                
                llFullName.setVisibility(View.GONE);
                llPassword.setVisibility(View.GONE);
                llPinFields.setVisibility(View.GONE);
                clOptions.setVisibility(View.GONE);
                llSocialLogin.setVisibility(View.GONE);
                
                tvBottomLabel.setText("Already have an account? ");
                tvBottomAction.setText("Sign In");
                ivBack.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setupLogic() {
        // Mode switching logic
        tvBottomAction.setOnClickListener(v -> {
            if (currentMode == Mode.SIGN_IN) {
                currentMode = Mode.SIGN_UP;
            } else {
                currentMode = Mode.SIGN_IN;
            }
            updateUI();
        });

        tvForgotPassword.setOnClickListener(v -> {
            currentMode = Mode.FORGOT_PASSWORD;
            updateUI();
        });

        ivBack.setOnClickListener(v -> {
            if (currentMode == Mode.FORGOT_PASSWORD || currentMode == Mode.SIGN_UP) {
                if (!savedPassword.isEmpty()) {
                    currentMode = Mode.SIGN_IN;
                } else {
                    currentMode = Mode.SIGN_UP;
                }
                updateUI();
            }
        });

        // Password Visibility Toggle
        ivTogglePass.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(null);
                ivTogglePass.setImageResource(R.drawable.ic_visibility);
            } else {
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                ivTogglePass.setImageResource(R.drawable.ic_visibility);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Main Button Logic
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            
            // Email Verification
            if (email.isEmpty()) {
                showCustomToast("Please enter email");
                return;
            }
            if (!isValidEmail(email)) {
                showCustomToast("Invalid email format");
                return;
            }

            if (currentMode == Mode.FORGOT_PASSWORD) {
                showCustomToast("Verification code sent to " + email);
                return;
            }

            String passInput = etPassword.getText().toString().trim();
            if (passInput.isEmpty()) {
                showCustomToast("Password cannot be empty");
                return;
            }

            if (currentMode == Mode.SIGN_UP) {
                if (passInput.length() > 12) {
                    showCustomToast("Password maximum length is 12 characters");
                    return;
                }

                if (!isPasswordComplex(passInput)) {
                    showCustomToast("Password must contain uppercase, lowercase, number and symbol");
                    return;
                }

                String pin = etPin.getText().toString();
                String confirmPin = etConfirmPin.getText().toString();

                if (pin.length() < 4) {
                    showCustomToast("PIN must be at least 4 digits");
                    return;
                }
                if (!pin.equals(confirmPin)) {
                    showCustomToast("PINs do not match");
                    return;
                }

                prefs.edit().putString("master_password", passInput).apply();
                prefs.edit().putString("saved_email", email).apply();
                prefs.edit().putBoolean("is_logged_in", true).apply();
                vaultPrefs.edit().putString("app_pin", pin).apply();

                savedPassword = passInput;
                showCustomToast("Account Created Successfully");
                new Handler().postDelayed(this::navigateToMain, 1000);
            } else {
                if (passInput.equals(savedPassword.trim())) {
                    prefs.edit().putBoolean("is_logged_in", true).apply();
                    navigateToMain();
                } else {
                    showCustomToast("Incorrect Password");
                }
            }
        });
    }

    private boolean isPasswordComplex(String password) {
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSymbol = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSymbol = true; // Any non-alphanumeric character is a symbol
        }
        return hasUpper && hasLower && hasDigit && hasSymbol;
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);

        TextView text = layout.findViewById(R.id.tvToastMessage);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 300);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private boolean isValidEmail(String email) {
        return ValidationUtils.isValidEmail(email);
    }

    private void setupBiometric() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                prefs.edit().putBoolean("is_logged_in", true).apply();
                navigateToMain();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showCustomToast("Authentication failed");
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        if (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG) == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("unlocked", true);
        startActivity(intent);
        finish();
    }
}
