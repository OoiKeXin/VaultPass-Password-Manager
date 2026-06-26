package my.edu.utar.assignment_1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import java.util.Random;

public class NewEntryActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private boolean isAdvancedVisible = false;
    private EditText etPassword, etSiteName, etUsername, etUrl;
    private EditText etPin, etQuestion, etAnswer, etNotes;
    private String selectedCategory = "Browser";
    private int selectedLogoRes = R.drawable.ic_browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Prevent screenshots and hide content in Recent Apps
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, 
                             android.view.WindowManager.LayoutParams.FLAG_SECURE);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_entry);

        etPassword = findViewById(R.id.etPassword);
        etSiteName = findViewById(R.id.etSiteName);
        etUsername = findViewById(R.id.etUsername);
        etUrl = findViewById(R.id.etUrl);
        
        // Advanced fields
        etPin = findViewById(R.id.etPin);
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        etNotes = findViewById(R.id.etNotes);
        
        LinearLayout llMainStrength = findViewById(R.id.llStrengthMeter);
        TextView tvMainStrengthText = findViewById(R.id.tvStrengthText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_entry_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCategorySelection();

        findViewById(R.id.tvAutoFill).setOnClickListener(v -> etPassword.setText(PasswordUtils.generateRandomPassword(16, true, true, true, true)));
        findViewById(R.id.tvOptions).setOnClickListener(v -> showGeneratorDialog());

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String password = s.toString();
                boolean upper = false, lower = false, numbers = false, symbols = false;
                for (char c : password.toCharArray()) {
                    if (Character.isUpperCase(c)) upper = true;
                    else if (Character.isLowerCase(c)) lower = true;
                    else if (Character.isDigit(c)) numbers = true;
                    else if (!Character.isWhitespace(c)) symbols = true;
                }
                updateStrengthUI(password.length(), upper, lower, numbers, symbols, llMainStrength, tvMainStrengthText);
            }
        });

        TextView btnToggle = findViewById(R.id.btnToggleAdvanced);
        View advancedSection = findViewById(R.id.advancedSection);
        NestedScrollView scrollView = findViewById(R.id.scrollView);

        btnToggle.setOnClickListener(v -> {
            if (isAdvancedVisible) {
                advancedSection.setVisibility(View.GONE);
                btnToggle.setText(R.string.show_advanced_options);
            } else {
                advancedSection.setVisibility(View.VISIBLE);
                btnToggle.setText(R.string.hide_advanced_options);
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
            isAdvancedVisible = !isAdvancedVisible;
        });

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String siteName = etSiteName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (siteName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields (*)", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", siteName);
            resultIntent.putExtra("username", username);
            resultIntent.putExtra("password", password);
            resultIntent.putExtra("url", etUrl.getText().toString().trim());
            resultIntent.putExtra("category", selectedCategory);
            resultIntent.putExtra("logoRes", selectedLogoRes);
            
            // Advanced fields
            resultIntent.putExtra("pin", etPin.getText().toString().trim());
            resultIntent.putExtra("securityQuestion", etQuestion.getText().toString().trim());
            resultIntent.putExtra("securityAnswer", etAnswer.getText().toString().trim());
            resultIntent.putExtra("notes", etNotes.getText().toString().trim());

            setResult(RESULT_OK, resultIntent);
            finish();
        });

        ImageView ivToggle = findViewById(R.id.ivVisibilityToggle);
        ivToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private void setupCategorySelection() {
        LinearLayout btnBrowser = findViewById(R.id.btnCategoryBrowser);
        LinearLayout btnMobile = findViewById(R.id.btnCategoryMobile);
        LinearLayout btnPayment = findViewById(R.id.btnCategoryPayment);

        updateCategoryUI("Browser");

        btnBrowser.setOnClickListener(v -> updateCategoryUI("Browser"));
        btnMobile.setOnClickListener(v -> updateCategoryUI("Mobile App"));
        btnPayment.setOnClickListener(v -> updateCategoryUI("Payment"));
    }

    private void updateCategoryUI(String category) {
        selectedCategory = category;

        LinearLayout btnBrowser = findViewById(R.id.btnCategoryBrowser);
        LinearLayout btnMobile = findViewById(R.id.btnCategoryMobile);
        LinearLayout btnPayment = findViewById(R.id.btnCategoryPayment);

        ImageView ivBrowser = findViewById(R.id.ivCategoryBrowser);
        ImageView ivMobile = findViewById(R.id.ivCategoryMobile);
        ImageView ivPayment = findViewById(R.id.ivCategoryPayment);

        TextView tvBrowser = findViewById(R.id.tvCategoryBrowser);
        TextView tvMobile = findViewById(R.id.tvCategoryMobile);
        TextView tvPayment = findViewById(R.id.tvCategoryPayment);

        int lime = ContextCompat.getColor(this, R.color.accent_lime);
        int secondary = ContextCompat.getColor(this, R.color.text_secondary);
        int dark = ContextCompat.getColor(this, R.color.bg_dark);

        btnBrowser.setBackgroundResource(R.drawable.item_bg_rounded);
        btnMobile.setBackgroundResource(R.drawable.item_bg_rounded);
        btnPayment.setBackgroundResource(R.drawable.item_bg_rounded);
        
        btnBrowser.setBackgroundTintList(null);
        btnMobile.setBackgroundTintList(null);
        btnPayment.setBackgroundTintList(null);

        ivBrowser.setColorFilter(secondary);
        ivMobile.setColorFilter(secondary);
        ivPayment.setColorFilter(secondary);

        tvBrowser.setTextColor(secondary);
        tvMobile.setTextColor(secondary);
        tvPayment.setTextColor(secondary);

        switch (category) {
            case "Browser":
                selectedLogoRes = R.drawable.ic_browser;
                btnBrowser.setBackgroundTintList(android.content.res.ColorStateList.valueOf(lime));
                ivBrowser.setColorFilter(dark);
                tvBrowser.setTextColor(dark);
                break;
            case "Mobile App":
                selectedLogoRes = R.drawable.ic_mobile_app;
                btnMobile.setBackgroundTintList(android.content.res.ColorStateList.valueOf(lime));
                ivMobile.setColorFilter(dark);
                tvMobile.setTextColor(dark);
                break;
            case "Payment":
                selectedLogoRes = R.drawable.ic_payment;
                btnPayment.setBackgroundTintList(android.content.res.ColorStateList.valueOf(lime));
                ivPayment.setColorFilter(dark);
                tvPayment.setTextColor(dark);
                break;
        }
    }

    private void showGeneratorDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_generator, null);
        bottomSheetDialog.setContentView(view);
        TextView tvGenerated = view.findViewById(R.id.tvGeneratedPassword);
        Slider slider = view.findViewById(R.id.sliderLength);
        TextView tvLengthValue = view.findViewById(R.id.tvLengthValue);
        TextView tvStrengthValue = view.findViewById(R.id.tvStrengthValue);
        LinearLayout llGenStrength = view.findViewById(R.id.llGenStrength);
        View viewUpper = view.findViewById(R.id.switchUpper);
        View viewLower = view.findViewById(R.id.switchLower);
        View viewNumbers = view.findViewById(R.id.switchNumbers);
        View viewSymbols = view.findViewById(R.id.switchSymbols);
        ((TextView) viewUpper.findViewById(R.id.tvSwitchLabel)).setText(R.string.uppercase);
        ((TextView) viewLower.findViewById(R.id.tvSwitchLabel)).setText(R.string.lowercase);
        ((TextView) viewNumbers.findViewById(R.id.tvSwitchLabel)).setText(R.string.numbers);
        ((TextView) viewSymbols.findViewById(R.id.tvSwitchLabel)).setText(R.string.symbols);
        MaterialSwitch sUpper = viewUpper.findViewById(R.id.switchGen);
        MaterialSwitch sLower = viewLower.findViewById(R.id.switchGen);
        MaterialSwitch sNumbers = viewNumbers.findViewById(R.id.switchGen);
        MaterialSwitch sSymbols = viewSymbols.findViewById(R.id.switchGen);
        Runnable updatePassword = () -> {
            int length = (int) slider.getValue();
            tvLengthValue.setText(String.valueOf(length));
            tvGenerated.setText(PasswordUtils.generateRandomPassword(length, sUpper.isChecked(), sLower.isChecked(), sNumbers.isChecked(), sSymbols.isChecked()));
            updateStrengthUI(length, sUpper.isChecked(), sLower.isChecked(), sNumbers.isChecked(), sSymbols.isChecked(), llGenStrength, tvStrengthValue);
        };
        updatePassword.run();
        slider.addOnChangeListener((s, val, fromUser) -> updatePassword.run());
        sUpper.setOnCheckedChangeListener((b, checked) -> updatePassword.run());
        sLower.setOnCheckedChangeListener((b, checked) -> updatePassword.run());
        sNumbers.setOnCheckedChangeListener((b, checked) -> updatePassword.run());
        sSymbols.setOnCheckedChangeListener((b, checked) -> updatePassword.run());
        view.findViewById(R.id.ivRegenerate).setOnClickListener(v -> updatePassword.run());
        view.findViewById(R.id.btnUsePassword).setOnClickListener(v -> {
            etPassword.setText(tvGenerated.getText().toString());
            bottomSheetDialog.dismiss();
        });
        view.findViewById(R.id.ivClose).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private void updateStrengthUI(int length, boolean upper, boolean lower, boolean numbers, boolean symbols, LinearLayout llGenStrength, TextView tvStrengthValue) {
        if (length == 0) {
            if (tvStrengthValue != null) {
                tvStrengthValue.setText(R.string.strength);
                tvStrengthValue.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            }
            if (llGenStrength != null) {
                int inactiveColor = ContextCompat.getColor(this, R.color.divider_color);
                for (int i = 0; i < llGenStrength.getChildCount(); i++) {
                    llGenStrength.getChildAt(i).setBackgroundColor(inactiveColor);
                }
            }
            return;
        }
        int types = 0;
        if (upper) types++;
        if (lower) types++;
        if (numbers) types++;
        if (symbols) types++;
        int score = 0;
        if (length >= 4) score++;
        if (length >= 8) score++;
        if (length >= 12 || (length >= 8 && types >= 2)) score++;
        if (length >= 16 || (length >= 12 && types >= 3)) score++;
        if (types >= 4 && length >= 12) score++;
        String strengthText;
        int activeColor;
        int cyan = Color.parseColor("#00E5FF");
        int inactiveColor = ContextCompat.getColor(this, R.color.divider_color);
        switch (score) {
            case 1: strengthText = "VERY WEAK"; activeColor = Color.parseColor("#FF4B5C"); break;
            case 2: strengthText = "WEAK"; activeColor = Color.parseColor("#FF4B5C"); break;
            case 3: strengthText = "FAIR"; activeColor = Color.parseColor("#FF9900"); break;
            case 4: strengthText = "STRONG"; activeColor = cyan; break;
            case 5: strengthText = "VERY STRONG"; activeColor = cyan; break;
            default: strengthText = "STRENGTH"; activeColor = ContextCompat.getColor(this, R.color.text_secondary);
        }
        if (tvStrengthValue != null) {
            tvStrengthValue.setText(strengthText);
            tvStrengthValue.setTextColor(activeColor);
        }
        if (llGenStrength != null) {
            for (int i = 0; i < llGenStrength.getChildCount(); i++) {
                View segment = llGenStrength.getChildAt(i);
                segment.setBackgroundColor(i < score ? activeColor : inactiveColor);
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
        for (int i = 0; i < length; i++) sb.append(chars.charAt(random.nextInt(chars.length())));
        return sb.toString();
    }
}
