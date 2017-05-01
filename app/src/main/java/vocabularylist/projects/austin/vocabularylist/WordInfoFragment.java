package vocabularylist.projects.austin.vocabularylist;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;

import vocabularylist.projects.austin.vocabularylist.model.Word;
import vocabularylist.projects.austin.vocabularylist.model.WordManager;
import vocabularylist.projects.austin.vocabularylist.model.WordVariant;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordInfoFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_WORD = "";
    private static final String ARG_PARAM2 = "World";

    // TODO: Rename and change types of parameters
    private String word;
    private TextView textView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param word Parameter 1.
     * @return A new instance of fragment WordInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordInfoFragment newInstance(String word) {
        WordInfoFragment fragment = new WordInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORD, word);
        fragment.setArguments(args);
        return fragment;
    }

    public WordInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            word = getArguments().getString(ARG_WORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_word_info, container, false);
        ListView lv = (ListView) v.findViewById(R.id.wordVariants);
        Word w = WordManager.getInstance().getWord(word);
        final ArrayAdapter la = new ArrayAdapter<WordVariant>(getActivity(), android.R.layout.simple_list_item_1, w.getWordVariants());
        lv.setAdapter(la);
        TextView wordDisplay = (TextView) v.findViewById(R.id.wordDisplay);
        wordDisplay.setText(w.getTopLevelName());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                System.out.println(getActivity().getSupportFragmentManager().getBackStackEntryCount());
                ft.replace(R.id.fragment_container, WordVariantInfoFragment.newInstance((WordVariant) la.getItem(position))).commit();
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
