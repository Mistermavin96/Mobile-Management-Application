package WGUProgram.try3.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import WGUProgram.try3.DAO.AssessmentDAO;
import WGUProgram.try3.DAO.CourseDAO;
import WGUProgram.try3.DAO.TermDAO;
import WGUProgram.try3.Entities.Assessment;
import WGUProgram.try3.Entities.Course;
import WGUProgram.try3.Entities.Term;

@Database(entities = { Assessment.class, Course.class, Term.class}, version = 12, exportSchema = false)
public abstract class SchedulerDB extends RoomDatabase {

    public abstract AssessmentDAO getAssessmentDAO();
    public abstract CourseDAO getCourseDAO();
    public abstract TermDAO getTermDAO();

    private static volatile SchedulerDB INSTANCE;

    static SchedulerDB getDatabase(final Context context) {
        if (INSTANCE==null) {
            synchronized (SchedulerDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SchedulerDB.class,
                                    "mySchedulingDatabase")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }

        }
        return INSTANCE;
    }
}
