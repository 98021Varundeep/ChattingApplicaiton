package com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.tv.TvContract;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.MainActivity;
import com.example.whatsapp.Models.Users;
import com.example.whatsapp.R;
import com.example.whatsapp.databinding.ActivitySignInBinding;
import com.example.whatsapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
      ActivitySignInBinding binding;
      ProgressDialog progressDialog;
      FirebaseDatabase database;
      private FirebaseAuth auth;

       GoogleSignInClient mGoogleSignInClient;

      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        auth  =FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        progressDialog=new ProgressDialog(SignInActivity.this);
         progressDialog.setTitle("Login");
         progressDialog.setMessage("Loging to your Account\n");

          GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                  .requestIdToken(getString(R.string.default_web_client_id))
                  .requestEmail()
                  .build();
       mGoogleSignInClient= GoogleSignIn.getClient(this,gso) ;


      binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
             @Override
              public void onClick(View view) {
              if(binding.etEmail.getText().toString().isEmpty()){
                   binding.etEmail.setError("Enter Your email");
                 return;
              }
                 if(binding.etPassword.getText().toString().isEmpty()){
                     binding.etPassword.setError("Enter Your Password");
                     return;
                 }

                 progressDialog.show();
            auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(),binding.etPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Intent intent=new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);


                            }
                           else
                               Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });
             }
         });
         binding.tvclickSignUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                 startActivity(intent);
             }
         });
         binding.btngoogle.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
             signIn();
             }
         });
     if(auth.getCurrentUser()!=null){
         Intent intent=new Intent(SignInActivity.this,MainActivity.class);
         startActivity(intent);
     }
    }
    int RC_SIGN_IN=65;
    private void signIn(){
          Intent signInIntent=mGoogleSignInClient.getSignInIntent();

          startActivityForResult(signInIntent,RC_SIGN_IN);
  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      if(requestCode==RC_SIGN_IN){
          Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);
          try{
              GoogleSignInAccount account=task.getResult(ApiException.class);
              Log.d("TAG","firebaseAuthWithGoogle:" +account.getId());
              fireBaseAuthWithGoogle(account.getIdToken());
          }
          catch (ApiException e){
              Log.w("TAG","Google sign failed",e);

          }
      }
      }
   private void fireBaseAuthWithGoogle(String idToken){
       AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
       auth.signInWithCredential(credential)
               .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Log.d("TAG","Sign in with Creadentaila SUCCess");
                           FirebaseUser user=auth.getCurrentUser();
                       //    updataUI(user);
                           Users users=new Users();
                           users.setUserId(user.getUid());
                           users.setUserName(user.getDisplayName());
                           users.setProfilepic(user.getPhotoUrl().toString());
                           database.getReference().child("Users").child(user.getUid()).setValue(users);
                           Intent intent =new Intent(SignInActivity.this,MainActivity.class);
                           startActivity(intent);

                       }
                       else
                       {
                           Log.w("TAG","falied",task.getException());
                          // Snackbar.make(mBinding.mainLayout,"Failed",Snackbar.LENGTH_SHORT).show();
                        //   updateUI(null);

                       }
                   }
               });
   }
}