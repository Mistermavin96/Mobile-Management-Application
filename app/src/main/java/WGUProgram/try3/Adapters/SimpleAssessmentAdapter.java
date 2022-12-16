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

public class SimpleAssessmentAdapter extends RecyclerView.Adapter<SimpleAssessmentAdapter.genericViewHolder> {
    private List<Assessment> AssessmentList;
    private final List<Course> courseList;
    private final Repository Repo;
    String format = "MM/dd/yy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateSetListener;
    DatePickerDialog.OnDateSetListener startDateSetListener;

    public SimpleAssessmentAdapter(List<Assessment> assessmentList, List<Course> courseList, Repository repo) {
        this.AssessmentList = assessmentList;
        this.courseList = courseList;
        this.Repo = repo;
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
    public void onBindViewHolder(@NotNull SimpleAssessmentAdapter.genericViewHolder holder, int position) {
        String Title = "Assessment: " + AssessmentList.get(position).getTitle();
        holder.txt_Title.setText(Title);

        holder.btn_Delete.setOnClickListener(view -> {
            holder.btn_Delete.setEnabled(false);
            if (AssessmentList.get(holder.getAdapterPosition()).getAlert1()) {
                AlarmSetter.destroyAlarm(AssessmentList.get(holder.getAdapterPosition()).getAlarm1(), view.getContext());
            }

            if (AssessmentList.get(holder.getAdapterPosition()).getAlert2()) {
                AlarmSetter.destroyAlarm(AssessmentList.get(holder.getAdapterPosition()).getAlarm2(), view.getContext());
            }
            Repo.deleteAssessment(AssessmentList.get(holder.getAdapterPosition()));
            Toast.makeText(view.getContext(), "Course successfully deleted", Toast.LENGTH_SHORT).show();
            holder.btn_Delete.setEnabled(true);
            this.AssessmentList = this.Repo.getAllAssessments();
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

            Assessment updateAssessment = AssessmentList.get(holder.getAdapterPosition());

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
        return AssessmentList.size();
    }
}