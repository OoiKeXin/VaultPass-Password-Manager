package my.edu.utar.assignment_1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import java.util.Random;

public class GeneratorActivity extends AppCompatActivity {

    private TextView tvGenerated;
    private Slider slider;
    private TextView tvLengthValue;
    private TextView tvStrengthValue;
    private LinearLayout llGenStrength;
    private MaterialSwitch sUpper, sLower, sNumbers, sSymbols;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Prevent screenshots and hide content in Recent Apps
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, 
                             android.view.WindowManager.LayoutParams.FLAG_SECURE);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generator);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.generator_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupLogic();
        setupNavigation();
    }

    private void initViews() {
        tvGenerated = findViewById(R.id.tvGeneratedPassword);
        slider = findViewById(R.id.sliderLength);
        tvLengthValue = findViewById(R.id.tvLengthValue);
        tvStrengthValue = findViewById(R.id.tvGenStrengthText);
        llGenStrength = findViewById(R.id.llGenStrength);

        View viewUpper = findViewById(R.id.switchUpper);
        View viewLower = findViewById(R.id.switchLower);
        View viewNumbers = findViewById(R.id.switchNumbers);
        View viewSymbols = findViewById(R.id.switchSymbols);

        ((TextView) viewUpper.findViewById(R.id.tvSwitchLabel)).setText(R.string.uppercase);
        ((TextView) viewLower.findViewById(R.id.tvSwitchLabel)).setText(R.string.lowercase);
        ((TextView) viewNumbers.findViewById(R.id.tvSwitchLabel)).setText(R.string.numbers);
        ((TextView) viewSymbols.findViewById(R.id.tvSwitchLabel)).setText(R.string.symbols);

        sUpper = viewUpper.findViewById(R.id.switchGen);
        sLower = viewLower.findViewById(R.id.switchGen);
        sNumbers = viewNumbers.findViewById(R.id.switchGen);
        sSymbols = viewSymbols.findViewById(R.id.switchGen);
    }

    private void setupLogic() {
        Runnable updatePassword = () -> {
            int length = (int) slider.getValue();
            tvLengthValue.setText(String.valueOf(length));
            tvGenerated.setText(PasswordUtils.generateRandomPassword(length, sUpper.isChecked(), sLower.isChecked(), sNumbers.isChecked(), sSymbols.isChecked()));
            updateStrengthUI(length, sUpper.isChecked(), sLower.isChecked(), sNumbers.isChecked(), sSymbols.isChecked());
        };

        updatePassword.run();

        slider.addOnChangeListener((s, value, fromUser) -> updatePassword.run());
        sUpper.setOnCheckedChangeListener((b, checked) -> updatePassword.run());
        sLower.setOnCheckedChangeListener((b, checked) -> updatePassword.run());
        sNumbers.setOnCheckedChangeListener((b, checked) -> updatePassword.run());
        sSymbols.setOnCheckedChangeListener((b, checked) -> updatePassword.run());

        findViewById(R.id.ivRegenerate).setOnClickListener(v -> updatePassword.run());
        findViewById(R.id.ivCopyGen).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", tvGenerated.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateStrengthUI(int length, boolean upper, boolean lower, boolean numbers, boolean symbols) {
        int types = 0;
        if (upper) types++;
        if (lower) types++;
        if (numbers) types++;
        if (symbols) types++;

        if (types == 0) {
            tvStrengthValue.setText("SELECT OPTIONS");
            tvStrengthValue.setTextColor(Color.parseColor("#FF4B5C"));
            for (int i = 0; i < llGenStrength.getChildCount(); i++) {
                llGenStrength.getChildAt(i).setBackgroundColor(Color.parseColor("#1A1D2D"));
            }
            return;
        }

        int score = 0;
        if (length >= 8) score++;
        if (length >= 12) score++;
        if (types >= 2) score++;
        if (types >= 3) score++;
        if (types >= 4 && length >= 14) score++;

        String strengthText;
        int activeColor;
        int inactiveColor = Color.parseColor("#1A1D2D");

        switch (score) {
            case 1:
                strengthText = "WEAK";
                activeColor = Color.parseColor("#FF4B5C");
                break;
            case 2:
                strengthText = "FAIR";
                activeColor = Color.parseColor("#FF9900");
                break;
            case 3:
                strengthText = "GOOD";
                activeColor = Color.parseColor("#947BFF");
                break;
            case 4:
                strengthText = "STRONG";
                activeColor = Color.parseColor("#00E5FF");
                break;
            case 5:
                strengthText = "VERY STRONG";
                activeColor = Color.parseColor("#00E5FF");
                break;
            default:
                strengthText = "TOO SHORT";
                activeColor = Color.parseColor("#FF4B5C");
                score = 0;
        }

        tvStrengthValue.setText(strengthText);
        tvStrengthValue.setTextColor(activeColor);

        for (int i = 0; i < llGenStrength.getChildCount(); i++) {
            View segment = llGenStrength.getChildAt(i);
            if (i < score) {
                segment.setBackgroundColor(activeColor);
            } else {
                segment.setBackgroundColor(inactiveColor);
            }
        }
    }

    private String generateRandomPassword(int length, boolean upper, boolean lower, boolean numbers, boolean symbols) {
        StringBuilder chars = new StringBuilder();
        if (upper) chars.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        if (lower) chars.append("abcdefghijklmnopqrstuvwxyz");
        if (numbers) chars.append("0123456789");
        if (symbols) chars.append("!@#$%^&*()");
        
        if (chars.length() == 0) return "";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void setupNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        
        findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }
}
