package WGUProgram.try3.DB;

import android.app.Application;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import WGUProgram.try3.DAO.AssessmentDAO;
import WGUProgram.try3.DAO.CourseDAO;
import WGUProgram.try3.DAO.TermDAO;
import WGUProgram.try3.Entities.Assessment;
import WGUProgram.try3.Entities.Course;
import WGUProgram.try3.Entities.Term;

public class Repository {
    private CourseDAO mCourseDAO;
    private TermDAO mTermDAO;
    private AssessmentDAO mAssessmentDAO;

    private List<Course> mGetAllCourses;
    private List<Term> mGetAllTerms;
    private List<Assessment> mGetAllAssessments;

    private static int NUMBER_OF_THREADS=4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        SchedulerDB db = SchedulerDB.getDatabase(application);
        mAssessmentDAO=db.getAssessmentDAO();
        mCourseDAO=db.getCourseDAO();
        mTermDAO=db.getTermDAO();
    }

    public List<Term>getAllTerms(){
        databaseExecutor.execute(()->{
            mGetAllTerms=mTermDAO.getAll();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mGetAllTerms;
    }

    public List<Course>getAllCourses(){
        databaseExecutor.execute(()->{
            mGetAllCourses=mCourseDAO.getAll();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mGetAllCourses;
    }

    public List<Assessment>getAllAssessments(){
        databaseExecutor.execute(()->{
            mGetAllAssessments=mAssessmentDAO.getAll();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mGetAllAssessments;
    }

    public void insertAssessment(Assessment assessment) {
        databaseExecutor.execute(()->{
          mAssessmentDAO.insert(assessment);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insertCourse(Course course) {
        databaseExecutor.execute(()->{
            mCourseDAO.insert(course);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insertTerm(Term term) {
        databaseExecutor.execute(()->{
            mTermDAO.insert(term);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateAssessment(Assessment assessment) {
        databaseExecutor.execute(()->{
            mAssessmentDAO.update(assessment);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateCourse(Course course) {
        databaseExecutor.execute(()->{
            mCourseDAO.update(course);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateTerm(Term term) {
        databaseExecutor.execute(()->{
            mTermDAO.update(term);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteAssessment(Assessment assessment) {
        databaseExecutor.execute(()->{
            mAssessmentDAO.delete(assessment);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteCourse(Course course) {
        databaseExecutor.execute(()->{
            mCourseDAO.delete(course);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteTerm(Term term) {
        databaseExecutor.execute(()->{
            mTermDAO.delete(term);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
