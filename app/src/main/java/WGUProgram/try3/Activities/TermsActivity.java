package WGUProgram.try3.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import WGUProgram.try3.Adapters.ExtendedTermAdapter;
import WGUProgram.try3.Adapters.SimpleTermAdapter;
import WGUProgram.try3.DB.Repository;
import WGUProgram.try3.Entities.Course;
import WGUProgram.try3.Entities.Term;
import WGUProgram.try3.Functions.AlarmSetter;
import WGUProgram.try3.R;

public class TermsActivity extends AppCompatActivity {

    public Repository repo;
    public List<Term> termList;
    public List<Course> courseList;
    public SimpleTermAdapter sAdapter;
    public ExtendedTermAdapter eAdapter;
    String adapterType = "Simple";
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateSetListener;
    DatePickerDialog.OnDateSetListener startDateSetListener;
    final String format = "MM/dd/yy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar myToolbar = findViewById(R.id.tlb_ActionBar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Terms");
        setupAdapterVariables();
        setRecycler(adapterType);
    }

    public void setupAdapterVariables() {
        repo = new Repository(getApplication());
        termList = repo.getAllTerms();
        courseList = repo.getAllCourses();
        sAdapter = new SimpleTermAdapter(termList, courseList, repo);
        eAdapter = new ExtendedTermAdapter(termList, courseList, repo);
    }

    public void setRecycler(String adapterType) {
        RecyclerView recyclerView = findViewById(R.id.lst_Term);
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
        termList.clear();
        termList.addAll(repo.getAllTerms());
        sAdapter.notifyDataSetChanged();
        eAdapter.notifyDataSetChanged();
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.pop_up_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.addItem) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TermsActivity.this);

                LayoutInflater LI = LayoutInflater.from(TermsActivity.this);
                View popupView = LI.inflate(R.layout.add_term, null);

                EditText mTxtEditTitleTerm = popupView.findViewById(R.id.txt_editTitleTerm);
                TextView mLblEndTerm = popupView.findViewById(R.id.lbl_editEndTerm);
                TextView mLblStartTerm = popupView.findViewById(R.id.lbl_editStartTerm);
                ImageButton mTermEndDate = popupView.findViewById(R.id.btn_TermEndDate);
                ImageButton mTermStartDate = popupView.findViewById(R.id.btn_TermStartDate);
                SwitchCompat mTermAlert = popupView.findViewById(R.id.swt_TermStartAlert);
                SwitchCompat mTermAlert2 = popupView.findViewById(R.id.swt_TermEndAlert);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);

                builder.setTitle("Please enter the Term information");
                builder.setView(popupView);
                builder.setPositiveButton("Create", (dialogInterface, i) -> {
                    String Title = mTxtEditTitleTerm.getText().toString();
                    String Start = mLblStartTerm.getText().toString().substring(12);
                    String End = mLblEndTerm.getText().toString().substring(10);
                    Boolean Alert = mTermAlert.isChecked();
                    Boolean Alert2 = mTermAlert2.isChecked();
                        Term tempTerm = new Term(0,
                                Title,
                                Start,
                                End,
                                Alert,
                                Alert2);

                    if (Alert) {
                        tempTerm.setAlarm1(AlarmSetter.generateNewAlarm(this,
                                simpleDateFormat,
                                Start,
                                "The following term is starting today: " + Title));
                    }

                    if (Alert2) {
                        tempTerm.setAlarm2(AlarmSetter.generateNewAlarm(this,
                                simpleDateFormat,
                                End,
                                "The following term is ending today: " + Title));
                    }

                        repo.insertTerm(tempTerm);
                        updateData();


                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {});

                AlertDialog dialog = builder.create();

                dialog.show();

                mTermEndDate.setOnClickListener(view12 -> new DatePickerDialog(TermsActivity.this,
                            endDateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show());

                mTermStartDate.setOnClickListener(view1 -> new DatePickerDialog(TermsActivity.this,
                            startDateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show());

                startDateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mLblStartTerm.setText("Start Date: " + simpleDateFormat.format(calendar.getTime()));
                };

                endDateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mLblEndTerm.setText("End Date: " + simpleDateFormat.format(calendar.getTime()));
                };

                return true;
            }

            else if (id == R.id.extendView) {
                if (adapterType.equals("Simple")) { adapterType = "Extended"; } else { adapterType = "Simple"; }
                setRecycler(adapterType);
                return true;
            } else { return false; }});
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
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