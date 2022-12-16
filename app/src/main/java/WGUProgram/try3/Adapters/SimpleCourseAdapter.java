package WGUProgram.try3.Adapters;

import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import WGUProgram.try3.DB.Repository;
import WGUProgram.try3.Entities.Assessment;
import WGUProgram.try3.Entities.Course;
import WGUProgram.try3.Entities.Term;
import WGUProgram.try3.Functions.AlarmSetter;
import WGUProgram.try3.R;

public class SimpleCourseAdapter extends RecyclerView.Adapter<SimpleCourseAdapter.genericViewHolder> {
    private List<Course> CourseList;
    private final List<Assessment> assessmentList;
    private final Repository Repo;
    private final List<Term> TermList;
    String format = "MM/dd/yy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateSetListener;
    DatePickerDialog.OnDateSetListener startDateSetListener;

    public SimpleCourseAdapter(List<Course> courseList, List<Assessment> assessmentList, Repository repo, List<Term> termList) {
        this.CourseList = courseList;
        this.assessmentList = assessmentList;
        this.Repo = repo;
        this.TermList = termList;
    }

    public static class genericViewHolder extends RecyclerView.ViewHolder {
        TextView txt_Title;
        ImageButton btn_Delete;
        ImageButton btn_Edit;

        public genericViewHolder(final View view) {
            super(view);
            txt_Title = view.findViewById(R.id.genericTitleItem);
            btn_Delete = view.findViewById(R.id.btn_genericItemDelete);
            btn_Edit = view.findViewById(R.id.btn_GenericItemEdit);
        }
    }

    @NonNull
    @Override
    public genericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.generic_recycler_item, parent, false);
        return new genericViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull SimpleCourseAdapter.genericViewHolder holder, int position) {
        String Title = "Course: " + CourseList.get(position).getTitle();
        holder.txt_Title.setText(Title);

        holder.btn_Delete.setOnClickListener(view -> {
            holder.btn_Delete.setEnabled(false);
            for (Assessment a : assessmentList) {
                if (a.getCourseId() == CourseList.get(holder.getAdapterPosition()).getId()) {
                    Toast.makeText(view.getContext(), "Please delete related assessments", Toast.LENGTH_SHORT).show();
                    holder.btn_Delete.setEnabled(true);
                    return;
                }
            }

            if (CourseList.get(holder.getAdapterPosition()).getAlert1()) {
                AlarmSetter.destroyAlarm(CourseList.get(holder.getAdapterPosition()).getAlarm1(), view.getContext());
            }

            if (CourseList.get(holder.getAdapterPosition()).getAlert2()) {
                AlarmSetter.destroyAlarm(CourseList.get(holder.getAdapterPosition()).getAlarm2(), view.getContext());
            }

            Repo.deleteCourse(CourseList.get(holder.getAdapterPosition()));
            Toast.makeText(view.getContext(), "Course successfully deleted", Toast.LENGTH_SHORT).show();
            holder.btn_Delete.setEnabled(true);
            this.CourseList = this.Repo.getAllCourses();
            this.notifyDataSetChanged();
        });

        holder.btn_Edit.setOnClickListener( view -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext());

            LayoutInflater LI = LayoutInflater.from(view.getContext());
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

            Course updatedCourse = CourseList.get(holder.getAdapterPosition());

            alertBuilder.setTitle("Please enter the course information");
            alertBuilder.setView(popupView);

            mCourseTitle.setText(updatedCourse.getTitle());
            mCourseStart.setText("Start Date: " + updatedCourse.getStartDate());
            mCourseEnd.setText("End Date: " + updatedCourse.getEndDate());
            mCourseNotes.setText(updatedCourse.getNote());
            mInstructorName.setText(updatedCourse.getInstructorName());
            mInstructorPhone.setText(updatedCourse.getInstructorPhoneNumber());
            mInstructorEmail.setText(updatedCourse.getInstructorEmailAddress());
            mCourseAlert.setChecked(updatedCourse.getAlert1());
            mCourseAlert2.setChecked(updatedCourse.getAlert2());

            alertBuilder.setPositiveButton("Edit", (dialogInterface, i) -> {
                String CourseTitle = mCourseTitle.getText().toString();
                String CourseStart = mCourseStart.getText().toString().substring(12);
                String CourseEnd = mCourseEnd.getText().toString().substring(10);
                String CourseNotes = mCourseNotes.getText().toString();
                String CourseIName = mInstructorName.getText().toString();
                String CourseIPhone = mInstructorPhone.getText().toString();
                String CourseIEmail = mInstructorEmail.getText().toString();
                Term term = (Term) termSpinner.getSelectedItem();
                String CourseStatus = courseSpinner.getSelectedItem().toString();
                boolean CourseAlert = mCourseAlert.isChecked();
                boolean CourseAlert2 = mCourseAlert2.isChecked();

                if (CourseAlert && !updatedCourse.getAlert1()) {
                    updatedCourse.setAlarm1(AlarmSetter.generateNewAlarm(view.getContext(),
                            simpleDateFormat,
                            CourseStart,
                            "The following course is starting today: " + CourseTitle));
                } else if (!CourseAlert && updatedCourse.getAlert1()) {
                    AlarmSetter.destroyAlarm(updatedCourse.getAlarm1(), view.getContext());
                }

                if (CourseAlert2 && !updatedCourse.getAlert1()) {
                    updatedCourse.setAlarm2(AlarmSetter.generateNewAlarm(view.getContext(),
                            simpleDateFormat,
                            CourseEnd,
                            "The following course is ending today: " + CourseTitle));
                } else if (!CourseAlert2 && updatedCourse.getAlert2()) {
                    AlarmSetter.destroyAlarm(updatedCourse.getAlarm2(), view.getContext());
                }

                updatedCourse.setTitle(CourseTitle);
                updatedCourse.setStartDate(CourseStart);
                updatedCourse.setEndDate(CourseEnd);
                if (!CourseNotes.equals("")) {
                    updatedCourse.setNote(CourseNotes);
                }
                updatedCourse.setAlert1(CourseAlert);
                updatedCourse.setAlert2(CourseAlert2);
                updatedCourse.setInstructorName(CourseIName);
                updatedCourse.setInstructorPhoneNumber(CourseIPhone);
                updatedCourse.setInstructorEmailAddress(CourseIEmail);
                updatedCourse.setStatus(CourseStatus);
                updatedCourse.setTermId(term.getId());


                Repo.updateCourse(updatedCourse);
                this.notifyDataSetChanged();
            });
            alertBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            });

            AlertDialog dialog = alertBuilder.create();

            ArrayAdapter<Term> termAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item);

            termAdapter.clear();


            for (int i = 0; i < TermList.size(); i++) {
                termAdapter.add(TermList.get(i));
            }

            termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            termSpinner.setAdapter(termAdapter);

            ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.CourseStatusArray, android.R.layout.simple_spinner_item);
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courseSpinner.setAdapter(statusAdapter);

            dialog.show();

            for (int i = 0; i < courseSpinner.getCount(); i++) {
                if (courseSpinner.getItemAtPosition(i).equals(updatedCourse.getStatus())) {
                    courseSpinner.setSelection(i);
                }
            }

            for (int i = 0; i < termSpinner.getCount(); i++) {
                Term test = (Term) termSpinner.getItemAtPosition(i);
                if (test.getId() == (updatedCourse.getTermId())) {
                    termSpinner.setSelection(i);
                }
            }

            mBtnCourseEnd.setOnClickListener(view1 -> new DatePickerDialog(view.getContext(),
                    endDateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show());

            mBtnCourseStart.setOnClickListener(view12 -> new DatePickerDialog(view.getContext(),
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
        });
    }

    @Override
    public int getItemCount() {
        return CourseList.size();
    }
}