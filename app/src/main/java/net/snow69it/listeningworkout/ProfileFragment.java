package net.snow69it.listeningworkout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class ProfileFragment extends Fragment {

    @BindView(R.id.buttonLogout) Button buttonLogout;
    @BindView(R.id.buttonLogin) Button buttonLogin;
    @BindView(R.id.textViewName)
    TextView textViewName;

    public ProfileFragment() {
    }

    /**
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            getActivity().finish();
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            return rootView;
        }

        textViewName.setText(getDisplayName(user));



        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        return rootView;
    }

    private String getDisplayName(FirebaseUser user) {
        if (!TextUtils.isEmpty(user.getDisplayName())) {
            return user.getDisplayName();
        } else if (!TextUtils.isEmpty(user.getEmail())) {
            return user.getEmail();
        } else {
            return getString(R.string.tmpl_guest);
        }
    }
}
