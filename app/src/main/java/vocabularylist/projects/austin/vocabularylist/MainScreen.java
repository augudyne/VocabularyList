package vocabularylist.projects.austin.vocabularylist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import java.util.Calendar;

import vocabularylist.projects.austin.vocabularylist.wordofday.WordOfTheDayReceiver;
import vocabularylist.projects.austin.vocabularylist.model.WordManager;
import vocabularylist.projects.austin.vocabularylist.providers.DatabaseIO;


public class MainScreen extends AppCompatActivity implements WordInfoFragment.OnFragmentInteractionListener,
        WordVariantInfoFragment.OnFragmentInteractionListener,
        WordSuggestionsFragment.OnFragmentInteractionListener,
        OfflineWordsFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_page);
        Intent parentIntent = getIntent();
        String wordToShow = parentIntent.getStringExtra("wordToShow");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(wordToShow != null){
            System.out.println("Word To Show: " + wordToShow);
            ft.add(R.id.fragment_container, new ListWithButtonFragment());
            ft.commit();
            ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.fragment_container, WordInfoFragment.newInstance(wordToShow));
            ft.commit();
        } else {
            ft.add(R.id.fragment_container, new ListWithButtonFragment());
            ft.commit();
        }
        startNotifyWordOfTheDay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("On Pause Called, Writing to file");
        DatabaseIO.getInstance(this).writeDatabaseToFile();
        //TODO: get offline words and add to shared preferences
        // Alternatively, also make list of used words and check + load
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.offline_words){
            //show the offline words fragment
            System.out.println("Menu Option selected: 'Offline Words'");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, OfflineWordsFragment.newInstance(WordManager.getInstance().getOfflineWords()));
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }

        if(id == R.id.export){
            //share to somewhere
            System.out.println("Menu Option selected: 'Offline Words'");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, DatabaseIO.getInstance(this).stateToJSONObject().toString());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share_to)));
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    public void startNotifyWordOfTheDay() {
        System.out.println("Starting Notification Repeating");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, WordOfTheDayReceiver.class);
        int interval = 1000*60*60*24;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        calendar.set(Calendar.MINUTE, 30);
        calendar.add(Calendar.DATE, 1);

        PendingIntent myPendingIntent = PendingIntent.getBroadcast(MainScreen.this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, myPendingIntent);
    }
}
