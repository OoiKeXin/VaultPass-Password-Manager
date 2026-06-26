package my.edu.utar.assignment_1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int DETAIL_REQUEST_CODE = 101;
    private static final int NEW_ENTRY_REQUEST_CODE = 102;
    
    private List<PasswordItem> passwordItems;
    private AppDatabase db;
    private String selectedCategory = null;
    private String currentSearchQuery = "";
    private LinearLayout llItemContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Prevent screenshots and hide content in Recent Apps
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, 
                             android.view.WindowManager.LayoutParams.FLAG_SECURE);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        db = AppDatabase.getInstance(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        llItemContainer = findViewById(R.id.llItemContainer);
        // Refreshing data from Room Database
        initData();
        initCategoryViews();
        setupClickListeners();
        setupSearch();
        setupBottomNav();
        setupCategoryFilters();
        
        refreshList();
    }

    private void initData() {
        passwordItems = db.passwordDao().getAll();
    }

    private void initCategoryViews() {
        int pink = ContextCompat.getColor(this, R.color.accent_pink);

        // Initialize Browser Category
        View catBrowser = findViewById(R.id.catBrowser);
        ((TextView) catBrowser.findViewById(R.id.tvCatTitle)).setText("Browser");
        ((ImageView) catBrowser.findViewById(R.id.ivCatIcon)).setImageResource(R.drawable.ic_browser);
        ((ImageView) catBrowser.findViewById(R.id.ivCatIcon)).setColorFilter(pink);

        // Initialize Mobile Category
        View catMobile = findViewById(R.id.catMobile);
        ((TextView) catMobile.findViewById(R.id.tvCatTitle)).setText("Mobile");
        ((ImageView) catMobile.findViewById(R.id.ivCatIcon)).setImageResource(R.drawable.ic_mobile_app);
        ((ImageView) catMobile.findViewById(R.id.ivCatIcon)).setColorFilter(pink);

        // Initialize Payment Category
        View catPayment = findViewById(R.id.catPayment);
        ((TextView) catPayment.findViewById(R.id.tvCatTitle)).setText("Payment");
        ((ImageView) catPayment.findViewById(R.id.ivCatIcon)).setImageResource(R.drawable.ic_payment);
        ((ImageView) catPayment.findViewById(R.id.ivCatIcon)).setColorFilter(pink);
    }

    private void refreshList() {
        passwordItems = db.passwordDao().getAll();
        llItemContainer.removeAllViews();
        
        View emptyState = findViewById(R.id.llEmptyState);
        boolean isEmpty = true;

        String currentLetter = "";
        int browserCount = 0, mobileCount = 0, paymentCount = 0;

        for (PasswordItem item : passwordItems) {
            boolean matchesSearch = item.getName().toLowerCase().contains(currentSearchQuery.toLowerCase());
            boolean matchesCategory = selectedCategory == null || item.getCategory().equals(selectedCategory);

            if (matchesSearch && matchesCategory) {
                isEmpty = false;
                switch (item.getCategory()) {
                    case "Browser": browserCount++; break;
                    case "Mobile App": mobileCount++; break;
                    case "Payment": paymentCount++; break;
                }

                String firstLetter = item.getName().substring(0, 1).toUpperCase();
                if (!firstLetter.equals(currentLetter)) {
                    currentLetter = firstLetter;
                    addSectionHeader(currentLetter);
                }
                addItemView(item);
            }
        }
        
        if (emptyState != null) {
            emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }

        updateCategoryCounts(browserCount, mobileCount, paymentCount);
    }

    private void addSectionHeader(String letter) {
        View headerView = LayoutInflater.from(this).inflate(R.layout.section_header, llItemContainer, false);
        ((TextView) headerView.findViewById(R.id.tvSectionLetter)).setText(letter);
        llItemContainer.addView(headerView);
    }

    private void addItemView(PasswordItem item) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_password, llItemContainer, false);
        ((ImageView) itemView.findViewById(R.id.ivIcon)).setImageResource(item.getLogoRes());
        ((TextView) itemView.findViewById(R.id.tvAccountName)).setText(item.getName());
        ((TextView) itemView.findViewById(R.id.tvEmail)).setText(item.getUsername());

        itemView.setOnClickListener(v -> openDetail(item));
        
        itemView.setOnLongClickListener(v -> {
            copyToClipboard("Password", item.getPassword());
            return true;
        });
        
        llItemContainer.addView(itemView);
    }

    private void copyToClipboard(String label, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, label + " copied (auto-clears in 30s)", Toast.LENGTH_SHORT).show();
        
        // Auto-clear clipboard after 30 seconds for security
        new android.os.Handler().postDelayed(() -> {
            if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().getLabel().equals(label)) {
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("", ""));
                // Optional: Toast.makeText(this, "Clipboard cleared for security", Toast.LENGTH_SHORT).show();
            }
        }, 30000); // 30 seconds
    }

    private void updateCategoryCounts(int b, int m, int p) {
        ((TextView) findViewById(R.id.catBrowser).findViewById(R.id.tvCatCount)).setText(b + (b == 1 ? " Password" : " Passwords"));
        ((TextView) findViewById(R.id.catMobile).findViewById(R.id.tvCatCount)).setText(m + (m == 1 ? " Password" : " Passwords"));
        ((TextView) findViewById(R.id.catPayment).findViewById(R.id.tvCatCount)).setText(p + (p == 1 ? " Password" : " Passwords"));
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearch);
        ImageView ivClear = findViewById(R.id.ivClearSearch);

        ivClear.setOnClickListener(v -> etSearch.setText(""));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                ivClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                refreshList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCategoryFilters() {
        findViewById(R.id.catBrowser).setOnClickListener(v -> toggleCategory("Browser"));
        findViewById(R.id.catMobile).setOnClickListener(v -> toggleCategory("Mobile App"));
        findViewById(R.id.catPayment).setOnClickListener(v -> toggleCategory("Payment"));
    }

    private void toggleCategory(String category) {
        selectedCategory = category.equals(selectedCategory) ? null : category;
        updateCategoryUI();
        refreshList();
    }

    private void updateCategoryUI() {
        View catBrowser = findViewById(R.id.catBrowser);
        View catMobile = findViewById(R.id.catMobile);
        View catPayment = findViewById(R.id.catPayment);

        updateSingleCategoryUI(catBrowser, "Browser".equals(selectedCategory));
        updateSingleCategoryUI(catMobile, "Mobile App".equals(selectedCategory));
        updateSingleCategoryUI(catPayment, "Payment".equals(selectedCategory));
    }

    private void updateSingleCategoryUI(View view, boolean isSelected) {
        int lime = ContextCompat.getColor(this, R.color.accent_lime);
        int cardBg = ContextCompat.getColor(this, R.color.card_bg);
        int black = ContextCompat.getColor(this, R.color.black);
        int white = ContextCompat.getColor(this, R.color.white);
        int secondary = ContextCompat.getColor(this, R.color.text_secondary);

        if (isSelected) {
            view.setBackgroundTintList(android.content.res.ColorStateList.valueOf(lime));
            ((TextView) view.findViewById(R.id.tvCatTitle)).setTextColor(black);
            ((TextView) view.findViewById(R.id.tvCatCount)).setTextColor(black);
            ((ImageView) view.findViewById(R.id.ivCatIcon)).setColorFilter(black);
        } else {
            view.setBackgroundTintList(null); // Reset to original background
            ((TextView) view.findViewById(R.id.tvCatTitle)).setTextColor(white);
            ((TextView) view.findViewById(R.id.tvCatCount)).setTextColor(secondary);
            ((ImageView) view.findViewById(R.id.ivCatIcon)).setColorFilter(ContextCompat.getColor(this, R.color.accent_pink));
        }
    }

    private void setupClickListeners() {
        View.OnClickListener addAction = v -> startActivityForResult(new Intent(this, NewEntryActivity.class), NEW_ENTRY_REQUEST_CODE);
        findViewById(R.id.fabAdd).setOnClickListener(addAction);
        if (findViewById(R.id.btnEmptyAdd) != null) {
            findViewById(R.id.btnEmptyAdd).setOnClickListener(addAction);
        }
    }

    private void openDetail(PasswordItem item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("name", item.getName());
        intent.putExtra("username", item.getUsername());
        intent.putExtra("password", item.getPassword());
        intent.putExtra("iconRes", item.getLogoRes());
        intent.putExtra("url", item.getUrl());
        intent.putExtra("category", item.getCategory());
        
        intent.putExtra("pin", item.getPin());
        intent.putExtra("securityQuestion", item.getSecurityQuestion());
        intent.putExtra("securityAnswer", item.getSecurityAnswer());
        intent.putExtra("notes", item.getNotes());
        
        startActivityForResult(intent, DETAIL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == NEW_ENTRY_REQUEST_CODE) {
                PasswordItem newItem = new PasswordItem(
                    data.getStringExtra("name"),
                    data.getStringExtra("username"),
                    data.getStringExtra("password"),
                    data.getStringExtra("url"),
                    data.getStringExtra("category"),
                    data.getIntExtra("logoRes", R.drawable.ic_browser),
                    R.color.accent_lime
                );
                newItem.setPin(data.getStringExtra("pin"));
                newItem.setSecurityQuestion(data.getStringExtra("securityQuestion"));
                newItem.setSecurityAnswer(data.getStringExtra("securityAnswer"));
                newItem.setNotes(data.getStringExtra("notes"));
                db.passwordDao().insert(newItem);
            } else if (requestCode == DETAIL_REQUEST_CODE) {
                if (data.getBooleanExtra("deleted", false)) {
                    int id = data.getIntExtra("id", -1);
                    for (PasswordItem item : passwordItems) {
                        if (item.getId() == id) {
                            db.passwordDao().delete(item);
                            break;
                        }
                    }
                } else if (data.getBooleanExtra("updated", false)) {
                    int id = data.getIntExtra("id", -1);
                    for (PasswordItem item : passwordItems) {
                        if (item.getId() == id) {
                            item.setName(data.getStringExtra("name"));
                            item.setUsername(data.getStringExtra("username"));
                            item.setPassword(data.getStringExtra("password"));
                            item.setUrl(data.getStringExtra("url"));
                            item.setCategory(data.getStringExtra("category"));
                            item.setLogoRes(data.getIntExtra("logoRes", R.drawable.ic_browser));
                            item.setPin(data.getStringExtra("pin"));
                            item.setSecurityQuestion(data.getStringExtra("securityQuestion"));
                            item.setSecurityAnswer(data.getStringExtra("securityAnswer"));
                            item.setNotes(data.getStringExtra("notes"));
                            db.passwordDao().update(item);
                            break;
                        }
                    }
                }
            }
            refreshList();
        }
    }

    private void setupBottomNav() {
        findViewById(R.id.navGenerator).setOnClickListener(v -> {
            startActivity(new Intent(this, GeneratorActivity.class));
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.navHome).setOnClickListener(v -> refreshList());
    }
}
