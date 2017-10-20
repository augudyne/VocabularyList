package vocabularylist.projects.austin.vocabularylist;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import vocabularylist.projects.austin.vocabularylist.parsers.DatabaseIO;


public class MainScreen extends AppCompatActivity implements WordInfoFragment.OnFragmentInteractionListener,
        WordVariantInfoFragment.OnFragmentInteractionListener,
        WordSuggestionsFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_page);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
