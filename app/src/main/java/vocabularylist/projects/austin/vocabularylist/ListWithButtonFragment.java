package vocabularylist.projects.austin.vocabularylist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import vocabularylist.projects.austin.vocabularylist.model.Word;
import vocabularylist.projects.austin.vocabularylist.model.WordManager;
import vocabularylist.projects.austin.vocabularylist.parsers.DatabaseIO;
import vocabularylist.projects.austin.vocabularylist.parsers.DefinitionParser;


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

        setupListView();
        setupButton();
        return v;
    }

    private void setupListView() {

        final WordAdapter la = new WordAdapter(getActivity(), R.layout.listview_item_row, WordManager.getInstance().getWords());
        DatabaseIO databaseIO = DatabaseIO.getInstance(getActivity());

        lv.setAdapter(la);
        la.clear();
        databaseIO.loadDatabase();
        Set<String> offlineWords = getActivity().getPreferences(Context.MODE_PRIVATE).getStringSet("offlineWords", new HashSet<String>());
        if(!offlineWords.isEmpty()){
            //has offline words to add
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ArrayList<String> offlineWordsArray = new ArrayList<>();
            offlineWordsArray.addAll(offlineWords);
            ft.replace(R.id.fragment, OfflineWordsFragment.newInstance(offlineWordsArray));
            ft.addToBackStack(null);
            ft.commit();
        }
        la.addAll(WordManager.getInstance().getWords());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word selectedWord = (Word) la.getItem(position);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                System.out.println(ft.isAddToBackStackAllowed());
                ft.replace(R.id.fragment, WordInfoFragment.newInstance(selectedWord.getTopLevelName()), "wordInfo");
                ft.addToBackStack(null);
                ft.commit();
                //ft.replace(R.id.fragment, WordInfoFragment.newInstance(selectedWord.getTopLevelName())).addToBackStack(null).commit();
                System.out.println(getActivity().getFragmentManager().getBackStackEntryCount());
            }

        });
        lv.setLongClickable(true);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                builder.show();

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
}
