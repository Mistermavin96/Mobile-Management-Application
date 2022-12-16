package WGUProgram.try3.Entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "Course_Table",
        foreignKeys = @ForeignKey(entity = Term.class,
            parentColumns = "termId",
            childColumns = "termId",
            onDelete = CASCADE),
        indices = {@Index(value = {"courseId"}, unique = true), @Index(value = "termId")})
public class Course implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "courseId")
    private int id;

    @ColumnInfo(name = "termId")
    private int termId;

    @ColumnInfo(name = "title")
    @NotNull
    private String title;

    @ColumnInfo(name = "startDate")
    @NotNull
    private String startDate;

    @ColumnInfo(name = "endDate")
    @NotNull
    private String endDate;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name="instructorName")
    @NotNull
    private String instructorName;

    @ColumnInfo(name="instructorPhoneNumber")
    @NotNull
    private String instructorPhoneNumber;

    @ColumnInfo(name="instructorEmailAddress")
    @NotNull
    private String instructorEmailAddress;

    @ColumnInfo(name="Alert1")
    @NotNull
    private Boolean Alert1;

    @ColumnInfo(name="Alert2")
    @NotNull
    private Boolean Alert2;

    @ColumnInfo(name="alarm1")
    private int alarm1;

    @ColumnInfo(name="alarm2")
    private int alarm2;

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    public Course(int id, int termId, @NonNull String title, @NonNull String startDate, @NonNull String endDate, String status, String note, @NonNull String instructorName, @NonNull String instructorPhoneNumber, @NonNull String instructorEmailAddress, @NonNull Boolean Alert1, @NonNull Boolean Alert2) {
        this.id = id;
        this.termId = termId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.note = note;
        this.instructorName = instructorName;
        this.instructorPhoneNumber = instructorPhoneNumber;
        this.instructorEmailAddress = instructorEmailAddress;
        this.Alert1 = Alert1;
        this.Alert2 = Alert2;
    }

    public int getId() {
        return id;
    }

    public int getTermId() {
        return termId;
    }

    @NonNull public String getTitle() {
        return title;
    }

    @NonNull public String getStartDate() {
        return startDate;
    }

    @NonNull public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    @NonNull public String getInstructorName() {
        return instructorName;
    }

    @NonNull public String getInstructorPhoneNumber() {
        return instructorPhoneNumber;
    }

    @NonNull public String getInstructorEmailAddress() {
        return instructorEmailAddress;
    }

    @NonNull public Boolean getAlert1() { return Alert1;}

    @NonNull public Boolean getAlert2() { return Alert2;}

    public int getAlarm1() {
        return alarm1;
    }

    public int getAlarm2() {
        return alarm2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setStartDate(@NonNull String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(@NonNull String endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setInstructorName(@NonNull String instructorName) {
        this.instructorName = instructorName;
    }

    public void setInstructorPhoneNumber(@NonNull String instructorPhoneNumber) {
        this.instructorPhoneNumber = instructorPhoneNumber;
    }

    public void setInstructorEmailAddress(@NonNull String instructorEmailAddress) {
        this.instructorEmailAddress = instructorEmailAddress;
    }

    public void setAlert1(@NonNull Boolean Alert1) { this.Alert1 = Alert1;}

    public void setAlert2(@NonNull Boolean Alert2) { this.Alert2 = Alert2;}

    public void setAlarm1(int alarm1) {
        this.alarm1 = alarm1;
    }

    public void setAlarm2(int alarm2) {
        this.alarm2 = alarm2;
    }
}
