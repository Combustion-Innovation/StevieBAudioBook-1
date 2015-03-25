package com.steviebaudiobook.steviebaudiobook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AudioBookArrayAdapter extends ArrayAdapter<ABChapter> implements HorizontalListView.AdapterComm {

	Resources res;
	Communicator comm;
	Context context;
	ABChapter rowItem;
	double height;
	double width;
	int adjustedwidth;
	int iconwidth;
	int iconwidthtwo;
	int iconwidththree;
	
	Drawable transDrawable;
	
	int progress;
	
	HorizontalListView hlv;
	ViewHolder holder;
	
	File extDir;
	long chapterSize;
	int position;
	String chapterFile;
	
	ArrayList<Drawable> drawables;
	
	boolean buttonClicked = false;
	
	
	public AudioBookArrayAdapter(Context context, int resourceId, List<ABChapter> items) {
	    super(context, resourceId, items);
	    this.context = context;
	    
	    transDrawable = context.getResources().getDrawable(R.drawable.darkerbackgroundoverlay);
	    extDir = new File(context.getFilesDir().getPath() + "/audio/");
	   
	    drawables = new ArrayList<Drawable>();
	    
	    for(int i=0; i<items.size(); i++) {
	    	Drawable drawable = context.getResources().getDrawable(items.get(i).getImg());
	    	drawables.add(drawable);
	    }
	    
	
	    height =  context.getResources().getDisplayMetrics().heightPixels;
	    width =  context.getResources().getDisplayMetrics().widthPixels;
	    adjustedwidth = (int) (width * .9);
	    iconwidth =  (int) (width * .275);
	    iconwidthtwo =  (int) (width * .25);
	    iconwidththree = (int) (width * .30);
    
    }

	/*private view holder class*/
	private class ViewHolder {
	
		LinearLayout layout, transLayer;
		TextView titleView, subtitleView, yearsView, chapterNumber;
		ImageButton audioCheckBtn;
		
		
		ProgressBar progress;
		
				
	}
	
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		
		holder = null;
	    rowItem = getItem(position);
	    
	    this.position = position;
	    
	    
	    
	    chapterFile = rowItem.getAudioPath();
	    chapterSize = rowItem.getFileSize();
	    LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	    
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.chapter_item, parent, false);
		   	holder = new ViewHolder();
		   	holder.layout = (LinearLayout)convertView.findViewById(R.id.chapter_layout);
		   	
		   	holder.titleView = (TextView)convertView.findViewById(R.id.chapter_title);
		   	holder.subtitleView = (TextView)convertView.findViewById(R.id.chapter_subtitle);
		   	holder.yearsView = (TextView)convertView.findViewById(R.id.chapter_years);
		   	
		   	holder.progress = (ProgressBar)convertView.findViewById(R.id.progressBar1);
		   	holder.chapterNumber = (TextView)convertView.findViewById(R.id.number_text);
		   	holder.audioCheckBtn = (ImageButton)convertView.findViewById(R.id.ci_audiocheck);
		   	holder.transLayer = (LinearLayout)convertView.findViewById(R.id.chapter_trans_layer);
		   	
		   	   
		   	   
		   	
		   		
			convertView.setTag(holder);
		} 
		else {
		    holder = (ViewHolder) convertView.getTag();
			    
	    			
		}
		
		holder.transLayer.setBackground(transDrawable);
		
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
		
		holder.titleView.setTypeface(tf);
		holder.subtitleView.setTypeface(tf);
		holder.yearsView.setTypeface(tf);
		holder.chapterNumber.setTypeface(tf);
		   
		//holder.layout.setBackgroundResource(rowItem.getImg());
		holder.layout.setBackground(drawables.get(position));
		holder.titleView.setText(rowItem.getTitle());
		holder.subtitleView.setText(Html.fromHtml(rowItem.getSubtitle()));
		holder.yearsView.setText(rowItem.getYears());
		holder.chapterNumber.setText(Integer.toString(rowItem.getChapterNumber() + 1));
		holder.progress.setMax(100);
		
		
		holder.progress.setAlpha(10);
		
		File file = new File(context.getFilesDir() + "/audio/" + chapterFile);
		
		if(file.exists()) {
			setDeleteButton();
		}
		else {
			setDownloadButton();
		}
		
		updateProgress();
		/*
		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!buttonClicked) {
					
					buttonClicked = true;
					comm.goToPlayer();
					buttonClicked = false;
				}
					
			}
			
		});
		*/	
	     return convertView;
	}
	
	
	
	public long getProgress(long l, long m) {
		
		long percent = (100 * l)/m;
		
		
		
		return percent;
	}
	
	

	
	public interface Communicator {
		
		//public void deleteThisABChapter(String plate_number, int pos);
		
		public void goToPlayer();
		public void updateChapter(int position, boolean isDeleted);
		public void downloadFile(int position);
	}
	
	public void setCommunicator(Communicator c) {
		comm = c;
	}

	@Override
	public void disableClick() {
		// TODO Auto-generated method stub
		buttonClicked = true;
		
	}

	@Override
	public void enableClick() {
		// TODO Auto-generated method stub
		buttonClicked = false;
		
	}

	public void setHLVcomm(HorizontalListView hlv) {
		this.hlv = hlv;
		hlv.setAdapterComm(this);
	}
	
	public void updateProgress() {
		
		progress = (int) (100 * rowItem.getAudioProgress()/rowItem.getAudioLength());
		Log.d("updatepercent", Integer.toString(progress));
		ProgressBarAnimation anim = new ProgressBarAnimation(holder.progress, 2000);
		anim.setProgress(progress);
		
		
		
		
		
	}
	
	public void updateButton() {
		if(rowItem.doesAudioExist()) {
			setDeleteButton();
		}
		else {
			setDownloadButton();
		}
	}
	
	public void setDeleteButton() {
		holder.audioCheckBtn.setBackgroundResource(R.drawable.clouddelete);
		holder.audioCheckBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("Cloudtap", Boolean.toString(isCloudPressed()));
				
				if(!buttonClicked) {
					
					buttonClicked = true;
					File file = new File(context.getFilesDir() + "/audio/" + chapterFile);
					file.delete();
					boolean isDeleted = true;
					
					comm.updateChapter(position, isDeleted);
					
					
					
					setDownloadButton();
					buttonClicked = false;
					
					
					Toast.makeText(context, "Audio Deleted", Toast.LENGTH_LONG).show();
				}
			}
			
			
		});
	}
	
	public void setDownloadButton() {
		holder.audioCheckBtn.setBackgroundResource(R.drawable.clouddownload);
		holder.audioCheckBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				turnOffHLV();
				Log.d("Cloudtap", Boolean.toString(isCloudPressed()));
				if(!buttonClicked) {
					hlv.setEnabled(false);
					hlv.setClickable(false);
					buttonClicked = true;
					
					if(extDir.exists()) {
						
						comm.downloadFile(position);
						
						
					}
					else if(extDir.getFreeSpace() < chapterSize) {
						
						
						makeFreeSpaceAlert();
						
					}
					else {
						makeNoSDAlert();
					}
					
			        buttonClicked = false;
			        hlv.setClickable(true);
			        hlv.setEnabled(true);
				}
			}
			
		});
		
	}
	
	public void makeDialog() {
		new AlertDialog.Builder(context)
	  	.setTitle("Download MP3")
	  	.setMessage("Your MP3 file is missing.\nWould you like to download it now?")
	  	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(extDir.exists()) {
					
					comm.downloadFile(position);
					
					
				}
				else if(extDir.getFreeSpace() < chapterSize) {
					
					
					makeFreeSpaceAlert();
					
				}
				else {
					makeNoSDAlert();
				}
			}
	  	})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				
			}
	  		
	  	}).show();
	}
	
	public void makeFreeSpaceAlert() {
		new AlertDialog.Builder(context)
	  	.setTitle("Not enough free space")
	  	.setMessage("Please clear some space and try again./n"
	  			+ "Download size: " + Long.toString(chapterSize) + "\n"
	  			+ "Free space: " + Long.toString(extDir.getFreeSpace()))
	  	.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
	  	}).show();
	}
	
	public void makeNoSDAlert() {
		new AlertDialog.Builder(context)
	  	.setTitle("No SD Card")
	  	.setMessage("Your SD Card is missing.\nPlease insert card and try again.")
	  	.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
	  	}).show();
	}
	
	
	public boolean isCloudPressed() {
	
		return holder.audioCheckBtn.isPressed();
	}
	
	public void turnOffHLV() {
		
		hlv.setActivated(false);
		
		/*
		new Handler().postDelayed(new Runnable() {
			
            @Override
            public void run() {
            
            	hlv.setActivated(true);
            	
            	
    	        
            } 
            
        }, 1000);
        */
	}
	
	
	



	
		
		
}

