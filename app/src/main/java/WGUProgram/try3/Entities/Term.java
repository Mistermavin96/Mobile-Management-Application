package WGUProgram.try3.Entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Term_Table",
        indices = {@Index(value = {"termId"}, unique = true)})

public class Term implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "termId")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "startDate")
    private String startDate;

    @ColumnInfo(name = "endDate")
    private String endDate;

    @ColumnInfo(name="Alert1")
    private Boolean Alert1;

    @ColumnInfo (name="Alert2")
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

    public Term(int id, String title, String startDate, String endDate, Boolean Alert1, Boolean Alert2) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.Alert1 = Alert1;
        this.Alert2 = Alert2;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Boolean getAlert1() { return Alert1;}

    public Boolean getAlert2() { return Alert2;}

    public int getAlarm1() {
        return alarm1;
    }

    public int getAlarm2() {
        return alarm2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setAlert1(Boolean alert1) { this.Alert1 = alert1; }

    public void setAlert2(Boolean alert2) { this.Alert2 = alert2;}

    public void setAlarm1(int alarm1) {
        this.alarm1 = alarm1;
    }

    public void setAlarm2(int alarm2) {
        this.alarm2 = alarm2;
    }
}