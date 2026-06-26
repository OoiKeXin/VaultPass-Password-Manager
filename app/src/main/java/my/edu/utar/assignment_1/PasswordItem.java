package my.edu.utar.assignment_1;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "passwords")
public class PasswordItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;
    private String username;
    private String password;
    private String url;
    private String category;
    private int logoRes;
    private int colorRes;
    
    // Advanced fields
    private String pin = "";
    private String securityQuestion = "";
    private String securityAnswer = "";
    private String notes = "";

    public PasswordItem(String name, String username, String password, String url, String category, int logoRes, int colorRes) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.url = url;
        this.category = category;
        this.logoRes = logoRes;
        this.colorRes = colorRes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getLogoRes() { return logoRes; }
    public void setLogoRes(int logoRes) { this.logoRes = logoRes; }
    public int getColorRes() { return colorRes; }
    public void setColorRes(int colorRes) { this.colorRes = colorRes; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
