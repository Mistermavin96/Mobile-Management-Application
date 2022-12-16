package WGUProgram.try3.Functions;

import static android.app.AlarmManager.RTC;
import static android.app.AlarmManager.RTC_WAKEUP;
import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class AlarmSetter {

    public static class generateRequestCode {
        private final static AtomicInteger r = new AtomicInteger(0);
        public static  int getNewID() {
            return r.incrementAndGet();
        }
    }

   public static class generateNotificationID {
            private final static AtomicInteger c = new AtomicInteger(0);
            public static int getNewID() {
                return c.incrementAndGet();
            }
    }

    public static int generateNewAlarm(Context context, SimpleDateFormat simpleDateFormat, String Time, String Body) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        int requestCode = generateRequestCode.getNewID();
        Date TimeDate = null;
        try {
            TimeDate = simpleDateFormat.parse(Time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert TimeDate != null;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("Body", Body);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExactAndAllowWhileIdle(RTC, TimeDate.getTime(), pendingIntent);
        return requestCode;
    }

    public static void destroyAlarm(int requestCode, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}

