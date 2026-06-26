package my.edu.utar.assignment_1;

public class LogoItem {
    private String name;
    private int iconRes;

    public LogoItem(String name, int iconRes) {
        this.name = name;
        this.iconRes = iconRes;
    }

    public String getName() {
        return name;
    }

    public int getIconRes() {
        return iconRes;
    }
}
