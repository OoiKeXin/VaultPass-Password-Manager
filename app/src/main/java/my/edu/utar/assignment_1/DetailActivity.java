package my.edu.utar.assignment_1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private boolean isPinVisible = false;
    private boolean isAnswerVisible = false;
    
    private String actualPassword = "";
    private String actualPin = "";
    private String actualAnswer = "";
    
    private String currentName, currentUsername, currentUrl, currentCategory, currentQuestion, currentNotes;
    private int currentIconRes;
    private int currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Prevent screenshots and hide content in Recent Apps
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, 
                             android.view.WindowManager.LayoutParams.FLAG_SECURE);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        Intent intent = getIntent();
        if (intent != null) {
            currentId = intent.getIntExtra("id", -1);
            currentName = intent.getStringExtra("name");
            currentUsername = intent.getStringExtra("username");
            currentIconRes = intent.getIntExtra("iconRes", R.drawable.ic_browser);
            currentUrl = intent.getStringExtra("url");
            currentCategory = intent.getStringExtra("category");
            actualPassword = intent.getStringExtra("password");
            
            // Advanced fields
            actualPin = intent.getStringExtra("pin");
            currentQuestion = intent.getStringExtra("securityQuestion");
            actualAnswer = intent.getStringExtra("securityAnswer");
            currentNotes = intent.getStringExtra("notes");
            
            updateUI();

            // Edit logic
            findViewById(R.id.ivEdit).setOnClickListener(v -> {
                Intent editIntent = new Intent(this, EditActivity.class);
                editIntent.putExtra("name", currentName);
                editIntent.putExtra("username", currentUsername);
                editIntent.putExtra("url", currentUrl);
                editIntent.putExtra("password", actualPassword);
                editIntent.putExtra("category", currentCategory);
                editIntent.putExtra("pin", actualPin);
                editIntent.putExtra("securityQuestion", currentQuestion);
                editIntent.putExtra("securityAnswer", actualAnswer);
                editIntent.putExtra("notes", currentNotes);
                startActivityForResult(editIntent, 100);
            });

            // Web Navigation
            findViewById(R.id.tvUrl).setOnClickListener(v -> {
                String webUrl = currentUrl;
                if (webUrl != null && !webUrl.isEmpty()) {
                    if (!webUrl.startsWith("http")) webUrl = "https://" + webUrl;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                    startActivity(browserIntent);
                }
            });

            findViewById(R.id.ivDelete).setOnClickListener(v -> showDeleteDialog(currentName));
            findViewById(R.id.ivCopyUser).setOnClickListener(v -> copyToClipboard("Username", currentUsername));
            findViewById(R.id.ivCopyPass).setOnClickListener(v -> copyToClipboard("Password", actualPassword));
        }
    }

    private void updateUI() {
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvUrl = findViewById(R.id.tvUrl);
        TextView tvUsername = findViewById(R.id.tvUsernameValue);
        TextView tvPassword = findViewById(R.id.tvPassValue);
        ImageView ivLargeIcon = findViewById(R.id.ivLargeIcon);
        ImageView ivToggle = findViewById(R.id.ivTogglePass);
        
        // Advanced views
        TextView tvPin = findViewById(R.id.tvPinValue);
        TextView tvQuestion = findViewById(R.id.tvQuestionValue);
        TextView tvAnswer = findViewById(R.id.tvAnswerValue);
        TextView tvNotes = findViewById(R.id.tvNotesValue);
        
        ImageView ivTogglePin = findViewById(R.id.ivTogglePin);
        ImageView ivToggleAnswer = findViewById(R.id.ivToggleAnswer);

        if (tvTitle != null) tvTitle.setText(currentName);
        if (tvUrl != null) {
            if (currentUrl != null && !currentUrl.isEmpty()) {
                tvUrl.setText(currentUrl);
                tvUrl.setVisibility(View.VISIBLE);
            } else {
                tvUrl.setVisibility(View.GONE);
            }
        }
        if (tvUsername != null) tvUsername.setText(currentUsername);
        if (ivLargeIcon != null) {
            ivLargeIcon.setImageResource(currentIconRes);
            ivLargeIcon.setColorFilter(null);
        }

        // Password Toggle
        if (tvPassword != null && ivToggle != null) {
            if (isPasswordVisible) {
                tvPassword.setText(actualPassword);
                ivToggle.setColorFilter(ContextCompat.getColor(this, R.color.accent_lime));
            } else {
                tvPassword.setText(R.string.masked_password);
                ivToggle.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
            }
            ivToggle.setOnClickListener(v -> { isPasswordVisible = !isPasswordVisible; updateUI(); });
        }

        // --- Advanced Section Hiding Logic ---
        boolean hasPin = actualPin != null && !actualPin.isEmpty();
        boolean hasQuestion = currentQuestion != null && !currentQuestion.isEmpty();
        boolean hasAnswer = actualAnswer != null && !actualAnswer.isEmpty();
        boolean hasNotes = currentNotes != null && !currentNotes.isEmpty();

        // Handle PIN Row
        int pinVis = hasPin ? View.VISIBLE : View.GONE;
        findViewById(R.id.ivPinIcon).setVisibility(pinVis);
        findViewById(R.id.tvPinLabel).setVisibility(pinVis);
        if (tvPin != null) {
            tvPin.setVisibility(pinVis);
            tvPin.setText(isPinVisible ? actualPin : "••••");
        }
        if (ivTogglePin != null) {
            ivTogglePin.setVisibility(pinVis);
            ivTogglePin.setOnClickListener(v -> { isPinVisible = !isPinVisible; updateUI(); });
            ivTogglePin.setColorFilter(ContextCompat.getColor(this, isPinVisible ? R.color.accent_lime : R.color.text_secondary));
        }
        findViewById(R.id.divider2).setVisibility(pinVis);

        // Handle Question Row
        int questionVis = hasQuestion ? View.VISIBLE : View.GONE;
        findViewById(R.id.ivQuestionIcon).setVisibility(questionVis);
        findViewById(R.id.tvQuestionLabel).setVisibility(questionVis);
        if (tvQuestion != null) {
            tvQuestion.setVisibility(questionVis);
            tvQuestion.setText(currentQuestion);
        }
        findViewById(R.id.divider3).setVisibility(questionVis);

        // Handle Answer Row
        int answerVis = hasAnswer ? View.VISIBLE : View.GONE;
        findViewById(R.id.ivAnswerIcon).setVisibility(answerVis);
        findViewById(R.id.tvAnswerLabel).setVisibility(answerVis);
        if (tvAnswer != null) {
            tvAnswer.setVisibility(answerVis);
            tvAnswer.setText(isAnswerVisible ? actualAnswer : "••••••••");
        }
        if (ivToggleAnswer != null) {
            ivToggleAnswer.setVisibility(answerVis);
            ivToggleAnswer.setOnClickListener(v -> { isAnswerVisible = !isAnswerVisible; updateUI(); });
            ivToggleAnswer.setColorFilter(ContextCompat.getColor(this, isAnswerVisible ? R.color.accent_lime : R.color.text_secondary));
        }
        findViewById(R.id.divider4).setVisibility(answerVis);

        // Handle Notes Row
        int notesVis = hasNotes ? View.VISIBLE : View.GONE;
        findViewById(R.id.ivNotesIcon).setVisibility(notesVis);
        findViewById(R.id.tvNotesLabel).setVisibility(notesVis);
        if (tvNotes != null) {
            tvNotes.setVisibility(notesVis);
            tvNotes.setText(currentNotes);
        }

        // Hide the entire advanced card if everything is empty
        View cardAdvanced = findViewById(R.id.cardAdvanced);
        if (cardAdvanced != null) {
            cardAdvanced.setVisibility((hasPin || hasQuestion || hasAnswer || hasNotes) ? View.VISIBLE : View.GONE);
        }
    }

    private void showDeleteDialog(String name) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(dialogView).create();
        String msg = "Are you sure you want to permanently delete the credentials for <b>" + name + "</b>?";
        ((TextView) dialogView.findViewById(R.id.tvDeleteMessage)).setText(Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY));

        dialogView.findViewById(R.id.btnConfirmDelete).setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("deleted", true);
            resultIntent.putExtra("id", currentId);
            resultIntent.putExtra("name", currentName);
            setResult(RESULT_OK, resultIntent);
            dialog.dismiss();
            finish();
        });
        dialogView.findViewById(R.id.btnCancelDelete).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String originalName = data.getStringExtra("original_name");
            currentName = data.getStringExtra("name");
            currentUsername = data.getStringExtra("username");
            actualPassword = data.getStringExtra("password");
            currentUrl = data.getStringExtra("url");
            currentCategory = data.getStringExtra("category");
            currentIconRes = data.getIntExtra("logoRes", R.drawable.ic_browser);
            
            // Advanced fields
            actualPin = data.getStringExtra("pin");
            currentQuestion = data.getStringExtra("securityQuestion");
            actualAnswer = data.getStringExtra("securityAnswer");
            currentNotes = data.getStringExtra("notes");

            updateUI();
            
            String currentDate = new SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault()).format(new Date());
            View lastUpdatedView = findViewById(R.id.tvLastUpdated);
            if (lastUpdatedView instanceof TextView) {
                ((TextView) lastUpdatedView).setText("Last updated: " + currentDate);
            }

            // Pass the update back to MainActivity
            Intent updateIntent = new Intent();
            updateIntent.putExtra("updated", true);
            updateIntent.putExtra("id", currentId);
            updateIntent.putExtra("original_name", originalName);
            updateIntent.putExtra("name", currentName);
            updateIntent.putExtra("username", currentUsername);
            updateIntent.putExtra("password", actualPassword);
            updateIntent.putExtra("url", currentUrl);
            updateIntent.putExtra("category", currentCategory);
            updateIntent.putExtra("logoRes", currentIconRes);
            
            // Advanced fields
            updateIntent.putExtra("pin", actualPin);
            updateIntent.putExtra("securityQuestion", currentQuestion);
            updateIntent.putExtra("securityAnswer", actualAnswer);
            updateIntent.putExtra("notes", currentNotes);

            setResult(RESULT_OK, updateIntent);
        }
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text));
        Toast.makeText(this, label + " copied", Toast.LENGTH_SHORT).show();
    }
}
