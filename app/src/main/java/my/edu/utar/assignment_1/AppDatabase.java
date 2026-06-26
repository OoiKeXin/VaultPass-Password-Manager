package my.edu.utar.assignment_1;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PasswordItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract PasswordDao passwordDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "vault_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Not recommended for production, but simple for learning
                    .build();
        }
        return instance;
    }
}
