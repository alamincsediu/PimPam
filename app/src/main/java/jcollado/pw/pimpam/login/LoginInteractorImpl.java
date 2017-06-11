package jcollado.pw.pimpam.login;

import android.support.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import jcollado.pw.pimpam.utils.FirebaseModule;


/**
 * Created by jcolladosp on 10/06/2017.
 */

public class LoginInteractorImpl implements LoginInteractor{
    private FirebaseAuth mAuth = FirebaseModule.getInstance().getmAuth();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private boolean signInWithGoogle;

    @Override
    public void sendEmailLoginRequest(String email, String password, final OnLoginFinishedListener listener) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                           listener.onLoginMailFail(task.getException().getMessage());
                        } else {
                            listener.onLoginMailSuccess();
                        }
                    }
                });

    }

    @Override
    public void sendEmailRegisterRequest(String email, String password, final OnLoginFinishedListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            listener.onRegisterMailFail(task.getException().getMessage());
                        } else {
                            listener.onRegisterMailSuccess();
                        }

                    }
                });
    }

    @Override
    public void restorePasswordRequest(String password, final OnLoginFinishedListener listener) {
        mAuth.sendPasswordResetEmail(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onRestorePasswordFail();

                        } else {
                            listener.onRestorePasswordSuccess();
                        }

                    }
                });

    }

    @Override
    public void loginWithGoogleRequest(OnLoginFinishedListener listener) {
        signInWithGoogle = true;
    }

    @Override
    public void authListener(final OnLoginFinishedListener listener){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    if(signInWithGoogle){
                      listener.onLoginGoogleSuccess();

                    }
                    else{
                        listener.onRegisterMailSuccess();

                    }

                } else {
                    // User is signed out
                    // Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void addAuthStateListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void removeAuthStateListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}