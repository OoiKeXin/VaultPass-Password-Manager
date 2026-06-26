package my.edu.utar.assignment_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.concurrent.Executor;

public class LockActivity extends AppCompatActivity {

    private SharedPreferences vaultPrefs;
    private EditText etPin;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Prevent screenshots and hide content in Recent Apps
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, 
                             android.view.WindowManager.LayoutParams.FLAG_SECURE);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lock);

        vaultPrefs = getSharedPreferences("VaultPrefs", MODE_PRIVATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lock_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etPin = findViewById(R.id.etPin);
        setupBiometric();
        
        // Auto-trigger biometric
        checkAndAuthenticate();

        etPin.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (s.length() >= 4) {
                    verifyPin(s.toString());
                }
            }
        });

        findViewById(R.id.btnUnlock).setOnClickListener(v -> verifyPin(etPin.getText().toString()));
        
        // Allow manual trigger via an icon (we'll add this to XML next)
        View ivBio = findViewById(R.id.ivBiometricUnlock);
        if (ivBio != null) {
            ivBio.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));
        }

        findViewById(R.id.tvLogout).setOnClickListener(v -> {
            // Log out and go back to LoginActivity
            SharedPreferences loginPrefs = getSharedPreferences("VaultPassPrefs", MODE_PRIVATE);
            loginPrefs.edit().putBoolean("is_logged_in", false).apply();
            
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupBiometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(LockActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Silence errors if user cancels
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Vault Unlock")
                .setSubtitle("Unlock your vault using biometrics")
                .setNegativeButtonText("Use PIN")
                .build();
    }

    private void checkAndAuthenticate() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private void verifyPin(String inputPin) {
        String savedPin = vaultPrefs.getString("app_pin", "1234");
        if (inputPin.equals(savedPin)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (inputPin.length() >= 4) {
            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
            etPin.setText("");
        }
    }
}
