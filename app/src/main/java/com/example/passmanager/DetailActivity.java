package com.example.passmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class DetailActivity extends AppCompatActivity {

    private static final int INTENT_REQUEST_DETAIL_CODE = 2;

    private TextView mainTitle;
    private EditText title;
    private EditText username;
    private EditText pass;
    private EditText category;
    private EditText email;
    private EditText note;
    private ImageButton editButton;

    private String itemId;
    private List<String> listFromRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mainTitle = findViewById(R.id.main_title);
        title = findViewById(R.id.title_edit);
        username = findViewById(R.id.username_edit);
        pass = findViewById(R.id.password_edit);
        category = findViewById(R.id.category_edit);
        email = findViewById(R.id.email_edit);
        note = findViewById(R.id.note_edit);
        editButton = findViewById(R.id.editButton);

        //make the EditText not editable
        title.setFocusable(false);
        username.setFocusable(false);
        pass.setFocusable(false);
        category.setFocusable(false);
        email.setFocusable(false);
        note.setFocusable(false);

        listFromRecycler = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            itemId = bundle.getString("id");
            mainTitle.setText(bundle.getString("title"));

            title.setText(bundle.getString("title"));
            username.setText(bundle.getString("username"));
            pass.setText(bundle.getString("password"));
            category.setText(bundle.getString("category"));
            listFromRecycler = getIntent().getStringArrayListExtra("categoryList");

            String emailText = bundle.getString("email");
            if(emailText == null || emailText.isEmpty()){
                TextView emailTitle = findViewById(R.id.email);
                emailTitle.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
            } else {
                email.setText(emailText);
            }

            String noteText = bundle.getString("note");
            if(noteText == null || noteText.isEmpty()){
                TextView noteTitle = findViewById(R.id.note);
                noteTitle.setVisibility(View.GONE);
                note.setVisibility(View.GONE);
            } else {
                note.setText(noteText);
            }
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, PassActivity.class);

                intent.putExtra("id", itemId);
                intent.putExtra("title", title.getText().toString());
                intent.putExtra("username", username.getText().toString());
                intent.putExtra("password", pass.getText().toString());
                intent.putExtra("category", category.getText().toString());
                intent.putExtra("email", email.getText().toString());
                intent.putExtra("note", note.getText().toString());
                intent.putExtra("requestCode", INTENT_REQUEST_DETAIL_CODE);
                intent.putStringArrayListExtra("categoryList", (ArrayList<String>) listFromRecycler);
                startActivityForResult(intent, INTENT_REQUEST_DETAIL_CODE);
            }
        });

    }

    //after edit
   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode == INTENT_REQUEST_DETAIL_CODE && resultCode == RESULT_OK) {
           assert data != null;

           mainTitle.setText(data.getStringExtra("title"));
           title.setText(data.getStringExtra("title"));
           username.setText(data.getStringExtra("username"));
           pass.setText(data.getStringExtra("password"));
           category.setText(data.getStringExtra("category"));

           String emailText = data.getStringExtra("email");
           TextView emailTitle = findViewById(R.id.email);
           if(emailText == null || emailText.isEmpty()){
               emailTitle.setVisibility(View.GONE);
               email.setVisibility(View.GONE);
           } else {
               email.setText(emailText);
               emailTitle.setVisibility(View.VISIBLE);
               email.setVisibility(View.VISIBLE);
           }

           String noteText = data.getStringExtra("note");
           TextView noteTitle = findViewById(R.id.note);
           if(noteText == null || noteText.isEmpty()){
               noteTitle.setVisibility(View.GONE);
               note.setVisibility(View.GONE);
           } else {
               note.setText(noteText);
               noteTitle.setVisibility(View.VISIBLE);
               note.setVisibility(View.VISIBLE);
           }
       }

    }
}
