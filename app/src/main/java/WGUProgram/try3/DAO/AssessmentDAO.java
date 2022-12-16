package WGUProgram.try3.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import WGUProgram.try3.Entities.Assessment;

@Dao
public interface AssessmentDAO {

    @Insert
    void insert(Assessment... assessments);

    @Update
    void update(Assessment... assessments);

    @Delete
    void delete(Assessment... assessments);

    @Query("SELECT * FROM Assessment_Table")
    List<Assessment> getAll();
}
