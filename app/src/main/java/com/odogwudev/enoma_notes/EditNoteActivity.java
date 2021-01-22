package com.odogwudev.enoma_notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    EditText edit_title,edit_des;
    Button edit_note,delete_note;
    DatabaseReference reference;
    String Key;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("EditNote") ;
        }
        edit_title = findViewById(R.id.title_edit);
        edit_des = findViewById(R.id.description_edit);
        edit_note = findViewById(R.id.edit_note);
        delete_note= findViewById(R.id.delete_note);

        edit_title.setText(getIntent().getStringExtra("title"));
        edit_des.setText(getIntent().getStringExtra("description"));
        Key = getIntent().getStringExtra("NoteID");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        edit_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Note"+ Key);
                final Map<String,Object> noteMap = new HashMap<>();
                noteMap.put("title", edit_title.getText().toString());
                noteMap.put("description", edit_des.getText().toString());
                noteMap.put("timestamp", ServerValue.TIMESTAMP);
                noteMap.put("NoteID",Key);

                final Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        reference.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditNoteActivity.this, "Note Edited", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EditNoteActivity.this,MainActivity.class);
                                    overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
                                    startActivity(intent);
                                }
                                else
                                {
                                    if (task.getException()!=null) {
                                        Toast.makeText(EditNoteActivity.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });
                    }
                });
                mainThread.start();
            }
        });

        delete_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Note"+ Key);
                reference.removeValue();
                comeout(edit_title.getText().toString(),edit_des.getText().toString());

            }
        });

    }

    private void comeout(String title,String desc) {
        Intent intent = new Intent(EditNoteActivity.this,MainActivity.class);
        overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
        startActivity(intent);
        finish();
    }
}