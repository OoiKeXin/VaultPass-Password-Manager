package my.edu.utar.assignment_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences vaultPrefs;
    private SharedPreferences loginPrefs;
    private TextView tvAutoLockValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        vaultPrefs = getSharedPreferences("VaultPrefs", MODE_PRIVATE);
        loginPrefs = getSharedPreferences("VaultPassPrefs", MODE_PRIVATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvAutoLockValue = findViewById(R.id.tvAutoLockValue);
        updateAutoLockDisplay();

        setupClickListeners();
        setupNavigation();
    }

    private void updateAutoLockDisplay() {
        int minutes = vaultPrefs.getInt("auto_lock_minutes", 0);
        if (minutes == 0) {
            tvAutoLockValue.setText("Never");
        } else if (minutes < 60) {
            tvAutoLockValue.setText(minutes + " mins");
        } else {
            tvAutoLockValue.setText((minutes / 60) + " hour" + (minutes >= 120 ? "s" : ""));
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.btnChangePin).setOnClickListener(v -> showChangePinDialog());

        findViewById(R.id.btnAutoLock).setOnClickListener(v -> showAutoLockDialog());
        
        findViewById(R.id.btnAboutApp).setOnClickListener(v -> showAboutDialog());
        findViewById(R.id.btnPrivacyPolicy).setOnClickListener(v -> showPrivacyPolicyDialog());
        findViewById(R.id.btnClearData).setOnClickListener(v -> showClearDataDialog());
        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutDialog());
    }

    private void showChangePinDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_pin, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(dialogView).create();

        EditText etOldPin = dialogView.findViewById(R.id.etOldPin);
        EditText etNewPin = dialogView.findViewById(R.id.etNewPin);
        EditText etConfirmPin = dialogView.findViewById(R.id.etConfirmPin);

        dialogView.findViewById(R.id.btnSavePin).setOnClickListener(v -> {
            String oldPin = etOldPin.getText().toString();
            String newPin = etNewPin.getText().toString();
            String confirmPin = etConfirmPin.getText().toString();

            String savedPin = vaultPrefs.getString("app_pin", "1234"); // Default pin is 1234

            if (oldPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!oldPin.equals(savedPin)) {
                Toast.makeText(this, "Incorrect current pin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPin.length() < 4) {
                Toast.makeText(this, "Pin must be at least 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPin.equals(confirmPin)) {
                Toast.makeText(this, "New pins do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            vaultPrefs.edit().putString("app_pin", newPin).apply();
            Toast.makeText(this, "Pin updated successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btnCancelPin).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void showAutoLockDialog() {
        String[] options = {"Never", "1 minute", "5 minutes", "10 minutes", "30 minutes", "1 hour"};
        int[] values = {0, 1, 5, 10, 30, 60};
        
        int currentMinutes = vaultPrefs.getInt("auto_lock_minutes", 0);
        int checkedItem = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == currentMinutes) {
                checkedItem = i;
                break;
            }
        }

        final int[] selectedValue = {values[checkedItem]};

        new MaterialAlertDialogBuilder(this, R.style.MaterialDialogTheme)
                .setTitle("Auto-Lock Timer")
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    selectedValue[0] = values[which];
                })
                .setPositiveButton("Save", (dialog, which) -> {
                    vaultPrefs.edit().putInt("auto_lock_minutes", selectedValue[0]).apply();
                    updateAutoLockDisplay();
                    Toast.makeText(this, "Auto-lock updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(dialogView).create();
        
        ((TextView) dialogView.findViewById(R.id.tvDeleteTitle)).setText("Logout?");
        ((TextView) dialogView.findViewById(R.id.tvDeleteMessage)).setText("Are you sure you want to logout of your vault?");
        ((TextView) dialogView.findViewById(R.id.btnConfirmDelete)).setText("Logout");

        dialogView.findViewById(R.id.btnConfirmDelete).setOnClickListener(v -> {
            dialog.dismiss();
            performLogout();
        });
        dialogView.findViewById(R.id.btnCancelDelete).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void performLogout() {
        loginPrefs.edit().putBoolean("is_logged_in", false).apply();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(this, R.style.MaterialDialogTheme)
               .setTitle("About App")
               .setMessage("VaultPass v1.0\nSecurely manage your passwords offline.")
               .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
               .show();
    }

    private void showPrivacyPolicyDialog() {
        new MaterialAlertDialogBuilder(this, R.style.MaterialDialogTheme)
                .setTitle(R.string.privacy_policy_title)
                .setMessage(Html.fromHtml(getString(R.string.privacy_policy_content), Html.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showClearDataDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(dialogView).create();
        ((TextView) dialogView.findViewById(R.id.tvDeleteTitle)).setText("Clear All Data?");
        ((TextView) dialogView.findViewById(R.id.tvDeleteMessage)).setText("Are you sure you want to permanently delete ALL saved passwords?");
        dialogView.findViewById(R.id.btnConfirmDelete).setOnClickListener(v -> {
            vaultPrefs.edit().clear().apply();
            loginPrefs.edit().putBoolean("is_logged_in", false).apply();
            Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
        dialogView.findViewById(R.id.btnCancelDelete).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void setupNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.navGenerator).setOnClickListener(v -> {
            startActivity(new Intent(this, GeneratorActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }
}
