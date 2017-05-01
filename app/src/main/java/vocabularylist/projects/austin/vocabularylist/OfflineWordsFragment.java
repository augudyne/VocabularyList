package vocabularylist.projects.austin.vocabularylist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import vocabularylist.projects.austin.vocabularylist.model.Word;
import vocabularylist.projects.austin.vocabularylist.model.WordManager;
import vocabularylist.projects.austin.vocabularylist.providers.DatabaseIO;
import vocabularylist.projects.austin.vocabularylist.providers.DefinitionParser;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class OfflineWordsFragment extends Fragment implements WordInfoFragment.OnFragmentInteractionListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String WORD_LIST = "param1";


    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter<String> offlineWordsAdapter;

    public static OfflineWordsFragment newInstance(ArrayList<String> param1) {
        OfflineWordsFragment fragment = new OfflineWordsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(WORD_LIST, param1);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfflineWordsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offlineswords_layout, container, false);


        setupList(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    void setupList(View v){
        WordManager wm = WordManager.getInstance();
        offlineWordsAdapter = new OfflineWordsAdapter(getActivity(),
                R.layout.offline_listview_row, wm.getOfflineWords());
        System.out.println("Offline Words: " + wm.getOfflineWords().toString());
        final ListView offlineWordsList = (ListView) v.findViewById(R.id.offlineWordsListView);
        offlineWordsList.setAdapter(offlineWordsAdapter);
        //offlineWordsAdapter.notifyDataSetChanged();
        offlineWordsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedString = offlineWordsAdapter.getItem(position);
                DefinitionParser df = new DefinitionParser(getActivity(), selectedString);
                DatabaseIO.getInstance(getActivity()).writeDatabaseToFile();
                df.execute(offlineWordsAdapter);
            }

        });
        offlineWordsList.setLongClickable(true);

        offlineWordsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                        String wordToDelete = offlineWordsAdapter.getItem(pos);
                        WordManager.getInstance().removeOfflineWord(wordToDelete);
                        offlineWordsAdapter.remove(wordToDelete);
                        offlineWordsAdapter.notifyDataSetChanged();
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



}
