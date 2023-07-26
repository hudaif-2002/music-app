package com.example.audioplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final int REQUEST_CODE=1;
    static  ArrayList<MusicFiles> musicFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();

    }

    //permission for reading  file
    private void permission() {
       if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
       {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
       }else{
//           Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
           musicFiles=getAllAudio(this);
           initPager();
       }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                musicFiles=getAllAudio(this);
                initPager();
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        }
    }

    //song and album bar
    private void initPager() {
        ViewPager viewPager=findViewById(R.id.viewpager);
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragements(new SongsFragment(),"Songs");
        viewPagerAdapter.addFragements(new AlbumFragment(),"Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    public static class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        void addFragements(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public static ArrayList<MusicFiles> getAllAudio(Context context){
        ArrayList<MusicFiles> tempAudioList=new ArrayList<>();
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection={
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
        };
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
        if(cursor!=null){
            while (cursor.moveToNext()){
                String album=cursor.getString(0);
                String title=cursor.getString(1);
                String duration=cursor.getString(2);
                String path=cursor.getString(3);
                String artist=cursor.getString(4);

                MusicFiles musicFiles=new MusicFiles(path,title,artist,album,duration);
                Log.e("Path :"+path,"Album : "+album);
                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return  tempAudioList;
    }

    @Override
    //searching
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem=menu.findItem(R.id.search_option);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    //adding to search filter
    public boolean onQueryTextChange(String newText) {
        String userInput=newText.toLowerCase();
        ArrayList<MusicFiles> myFiles=new ArrayList<>();
        for(MusicFiles song : musicFiles){
            if(song.getTitle().toLowerCase().contains(userInput)){
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(myFiles);
        return true;
    }
}