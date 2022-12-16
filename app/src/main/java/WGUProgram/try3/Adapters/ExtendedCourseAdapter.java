package WGUProgram.try3.Adapters;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.Objects;

import WGUProgram.try3.DB.Repository;
import WGUProgram.try3.Entities.Assessment;
import WGUProgram.try3.Entities.Course;
import WGUProgram.try3.Entities.Term;
import WGUProgram.try3.Functions.AlarmSetter;
import WGUProgram.try3.R;

public class ExtendedCourseAdapter extends RecyclerView.Adapter<ExtendedCourseAdapter.courseViewHolder> {

    private List<Course> courseList;
    private final List<Term> TermList;
    private final List<Assessment> assessmentList;
    private final Repository Repo;
    private final SmsManager smsManager = SmsManager.getDefault();
    String format = "MM/dd/yy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateSetListener;
    DatePickerDialog.OnDateSetListener startDateSetListener;

    public ExtendedCourseAdapter(List<Course> CourseList, List<Term> termList, List<Assessment> assessmentList, Repository repo) {
        this.courseList = CourseList;
        this.TermList = termList;
        this.assessmentList = assessmentList;
        Repo = repo;
    }

    public static class courseViewHolder extends RecyclerView.ViewHolder {
        TextView txt_ItemCourseTitle;
        TextView txt_ItemCourseStart;
        TextView txt_ItemCourseEnd;
        TextView txt_ItemCourseStatus;
        TextView txt_ItemCourseNotes;
        TextView txt_CourseAssessmentList;

        TextView txt_ItemCourseInstructorName;
        TextView txt_ItemCourseInstructorPhone;
        TextView txt_ItemCourseInstructorEmail;

        ImageButton btn_Delete;
        ImageButton btn_Edit;
        ImageButton btn_SMS;

        public courseViewHolder(final View view) {
            super (view);
            txt_ItemCourseTitle = view.findViewById(R.id.txt_ItemCourseTitle);
            txt_ItemCourseStart = view.findViewById(R.id.txt_ItemCourseStart);
            txt_ItemCourseEnd = view.findViewById(R.id.txt_ItemCourseEnd);
            txt_ItemCourseStatus = view.findViewById(R.id.txt_ItemCourseStatus);
            txt_ItemCourseNotes = view.findViewById(R.id.txt_ItemCourseNotes);
            txt_CourseAssessmentList = view.findViewById(R.id.txt_CourseAssessmentList);

            txt_ItemCourseInstructorName = view.findViewById(R.id.txt_ItemCourseInstructorName);
            txt_ItemCourseInstructorPhone = view.findViewById(R.id.txt_ItemCourseInstructorPhone);
            txt_ItemCourseInstructorEmail = view.findViewById(R.id.txt_ItemCourseInstructorEmail);

            btn_Delete = view.findViewById(R.id.btn_ItemDeleteCourse);
            btn_Edit = view.findViewById(R.id.btn_itemEditCourse);
            btn_SMS = view.findViewById(R.id.btn_ItemMailNotes);
        }
    }



    @NonNull
    @Override
    public courseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new courseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull courseViewHolder holder, int position) {
        String Title = "Course: " + courseList.get(position).getTitle();
        String Start = "Start Date: " + courseList.get(position).getStartDate();
        String End = "End Date: " + courseList.get(position).getEndDate();
        String Status = "Status: " + courseList.get(position).getStatus();
        String Notes = "Notes: " + courseList.get(position).getNote();
        String Name = "Name: " + courseList.get(position).getInstructorName();
        String Phone = "Phone Number: " + courseList.get(position).getInstructorPhoneNumber();
        String Email = "E-mail: " + courseList.get(position).getInstructorEmailAddress();

        StringBuilder Assessments = new StringBuilder("Assessments: ");

        // Runs through each assessment in the list and compares it's associated courseID to the current courseID, appending if found.
        for (Assessment a : assessmentList) {
            if (a.getCourseId() == courseList.get(position).getId()) {
                Assessments.append(a.getTitle()).append(" - ");
            }
        }

        // This checks that there are assessments assigned, and deletes the unnecessary dash. If there are no assessments, it removes the text.
        if (Assessments.length() > 13 && Assessments.charAt(Assessments.length() - 2) == '-') {
            Assessments.deleteCharAt(Assessments.length() - 2);
        } else if (Assessments.length() == 13) {
            Assessments.append("None");
        }

        holder.txt_ItemCourseTitle.setText(Title);
        holder.txt_ItemCourseStart.setText(Start);
        holder.txt_ItemCourseEnd.setText(End);
        holder.txt_ItemCourseStatus.setText(Status);
        if (!Objects.equals(Notes, "")) {
            holder.txt_ItemCourseNotes.setText(Notes);
        } else {
            holder.txt_ItemCourseNotes.setText("");
        }
        holder.txt_ItemCourseInstructorName.setText(Name);
        holder.txt_ItemCourseInstructorPhone.setText(Phone);
        holder.txt_ItemCourseInstructorEmail.setText(Email);

        holder.txt_CourseAssessmentList.setText(Assessments);

        holder.btn_Delete.setOnClickListener(view -> {
            holder.btn_Delete.setEnabled(false);
            for (Assessment a : assessmentList) {
                if (a.getCourseId() == courseList.get(holder.getAdapterPosition()).getId()) {
                    Toast.makeText(view.getContext(), "Please delete related assessments", Toast.LENGTH_SHORT).show();
                    holder.btn_Delete.setEnabled(true);
                    return;
                }
            }

            if (courseList.get(holder.getAdapterPosition()).getAlert1()) {
                AlarmSetter.destroyAlarm(courseList.get(holder.getAdapterPosition()).getAlarm1(), view.getContext());
            }

            if (courseList.get(holder.getAdapterPosition()).getAlert2()) {
                AlarmSetter.destroyAlarm(courseList.get(holder.getAdapterPosition()).getAlarm2(), view.getContext());
            }

            Repo.deleteCourse(courseList.get(holder.getAdapterPosition()));
            Toast.makeText(view.getContext(), "Course successfully deleted", Toast.LENGTH_SHORT).show();
            holder.btn_Delete.setEnabled(true);
            this.courseList = this.Repo.getAllCourses();
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

            Course updatedCourse = courseList.get(holder.getAdapterPosition());

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
                            "The following course is starting today: "));
                } else if (!CourseAlert && updatedCourse.getAlert1()) {
                    AlarmSetter.destroyAlarm(updatedCourse.getAlarm1(), view.getContext());
                }

                if (CourseAlert2 && !updatedCourse.getAlert2()) {
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

        holder.btn_SMS.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Please enter the phone number to send notes to");
            final EditText input = new EditText(view.getContext());
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            builder.setView(input);

            builder.setPositiveButton("Send", (dialogInterface, i) -> {
                try {
                    Intent sendIntent=new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,courseList.get(holder.getAdapterPosition()).getNote());
                    sendIntent.putExtra(Intent.EXTRA_TITLE,"Message Title");
                    sendIntent.setType("text/plain");
                    Intent shareIntent=Intent.createChooser(sendIntent,null);
                    view.getContext().startActivity(shareIntent);

                    //Old method, works with service, but does not populate without service.
                    /*smsManager.sendTextMessage(input.getText().toString(), null, courseList.get(holder.getAdapterPosition()).getNote(), null, null);
                    Toast.makeText(view.getContext(), "Message sent!", Toast.LENGTH_SHORT).show();*/
                } catch(Exception e) {
                    Toast.makeText(view.getContext(), "Message could not be sent", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {});

            builder.show();
        });
    }


    @Override
    public int getItemCount() {
        return courseList.size();
    }
}
