package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.SignInActivity;
import com.example.whatsapp.Adatpers.FragmentAdapter;
import com.example.whatsapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
     ActivityMainBinding binding;
     FirebaseAuth auth;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
      binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
      binding.tabLayout.setupWithViewPager(binding.viewPager);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
         return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settings:
              Intent intent =new Intent(MainActivity.this,SettingsActivity.class);
             startActivity(intent);
                //     Toast.makeText(this,"Setting clicked",Toast.LENGTH_LONG).show();
                break;
            case R.id.logout:
                auth.signOut();
               Intent intent3=new Intent(MainActivity.this, SignInActivity.class);
               startActivity(intent3);
                break;
            case R.id.groupChat:
                Intent intentw =new Intent(MainActivity.this,GroupChatActivity.class);
                startActivity(intentw);
        }
         return true;
    }
}