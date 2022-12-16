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
import WGUProgram.try3.Functions.AlarmSetter;
import WGUProgram.try3.R;

public class ExtendedAssessmentAdapter extends RecyclerView.Adapter<ExtendedAssessmentAdapter.assessmentViewHolder> {

    private List<Assessment> assessmentList;
    private final Repository Repo;
    private final List<Course> courseList;
    String format = "MM/dd/yy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateSetListener;
    DatePickerDialog.OnDateSetListener startDateSetListener;

    public ExtendedAssessmentAdapter(List<Assessment> AssessmentList, Repository Repo, List<Course> courseList) {
        this.assessmentList = AssessmentList;
        this.Repo = Repo;
        this.courseList = courseList;
    }

    public static class assessmentViewHolder extends RecyclerView.ViewHolder {
        TextView txt_ItemAssessmentTitle;
        TextView txt_ItemAssessmentStart;
        TextView txt_ItemAssessmentEnd;
        TextView txt_ItemAssessmentType;
        ImageButton btn_Delete;
        ImageButton btn_Edit;

        public assessmentViewHolder(final View view) {
            super (view);
            txt_ItemAssessmentTitle = view.findViewById(R.id.txt_ItemAssessmentTitle);
            txt_ItemAssessmentStart = view.findViewById(R.id.txt_ItemAssessmentStart);
            txt_ItemAssessmentEnd = view.findViewById(R.id.txt_ItemAssessmentEnd);
            txt_ItemAssessmentType = view.findViewById(R.id.txt_ItemAssessmentType);
            btn_Delete = view.findViewById(R.id.btn_ItemDeleteAssessment);
            btn_Edit = view.findViewById(R.id.btn_ItemEditAssessment);
        }
    }

    @NonNull
    @Override
    public ExtendedAssessmentAdapter.assessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_item, parent, false);
        return new ExtendedAssessmentAdapter.assessmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ExtendedAssessmentAdapter.assessmentViewHolder holder, int position) {
        String Title = "Assessment: " + assessmentList.get(position).getTitle();
        String Start = "Start Date:" + assessmentList.get(position).getStartDate();
        String End = "End Date: " + assessmentList.get(position).getEndDate();
        String Type = "Assessment Type: " + assessmentList.get(position).getType();

        holder.txt_ItemAssessmentTitle.setText(Title);
        holder.txt_ItemAssessmentStart.setText(Start);
        holder.txt_ItemAssessmentEnd.setText(End);
        holder.txt_ItemAssessmentType.setText(Type);

        holder.btn_Delete.setOnClickListener(view -> {
            holder.btn_Delete.setEnabled(false);

            if (assessmentList.get(holder.getAdapterPosition()).getAlert1()) {
                AlarmSetter.destroyAlarm(assessmentList.get(holder.getAdapterPosition()).getAlarm1(), view.getContext());
            }
            if (assessmentList.get(holder.getAdapterPosition()).getAlert2()) {
                AlarmSetter.destroyAlarm(assessmentList.get(holder.getAdapterPosition()).getAlarm2(), view.getContext());
            }

            Repo.deleteAssessment(assessmentList.get(holder.getAdapterPosition()));
            Toast.makeText(view.getContext(), "Course successfully deleted", Toast.LENGTH_SHORT).show();
            holder.btn_Delete.setEnabled(true);
            this.assessmentList = this.Repo.getAllAssessments();
            this.notifyDataSetChanged();
        });

        holder.btn_Edit.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            LayoutInflater LI = LayoutInflater.from(view.getContext());
            View popupView = LI.inflate(R.layout.add_assessment, null);

            TextView mAssessmentTitle = popupView.findViewById(R.id.txt_editAssessmentTitle);
            TextView mAssessmentStart = popupView.findViewById(R.id.lbl_editStartAssessment);
            TextView mAssessmentEnd = popupView.findViewById(R.id.lbl_editEndAssessment);
            ImageButton mBtnAssessmentStartDate = popupView.findViewById(R.id.btn_AssessmentStartDate);
            ImageButton mBtnAssessmentEndDate = popupView.findViewById(R.id.btn_AssessmentEndDate);
            Spinner typeSpinner = popupView.findViewById(R.id.spn_typeAssessment);
            Spinner courseSpinner = popupView.findViewById(R.id.spn_AssessmentCourse);
            SwitchCompat mAssessmentAlert = popupView.findViewById(R.id.swt_AssessmentAlert);
            SwitchCompat mAssessmentAlert2 = popupView.findViewById(R.id.swt_AssessmentAlert2);

            Assessment updateAssessment = assessmentList.get(holder.getAdapterPosition());

            mAssessmentTitle.setText(updateAssessment.getTitle());
            mAssessmentStart.setText("Start Date: " + updateAssessment.getStartDate());
            mAssessmentEnd.setText("End Date: " + updateAssessment.getEndDate());
            mAssessmentAlert.setChecked(updateAssessment.getAlert1());
            mAssessmentAlert2.setChecked(updateAssessment.getAlert2());

            builder.setTitle("Please enter the assessment information");
            builder.setView(popupView);
            builder.setPositiveButton("Edit", (dialogInterface, i) -> {
                String AssessmentTitle = mAssessmentTitle.getText().toString();
                String AssessmentStart = mAssessmentStart.getText().toString().substring(12);
                String AssessmentEnd = mAssessmentEnd.getText().toString().substring(10);
                String AssessmentType = typeSpinner.getSelectedItem().toString();
                Course AssessmentCourse = (Course) courseSpinner.getSelectedItem();
                boolean AssessmentAlert = mAssessmentAlert.isChecked();
                boolean AssessmentAlert2 = mAssessmentAlert2.isChecked();

                if (AssessmentAlert && !updateAssessment.getAlert1()) {
                     updateAssessment.setAlarm1(AlarmSetter.generateNewAlarm(view.getContext(),
                            simpleDateFormat,
                            AssessmentStart,
                            "The following assessment is starting today: " + AssessmentTitle));


                } else if (!AssessmentAlert && updateAssessment.getAlert1()) {
                    AlarmSetter.destroyAlarm(updateAssessment.getAlarm1(), view.getContext());
                }

                if (AssessmentAlert2 && !updateAssessment.getAlert2()) {
                    updateAssessment.setAlarm2(AlarmSetter.generateNewAlarm(view.getContext(),
                            simpleDateFormat,
                            AssessmentEnd,
                            "The following assessment is ending today: " + AssessmentTitle));
                } else if (!AssessmentAlert2 && updateAssessment.getAlert2()) {
                    AlarmSetter.destroyAlarm(updateAssessment.getAlarm2(), view.getContext());
                }

                updateAssessment.setTitle(AssessmentTitle);
                updateAssessment.setStartDate(AssessmentStart);
                updateAssessment.setEndDate(AssessmentEnd);
                updateAssessment.setAlert1(AssessmentAlert);
                updateAssessment.setAlert2(AssessmentAlert2);
                updateAssessment.setType(AssessmentType);
                updateAssessment.setCourseId(AssessmentCourse.getId());

                Repo.updateAssessment(updateAssessment);
                this.notifyDataSetChanged();
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            });

            AlertDialog dialog = builder.create();

            ArrayAdapter<Course> courseAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item);

            courseAdapter.clear();
            for (int i = 0; i < courseList.size(); i++) {
                courseAdapter.add(courseList.get(i));
                if (courseList.get(i).getId() == updateAssessment.getCourseId()) {
                    courseSpinner.setPrompt(courseList.get(i).getTitle());
                }
            }


            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courseSpinner.setAdapter(courseAdapter);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.AssessmentTypeArray, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinner.setAdapter(adapter);

            dialog.show();

            for (int i = 0; i < typeSpinner.getCount(); i++) {
                if (typeSpinner.getItemAtPosition(i).equals(updateAssessment.getType())) {
                    typeSpinner.setSelection(i);
                }
            }

            for (int i = 0; i < courseSpinner.getCount(); i++) {
                Course test = (Course) courseSpinner.getItemAtPosition(i);
                if (test.getId() == (updateAssessment.getCourseId())) {
                    courseSpinner.setSelection(i);
                }
            }

            mBtnAssessmentEndDate.setOnClickListener(view12 -> new DatePickerDialog(view.getContext(),
                    endDateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show());

            mBtnAssessmentStartDate.setOnClickListener(view1 -> new DatePickerDialog(view.getContext(),
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
        });
    }

    @Override
    public int getItemCount() {
        return assessmentList.size();
    }
}
