package WGUProgram.try3.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import WGUProgram.try3.Entities.Term;

@Dao
public interface TermDAO {

    @Insert
    void insert(Term... term);

    @Update
    void update(Term... term);

    @Delete
    void delete(Term... term);

    @Query("SELECT * FROM Term_Table")
    List<Term> getAll();
}
