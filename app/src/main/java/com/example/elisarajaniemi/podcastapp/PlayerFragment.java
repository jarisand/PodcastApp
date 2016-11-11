package com.example.elisarajaniemi.podcastapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.TextView;

/**
 * Created by Kade on 28.10.2016.
 */

public class PlayerFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, OnBufferingUpdateListener, OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private ImageView sleepBtn, replayBtn, playBtn, forwardBtn, speedBtn, previousBtn, nextBtn, queueBtn, playlistBtn, favoriteBtn, shareBtn;
    private SeekBar seekbar;
    private TextView currentTime, fullTime;
    private int mediaFileLengthInMilliseconds;
    private final Handler handler = new Handler();
    private boolean playServiceStarted;
    private Utilities utils;
    private PodcastItem piFromService, piFromClick, pi2;
    public static final String START_SERVICE = "start_service";


    MainActivity mActivity;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.mActivity = (MainActivity) getActivity();
        playServiceStarted = mActivity.isMyServiceRunning(PlayService.class);
        utils = new Utilities();
        View view = inflater.inflate(R.layout.play_screen, container, false);
        piFromService = mActivity.pServ.getPodcastObject();
        if( getArguments() == null) piFromClick = piFromService;
        else{
            piFromClick = (PodcastItem) getArguments().getSerializable("episode");
            pi2 = (PodcastItem) getArguments().getSerializable("podcastItem");
            if(pi2 != null) {
                piFromClick = pi2;
                System.out.println("Podcast URL ELSE IF: " + piFromClick.url);
            }
        }



        sleepBtn = (ImageView) view.findViewById(R.id.sleepBtn);
        replayBtn = (ImageView) view.findViewById(R.id.replayBtn);
        playBtn = (ImageView) view.findViewById(R.id.playBtn);
        forwardBtn = (ImageView) view.findViewById(R.id.forwardBtn);
        speedBtn = (ImageView) view.findViewById(R.id.speedBtn);
        previousBtn = (ImageView) view.findViewById(R.id.previousBtn);
        nextBtn = (ImageView) view.findViewById(R.id.nextBtn);
        queueBtn = (ImageView) view.findViewById(R.id.queueBtn);
        playlistBtn = (ImageView) view.findViewById(R.id.playlistBtn);
        favoriteBtn = (ImageView) view.findViewById(R.id.favoriteBtn);
        shareBtn = (ImageView) view.findViewById(R.id.shareBtn);

        sleepBtn.setOnClickListener(this);
        replayBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        forwardBtn.setOnClickListener(this);
        speedBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        queueBtn.setOnClickListener(this);
        playlistBtn.setOnClickListener(this);
        favoriteBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);

        seekbar = (SeekBar) view.findViewById(R.id.seekBar);
        seekbar.setMax(99);
        seekbar.setOnTouchListener(this);

        currentTime = (TextView) view.findViewById(R.id.currentTime) ;
        fullTime = (TextView) view.findViewById(R.id.fullTime) ;
        currentTime.setText("00:00");
        fullTime.setText("00:00");


        seekbar.setOnSeekBarChangeListener(this);
        utils = new Utilities();
        if(!playServiceStarted) {
            Intent podcast = new Intent(getActivity(), PlayService.class);
            podcast.setAction(START_SERVICE);
            getActivity().startService(podcast);

            playServiceStarted = true;
            mActivity.pServ.setPodcastObject(piFromClick);
            mActivity.pServ.setAudioPath();
            mActivity.pServ.mPlayer.setOnBufferingUpdateListener(this);
            mActivity.pServ.mPlayer.setOnCompletionListener(this);
        }
        //if(mActivity.pServ.isPaused() || mActivity.pServ.mPlayer.isPlaying()){
        else{
            mediaFileLengthInMilliseconds = mActivity.pServ.mPlayer.getDuration(); // gets the song length in milliseconds from URL
            updateProgressBar();
            playBtn.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            System.out.println("PI FROM CLICK " + piFromClick);
            System.out.println("PI FROM SERVICE " + piFromService);
            if(!piFromClick.url.equals(piFromService.url)){

                mediaFileLengthInMilliseconds = 0;
                mActivity.pServ.stopMusic();
                mActivity.pServ.initPlayer();
                mActivity.pServ.setPodcastObject(piFromClick);
                mActivity.pServ.setAudioPath();
                playBtn.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                mActivity.pServ.mPlayer.setOnBufferingUpdateListener(this);
                mActivity.pServ.mPlayer.setOnCompletionListener(this);
            }
        }
        return view;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sleepBtn:

                break;
            case R.id.replayBtn:
                mActivity.pServ.setPosition( mActivity.pServ.mPlayer.getCurrentPosition() - 10000);
                mActivity.pServ.mPlayer.seekTo( mActivity.pServ.mPlayer.getCurrentPosition() - 10000);
                break;
            case R.id.playBtn:

                if(!playServiceStarted) mActivity.pServ.playMusic();
                else if(mActivity.pServ.mPlayer.isPlaying()) mActivity.pServ.pauseMusic();
                     else mActivity.pServ.resumeMusic();


                mediaFileLengthInMilliseconds = mActivity.pServ.mPlayer.getDuration(); // gets the song length in milliseconds from URL
                updateProgressBar();
                break;
            case R.id.forwardBtn:
                mActivity.pServ.setPosition( mActivity.pServ.mPlayer.getCurrentPosition() + 10000);
                mActivity.pServ.mPlayer.seekTo( mActivity.pServ.mPlayer.getCurrentPosition() + 10000);
                break;
            case R.id.speedBtn:

                break;
            case R.id.previousBtn:

                break;
            case R.id.nextBtn:

                break;
            case R.id.queueBtn:

                break;
            case R.id.playlistBtn:

                break;
            case R.id.favoriteBtn:

                break;
            case R.id.shareBtn:

                break;
        }
    }


    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            int millis = mActivity.pServ.mPlayer.getCurrentPosition();
            int millisLeft = mActivity.pServ.mPlayer.getDuration()-mActivity.pServ.mPlayer.getCurrentPosition();

            // Displaying Total Duration time
            fullTime.setText(""+utils.milliSecondsToTimer(millisLeft));
            // Displaying time completed playing
            currentTime.setText(""+utils.milliSecondsToTimer(millis));

            // Updating progress bar
            seekbar.setProgress((int) (((float) mActivity.pServ.mPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"


            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
            if(mActivity.pServ.mPlayer.isPlaying())playBtn.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            else playBtn.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);


        }
    };
    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        System.out.println("ON START TRACHING TOUCH");
        handler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        System.out.println("------------ON STOP TRACKING---------");
        handler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mActivity.pServ.mPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds

        mActivity.pServ.mPlayer.seekTo(currentPosition);

        // update timer progress again

        updateProgressBar();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        if (v.getId() == R.id.seekBar) {
            /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
            if (mActivity.mIsBound) {
                SeekBar sb = (SeekBar) v;
                int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                mActivity.pServ.setPosition( playPositionInMillisecconds);
                mActivity.pServ.mPlayer.seekTo(playPositionInMillisecconds);
            }
        }

        return false;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(mUpdateTimeTask);
        //killMediaPlayer();
    }

    private void killMediaPlayer() {
        if (mActivity.pServ.mPlayer != null) {
            try {
                mActivity.pServ.mPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void onCompletion(MediaPlayer mp) {
        /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
        playBtn.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
    }

    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
        seekbar.setSecondaryProgress(percent);
    }

    /**
     @Override
     public void onBackPressed() {
     pf = new PlaylistsFragment();
     getActivity().getSupportFragmentManager().beginTransaction()
     .replace(R.id.menu_frag_container, pf).commit();
     }
     */


}

