package com.odogwudev.enoma_notes;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView Username_img,search_btn;
    FirebaseAuth auth;FirebaseUser user;
    TextView Username;
    EditText search;
    RecyclerView recyclerView;
    DatabaseReference reference;
    ArrayList<Model> list;
    NoteAdapter adapter;
    LottieAnimationView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //Going from MainActivity to ProfileActivity
        Username_img = findViewById(R.id.Username_img);
        Username_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(profile);
            }
        });

        Username = findViewById(R.id.Username);
        auth = FirebaseAuth.getInstance();

        //On Fab button click we will call NoteActivity to create to Note Task
        FloatingActionButton fab = findViewById(R.id.add_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NoteDetailActivity.class);
                startActivity(intent);
            }
        });

        //This code part is helpful in getting the User name and his profile Image
        //when he login with his Google Account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account!=null)
        {
            String person_name = account.getDisplayName();
            String name = getResources().getString(R.string.Hello) + person_name;
            Username.setText(name);
            Uri person_img = account.getPhotoUrl();
            Glide.with(this).load(person_img).into(Username_img);
        }

        recyclerView = findViewById(R.id.recyclerview);
        list = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user!=null){
            reference = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        }
        reference.keepSynced(true);

        empty = findViewById(R.id.empty);


        search = findViewById(R.id.search);
        search_btn = findViewById(R.id.search_btn);


    }

    public void searchData(View view) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        Query query = ref.orderByChild("title").equalTo(search.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Model model = postSnapshot.getValue(Model.class);
                    list.add(model);

                }
                adapter = new NoteAdapter(MainActivity.this, list);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    Refresh(1000);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void Refresh(int time) {
        final Handler handler= new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showAll();
            }
        };
        handler.postDelayed(runnable,time);
    }

    public void showAll() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Model model =dataSnapshot1.getValue(Model.class);
                    list.add(model);

                }
                adapter = new NoteAdapter(MainActivity.this,list);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter.notifyDataSetChanged();
                if (adapter.getItemCount()==0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showAll();
    }
}