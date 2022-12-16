package WGUProgram.try3.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import WGUProgram.try3.Entities.Course;

@Dao
public interface CourseDAO {

    @Insert
    void insert(Course... courses);

    @Update
    void update(Course... courses);

    @Delete
    void delete(Course... courses);

    @Query("SELECT * FROM Course_Table")
    List<Course> getAll();
}
