package com.example.ekene.blogzone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private Menu titleBar;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private Query mQuery;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean myPosts = false;
    private FirebaseRecyclerAdapter<Blogzone, BlogzoneViewHolder> FBRAQ,FBRA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blogzone");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser()==null){
                    Intent loginIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);startActivity(loginIntent);
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FBRA = new FirebaseRecyclerAdapter<Blogzone, BlogzoneViewHolder>(
                Blogzone.class,
                R.layout.card_items,
                BlogzoneViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogzoneViewHolder viewHolder, Blogzone model, int position) {
                final String post_key = getRef(position).getKey().toString();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setImageUrl(getApplicationContext(), model.getImageUrl());
                viewHolder.setUserName(model.getUsername());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleActivity = new Intent(MainActivity.this, SinglePostActivity.class);
                        singleActivity.putExtra("PostID", post_key);
                        startActivity(singleActivity);
                    }
                });
            }
        };
        if (mAuth.getCurrentUser() != null) {
            mQuery = mDatabase.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());

            FBRAQ = new FirebaseRecyclerAdapter<Blogzone, BlogzoneViewHolder>(
                    Blogzone.class,
                    R.layout.card_items,
                    BlogzoneViewHolder.class,
                    mQuery
            ) {
                @Override
                protected void populateViewHolder(BlogzoneViewHolder viewHolder, Blogzone model, int position) {
                    final String post_key = getRef(position).getKey().toString();
                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setImageUrl(getApplicationContext(), model.getImageUrl());
                    viewHolder.setUserName(model.getUsername());
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent singleActivity = new Intent(MainActivity.this, SinglePostActivity.class);
                            singleActivity.putExtra("PostID", post_key);
                            startActivity(singleActivity);
                        }
                    });
                }
            };
        }


        recyclerView.setAdapter(FBRA);
    }

    public static class BlogzoneViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public BlogzoneViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.post_title_txtview);
            post_title.setText(title);
        }
        public void setImageUrl(Context ctx, String imageUrl){
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(imageUrl).into(post_image);
        }
        public void setUserName(String userName){
            TextView postUserName = mView.findViewById(R.id.post_user);
            postUserName.setText(userName);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        titleBar = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //Toast.makeText(this, "Kaam Chalu Hai", Toast.LENGTH_SHORT).show();
            MenuItem menuItem = titleBar.findItem(R.id.action_settings);
            if (!myPosts){
                recyclerView.swapAdapter(FBRAQ,false);
                menuItem.setTitle("All Posts");
            }
            else {
                recyclerView.swapAdapter(FBRA, false);
                menuItem.setTitle("My Posts");
            }
            myPosts = !myPosts;
        }
       else if (id == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }
       else if (id == R.id.logout){
            mAuth.signOut();
            Intent logouIntent = new Intent(MainActivity.this, RegisterActivity.class);
            logouIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logouIntent);
            MainActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
