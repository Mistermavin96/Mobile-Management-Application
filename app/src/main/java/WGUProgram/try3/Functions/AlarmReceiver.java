package WGUProgram.try3.Functions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import WGUProgram.try3.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int alertID = AlarmSetter.generateNotificationID.getNewID();

        Bundle extras = intent.getExtras();
        String Body = extras.getString("Body");

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, "WGUSchedulerKavin")
                .setContentTitle("WGU date today")
                .setContentText(Body).setChannelId("base_notification_channel")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.wgu_launcher_ico3);
        NotificationManagerCompat mNotifyManager = NotificationManagerCompat.from(context);

        mNotifyManager.notify(alertID, mNotifyBuilder.build());
    }
}
