package vocabularylist.projects.austin.vocabularylist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuDialogHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;


import vocabularylist.projects.austin.vocabularylist.model.Word;
import vocabularylist.projects.austin.vocabularylist.model.WordManager;
import vocabularylist.projects.austin.vocabularylist.providers.DatabaseIO;
import vocabularylist.projects.austin.vocabularylist.providers.DefinitionParser;
import vocabularylist.projects.austin.vocabularylist.wordofday.WordOfTheDayReceiver;


/**
 * A placeholder fragment containing a simple view.
 */
public class ListWithButtonFragment extends Fragment implements WordInfoFragment.OnFragmentInteractionListener {
    private ListView lv;
    private Button btnAddWord;
    private String m_text = "";


    public ListWithButtonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_default_page, container, false);
        lv = (ListView) v.findViewById(R.id.listView);
        btnAddWord = (Button) v.findViewById(R.id.btnAddWord);

        /*if(offlineWords != null && !offlineWords.isEmpty()){
            //has offline words to add
            System.out.println("Has offline words to add...");
            btnAddWord.setVisibility(View.INVISIBLE);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ArrayList<String> offlineWordsArray = new ArrayList<>();
            offlineWordsArray.addAll(offlineWords);
            ft.replace(R.id.fragment, OfflineWordsFragment.newInstance(offlineWordsArray));
            ft.addToBackStack(null);
            ft.commit();
        }*/

        setupButton();
        setupList();

        return v;
    }


    private void setupList() {

        final WordAdapter la = new WordAdapter(getActivity(), R.layout.listview_item_row, WordManager.getInstance().getWords());
        DatabaseIO databaseIO = DatabaseIO.getInstance(getActivity());

        lv.setAdapter(la);
        la.clear();
        databaseIO.loadDatabase();

        la.addAll(WordManager.getInstance().getWords());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word selectedWord = (Word) la.getItem(position);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, WordInfoFragment.newInstance(selectedWord.getTopLevelName()), "wordInfo");
                ft.addToBackStack(null);
                ft.commit();
                //ft.replace(R.id.fragment, WordInfoFragment.newInstance(selectedWord.getTopLevelName())).addToBackStack(null).commit();

            }

        });
        lv.setLongClickable(true);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Word selectedWord = la.getItem(position);
                final PopupMenu wordMenu = new PopupMenu(getActivity(), view);
                wordMenu.inflate(R.menu.long_select);
                wordMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        System.out.println("Selected: " + item.toString());
                        if (item.getItemId() == R.id.delete_word) {
                            //delete word selected
                            System.out.println("Delete Selected");
                            menuSelectDeleteWord(selectedWord, la);
                            return true;
                        } else if (item.getItemId() == R.id.share_word) {
                            System.out.println("Share selected");
                            menuSelectShareWord(selectedWord);
                            return true;
                        } else
                            System.out.println("Menu Click Not Handled");
                        return false;
                    }
                });
                wordMenu.show();
                /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm Delete Word");
                final int pos = position;
                final TextView confirmMessage = new TextView(getActivity());
                builder.setView(confirmMessage);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Word wordToDelete = (Word) la.getItem(pos);
                        WordManager.getInstance().removeWord(wordToDelete);
                        la.remove(wordToDelete);
                        la.notifyDataSetChanged();
                        DatabaseIO.getInstance(getActivity()).writeDatabaseToFile();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();*/

                return true;
            }
        });
    }

    private void setupButton(){
        btnAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add New Word");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
                builder.setView(input);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_text = input.getText().toString();
                        DefinitionParser df = new DefinitionParser(getActivity(), m_text);
                        df.execute((WordAdapter) lv.getAdapter());
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog.cancel();

                    }
                });

                builder.show();
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void menuSelectDeleteWord(Word w, final ArrayAdapter<Word> arrayAdapter){
        //Alert the user
        final Word selectedWord = w;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Delete");
        TextView dialogView = new TextView(getActivity());
        dialogView.setText("Are you sure you want to delete " + w.getTopLevelName());
        builder.setView(dialogView);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WordManager.getInstance().removeWord(selectedWord);
                DatabaseIO.getInstance(getActivity()).writeDatabaseToFile();
                arrayAdapter.remove(selectedWord);
            }
        });
        builder.show();
    }

    private void menuSelectShareWord(Word w){
        Intent shareWord = new Intent(Intent.ACTION_SEND);
        shareWord.putExtra(Intent.EXTRA_TEXT, w.toString());
        shareWord.setType("text/plain");
        startActivity(Intent.createChooser(shareWord, "Share to..."));
    }


}
