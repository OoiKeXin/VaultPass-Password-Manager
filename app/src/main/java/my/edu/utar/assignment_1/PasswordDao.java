package my.edu.utar.assignment_1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PasswordDao {
    @Query("SELECT * FROM passwords ORDER BY name ASC")
    List<PasswordItem> getAll();

    @Insert
    void insert(PasswordItem item);

    @Update
    void update(PasswordItem item);

    @Delete
    void delete(PasswordItem item);
}
