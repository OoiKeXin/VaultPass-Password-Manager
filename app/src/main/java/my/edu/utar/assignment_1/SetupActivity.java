package my.edu.utar.assignment_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SetupActivity extends AppCompatActivity {

    private boolean isChanging = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setup);

        prefs = getSharedPreferences("VaultPassPrefs", MODE_PRIVATE);
        isChanging = getIntent().getBooleanExtra("is_changing", false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setup_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvTitle = findViewById(R.id.tvSetupTitle);
        TextView tvDesc = findViewById(R.id.tvSetupDesc);
        EditText etOld = findViewById(R.id.etOldPassword);
        EditText etNew = findViewById(R.id.etNewPassword);
        EditText etConfirm = findViewById(R.id.etConfirmPassword);
        View btnSave = findViewById(R.id.btnSave);
        ImageView ivBack = findViewById(R.id.ivBack);

        if (isChanging) {
            tvTitle.setText("Change Password");
            tvDesc.setText("Enter your current and new master password below.");
            etOld.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.GONE);
        }

        ivBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String oldPassInput = etOld.getText().toString().trim();
            String newPass = etNew.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            // 1. Validate New Password not empty
            if (newPass.isEmpty()) {
                Toast.makeText(this, "New password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Validate New and Confirm match
            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isChanging) {
                // 3. Validate Old Password
                String savedPass = prefs.getString("master_password", "");
                if (!oldPassInput.equals(savedPass)) {
                    Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 4. Update Password
                prefs.edit().putString("master_password", newPass).apply();
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                
                // 5. Navigate to Login
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // Initial Setup Mode
                prefs.edit().putString("master_password", newPass).apply();
                Toast.makeText(this, "Master password set successfully", Toast.LENGTH_SHORT).show();
                
                // Navigate to Main
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}