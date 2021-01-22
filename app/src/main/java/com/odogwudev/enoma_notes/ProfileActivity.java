package com.odogwudev.enoma_notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    ImageView profile_image;
    TextView profile_usr;
    LinearLayout logout,delete_data;
    FirebaseAuth auth;
    FirebaseUser user;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        profile_image = findViewById(R.id.profile_image);
        profile_usr = findViewById(R.id.profile_usr);
        logout = findViewById(R.id.logout_profile);
        delete_data = findViewById(R.id.delete_data);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //On this button click the Whole User data from Database is cleared
        delete_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this,R.style.CustomAlertDialog);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.delete_dialog, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
                TextView delete = dialog.findViewById(R.id.delete);
                if (delete != null) {
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(user.getUid());
                            reference.removeValue();
                            dialog.dismiss();
                            Toast.makeText(ProfileActivity.this,"Your Notes are deleted",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                TextView cancel = dialog.findViewById(R.id.cancel);
                if (cancel != null) {
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }

            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        //This code part is helpful in getting the User name and his profile Image
        //when he login with his Google Account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account!=null)
        {
            String person_name = account.getDisplayName();
            profile_usr.setText(person_name);
            Uri person_img = account.getPhotoUrl();
            Glide.with(this).load(person_img).into(profile_image);
        }

        //On this button click the User is Logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut();
                FirebaseAuth.getInstance().signOut();
                comeout();
            }
        });

        //On this button click the User is navigated back to MainActivity from ProfileActivity
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mainActivity();
    }

    private void mainActivity() {
        Intent back = new Intent(ProfileActivity.this,MainActivity.class);
        startActivity(back);
    }

    private void comeout() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}