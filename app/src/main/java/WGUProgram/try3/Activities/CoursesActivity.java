package WGUProgram.try3.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import WGUProgram.try3.Adapters.ExtendedCourseAdapter;
import WGUProgram.try3.Adapters.SimpleCourseAdapter;
import WGUProgram.try3.DB.Repository;
import WGUProgram.try3.Entities.Assessment;
import WGUProgram.try3.Entities.Course;
import WGUProgram.try3.Entities.Term;
import WGUProgram.try3.Functions.AlarmSetter;
import WGUProgram.try3.R;

public class CoursesActivity extends AppCompatActivity {

    public Repository repo;
    public List<Course> courseList;
    public List<Term> termList;
    public List<Assessment> assessmentList;
    public String adapterType = "Simple";
    public SimpleCourseAdapter sAdapter;
    public ExtendedCourseAdapter eAdapter;
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateSetListener;
    DatePickerDialog.OnDateSetListener startDateSetListener;
    final String format = "MM/dd/yy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        Toolbar myToolbar = findViewById(R.id.tlb_ActionBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Courses");
        setupAdapterVariables();
        setRecycler(adapterType);
    }

    public void setupAdapterVariables() {
        repo = new Repository(getApplication());
        courseList = repo.getAllCourses();
        termList = repo.getAllTerms();
        assessmentList = repo.getAllAssessments();
        sAdapter = new SimpleCourseAdapter(courseList, assessmentList, repo, termList);
        eAdapter = new ExtendedCourseAdapter(courseList, termList, assessmentList, repo);
    }

    public void setRecycler(String adapterType) {
        RecyclerView recyclerView = findViewById(R.id.lst_Course);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), 1);
        recyclerView.addItemDecoration(divider);
        if (Objects.equals(adapterType, "Simple")) {
            recyclerView.setAdapter(sAdapter);
        } else {
            recyclerView.setAdapter(eAdapter);
        }
    }

    public void updateData() {
        courseList.clear();
        courseList.addAll(repo.getAllCourses());
        sAdapter.notifyDataSetChanged();
        eAdapter.notifyDataSetChanged();
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.pop_up_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.addItem) {
                if (termList.size() == 0) {
                    Toast.makeText(this, "Please create a term first", Toast.LENGTH_SHORT).show();
                    return true;
                }

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CoursesActivity.this);

                LayoutInflater LI = LayoutInflater.from(CoursesActivity.this);
                View popupView = LI.inflate(R.layout.add_course, null);

                TextView mCourseTitle = popupView.findViewById(R.id.txt_CourseTitle);
                TextView mCourseStart = popupView.findViewById(R.id.lbl_CourseStartDate);
                TextView mCourseEnd = popupView.findViewById(R.id.lbl_CourseEndDate);
                TextView mCourseNotes = popupView.findViewById(R.id.txt_CourseNotes);
                TextView mInstructorName = popupView.findViewById(R.id.txt_InstructorName);
                TextView mInstructorPhone = popupView.findViewById(R.id.txt_InstructorPhone);
                TextView mInstructorEmail = popupView.findViewById(R.id.txt_InstructorEmail);
                ImageButton mBtnCourseStart = popupView.findViewById(R.id.btn_CourseStartDate);
                ImageButton mBtnCourseEnd = popupView.findViewById(R.id.btn_CourseEndDate);
                Spinner termSpinner = popupView.findViewById(R.id.spn_courseTerm);
                Spinner courseSpinner = popupView.findViewById(R.id.spn_statusCourse);
                SwitchCompat mCourseAlert = popupView.findViewById(R.id.swt_CourseStartAlert);
                SwitchCompat mCourseAlert2 = popupView.findViewById(R.id.swt_CourseEndAlert);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);

                alertBuilder.setTitle("Please enter the course information");
                alertBuilder.setView(popupView);
                alertBuilder.setPositiveButton("Create", (dialogInterface, i) -> {
                    String Title = mCourseTitle.getText().toString();
                    String Start = mCourseStart.getText().toString().substring(12);
                    String End = mCourseEnd.getText().toString().substring(10);
                    String Notes = mCourseNotes.getText().toString();
                    String iName = mInstructorName.getText().toString();
                    String iPhone = mInstructorPhone.getText().toString();
                    String iEmail = mInstructorEmail.getText().toString();
                    Term term = (Term) termSpinner.getSelectedItem();
                    String Status = courseSpinner.getSelectedItem().toString();
                    Boolean Alert = mCourseAlert.isChecked();
                    Boolean Alert2 = mCourseAlert2.isChecked();

                    Course tempCourse = new Course(0, term.getId(), Title, Start, End, Status, Notes, iName, iPhone, iEmail, Alert, Alert2);

                    if (Alert) {
                        tempCourse.setAlarm1(AlarmSetter.generateNewAlarm(this,
                                simpleDateFormat,
                                Start,
                                "The following course is starting today: " + Title));
                    }

                    if (Alert2) {
                        tempCourse.setAlarm2(AlarmSetter.generateNewAlarm(this,
                                simpleDateFormat,
                                End,
                                "The following course is ending today"));
                    }

                    repo.insertCourse(tempCourse);
                    updateData();
                });
                alertBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> {});

                AlertDialog dialog = alertBuilder.create();

                ArrayAdapter<Term> termAdapter = new ArrayAdapter<>(CoursesActivity.this, android.R.layout.simple_spinner_item);

                termAdapter.clear();

                for (int i = 0; i < termList.size(); i++) {
                    termAdapter.add(termList.get(i));
                }

                termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                termSpinner.setAdapter(termAdapter);

                ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(CoursesActivity.this, R.array.CourseStatusArray, android.R.layout.simple_spinner_item);
                statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                courseSpinner.setAdapter(statusAdapter);

                dialog.show();

                mBtnCourseEnd.setOnClickListener(view1 -> new DatePickerDialog(CoursesActivity.this,
                            endDateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show());

                mBtnCourseStart.setOnClickListener(view12 -> new DatePickerDialog(CoursesActivity.this,
                            startDateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show());

                startDateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mCourseStart.setText("Start Date: " + simpleDateFormat.format(calendar.getTime()));
                };

                endDateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mCourseEnd.setText("End Date: " + simpleDateFormat.format(calendar.getTime()));
                };

                return true;
            }

            else if (id == R.id.extendView) {
                if (adapterType.equals("Simple")) { adapterType = "Extended"; } else { adapterType = "Simple"; }
                setRecycler(adapterType);
                return true;} else { return false; } });
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_options) {
            showPopupMenu(findViewById(R.id.action_options));
            return true;
        } else { return super.onOptionsItemSelected(item); }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}