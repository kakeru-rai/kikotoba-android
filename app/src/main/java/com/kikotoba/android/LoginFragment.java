package com.kikotoba.android;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import com.kikotoba.android.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.login_form) View mLoginFormView;
    @BindView(R.id.login_progress) View mProgressView;
    @BindView(R.id.textViewTerm) TextView textViewTerm;
    @BindView(R.id.textViewPrivacy) TextView textViewPrivacy;

    @BindView(R.id.email) EditText mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.email_sign_in_button) Button buttonLoginEmail;
    @BindView(R.id.textViewStartWithoutRegister) TextView textViewStartWithoutRegister;
    @BindView(R.id.loginStartButton) Button loginStartButton;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private View.OnClickListener termListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WebViewActivity.move(getActivity(),
                    Util.getSdPath(getActivity(), "/html/term.html"),
                    getString(R.string.tmpl_term));
        }
    };

    private View.OnClickListener privacyPolicyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WebViewActivity.move(getActivity(),
                    Util.getSdPath(getActivity(), "/html/privacyPolicy.html"),
                    getString(R.string.tmpl_privacy_policy));
        }
    };

    private View.OnClickListener googleLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener facebookLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener emailLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!validateEmail()) {
                return;
            }
            showProgress(true);
            mAuth.signInWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmailAndPassword:onComplete:" + task.isSuccessful());
                            showProgress(false);
                            if (task.isSuccessful()) {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                return;
                            }
                            Log.w(TAG, "signInWithEmailAndPassword", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
            mAuth.createUserWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            showProgress(false);

                            if (task.isSuccessful()) {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                return;
                            }

                            Log.w(TAG, "createUserWithEmail", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            } else if (e instanceof FirebaseAuthUserCollisionException
                                    && ((FirebaseAuthUserCollisionException) e).getErrorCode() == "ERROR_EMAIL_ALREADY_IN_USE") {
                                //

                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            } else if (e instanceof FirebaseAuthActionCodeException) {
                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                            } else if (e instanceof FirebaseAuthRecentLoginRequiredException) {

                            }

                        }
                    });
        }
    };

    private View.OnClickListener anonymouseLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showProgress(true);
            mAuth.signInAnonymously()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                            showProgress(false);
                            if (task.isSuccessful()) {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                            } else {
                                Log.w(TAG, "signInAnonymously", task.getException());
                                Toast.makeText(
                                        getActivity(),
                                        R.string.login_start_failed,
                                        Toast.LENGTH_SHORT)
                                    .show();
                            }
                        }
                    });
        }
    };

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);

        loginStartButton.setOnClickListener(anonymouseLoginListener);
        textViewTerm.setOnClickListener(termListener);
        textViewPrivacy.setOnClickListener(privacyPolicyListener);
        mAuth = FirebaseAuth.getInstance();


        if (true) {
            // アカウント管理機能ができるまで
            return rootView;
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        buttonLoginEmail.setOnClickListener(emailLoginListener);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            textViewStartWithoutRegister.setOnClickListener(anonymouseLoginListener);
        } else {
            textViewStartWithoutRegister.setVisibility(View.GONE);
        }

        return rootView;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }

    private boolean validateEmail() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }

        return !cancel;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView
                    .animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView
                    .animate()
                    .setDuration(shortAnimTime).alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
