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

public class SimpleTermAdapter extends RecyclerView.Adapter<SimpleTermAdapter.genericViewHolder> {
    private List<Term> TermList;
    private final List<Course> CourseList;
    private final Repository Repo;
    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateSetListener;
    DatePickerDialog.OnDateSetListener startDateSetListener;
    final String format = "MM/dd/yy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);

    public SimpleTermAdapter(List<Term> termList, List<Course> courseList, Repository repo) {
        this.TermList = termList;
        this.CourseList = courseList;
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
    public void onBindViewHolder(@NotNull SimpleTermAdapter.genericViewHolder holder, int position) {
        String Title = "Term: " + TermList.get(position).getTitle();
        holder.txt_Title.setText(Title);

        holder.btn_Delete.setOnClickListener(view -> {
            holder.btn_Delete.setEnabled(false);
            for (Course c : CourseList) {
                if (c.getTermId() == TermList.get(holder.getAdapterPosition()).getId()) {
                    Toast.makeText(view.getContext(), "Please delete related courses", Toast.LENGTH_SHORT).show();
                    holder.btn_Delete.setEnabled(true);
                    return;
                }
            }

            if (TermList.get(holder.getAdapterPosition()).getAlert1()) {
                AlarmSetter.destroyAlarm(TermList.get(holder.getAdapterPosition()).getAlarm1(), view.getContext());
            }

            if (TermList.get(holder.getAdapterPosition()).getAlert2()) {
                AlarmSetter.destroyAlarm(TermList.get(holder.getAdapterPosition()).getAlarm2(), view.getContext());
            }

            Repo.deleteTerm(TermList.get(holder.getAdapterPosition()));
            Toast.makeText(view.getContext(), "Term successfully deleted", Toast.LENGTH_SHORT).show();
            holder.btn_Delete.setEnabled(true);
            this.TermList = this.Repo.getAllTerms();
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

            Term updatedTerm = TermList.get(holder.getAdapterPosition());

            mTxtEditTitleTerm.setText(updatedTerm.getTitle());
            mLblStartTerm.setText("Start Date: " + updatedTerm.getStartDate());
            mLblEndTerm.setText("End Date: " + updatedTerm.getEndDate());
            mTermAlert.setChecked(updatedTerm.getAlert1());
            mTermAlert2.setChecked(updatedTerm.getAlert2());

                builder.setView(editMenu);

                builder.setPositiveButton("Edit", (dialogInterface, i) -> {
                    String termTitle = mTxtEditTitleTerm.getText().toString();
                    String Start = mLblStartTerm.getText().toString().substring(12);
                    String End = mLblEndTerm.getText().toString().substring(10);
                    boolean Alert = mTermAlert.isChecked();
                    boolean Alert2 = mTermAlert2.isChecked();

                    if (Alert && !updatedTerm.getAlert1()) {
                        updatedTerm.setAlarm1(AlarmSetter.generateNewAlarm(view.getContext(),
                                simpleDateFormat,
                                Start,
                                "The following term is starting today: " + termTitle));
                    } else if (!Alert && updatedTerm.getAlert1()){
                        AlarmSetter.destroyAlarm(updatedTerm.getAlarm1(), view.getContext());
                    }

                    if (Alert2 && !updatedTerm.getAlert2()) {
                        updatedTerm.setAlarm2(AlarmSetter.generateNewAlarm(view.getContext(),
                                simpleDateFormat,
                                End,
                                "The following term is ending today: " + termTitle));
                    } else if (!Alert2 && updatedTerm.getAlert2()) {
                        AlarmSetter.destroyAlarm(updatedTerm.getAlarm2(), view.getContext());
                    }

                    updatedTerm.setTitle(termTitle);
                    updatedTerm.setAlert1(Alert);
                    updatedTerm.setAlert2(Alert2);
                    updatedTerm.setStartDate(Start);
                    updatedTerm.setEndDate(End);
                    Repo.updateTerm(updatedTerm);
                    this.notifyDataSetChanged();
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                });

                AlertDialog dialog = builder.create();

                dialog.show();

                mTermEndDate.setOnClickListener(view12 -> new DatePickerDialog(view12.getContext(),
                        endDateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show());

                mTermStartDate.setOnClickListener(view1 -> new DatePickerDialog(view1.getContext(),
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
        return TermList.size();
    }
}