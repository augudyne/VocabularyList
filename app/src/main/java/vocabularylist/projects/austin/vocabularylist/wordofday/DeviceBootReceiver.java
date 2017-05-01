package vocabularylist.projects.austin.vocabularylist.wordofday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Austin on 2017-04-13.
 */
public class DeviceBootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETE")) {
            //set the alarm
            System.out.println("Starting Notification Repeating");
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(context, WordOfTheDayReceiver.class);
            int interval = 1000*60*60*24;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 5);
            calendar.set(Calendar.MINUTE, 30);
            calendar.add(Calendar.DATE, 1);

            PendingIntent myPendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, myPendingIntent);
        }
    }
}
