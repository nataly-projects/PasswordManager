package com.example.passmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.passmanager.util.Util;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PassActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int INTENT_REQUEST_DETAIL_CODE = 2;

    private Button saveButton;
    private TextInputLayout titleLayout;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout noteLayout;
    private int requestCode;

    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private String categorySpinnerChoose;
    private int categorySpinnerChoosePosition;
    private List<String> list;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private DatabaseReference dbRef;
    private String itemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        titleLayout = findViewById(R.id.title);
        titleLayout.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        usernameLayout = findViewById(R.id.username);
        passwordLayout = findViewById(R.id.password);
        emailLayout = findViewById(R.id.email);
        noteLayout = findViewById(R.id.note);
        saveButton = findViewById(R.id.saveButton);

        spinner  = findViewById(R.id.spinner);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if(position == getCount()){
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() -1;
            }
        };

        list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.category_names)));

        List<String> listFromRecycler = getIntent().getStringArrayListExtra("categoryList");

        if(listFromRecycler != null){
            for(int i = 0; i < list.size(); i++){
                if(!listFromRecycler.contains(list.get(i))){
                    listFromRecycler.add(list.get(i));
                }
            }
        }

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(int i = 0; i < listFromRecycler.size(); i++){
            spinnerAdapter.add(listFromRecycler.get(i));
        }

        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerAdapter.getPosition("Choose Category"), false);
        spinner.setOnItemSelectedListener(this);

        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            itemId = bundle.getString("id");
            titleLayout.getEditText().setText(bundle.getString("title"));
            TextView mainTitle = findViewById(R.id.enter_item);
            mainTitle.setText("Edit Item");
            usernameLayout.getEditText().setText(bundle.getString("username"));
            passwordLayout.getEditText().setText(bundle.getString("password"));
            categorySpinnerChoose = bundle.getString("category");

            spinner.setSelection(listFromRecycler.indexOf(categorySpinnerChoose));
            emailLayout.getEditText().setText(bundle.getString("email"));
            noteLayout.getEditText().setText(bundle.getString("note"));

            requestCode = bundle.getInt("requestCode");
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(requestCode == INTENT_REQUEST_DETAIL_CODE){ //save - after edit an item

                    boolean result = confirmInput();
                    if(result){
                        HashMap<String, Object> itemBodyMap = new HashMap<>();
                        itemBodyMap.put("title", titleLayout.getEditText().getText().toString());
                        itemBodyMap.put("userName", usernameLayout.getEditText().getText().toString());

                        String password = passwordLayout.getEditText().getText().toString();
                        String key = null;
                        try {
                            key = Util.encryptMsg(password);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        itemBodyMap.put("password", key);
                        itemBodyMap.put("category", categorySpinnerChoose);

                        String email = emailLayout.getEditText().getText().toString();
                        if(!email.isEmpty()) {
                            itemBodyMap.put("email", email);
                        } else {
                            itemBodyMap.put("email", "");
                        }

                        String notes =  noteLayout.getEditText().getText().toString();
                        if(!notes.isEmpty()) {
                            itemBodyMap.put("note", notes);
                        } else{
                            itemBodyMap.put("note", "");
                        }

                        dbRef.child(currentUserId).child(itemId).updateChildren(itemBodyMap);
                        intent.putExtra("title", titleLayout.getEditText().getText().toString());
                        intent.putExtra("username", usernameLayout.getEditText().getText().toString());
                        intent.putExtra("password", passwordLayout.getEditText().getText().toString());

                        intent.putExtra("category", categorySpinnerChoose);
                        intent.putExtra("email", email);
                        intent.putExtra("note", notes);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

                else  { //save new item
                    Intent replyIntent = new Intent();

                    boolean result = confirmInput();
                    if(result){
                        DatabaseReference userMessageKeyRef = dbRef.child(currentUserId).push();
                        String itemKeyID = userMessageKeyRef.getKey();

                        Map itemBodyMap = new HashMap<>();
                        itemBodyMap.put("itemId", itemKeyID);
                        itemBodyMap.put("title", titleLayout.getEditText().getText().toString());
                        itemBodyMap.put("userName", usernameLayout.getEditText().getText().toString());

                        String password = passwordLayout.getEditText().getText().toString();
                        String key = null;
                        try {
                            key = Util.encryptMsg(password);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        itemBodyMap.put("password", key);
                        itemBodyMap.put("category", categorySpinnerChoose);
                        if(!emailLayout.getEditText().getText().toString().isEmpty()){
                            itemBodyMap.put("email", emailLayout.getEditText().getText().toString());
                        }
                        if(!noteLayout.getEditText().getText().toString().isEmpty()){
                            itemBodyMap.put("note", noteLayout.getEditText().getText().toString());
                        }

                        Map itemBodyDetail = new HashMap();
                        itemBodyDetail.put(itemKeyID, itemBodyMap);
                        dbRef.child(currentUserId).updateChildren(itemBodyDetail);

                        setResult(RESULT_OK, replyIntent);
                        finish();
                    }

                }
            }
        });
    }

    private boolean validateEmail() {
        String emailInput = emailLayout.getEditText().getText().toString().trim();

        if (!emailInput.isEmpty()) {
            if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
                emailLayout.getEditText().setError("Please enter a valid email address");
                return false;
            }
        }
        return true;
    }

    private boolean validateTitle() {
        String titleInput = titleLayout.getEditText().getText().toString().trim();

        if (titleInput.isEmpty()) {
            titleLayout.getEditText().setError("Field can't be empty");
            return false;
        } else {
            titleLayout.getEditText().setError(null);
            return true;
        }
    }

    private boolean validateUsername() {
        String usernameInput = usernameLayout.getEditText().getText().toString().trim();

        if (usernameInput.isEmpty()) {
            usernameLayout.getEditText().setError("Field can't be empty");
            return false;
        } else {
            usernameLayout.getEditText().setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = passwordLayout.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            passwordLayout.getEditText().setError("Field can't be empty");
            return false;
        } else {
            passwordLayout.getEditText().setError(null);
            return true;
        }
    }

    private boolean validateSpinner(){
        if(spinner.getSelectedItem().equals("Choose Category")) {
            View spinnerView = spinner.getSelectedView();
            TextView errorText = (TextView)spinnerView;

            if(spinnerView != null && spinnerView instanceof TextView){
                spinner.requestFocus();
                errorText.setError("error");
                errorText.setTextColor(Color.RED);
                errorText.setText("Empty");
                return false;
            }

        }
        return true;
    }

    public boolean confirmInput() {
        if (!validateEmail() | !validateUsername() | !validatePassword() | !validateTitle() | !validateSpinner()){
            return false;
        }
        return true;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();

        //create new category
        if(selected.equals("New Category")){
            View v = LayoutInflater.from(PassActivity.this).inflate(R.layout.new_category_dialog, null);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PassActivity.this);
            alertBuilder.setView(v);
            final EditText editText = v.findViewById(R.id.new_category);
            ImageView doneButton = v.findViewById(R.id.done);
            final Dialog dialog = alertBuilder.create();
            dialog.show();

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = editText.getText().toString();
                    if(value == null || value.isEmpty()){

                    } else {
                        categorySpinnerChoose = value;

                        list.add(0, categorySpinnerChoose);
                        spinnerAdapter.insert(categorySpinnerChoose, 0);

                        spinner.setSelection(spinnerAdapter.getPosition(categorySpinnerChoose));
                    }
                    dialog.dismiss();
                }
            });
        } else {
            categorySpinnerChoose = selected;
        }
        categorySpinnerChoosePosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}


