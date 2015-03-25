package com.steviebaudiobook.steviebaudiobook;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TrackPlayer implements OnCompletionListener {
	
	
	private Context context;
	
	private ToggleButton btnPlay;
    private ToggleButton btnForward;
    private ToggleButton btnBackward;
    
    private SeekBar songProgressBar;
    private SeekBar volumeBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    // Media Player
    private  MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    BroadcastReceiver receiver;
    
    private Utilities utils;
    private int seekForwardTime = 1000; // 5000 milliseconds
    private int seekBackwardTime = 1000; // 5000 milliseconds
    //private int currentSongIndex = 0;
    
   
    
    private String trackName;
    private long progress;
    
    public boolean isPlaying, isRewind, isFF, isPaused;
    
    public boolean buttonClicked = false;
    
    AudioManager audioManager;
   
	private Forward forward;
	private Rewind rewind;
    
    /**
     * 
     */
    public TrackPlayer(Context context, final ToggleButton btnPlay, final ToggleButton btnBackward, final ToggleButton btnForward, SeekBar songProgressBar, final TextView songCurrentDurationLabel, final TextView songTotalDurationLabel, String trackName, final SeekBar volumeBar, long progress) {
    	
    	this.context = context;
    	this.btnPlay = btnPlay;
    	this.btnForward = btnForward;
    	this.btnBackward = btnBackward;
    	this.songProgressBar = songProgressBar;
    	this.songCurrentDurationLabel = songCurrentDurationLabel;
    	this.songTotalDurationLabel = songTotalDurationLabel;
    	
    	this.volumeBar = volumeBar;
    	
    	this.trackName = trackName;
    	this.progress = progress;
    	
    	this.isPlaying = false;
    	this.isPaused = false;
    	this.isRewind = false;
    	this.isFF = false;
    	
    	mp = new MediaPlayer();
    	utils = new Utilities();
    	
    	//songProgressBar.setOnSeekBarChangeListener(this);
    	
    	mp.setOnCompletionListener(this);
    	
    	this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    	
    	
    	
    	volumeBar.setMax(this.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    	volumeBar.setProgress(this.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    	
    	mp.setVolume(1f, 1f);
    	
    	this.isPlaying = true;
        setReceiver();
    	playSong();
    	
    	volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if(!buttonClicked) {
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
					/*
					buttonClicked = true;
					float volume = ((float)progress)/100;
					
					mp.setVolume(volume, volume);
					*/
				}
				buttonClicked = false;
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	
    	btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!buttonClicked) {
					buttonClicked = true;
					if(isPaused) {
						
						mp.start();
						btnPlay.setBackgroundResource(R.drawable.pause);
						isPaused = false;
						isPlaying = true;
						
					}
					else {
						mp.pause();
						btnPlay.setBackgroundResource(R.drawable.play);
						isPaused = true;
						isPlaying = false;
					}
					if(isFF) {
						isFF = false;
						forward.cancel(true);
						forward = null;
						btnForward.setBackgroundResource(R.drawable.forward);
						btnForward.setChecked(false);
					}
					if(isRewind) {
						isRewind = false;
						rewind.cancel(true);
						rewind = null;
						btnBackward.setBackgroundResource(R.drawable.rewind);
						btnBackward.setChecked(false);
					}
					buttonClicked = false;
				}
				
			}
		});
    	
    	btnForward.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
					buttonClicked = true;
					if(isChecked && !isRewind) {
					
						isFF = true;
						forward = new Forward();
						forward.execute("");
						btnForward.setBackgroundResource(R.drawable.forwardpressed);
						btnPlay.setBackgroundResource(R.drawable.play);
						buttonClicked = false;
					}
					else {
						isFF = false;
						mp.start();
						btnPlay.setBackgroundResource(R.drawable.pause);
						btnForward.setBackgroundResource(R.drawable.forward);
						buttonClicked = false;
					}
					
				
			}
    		
    	});
    	
    	
    	btnBackward.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
					buttonClicked = true;
					

					if(isChecked && !isFF) {
						isRewind = true;
						rewind = new Rewind();
						rewind.execute("");
						btnBackward.setBackgroundResource(R.drawable.rewindpressed);
						btnPlay.setBackgroundResource(R.drawable.play);
					}
					else {
						isRewind = false;
						mp.start();
						btnPlay.setBackgroundResource(R.drawable.pause);
						btnBackward.setBackgroundResource(R.drawable.rewind);
						buttonClicked = false;
					}
					buttonClicked = false;
					
				
			}
    		
    	});
    	
    	this.songProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

    		@Override
    	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    	//		long totalDuration = mp.getDuration();
          //      long currentDuration = progress;
  
                
                int totalDuration = mp.getDuration();
    	        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
    	 
                // Displaying Total Duration time
                songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration-currentPosition));
                // Displaying time completed playing
                songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentPosition));
    			Log.d("pulling","pulling");
    	    }
    	 
    	    /**
    	     * When user starts moving the progress handler
    	     * */
    	    @Override
    	    public void onStartTrackingTouch(SeekBar seekBar) {
    	        // remove message Handler from updating progress bar
    	    	    	    	
    	       mHandler.removeCallbacks(mUpdateTimeTask);
    	    	int totalDuration = mp.getDuration();
                int currentDuration = seekBar.getProgress();
  
                // Displaying Total Duration time
                songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
    	    	
    	    }
    	 
    	    /**
    	     * When user stops moving the progress hanlder
    	     * */
    	    @Override
    	    public void onStopTrackingTouch(SeekBar seekBar) {
    	        mHandler.removeCallbacks(mUpdateTimeTask);
    	        int totalDuration = mp.getDuration();
    	        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
    	 
    	        // forward or backward to certain seconds
    	        mp.seekTo(currentPosition);
    	        if(!mp.isPlaying()) {
    	        	mp.start();
    	        	btnPlay.setBackgroundResource(R.drawable.pause);
    	        	isPlaying = true;
    	        }
    	       
    	        
    	        // update timer progress again
    	        updateProgressBar();
    	    }
    	
    	});
    	
    	mp.seekTo((int) this.progress);
    	
    	
    	
    	
    	
    }
    
    public void setTrackName(String trackName) {
    	this.trackName = trackName;
    }
    
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }   
 
    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
           public void run() {
               long totalDuration = mp.getDuration();
               long currentDuration = mp.getCurrentPosition();
 
               // Displaying Total Duration time
               songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
               // Displaying time completed playing
               songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
 
               // Updating progress bar
               int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
               //Log.d("Progress", ""+progress);
               songProgressBar.setProgress(progress);
 
               // Running this thread after 100 milliseconds
               mHandler.postDelayed(this, 100);
           }
        };
 
    /**
     *
     * */
    
    
    public void  playSong(){
        // Play song
        try {
        	
        	          
            mp.setDataSource(context.getFilesDir() + "/audio/" + trackName);
            
            
            mp.prepare();
            mp.start();
            // Displaying Song title
            //String songTitle = songsList.get(songIndex).get("songTitle");
            //songTitleLabel.setText(songTitle);
 
            // Changing Button Image to pause image
            //btnPlay.setImageResource(R.drawable.btn_pause);
 
            // set Progress bar values
            Log.d("length", Integer.toString(mp.getDuration()));
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);
 
            // Updating progress bar
            updateProgressBar();
            this.isPlaying = true;
            this.isPaused = false;
            btnPlay.setBackgroundResource(R.drawable.pause);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }
    
    public void resetProgress() {
    	songProgressBar.setProgress(0);
    	mp.seekTo(0);
    }
    
    public long getProgress() {
    	return mp.getCurrentPosition();
    }
    
    public long getTrackLength() {
    	return mp.getDuration();
    }
    
    public void pauseSong() {
    	mp.pause();
    	this.isPaused = true;
    }
    
    public void removeReceiver() {
    	context.unregisterReceiver(receiver);
    	receiver = null;
    }
    
    public void leavePlayer() {
    	
    	/*
    	if(mp.isPlaying()) {
    		mp.pause();
    	}
    	*/
    	mp.reset();
    	mp.release();
    	context.unregisterReceiver(receiver);
    	mHandler.removeCallbacks(mUpdateTimeTask);
    	mp = null;
    }

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		btnPlay.setBackgroundResource(R.drawable.play);
		this.isPlaying = false;
		this.isPaused = true;
		progress = mp.getCurrentPosition();
		
	}
	
	private class Rewind extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			while(btnBackward.isChecked()) {
				//btnPlay.setClickable(false);
				
				btnForward.setClickable(false);
				//buttonClicked = true;
				int currentPosition = mp.getCurrentPosition();
	            // check if seekBackward time is greater than 0 sec
	            if(currentPosition - seekBackwardTime >= 0){
	                // forward song
	                mp.seekTo(currentPosition - seekBackwardTime);
	            }else{
	                // backward to starting position
	                mp.seekTo(0);
	            }
			}
			
			//btnPlay.setClickable(true);
			btnForward.setClickable(true);
			return null;
		}
		
	}
	
	private class Forward extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			while(btnForward.isChecked()) {
				//buttonClicked = true;
				btnBackward.setClickable(false);
				
				//btnPlay.setClickable(false);
				
				int currentPosition = mp.getCurrentPosition();
	            if(currentPosition + seekForwardTime <= mp.getDuration()) {
	                // forward song
	                mp.seekTo(currentPosition + seekForwardTime);
	                
	            }
	            else {
	                // forward to end position
	                mp.seekTo(mp.getDuration());
	            }
				
			}
			btnBackward.setClickable(true);
			
			//btnPlay.setClickable(true);
			return null;
		}
		
	}
	
	public void goToProgress(long progress) {
		mp.seekTo((int)progress);
	}
	
	
	
	public void setListeners() {
		volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				if(!buttonClicked) {
					/*
					buttonClicked = true;
					float volume = ((float)progress)/100;
					
					mp.setVolume(volume, volume);
					*/
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
				}
				buttonClicked = false;
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	
    	btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!buttonClicked) {
					buttonClicked = true;
					
					if(isPaused) {
						
						mp.start();
						btnPlay.setBackgroundResource(R.drawable.pause);
						isPaused = false;
						isPlaying = true;
						
					}
					else {
						mp.pause();
						btnPlay.setBackgroundResource(R.drawable.play);
						isPaused = true;
						isPlaying = false;
					}
					if(isFF) {
						isFF = false;
						forward.cancel(true);
						forward = null;
						btnForward.setBackgroundResource(R.drawable.forward);
						btnForward.setChecked(false);
					}
					if(isRewind) {
						isRewind = false;
						rewind.cancel(true);
						rewind = null;
						btnBackward.setBackgroundResource(R.drawable.rewind);
						btnBackward.setChecked(false);
					}
					buttonClicked = false;
				}
				
			}
		});
    	
    	btnForward.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
					buttonClicked = true;
					if(isChecked && !isRewind) {
					
						isFF = true;
						forward = new Forward();
						forward.execute("");
						btnForward.setBackgroundResource(R.drawable.forwardpressed);
						btnPlay.setBackgroundResource(R.drawable.play);
						buttonClicked = false;
					}
					else {
						isFF = false;
						mp.start();
						btnPlay.setBackgroundResource(R.drawable.pause);
						btnForward.setBackgroundResource(R.drawable.forward);
						buttonClicked = false;
					}
					
				
			}
    		
    	});
    	
    	
    	btnBackward.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
					buttonClicked = true;
					

					if(isChecked && !isFF) {
						isRewind = true;
						rewind = new Rewind();
						rewind.execute("");
						btnBackward.setBackgroundResource(R.drawable.rewindpressed);
						btnPlay.setBackgroundResource(R.drawable.play);
					}
					else {
						isRewind = false;
						mp.start();
						btnPlay.setBackgroundResource(R.drawable.pause);
						btnBackward.setBackgroundResource(R.drawable.rewind);
						buttonClicked = false;
					}
					buttonClicked = false;
					
				
			}
    		
    	});
    	
    	this.songProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

    		@Override
    	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    			
    	    }
    	 
    	    /**
    	     * When user starts moving the progress handler
    	     * */
    	    @Override
    	    public void onStartTrackingTouch(SeekBar seekBar) {
    	        // remove message Handler from updating progress bar
    	    	
    	        //mHandler.removeCallbacks(mUpdateTimeTask);
    	    	//updateProgressBar();
    	    	
    	    }
    	 
    	    /**
    	     * When user stops moving the progress hanlder
    	     * */
    	    @Override
    	    public void onStopTrackingTouch(SeekBar seekBar) {
    	        mHandler.removeCallbacks(mUpdateTimeTask);
    	        int totalDuration = mp.getDuration();
    	        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
    	 
    	        // forward or backward to certain seconds
    	        mp.seekTo(currentPosition);
    	        
    	        // update timer progress again
    	        updateProgressBar();
    	        if(!mp.isPlaying()) {
    	        	
    	        	mp.start();
    	        }
    	    }
    	
    	});
	}
	
	public void stopPlayer() {
		mp.pause();
		isPlaying = false;
		isPaused = true;
		btnPlay.setBackgroundResource(R.drawable.play);
	}
	
	public void releasePlayer() {
		mHandler.removeCallbacks(mUpdateTimeTask);
		//context.unregisterReceiver(receiver);
		//mp.reset();
		mp.release();
		
	}
	
	public void killPlayer() {
		mp.release();
	}
	
	public void setReceiver() {
		
		receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equalsIgnoreCase("android.intent.action.PHONE_STATE")) {
                		
                	if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                		mp.pause();
                	}
                	if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
        				mp.pause();
        			}
                	if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                		mp.start();
                	}

        			
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
	}
	
	public void cancelAll() {
		if(mp.isPlaying()) {
			mp.pause();
		}
		if(btnForward.isChecked()) {
			btnForward.setChecked(false);
		}
		if(btnBackward.isChecked()) {
			btnBackward.setChecked(false);
		}
	}
	
	public void setVolumeBar(int progress) {
		volumeBar.setProgress(progress);
	}
	
	
}
