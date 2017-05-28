package vocabularylist.projects.austin.vocabularylist;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import vocabularylist.projects.austin.vocabularylist.model.WordVariant;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordVariantInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordVariantInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordVariantInfoFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "variantObject";

    // TODO: Rename and change types of parameters
    private WordVariant variant;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param wv Parameter 1.
     * @return A new instance of fragment WordVariantInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordVariantInfoFragment newInstance(WordVariant wv) {
        WordVariantInfoFragment fragment = new WordVariantInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, wv);
        fragment.setArguments(args);
        return fragment;
    }

    public WordVariantInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            variant = (WordVariant) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_word_variant_info, container, false);
        TextView wordDisplay = (TextView) v.findViewById(R.id.wordVariantDisplay);

        wordDisplay.setText(variant.toString());

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
