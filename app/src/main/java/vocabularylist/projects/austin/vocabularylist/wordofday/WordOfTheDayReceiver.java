package vocabularylist.projects.austin.vocabularylist.wordofday;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


import vocabularylist.projects.austin.vocabularylist.MainScreen;
import vocabularylist.projects.austin.vocabularylist.R;
import vocabularylist.projects.austin.vocabularylist.model.Word;
import vocabularylist.projects.austin.vocabularylist.model.WordManager;
import vocabularylist.projects.austin.vocabularylist.model.WordVariant;
import vocabularylist.projects.austin.vocabularylist.providers.DatabaseIO;

/**
 * Created by Austin on 2017-04-13.
 */
public class WordOfTheDayReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
            //show the notification with the word of the day
        System.out.println("RECEIVED BROADCAST FOR WORD");
        DatabaseIO.getInstance(context).loadDatabase();
        Word randomWord =  WordManager.getInstance().getRandomWord();
        WordVariant firstVariant = randomWord.getWordVariants().get(0);
        Toast.makeText(context, "Your Random Word Is: " + randomWord.getTopLevelName(), Toast.LENGTH_LONG).show();
        //build the notification
        int mId = 1;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("Word Of The Day")
                .setContentText(firstVariant.getValue()+ " : " + firstVariant.definitionsToString());
        Intent resultIntentB = new Intent(context, MainScreen.class);
        resultIntentB.putExtra("wordToShow", randomWord.getTopLevelName());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainScreen.class);
        stackBuilder.addNextIntent(resultIntentB);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mId, mBuilder.build());
    }
}
