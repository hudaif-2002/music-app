package com.example.audioplayer;

import static com.example.audioplayer.MusicAdapter.mFiles;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;


public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name,artist_name,duration_played,duration_total;
    ImageView cover_art,nextBtn,preBtn,backBtn,shuffledBtn,repeatBtn,backbtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int postion=-1;
    static  ArrayList<MusicFiles> listSongs=new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler=new Handler();
    private  Thread playThread,prevThread,nextThread,backThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        getIntentMethod();
        song_name.setText(listSongs.get(postion).getTitle());
        artist_name.setText(listSongs.get(postion).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        backThreadBtn();
        super.onResume();
    }

    private void backThreadBtn() {
        backThread=new Thread(){
            @Override
            public void run() {
                super.run();
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        backBtnClicked();
                    }
                });
            }
        };
        backThread.start();
    }

    private void backBtnClicked() {
        if(mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_24);
            mediaPlayer.stop();
//            mediaPlayer.release();

            mediaPlayer.setOnCompletionListener(this);
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{
            mediaPlayer.stop();
            mediaPlayer.setOnCompletionListener(this);
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    private void prevThreadBtn() {
        prevThread=new Thread(){
            @Override
            public void run() {
                super.run();
                preBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();

    }

    private void prevBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            postion=((postion+1) < 0 ? (listSongs.size()-1) : (postion-1));
            uri=Uri.parse(listSongs.get(postion).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(postion).getTitle());
            artist_name.setText(listSongs.get(postion).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.baseline_paus);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            postion=((postion+1) < 0 ? (listSongs.size()-1) : (postion-1));
            uri=Uri.parse(listSongs.get(postion).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(postion).getTitle());
            artist_name.setText(listSongs.get(postion).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.baseline_play_arrow_24);

        }
    }

    private void nextThreadBtn() {
        nextThread=new Thread(){
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();

    }

    private void nextBtnClicked() {
        Bitmap bitmap=null;
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            postion=((postion+1)%listSongs.size());
            uri=Uri.parse(listSongs.get(postion).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(postion).getTitle());
            artist_name.setText(listSongs.get(postion).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.baseline_paus);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            postion=((postion+1)%listSongs.size());
            uri=Uri.parse(listSongs.get(postion).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(postion).getTitle());
            artist_name.setText(listSongs.get(postion).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.baseline_play_arrow_24);

        }
    }

    private void playThreadBtn() {
        playThread=new Thread(){
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseBtnClicked() {
       if(mediaPlayer.isPlaying()){
           playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_24);
           mediaPlayer.pause();
           seekBar.setMax(mediaPlayer.getDuration()/1000);
           PlayerActivity.this.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   if(mediaPlayer!=null){
                       int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                       seekBar.setProgress(mCurrentPosition);
                   }
                   handler.postDelayed(this,1000);
               }
           });
       }else{
           playPauseBtn.setImageResource(R.drawable.baseline_paus);
           mediaPlayer.start();
           seekBar.setMax(mediaPlayer.getDuration()/1000);
           PlayerActivity.this.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   if(mediaPlayer!=null){
                       int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                       seekBar.setProgress(mCurrentPosition);
                   }
                   handler.postDelayed(this,1000);
               }
           });
       }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout="";
        String totalNew="";
        String seconds=String.valueOf(mCurrentPosition%60);
        String minutes=String.valueOf(mCurrentPosition/60);
        totalout=minutes+":"+seconds;
        totalNew=minutes+":"+"0"+seconds;
        if(seconds.length()==1){
            return totalNew;
        }
        else{
            return totalout;
        }
    }

    private void getIntentMethod() {
            postion=getIntent().getIntExtra("postion",-1);
//            String sender=getIntent().getStringExtra("sender");

                listSongs = mFiles;


            if(listSongs!=null){
                playPauseBtn.setImageResource(R.drawable.baseline_paus);
                uri=Uri.parse(listSongs.get(postion).getPath());

            }
            if(mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
            }
            else{
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
            }
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            metaData(uri);

    }

    private void initView() {
        song_name=(TextView) findViewById(R.id.song_name);
        artist_name=(TextView) findViewById(R.id.song_artist);
        duration_played=(TextView) findViewById(R.id.durationPlayed);
        duration_total=(TextView) findViewById(R.id.durationTotal);
        cover_art=(ImageView) findViewById(R.id.cover_art);
        nextBtn=(ImageView) findViewById(R.id.id_next);
        preBtn=(ImageView) findViewById(R.id.id_prev);
        backBtn=(ImageView) findViewById(R.id.back_btn);
        shuffledBtn=(ImageView) findViewById(R.id.id_shuffle);
        repeatBtn=(ImageView) findViewById(R.id.id_repeat);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        playPauseBtn=(FloatingActionButton) findViewById(R.id.play_pause);
        backbtn=(ImageView)findViewById(R.id.back_btn);
    }

    private void metaData(Uri uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(listSongs.get(postion).getDuration())/1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art=retriever.getEmbeddedPicture();

        Bitmap bitmap;
        if(art!=null){
//            Glide.with(this).asBitmap().load(art).into(cover_art);

            bitmap=BitmapFactory.decodeByteArray(art,0,art.length);
               ImageAnimation(this,cover_art,bitmap);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if(swatch!=null){
                        ImageView gredient=findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer=findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getTitleTextColor());
                    }else{
                        ImageView gredient=findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer=findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }

            });
        }
        else{
//            Glide.with(this).asBitmap().load(R.drawable.song_icon1).into(cover_art);
            ImageView gredient=findViewById(R.id.imageViewGradient);
            RelativeLayout mContainer=findViewById(R.id.mContainer);
            gredient.setBackgroundResource(R.drawable.gredient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }

    }

    //animation
    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap){
        Animation animeOut= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
       final Animation animeIn= AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                    Glide.with(context).load(bitmap).into(imageView);
                    animeIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    imageView.startAnimation(animeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animeOut);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextBtnClicked();
        if (mediaPlayer != null) {

            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}
