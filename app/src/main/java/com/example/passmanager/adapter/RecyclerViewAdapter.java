package com.example.passmanager.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passmanager.DetailActivity;
import com.example.passmanager.MainActivity;
import com.example.passmanager.R;
import com.example.passmanager.model.Header;
import com.example.passmanager.model.Item;
import com.example.passmanager.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<MainActivity.ListItem> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private LayoutInflater inflater;
    private HashMap<Integer, RecyclerView.ViewHolder> holderList;
    private DatabaseReference dbRef;
    private String currentId;

    public RecyclerViewAdapter(Context context, List<MainActivity.ListItem> itemList, String currentId) {
        this.context = context;
        this.itemList = itemList;
        holderList = new HashMap<>();
        this.currentId = currentId;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if(viewType == Util.LAYOUT_TITLE){
            View view = LayoutInflater.from(context).inflate(R.layout.group_title, viewGroup, false);
            return new HeaderViewHolder(view);
        }
        else if(viewType == Util.LAYOUT_ITEM){
            View view = LayoutInflater.from(context).inflate(R.layout.list_row, viewGroup, false);
            return new ItemViewHolder(view, context);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(!holderList.containsKey(position)){
            holderList.put(position,viewHolder);
        }
        if(viewHolder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            Header header = (Header) itemList.get(position);
            headerViewHolder.header.setText(header.getTitle());
        }
        else if(viewHolder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

            Item item = (Item) itemList.get(position);
            itemViewHolder.title.setText(item.getTitle());
            itemViewHolder.username.setText(item.getUserName());
            itemViewHolder.password.setText(item.getPassword());
        }
    }

    public boolean checkCategory(String name){
        for(int i = 0; i < itemList.size(); i++){
            if(itemList.get(i).getName().equals(name) && !itemList.get(i).isHeader()){
                return true;
            }
        }
        return false;
    }

    public int checkHeaderPosition(String name){
        int index = -1;
        for(int i = 0; i < itemList.size(); i++){
            if(itemList.get(i).getName().equals(name) && itemList.get(i).isHeader()){
                index = i;
            }
        }
        return index;
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(itemList.get(position).isHeader()){
            return Util.LAYOUT_TITLE;
        } else {
            return Util.LAYOUT_ITEM;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView header;
        private ConstraintLayout layout;

        private ImageView expandButton;

        public HeaderViewHolder(@NonNull View view) {
            super(view);

            header = view.findViewById(R.id.group_title);
            layout = view.findViewById(R.id.group_layout);
            expandButton = view.findViewById(R.id.expand_button);

            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            Header header = (Header)itemList.get(position);
            if(header.isExpand()){
                expandButton.setImageResource(R.drawable.ic_arrow_drop_up);
                header.setExpand(false);
                for(int i = position+1; i < itemList.size(); i++){

                    if(itemList.get(i).isHeader()){ //is header - break the loop
                        break;
                    } else{ //is item

                        View view = holderList.get(i).itemView;
                        ViewGroup.LayoutParams params = view.getLayoutParams();
                        params.height = 0;
                        view.setLayoutParams(params);
                        view.setVisibility(View.GONE);
                    }
                }
            } else {
                expandButton.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                header.setExpand(true);
                for(int i = position+1; i < itemList.size(); i++){

                    if(itemList.get(i).isHeader()){ //is header - break the loop
                        break;

                    }
                    else{ //is item
                        View view = holderList.get(i).itemView;
                        ViewGroup.LayoutParams params = view.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        view.setLayoutParams(params);
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }

        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView username;
        private TextView password;
        private Button showPasswordButton;
        private Button deleteButton;
        private CardView cardView;

        public ItemViewHolder(@NonNull View view, Context ctx) {
            super(view);
            context = ctx;

            title = view.findViewById(R.id.item_title);
            username = view.findViewById(R.id.item_username);
            password = view.findViewById(R.id.item_password);

            password.setVisibility(View.INVISIBLE);

            deleteButton = view.findViewById(R.id.delete_button);
            cardView = view.findViewById(R.id.cardView);
            showPasswordButton = view.findViewById(R.id.showPassword);

            cardView.setOnClickListener(this);
            showPasswordButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Item item = (Item) itemList.get(position);

            switch (v.getId()){

                case R.id.showPassword:
                    try {
                        showPassword(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.cardView:
                    try {
                        editItem(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.delete_button:
                    deleteItem(item);
                    break;
            }
        }

        private void showPassword(final Item item) throws Exception {

            if(password.getVisibility() == View.INVISIBLE){

                showPasswordButton.setBackgroundResource(R.drawable.ic_visibility_off_24px);
                String data = item.getPassword();
                String pass = Util.decryptMsg(data);
                password.setText(pass);
                password.setVisibility(View.VISIBLE);
            }
            else if(password.getVisibility() == View.VISIBLE){

                showPasswordButton.setBackgroundResource(R.drawable.ic_remove_red_eye_black_24dp);
                password.setVisibility(View.INVISIBLE);
            }
        }

        private void editItem(final Item item) throws Exception {
            List<String> list = new ArrayList<>();

            for(int i = 0; i<itemList.size(); i++){
                if(!list.contains(itemList.get(i).getName()) && !itemList.get(i).isHeader()){
                    list.add(itemList.get(i).getName());
                }
            }

            Intent intent = new Intent(context, DetailActivity.class);

            intent.putExtra("id", item.getItemId());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("username", item.getUserName());

            String data = item.getPassword();
            String pass = Util.decryptMsg(data);
            intent.putExtra("password", pass);

            intent.putExtra("category", item.getCategory());
            intent.putExtra("email", item.getEmail());
            intent.putExtra("note", item.getNote());
            intent.putStringArrayListExtra("categoryList", (ArrayList<String>)list);

            context.startActivity(intent);
        }

        private void deleteItem(final Item item) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_pop, null);

            Button yesButton = view.findViewById(R.id.yes_button);
            Button noButton = view.findViewById(R.id.no_button);

            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String categoryName = item.getCategory();

                    int position = getAdapterPosition();
                    dbRef.child("Users").child(currentId).child(item.getItemId()).removeValue();

                    itemList.remove(position);

                    notifyItemRemoved(position);

                    if(!checkCategory(categoryName)){

                        int headerPosition = checkHeaderPosition(categoryName);
                        itemList.remove(headerPosition);
                        notifyItemRemoved(headerPosition);
                    }

                    alertDialog.dismiss();
                }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }

    }
}
