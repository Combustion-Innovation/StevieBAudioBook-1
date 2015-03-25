package com.steviebaudiobook.steviebaudiobook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DownloadList extends Activity implements FileDownloader.Communicator {

	public final long[] filesizes = {46396864, 30004768, 32188672, 30628864, 38884672, 43900768, 35740864, 41500864, 15028768, 24628672, 23668768, 65164672, 24659224};
	private int totalChapters = 13;
	
	
	private LayoutInflater mInflater;
	private Context context;
	
	
	
	private LinearLayout listHolder, buttonLayout;
	private View itemLayout;
	//private TextView chapterText;
	//private CheckBox chapterCheck;
	private CheckBox checkAllCheck;
	private Button buttonSkip, buttonDL;
	
	FileDownloader fileDownloader;
	
	private ArrayList<String[]> missingChapters;
	private ArrayList<String> queue;
	private ArrayList<CheckBox> checkBoxes;
	
	private boolean allChecked = false;
	
	private int downloadNum = 0;
	private int width;
	private int dpi;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_list);
		context = this;
		
		
		fileDownloader = new FileDownloader(context);
		fileDownloader.setCommunicator(this);
		
		
		missingChapters = new ArrayList<String[]>();
		checkBoxes = new ArrayList<CheckBox>();
		getMissing();
		
		queue = new ArrayList<String>();
		
		setLayout();
		
		
		
		
	}
	
	
	
	
	@SuppressLint("InflateParams")
	public void setLayout() {
		
		
		mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);		
		
		listHolder = (LinearLayout)findViewById(R.id.dp_missing_holder);
		checkAllCheck = (CheckBox)findViewById(R.id.dp_checkall_checkbox);
		
		
		buttonLayout = (LinearLayout)findViewById(R.id.dp_button_holder);
		buttonSkip = (Button)findViewById(R.id.button_skip);
		buttonDL = (Button)findViewById(R.id.button_dl);
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
		
		
		buttonSkip.setTypeface(tf);
		buttonDL.setTypeface(tf);
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		dpi = (int)metrics.density;
		
		LayoutParams btnParams = buttonLayout.getLayoutParams();
		btnParams.width = width*2;
		
		buttonLayout.setLayoutParams(btnParams);
		LayoutParams myParams = buttonSkip.getLayoutParams();
		myParams.width = width;
		
		buttonSkip.setLayoutParams(myParams);
		LayoutParams myParams1 = buttonSkip.getLayoutParams();
		myParams1.width = width/2;
		buttonDL.setLayoutParams(myParams1);
		
		changeButtonsSingle();
		
		
		for(int i=0; i<missingChapters.size(); i++) {
			
			
			
			itemLayout = mInflater.inflate(R.layout.download_item, null);
			TextView chapterText = (TextView)itemLayout.findViewById(R.id.di_missing_chapter_text);
			final CheckBox chapterCheck = (CheckBox)itemLayout.findViewById(R.id.di_missing_chapter_checkbox);
			
			itemLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 50 * dpi));
			
			chapterText.setTypeface(tf);
			
			checkBoxes.add(chapterCheck);
			final String current = missingChapters.get(i)[0];
			String chapterString = missingChapters.get(i)[1];
			chapterText.setText(chapterString);
			
			Log.d("current", current);
			itemLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(chapterCheck.isChecked()) {
						chapterCheck.setChecked(false);
					}
					else {
						chapterCheck.setChecked(true);
					}
						
				}
				
			});
			
			chapterCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked) {
						
						queue.add(current);
						changeButtonsDouble();
						
						
					}
					else {
						queue.remove(current);
						if(!checkCheckBoxes()) {
							
							
						}
						else {
							
							changeButtonsSingle();
						}
					}
					if(allChecked) {
						allChecked = false;
						checkAllCheck.setChecked(false);
						
					}
					debug();
					
					
				}
				
			});
			
			listHolder.addView(itemLayout);
			
			
		}
		
		buttonSkip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
					Intent intent = new Intent(context, AudioBookActivity.class);
					startActivity(intent);
					finish();
				
			}
			
			
		});
		
		
		
		
		buttonDL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
					downloadNum = queue.size();
					fileDownloader.setUrl("http://combustioninnovation.com/steviebmusic.com/audioBookFiles/");
					fileDownloader.setFilePath("/StevieB/");
					fileDownloader.setOpenReceiver();
					fileDownloader.makeProgressDialog();
					for(int i=0; i<queue.size(); i++) {
						fileDownloader.init1xManager(queue.get(i));
						//fileDownloader.setOpenReceiver();
					}
					
				
			}
			
		});
		
		checkAllCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					for(int i=0; i<checkBoxes.size(); i++) {
						checkBoxes.get(i).setChecked(true);
						changeButtonsDouble();
					}
					downloadNum = checkBoxes.size();
					allChecked = true;
					
				}
				else {
					if(allChecked) {
					for(int i=0; i<checkBoxes.size(); i++) {
						checkBoxes.get(i).setChecked(false);
						changeButtonsSingle();
					}
					downloadNum = 0;
					allChecked = false;
					}
				}
				debug();
				
			}
			
		});
			
	}
	
	public void getMissing() {
		
	
		String missing = "";
		String missingString = "";
		
		
		for(int i=0; i<totalChapters; i++) {
			
			File file = new File(getFilesDir() + "/audio/chapter" + Integer.toString(i+1) + ".mp3");
			if(!file.exists()) {
				missing = "chapter" + Integer.toString(i+1) + ".mp3";
				missingString = "Chapter " + Integer.toString(i+1);
				String[] result = new String[2];
				result[0] = missing;
				result[1] = missingString;
				
				
				missingChapters.add(result);
				
			}
			
		}
		
		
		
	}
	
	public boolean checkCheckBoxes() {
		boolean noneChecked = true;
		for(int i=0; i<checkBoxes.size(); i++) {
			if(checkBoxes.get(i).isChecked()) {
				noneChecked = false;
			}
		}
		return noneChecked;
	}

	public void makeSharedPrefs() {
		SharedPreferences sharedPref = this.getSharedPreferences("stevieb", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		
		for(int i=0; i<totalChapters; i++) {
			File file = new File(getFilesDir() + "/audio/chapter" + Integer.toString(i+1) + ".mp3");
			if(!file.exists()) {
				editor.putBoolean("ch" + Integer.toString(i+1) + "exists", false);
				
			}
			else {
				editor.putBoolean("ch" + Integer.toString(i+1) + "exists", true);
			}
		}
		
		editor.commit();
		editor = null;
	}
	
	public boolean copyFiles() {
		/*
		Handler handler = new Handler();
		
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
		*/		
				File filepath = new File(getFilesDir() + "/audio/");
				if(!filepath.exists()) {
					filepath.mkdirs();
				}
				for(int i=0; i<totalChapters; i++) {
					
					File file1 = new File(Environment.getExternalStorageDirectory() + "/StevieB/chapter" + Integer.toString(i+1) + ".mp3");
					File file2 = new File(getFilesDir() + "/audio/chapter" + Integer.toString(i+1) + ".mp3");
					if(!file2.exists() && file1.exists()) {
						try {
							copy(file1, file2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						file1.delete();
					}
				}
		/*		
			}
			
			
			
			
		});
		*/
		
		return true;

	}
	
	public void copy(File src, File dst) throws IOException {
	    FileInputStream inStream = new FileInputStream(src);
	    FileOutputStream outStream = new FileOutputStream(dst);
	    FileChannel inChannel = inStream.getChannel();
	    FileChannel outChannel = outStream.getChannel();
	    inChannel.transferTo(0, inChannel.size(), outChannel);
	    inStream.close();
	    outStream.close();
	}


	@Override
	public void receiverReturn() {
		// TODO Auto-generated method stub
		
		Log.d("DownloadNum", Integer.toString(downloadNum));
		Log.d("Queue size", Integer.toString(queue.size()));
		Log.d("checkDownloads", Boolean.toString(checkAllDownloads()));
		
		if(checkAllDownloads()) {
			fileDownloader.removeReceiver();
			fileDownloader.changeProgressDialogMessage("Installing...");
			
			if(copyFiles()) {
			
				
				makeSharedPrefs();
				
				fileDownloader.removeProgressDialog();
				
				Intent intent = new Intent(this, AudioBookActivity.class);
				startActivity(intent);
				
				finish();
				
			}
			
		}
		//fileDownloader.removeReceiver();
	}
	
	@SuppressLint("NewApi")
	public void changeButtonsDouble() {
		ValueAnimator anim = ValueAnimator.ofInt(buttonSkip.getWidth(), width/2);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				int val = (Integer) animation.getAnimatedValue();
				LayoutParams params = buttonSkip.getLayoutParams();
				params.width = val;
				buttonSkip.setLayoutParams(params);
				
				
			}
		});
		anim.setDuration(800);
		anim.start();
		
	}
	
	@SuppressLint("NewApi")
	public void changeButtonsSingle() {
		ValueAnimator anim = ValueAnimator.ofInt(buttonSkip.getWidth(), width);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				int val = (Integer) animation.getAnimatedValue();
				LayoutParams params = buttonSkip.getLayoutParams();
				params.width = val;
				buttonSkip.setLayoutParams(params);
				
				
			}
		});
		anim.setDuration(800);
		anim.start();
		
	}
	
	public boolean checkAllDownloads() {
		String dir = Environment.getExternalStorageDirectory() + "/StevieB/";
		for(int i=0; i<queue.size(); i++) {
			File file = new File(dir, queue.get(i));
			if(!file.exists()) {// || file.length() < filesizes[i]) {
				return false;
			}
			
		}
		return true;
	
	}
	
	public void debug() {
		/*
		for(int i=0; i<queue.size(); i++) {
			Log.d("queue", queue.get(i));
		}
		*/
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		setResult(RESULT_OK, i);
		finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    
	}
}
