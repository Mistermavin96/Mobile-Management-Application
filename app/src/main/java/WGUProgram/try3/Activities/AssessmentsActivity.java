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

import WGUProgram.try3.Adapters.ExtendedAssessmentAdapter;
import WGUProgram.try3.Adapters.SimpleAssessmentAdapter;
import WGUProgram.try3.DB.Repository;
import WGUProgram.try3.Entities.Assessment;
import WGUProgram.try3.Entities.Course;
import WGUProgram.try3.Functions.AlarmSetter;
import WGUProgram.try3.R;

public class AssessmentsActivity extends AppCompatActivity {

    Repository repo;
    List<Assessment> assessmentList;
    List<Course> courseList;
    SimpleAssessmentAdapter sAdapter;
    ExtendedAssessmentAdapter eAdapter;
    String adapterType = "Simple";
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateSetListener;
    DatePickerDialog.OnDateSetListener startDateSetListener;
    final String format = "MM/dd/yy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments);
        Toolbar myToolbar = findViewById(R.id.tlb_ActionBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assessments");
        setupAdapterVariables();
        setRecycler(adapterType);
    }

    private void setupAdapterVariables() {
        repo = new Repository(getApplication());
        assessmentList = repo.getAllAssessments();
        courseList = repo.getAllCourses();
        sAdapter = new SimpleAssessmentAdapter(assessmentList, courseList, repo);
        eAdapter = new ExtendedAssessmentAdapter(assessmentList, repo, courseList);
    }

    public void setRecycler(String adapterType) {
        RecyclerView recyclerView = findViewById(R.id.lst_Assessment);
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

    public void setData() {
        assessmentList.clear();
        assessmentList.addAll(repo.getAllAssessments());
        sAdapter.notifyDataSetChanged();
        eAdapter.notifyDataSetChanged();
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.pop_up_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
                if (id == R.id.addItem) {
                    if (courseList.size() == 0) {
                        Toast.makeText(this, "Please create a course first", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(AssessmentsActivity.this);

                    LayoutInflater LI = LayoutInflater.from(AssessmentsActivity.this);
                    View popupView = LI.inflate(R.layout.add_assessment, null);

                    TextView mAssessmentTitle = popupView.findViewById(R.id.txt_editAssessmentTitle);
                    TextView mAssessmentStart = popupView.findViewById(R.id.lbl_editStartAssessment);
                    TextView mAssessmentEnd = popupView.findViewById(R.id.lbl_editEndAssessment);
                    ImageButton mBtnAssessmentStartDate = popupView.findViewById(R.id.btn_AssessmentStartDate);
                    ImageButton mBtnAssessmentEndDate = popupView.findViewById(R.id.btn_AssessmentEndDate);
                    Spinner typeSpinner = popupView.findViewById(R.id.spn_typeAssessment);
                    Spinner courseSpinner = popupView.findViewById(R.id.spn_AssessmentCourse);
                    SwitchCompat mAssessmentAlert1 = popupView.findViewById(R.id.swt_AssessmentAlert);
                    SwitchCompat mAssessmentAlert2 = popupView.findViewById(R.id.swt_AssessmentAlert2);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);

                    builder.setTitle("Please enter the assessment information");
                    builder.setView(popupView);
                    builder.setPositiveButton("Create", (dialogInterface, i) -> {
                            String Title = mAssessmentTitle.getText().toString();
                            String Start = mAssessmentStart.getText().toString().substring(12);
                            String End = mAssessmentEnd.getText().toString().substring(10);
                            String Type = typeSpinner.getSelectedItem().toString();
                            Course course = (Course) courseSpinner.getSelectedItem();
                            Boolean Alert1 = mAssessmentAlert1.isChecked();
                            Boolean Alert2 = mAssessmentAlert2.isChecked();

                            Assessment tempAssessment = new Assessment(0, course.getId(), Title, Start, End, Type, Alert1, Alert2);

                        if (Alert1) {
                            tempAssessment.setAlarm1(AlarmSetter.generateNewAlarm(this,
                                    simpleDateFormat,
                                    Start,
                                    "The following assessment is starting today: " + Title));
                        }

                        if (Alert2) {
                            tempAssessment.setAlarm2(AlarmSetter.generateNewAlarm(this,
                                    simpleDateFormat,
                                    End,
                                    "The following assessment is ending today: " + Title));
                        }

                            repo.insertAssessment(tempAssessment);
                            setData();
                    });

                    builder.setNegativeButton("Cancel", (dialogInterface, i) -> {});

                    AlertDialog dialog = builder.create();

                    ArrayAdapter<Course> courseAdapter = new ArrayAdapter<>(AssessmentsActivity.this, android.R.layout.simple_spinner_item);

                    courseAdapter.clear();
                    for (int i = 0; i < courseList.size(); i++) {
                        courseAdapter.add(courseList.get(i));
                    }

                    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    courseSpinner.setAdapter(courseAdapter);

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AssessmentsActivity.this, R.array.AssessmentTypeArray, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    typeSpinner.setAdapter(adapter);

                    dialog.show();

                    mBtnAssessmentEndDate.setOnClickListener(view12 -> new DatePickerDialog(AssessmentsActivity.this,
                                endDateSetListener,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show());

                    mBtnAssessmentStartDate.setOnClickListener(view1 -> new DatePickerDialog(AssessmentsActivity.this,
                                startDateSetListener,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show());

                    startDateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mAssessmentStart.setText("Start Date: " + simpleDateFormat.format(calendar.getTime()));
                    };

                    endDateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mAssessmentEnd.setText("End Date: " + simpleDateFormat.format(calendar.getTime()));
                    };

                    return true;
                }
                else if (id == R.id.extendView) {
                    if (adapterType.equals("Simple")) { adapterType = "Extended"; } else { adapterType = "Simple"; }
                    setRecycler(adapterType);
                    return true; } else { return false; }});
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}