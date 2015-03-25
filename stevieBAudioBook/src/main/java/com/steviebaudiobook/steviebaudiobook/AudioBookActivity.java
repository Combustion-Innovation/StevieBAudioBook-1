package com.steviebaudiobook.steviebaudiobook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.util.Log;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;



public class AudioBookActivity extends Activity implements AudioBookArrayAdapter.Communicator, HorizontalListView.Communicator, FileDownloader.Communicator {

	public final String[] chapterTitles = {"And The Last Shall Be First", "J.C. Productions", "The First Awakening", "Stevie B is Born", "Hit the Road", "$500,000 Short", "Many Rivers to Cross", "Racing the Moon", "And the Lions Feed", "Hollywood or Bust", "Bang the Drum Slowly", "The Renaissance Man", "Running for Miles"};
	public final String[] chapterSubtitles = {"", "", "", "the <i>Party Your Body</i> Years", "the <i>In My Eyes</i> Years", "the <i>Love & Emotion</i> Years", "the <i>Healing</i> Years", "the <i>Funky Melody</i> Years", "the <i>Waiting for Your Love</i> Years", "the <i>Right Here Right Now</i> Years", "<i>It's So Good</i> and Beyond", "<i>Special One</i> / <i>Terminator</i>", "<i>The King of Hearts</i>"};
	public final String[] chapterYears = {"1957 - 1975", "1975 - 1977", "1977 - 1979", "1987 - 1988", "1988 - 1989", "1989 - 1990", "1991 - 1994", "1994 - 1995", "1996 - 1997", "1998 - 1999", "1999 - 2006", "2006 - 2103", "2014 - Onwards"};
	
	public final String[] audioPaths = {"chapter1.mp3", "chapter2.mp3", "chapter3.mp3", "chapter4.mp3", "chapter5.mp3", "chapter6.mp3", "chapter7.mp3", "chapter8.mp3", "chapter9.mp3", "chapter10.mp3", "chapter11.mp3", "chapter12.mp3", "chapter13.mp3"};
	public final int[] backgroundImages = {R.drawable.chapter1, R.drawable.chapter2, R.drawable.chapter3, R.drawable.chapter4, R.drawable.chapter5, R.drawable.chapter6, R.drawable.chapter7, R.drawable.chapter8, R.drawable.chapter9, R.drawable.chapter10, R.drawable.chapter11, R.drawable.chapter12, R.drawable.chapter13};
	public final int[] albumImages = {0, 0, 0, R.drawable.pyb1, R.drawable.ime1, R.drawable.lne1, R.drawable.h1, R.drawable.fm1, R.drawable.wf2, R.drawable.rr1, R.drawable.isg2, R.drawable.te1, R.drawable.koh};
	public final String[] albumNames = {"", "", "", "Party Your Body", "In My Eyes", "Love & Emotion", "Healing", "Funky Melody", "Waiting For Your Love", "Right Here Right Now", "It's So Good", "Special One/Terminator", "The King of Hearts"};
	public final long[] lengths = {3866064, 2500056, 2682048, 2552064, 3240048, 3658056, 2978064, 3458064, 1252056, 2052048, 1972056, 5430048, 1541068};
	public final long[] filesizes = {46396864, 30004768, 32188672, 30628864, 38884672, 43900768, 35740864, 41500864, 15028768, 24628672, 23668768, 65164672, 24659224};
	public ABChapter abChapter;
	public ArrayList<ABChapter> abChapters;
	
	public HorizontalListView chapterListView;
	public AudioBookArrayAdapter adapter;
	
	public PageMarkers pageMarkers;
	
	public FileDownloader fileDownloader;
	
	
	
	
	
	private int downloadPosition;
	
	public SharedPreferences sharedPref;
	
	public boolean progressUpdated;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_book);
				
		Log.d("bookactivity", "created");
		
		sharedPref = this.getSharedPreferences("stevieb", Context.MODE_PRIVATE);
		
		abChapters = new ArrayList<ABChapter>();
		
		makeChapters();
		
		progressUpdated = false;
		
		
		fileDownloader = new FileDownloader(this);
		fileDownloader.setCommunicator(this);
		fileDownloader.setUrl("http://combustioninnovation.com/steviebmusic.com/audioBookFiles/");
		fileDownloader.setFilePath("/StevieB/");
		
		
		pageMarkers = (PageMarkers)findViewById(R.id.pageMarkers);
		chapterListView = (HorizontalListView)findViewById(R.id.ab_listview);
		
		chapterListView.setComm(this);
		adapter = new AudioBookArrayAdapter(this, R.layout.chapter_item, abChapters);
		adapter.setHLVcomm(chapterListView);
		adapter.setCommunicator(this);
		chapterListView.setAdapter(adapter);
		chapterListView.setNumberofItems(12);
		
		pageMarkers.setTotalPages(13);
		pageMarkers.makeView(0);
		
		
		debug();
	}

	public void makeChapters() {
		
		for(int i=0; i<chapterTitles.length; i++) {
			
			abChapter = new ABChapter(i, chapterTitles[i], chapterSubtitles[i], chapterYears[i], backgroundImages[i], albumImages[i], audioPaths[i], albumNames[i]);
			abChapters.add(abChapter);
		}
		
		
		
		for(int i=0; i<abChapters.size(); i++) {
			long progress = sharedPref.getLong("ch" + Integer.toString(i+1) + "progress", 0);
			if(progress > 0) {
				abChapters.get(i).setAudioProgress(progress);
				
			}
			abChapters.get(i).setAudioLength(lengths[i]);
			abChapters.get(i).setFileSize(filesizes[i]);
		}
		
	}

	@Override
	public void goToPlayer() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, SongPlayerActivity.class);
		
		int current = chapterListView.getActiveFeature();
		intent.putExtra("chapter", abChapters.get(current));
		intent.putExtra("current", current);
		
		
		
		startActivityForResult(intent, 1);
		
	}
	
	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		Log.d("requestcode", Integer.toString(requestCode));
		
		if(requestCode == 1) {
			
			if(resultCode == RESULT_OK) {
				
				ABChapter newChapter = (ABChapter) data.getSerializableExtra("chapter");
				int position = data.getIntExtra("position", -1);
				
				
				
				abChapters.set(position, newChapter);
				
				//Log.d("chapter", Boolean.toString(newChapter.doesAudioExist()));
				//Log.d("array", Boolean.toString(abChapters.get(position).doesAudioExist()));
				Log.d("newprogress", Long.toString(newChapter.getAudioProgress()));
				/*
				if(newChapter.doesAudioExist()) {
					adapter.setDeleteButton();
				}
				else {
					adapter.setDownloadButton();
				}
				*/
				
				//adapter.notifyDataSetInvalidated();
				
				
				
				
				if(newChapter.doesAudioExist()) {
					updateChapter(position, false);
				}
				else {
					updateChapter(position, true);
				}
				
				adapter.updateProgress();
				adapter.notifyDataSetChanged();
				updateProgress();
				
				
				//adapter.updateButton();
				//adapter.notifyDataSetChanged();
				//chapterListView.scrollTo(position);
			}
		}
	}
	
	public void updateProgress() {
		
		SharedPreferences.Editor editor = sharedPref.edit();
		
		for(int i=0; i<abChapters.size(); i++) {
			
			long progress = abChapters.get(i).getAudioProgress();
			editor.putLong("ch" + Integer.toString(i+1) + "progress", progress);
			
		}
		editor.commit();
		editor = null;
		
		
		
		
	}

	@Override
	public void setPageMarkers(int currentPage) {
		// TODO Auto-generated method stub
		pageMarkers.makeView(currentPage);
	}
	
	public void debug() {
		for(int i=0; i<abChapters.size(); i++) {
			Log.d("chapter", Long.toString(abChapters.get(i).getAudioProgress()));
		}
	}
	
	public boolean copy(File src, File dst) throws IOException {
	    FileInputStream inStream = new FileInputStream(src);
	    FileOutputStream outStream = new FileOutputStream(dst);
	    FileChannel inChannel = inStream.getChannel();
	    FileChannel outChannel = outStream.getChannel();
	    inChannel.transferTo(0, inChannel.size(), outChannel);
	    inStream.close();
	    outStream.close();
	    return true;
	}

	@Override
	public void updateChapter(int position, boolean isDeleted) {
		// TODO Auto-generated method stub
		
		if(isDeleted) {
			abChapters.get(position).setAudioExist(false);
			abChapters.get(position).setAudioProgress(0);
			adapter.notifyDataSetChanged();
		}
		else {
			abChapters.get(position).setAudioExist(true);
			adapter.notifyDataSetChanged();
		}
		
		
	}

	@Override
	public void receiverReturn() {
		// TODO Auto-generated method stub
		
		File file1 = new File(Environment.getExternalStorageDirectory() + "/StevieB/" + abChapters.get(downloadPosition).getAudioPath());
		File file2 = new File(getFilesDir() + "/audio/" + abChapters.get(downloadPosition).getAudioPath());
		
        try {
			if(copy(file1, file2)) {
				Log.d("fileExists", Boolean.toString(file2.exists()));
				file1.delete();
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean("ch" + (abChapters.get(downloadPosition).getChapterNumber() + 1) + "exists", true);
		        editor.putLong("ch" + (abChapters.get(downloadPosition).getChapterNumber() + 1) + "progress", 0);
		        editor.commit();
		        editor = null;
		        
		        Toast.makeText(this, "Download Complete", Toast.LENGTH_LONG).show();
		        //isDownloading = false;
		        fileDownloader.removeProgressDialog();
			    //trackPlayer.playSong();
			    
			    //playBtn.setBackgroundResource(R.drawable.pause);
				
			    
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        adapter.setDeleteButton();
        
		
	}

	@Override
	public void downloadFile(int position) {
		// TODO Auto-generated method stub
		downloadPosition = position;
		fileDownloader.setReceiver();
		fileDownloader.makeProgressDialog();
		fileDownloader.setFileString(abChapters.get(position).getAudioPath());
		
		fileDownloader.initManager();
		
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		setResult(RESULT_OK, i);
		finish();
	}
	
}
