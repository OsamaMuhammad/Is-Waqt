package com.example.iswakt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class FeedsActivity extends AppCompatActivity implements SocialFragment.OnFragmentInteractionListener,
        NewsFragment.OnFragmentInteractionListener,UpdatesFragment.OnFragmentInteractionListener{


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);

        mAuth=FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        final BottomAppBar bottomAppBar=(BottomAppBar)findViewById(R.id.bottom_appbar);
        setSupportActionBar(bottomAppBar);
        TabLayout tabLayout =(TabLayout) findViewById(R.id.tablayout);

        TabItem tabSocial = findViewById(R.id.tab_social);
        TabItem tabNews = findViewById(R.id.tab_news);
        TabItem tabUpdates = findViewById(R.id.tab_updates);

        ViewPager viewPager = findViewById(R.id.viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.feeds_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedsActivity.this,AddUpdateActivity.class));
            }
        });


        ViewPagerAdapter pageAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position==0) {
                    bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);

                }
                if(position==1) {
                    bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);

                }
                if(position==2) {

                    bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=new MenuInflater(this);
        menuInflater.inflate(R.menu.feed_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

          if(item.getItemId()==R.id.feed_settings){
                Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show();
            }
          if(item.getItemId()==R.id.feed_logout){
              signOut();
            }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void signOut() {
        // Firebase sign out

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth.signOut();
                startActivity(new Intent(FeedsActivity.this, LoginActivity.class));
                finish();
            }
        }, 1000);

        //facebook signout
        if(LoginManager.getInstance()!=null){
            LoginManager.getInstance().logOut();
        }


        //google signout;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_cloud_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(getApplicationContext(),gso);

        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

//        googleSignInClient.revokeAccess().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                    }
//                });
    }
}



