package com.example.passmanager;

import android.content.Intent;
import android.os.Bundle;

import com.example.passmanager.adapter.RecyclerViewAdapter;
import com.example.passmanager.model.Header;
import com.example.passmanager.model.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int INTENT_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<String> categoryNameList;
    private List<ListItem> array;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private DatabaseReference dbRef;
    private List<Item> listItems; //get all the item for the current user


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        listItems = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher_round);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //setup the Adapter
        recyclerViewAdapter = new RecyclerViewAdapter(this, array, currentUserId);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        dbRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listItems.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Item item = ds.getValue(Item.class);
                    listItems.add(item);
                }

                populateList();

                recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, array, currentUserId);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PassActivity.class);
                intent.putStringArrayListExtra("categoryList", (ArrayList<String>) categoryNameList);
                intent.putExtra("activity", "main");
                startActivityForResult(intent, INTENT_REQUEST_CODE);
            }
        });
    }

    private void populateList(){
        categoryNameList = new ArrayList<>();
        array = new ArrayList<>();

        if (listItems.size() > 0) {
            Collections.sort(listItems, new Comparator<Item>() {

                @Override
                public int compare(final Item item1, Item item2) {
                    return item1.getCategory().compareTo(item2.getCategory());
                }

            });
        }

        for(Item i: listItems){
            String category = i.getCategory();
            if(!categoryNameList.contains(category)){
                categoryNameList.add(category);
            }
        }

        array = new ArrayList<>();
        for(String str : categoryNameList){
            Header header = new Header();
            header.setTitle(str);
            array.add(header);
            for(int i=0; i<listItems.size();i++){
                if(listItems.get(i).getCategory().equals(str)){
                    array.add(listItems.get(i));
                }
            }
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        populateList();
        recyclerViewAdapter = new RecyclerViewAdapter(this, array, currentUserId);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void Logout(){
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_logout:
                firebaseAuth.signOut();
                Logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //Get back the data that the PassActivity reply to us
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == INTENT_REQUEST_CODE && resultCode == RESULT_OK){
            assert data != null;
            populateList();
            recyclerViewAdapter = new RecyclerViewAdapter(this, array, currentUserId);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public interface ListItem {
        boolean isHeader();
        String getName();
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //if the user not logged in
        if(currentUser == null){
           Logout();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //if the user is logged in
        if(currentUser != null){
            firebaseAuth.signOut();
            Logout();
        }

    }
}
