package WGUProgram.try3.Adapters;

import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
import WGUProgram.try3.Entities.Course;
import WGUProgram.try3.Entities.Term;
import WGUProgram.try3.Functions.AlarmSetter;
import WGUProgram.try3.R;

public class ExtendedTermAdapter extends RecyclerView.Adapter<ExtendedTermAdapter.termViewHolder> {

    private List<Term> termList;
    private final List<Course> courseList;
    private final Repository Repo;
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateSetListener;
    DatePickerDialog.OnDateSetListener startDateSetListener;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);

    public ExtendedTermAdapter(List<Term> TermList, List<Course> courseList, Repository repo) {
        this.termList = TermList;
        this.courseList = courseList;
        this.Repo = repo;
    }

    public static class termViewHolder extends RecyclerView.ViewHolder {
        TextView txt_ItemTermTitle;
        TextView txt_ItemTermStart;
        TextView txt_ItemTermEnd;
        TextView txt_TermCourseList;
        ImageButton btn_Delete;
        ImageButton btn_Edit;

        public termViewHolder(final View view) {
            super (view);
            txt_ItemTermTitle = view.findViewById(R.id.txt_ItemTermTitle);
            txt_ItemTermStart = view.findViewById(R.id.txt_ItemTermStart);
            txt_ItemTermEnd = view.findViewById(R.id.txt_ItemTermEnd);
            txt_TermCourseList = view.findViewById(R.id.txt_TermCourseList);
            btn_Delete = view.findViewById(R.id.btn_ItemDeleteTerm);
            btn_Edit = view.findViewById(R.id.btn_ItemEditTerm);
        }
    }

    @NonNull
    @Override
    public ExtendedTermAdapter.termViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.term_item, parent, false);
        return new ExtendedTermAdapter.termViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ExtendedTermAdapter.termViewHolder holder, int position) {
        String Title = ("Term: " + termList.get(position).getTitle());
        String Start = ("Start Date: " + termList.get(position).getStartDate());
        String End = ("End Date: " + termList.get(position).getEndDate());
        StringBuilder Courses = new StringBuilder("Courses: ");

        // Runs through each course in the list and compares it's associated termID to the current termID, appending if found.
        for (Course c : courseList) {
            if (c.getTermId() == termList.get(position).getId()) {
                Courses.append(c.getTitle()).append(" - ");
            }
        }

        // This checks that there are courses assigned, and deletes the unnecessary dash. If there are no courses, it removes the text.
        if (Courses.length() > 9 && Courses.charAt(Courses.length() - 2) == '-') {
            Courses.deleteCharAt(Courses.length() - 2);
        } else if (Courses.length() == 9) {
            Courses.append("None");
        }

        holder.txt_ItemTermTitle.setText(Title);
        holder.txt_ItemTermStart.setText(Start);
        holder.txt_ItemTermEnd.setText(End);
        holder.txt_TermCourseList.setText(Courses);

        holder.btn_Delete.setOnClickListener(view -> {
            holder.btn_Delete.setEnabled(false);
            for (Course c : courseList) {
                if (c.getTermId() == termList.get(holder.getAdapterPosition()).getId()) {
                    Toast.makeText(view.getContext(), "Please delete related courses", Toast.LENGTH_SHORT).show();
                    holder.btn_Delete.setEnabled(true);
                    return;
                }
            }

            if (termList.get(holder.getAdapterPosition()).getAlert1()) {
                AlarmSetter.destroyAlarm(termList.get(holder.getAdapterPosition()).getAlarm1(), view.getContext());
            }

            if (termList.get(holder.getAdapterPosition()).getAlert2()) {
                AlarmSetter.destroyAlarm(termList.get(holder.getAdapterPosition()).getAlarm2(), view.getContext());
            }

            Repo.deleteTerm(termList.get(holder.getAdapterPosition()));
            Toast.makeText(view.getContext(), "Term successfully deleted", Toast.LENGTH_SHORT).show();
            holder.btn_Delete.setEnabled(true);
            this.termList = this.Repo.getAllTerms();
            this.notifyDataSetChanged();


        });

        holder.btn_Edit.setOnClickListener(view ->  {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            LayoutInflater LI = LayoutInflater.from(view.getContext());
            View editMenu = LI.inflate(R.layout.add_term, null);
            builder.setTitle("Please enter the Term information");

            EditText mTxtEditTitleTerm = editMenu.findViewById(R.id.txt_editTitleTerm);
            TextView mLblEndTerm = editMenu.findViewById(R.id.lbl_editEndTerm);
            TextView mLblStartTerm = editMenu.findViewById(R.id.lbl_editStartTerm);
            ImageButton mTermEndDate = editMenu.findViewById(R.id.btn_TermEndDate);
            ImageButton mTermStartDate = editMenu.findViewById(R.id.btn_TermStartDate);
            SwitchCompat mTermAlert = editMenu.findViewById(R.id.swt_TermStartAlert);
            SwitchCompat mTermAlert2 = editMenu.findViewById(R.id.swt_TermEndAlert);

            Term updatedTerm = termList.get(holder.getAdapterPosition());

            mTxtEditTitleTerm.setText(updatedTerm.getTitle());
            mLblStartTerm.setText("Start Date: " + updatedTerm.getStartDate());
            mLblEndTerm.setText("End Date: " + updatedTerm.getEndDate());
            mTermAlert.setChecked(updatedTerm.getAlert1());
            mTermAlert2.setChecked(updatedTerm.getAlert2());

            builder.setView(editMenu);

            builder.setPositiveButton("Edit", (dialogInterface, i) -> {
                String termTitle = mTxtEditTitleTerm.getText().toString();
                String termStart = mLblStartTerm.getText().toString().substring(12);
                String termEnd = mLblEndTerm.getText().toString().substring(10);
                boolean termAlert = mTermAlert.isChecked();
                boolean termAlert2 = mTermAlert2.isChecked();

                if (termAlert && !updatedTerm.getAlert1()) {
                    updatedTerm.setAlarm1(AlarmSetter.generateNewAlarm(view.getContext(),
                            simpleDateFormat,
                            termStart,
                            "The following term is starting today: " + termTitle));
                } else if (!termAlert && updatedTerm.getAlert1()){
                    AlarmSetter.destroyAlarm(updatedTerm.getAlarm1(), view.getContext());
                }

                if (termAlert2 && !updatedTerm.getAlert2()) {
                    updatedTerm.setAlarm2(AlarmSetter.generateNewAlarm(view.getContext(),
                            simpleDateFormat,
                            termEnd,
                            "The following term is ending today: " + termTitle));
                } else if (!termAlert2 && updatedTerm.getAlert2()) {
                    AlarmSetter.destroyAlarm(updatedTerm.getAlarm2(), view.getContext());
                }

                updatedTerm.setTitle(termTitle);
                updatedTerm.setAlert1(termAlert);
                updatedTerm.setAlert2(termAlert2);
                updatedTerm.setStartDate(termStart);
                updatedTerm.setEndDate(termEnd);
                Repo.updateTerm(updatedTerm);
                this.notifyDataSetChanged();
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            });

            AlertDialog dialog = builder.create();

            dialog.show();

            mTermEndDate.setOnClickListener(view1 -> new DatePickerDialog(view1.getContext(),
                    endDateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show());

            mTermStartDate.setOnClickListener(view12 -> new DatePickerDialog(view12.getContext(),
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
        });
    }

    @Override
    public int getItemCount() {
        return termList.size();
    }
}
