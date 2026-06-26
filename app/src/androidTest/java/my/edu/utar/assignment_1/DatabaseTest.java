package my.edu.utar.assignment_1;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private PasswordDao passwordDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Use an in-memory database for testing so data is not saved permanently
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        passwordDao = db.passwordDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        PasswordItem item = new PasswordItem("TestSite", "TestUser", "TestPass", "test.com", "Browser", 0, 0);
        passwordDao.insert(item);
        List<PasswordItem> allItems = passwordDao.getAll();
        assertEquals("TestSite", allItems.get(0).getName());
    }
}
