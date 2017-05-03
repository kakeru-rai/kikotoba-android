package net.snow69it.listeningworkout;


import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    protected View inflateRootView(int layoutResourceId, ViewGroup container, LayoutInflater inflater) {
        View rootView = inflater.inflate(layoutResourceId, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    protected FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
