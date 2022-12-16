package WGUProgram.try3.Activities;

import static android.Manifest.permission.SEND_SMS;
import static androidx.core.content.PermissionChecker.PERMISSION_DENIED;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import WGUProgram.try3.Functions.AlarmSetter;
import WGUProgram.try3.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        if (ContextCompat.checkSelfPermission(this, SEND_SMS) == PERMISSION_DENIED) {
            int requestCode = AlarmSetter.generateRequestCode.getNewID();
            requestPermissions(new String[]{SEND_SMS}, requestCode);
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("base_notification_channel", "Alert Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("All notifications related to upcoming dates");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

    public void onTermsClick(View view) {
        Intent ActivityIntent = new Intent(this, TermsActivity.class);
        startActivity(ActivityIntent);
    }

    public void onCoursesClick(View view) {
        Intent ActivityIntent = new Intent(this, CoursesActivity.class);
        startActivity(ActivityIntent);
    }

    public void onAssessmentsClick(View view) {
        Intent ActivityIntent = new Intent(this, AssessmentsActivity.class);
        startActivity(ActivityIntent);
    }
}