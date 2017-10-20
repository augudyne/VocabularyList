package vocabularylist.projects.austin.vocabularylist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import vocabularylist.projects.austin.vocabularylist.parsers.DefinitionParser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordSuggestionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordSuggestionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordSuggestionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "suggest";
    private static final String ARG_PARAM2 = "word";

    // TODO: Rename and change types of parameters
    private ArrayList<String> suggestionsList;
    private String topLevelWord;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment WordSuggestionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordSuggestionsFragment newInstance(ArrayList<String> param1, String param2) {
        WordSuggestionsFragment fragment = new WordSuggestionsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WordSuggestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            suggestionsList = getArguments().getStringArrayList(ARG_PARAM1);
            topLevelWord = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_word_suggestions, container, false);

        TextView textView = (TextView) v.findViewById(R.id.sgtWordDisplay);
        if(suggestionsList.isEmpty()){
            textView.setText(topLevelWord + " not found in Merriam Webster \nNo suggestions for " + topLevelWord + ".");
        } else {
            textView.setText(topLevelWord + " not found in Merriam Webster \nDid you mean...");
        }

        final ListView lv = (ListView) v.findViewById(R.id.suggestionsDisplay);
        final ArrayAdapter<String> la = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, suggestionsList);
        lv.setAdapter(la);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedWord = la.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm Add Word");
                TextView txtDisplay = new TextView(getActivity());
                txtDisplay.setText("Add " + selectedWord + " to Vocabulary List?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DefinitionParser df = new DefinitionParser(getActivity(), selectedWord);
                        df.execute((ArrayAdapter) ((ListView) getActivity().findViewById(R.id.listView)).getAdapter());
                        FragmentManager ft = getActivity().getSupportFragmentManager();
                    }
                });
                builder.show();
            }
        });


        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        public void onFragmentInteraction(Uri uri);
    }

}
