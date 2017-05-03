package net.snow69it.listeningworkout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalkThroughFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WalkThroughFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalkThroughFragment extends Fragment {

    public WalkThroughFragment() {
        // Required empty public constructor
    }

    /**
     */
    // TODO: Rename and change types and number of parameters
    public static WalkThroughFragment newInstance() {
        WalkThroughFragment fragment = new WalkThroughFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_walk_through, container, false);
    }

}
