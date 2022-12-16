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

@Entity(tableName = "Assessment_Table",
        foreignKeys = @ForeignKey(entity = Course.class,
            parentColumns = "courseId",
            childColumns = "courseId",
            onDelete = CASCADE),
        indices = {@Index(value = {"courseId"}), @Index(value =  {"assessmentId"}, unique = true)})
public class Assessment implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "assessmentId")
    private int id;

    @ColumnInfo(name = "courseId")
    private int courseId;

    @ColumnInfo(name = "title")
    @NotNull
    private String title;

    @ColumnInfo(name = "startDate")
    @NotNull
    private String startDate;

    @ColumnInfo(name = "endDate")
    @NotNull
    private String endDate;

    @ColumnInfo(name = "type")
    @NotNull
    private  String type;

    @ColumnInfo(name="Alert1")
    @NotNull
    private Boolean Alert1;

    @ColumnInfo(name="Alert2")
    @NotNull
    private Boolean Alert2;

    @ColumnInfo(name="Alarm1")
    private int alarm1;

    @ColumnInfo(name="alarm2")
    private int alarm2;

    @Override
    public String toString() {
        return title;
    }

    public Assessment(int id, int courseId, String title, String startDate, String endDate, String type, Boolean Alert1, Boolean Alert2) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.Alert1 = Alert1;
        this.Alert2 = Alert2;
    }

    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getStartDate() {
        return startDate;
    }

    @NonNull
    public String getEndDate() {
        return endDate;
    }

    @NonNull
    public String getType() { return type;}

    @NonNull
    public Boolean getAlert1() { return Alert1;}

    @NotNull public  Boolean getAlert2() { return Alert2;}

    public int getAlarm1() {
        return alarm1;
    }

    public int getAlarm2() {
        return alarm2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    public void setType(@NotNull String type) { this.type = type; }

    public void setAlert1(@NotNull Boolean Alert1) { this.Alert1 = Alert1; }

    public void setAlert2(@NotNull Boolean alert2) { this.Alert2 = alert2; }

    public void setAlarm1(int alarm1) {
        this.alarm1 = alarm1;
    }

    public void setAlarm2(int alarm2) {
        this.alarm2 = alarm2;
    }
}