package com.example.gurulanguage;

import android.app.Activity;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class CloudDataManager {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mClient;
    Activity ctx;
    CloudDataManager(Activity ctx){
        this.ctx = ctx;
        mAuth = FirebaseAuth.getInstance();
        createRequest();
    }
   public FirebaseUser getProfile(){
       mAuth = FirebaseAuth.getInstance();
       FirebaseUser currentUser = mAuth.getCurrentUser();
       return currentUser;
   }
    private void createRequest(){
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(ctx.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mClient = GoogleSignIn.getClient(ctx, signInOptions);

    }
    public void userLogIn(String token, OnCompleteListener<AuthResult> listener){
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        if (mAuth != null) {
            mAuth.signInWithCredential(credential).addOnCompleteListener(ctx, listener);
        } else {
            // handle the case where mAuth is null
        }
    }
   public void startLoginActivity(ActivityResultLauncher activityResultLauncher){
       Intent intent = mClient.getSignInIntent();
       activityResultLauncher.launch(intent);
   }

}
