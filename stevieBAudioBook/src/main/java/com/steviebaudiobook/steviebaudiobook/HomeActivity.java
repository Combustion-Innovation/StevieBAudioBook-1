package com.steviebaudiobook.steviebaudiobook;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.util.Log;
import android.widget.RelativeLayout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;

import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;
import com.google.android.vending.licensing.AESObfuscator;

public class HomeActivity extends Activity implements FileDownloader.Communicator {
	
	Context context;
	SharedPreferences sharedPref;
	boolean allTracksDownloaded;
	long enqueue;
	
	int totalChapters = 13;
	FileDownloader fileDownloader;
	
	RelativeLayout downloadHolder;

	DownloadList downloadList;
	
	private Handler mHandler;
	private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiitmJXPyxPGn8KhL6V7leCwkD5uBTaB0F5NeyDR6VbV6vvaOnDvn84L4yge4XhPdiml87EgF9Ra3P7LdTTqhKJu1RmIN9NElRob+d7Bkrj+SaK+22OwoOp46WlasJWai6W22GsN8/SwwO0R2sACOy8poObeYm4mKsDxChI+gQJFMN3GhWzF/bMXlyi2XcrjSUTI6RC95FOhnSUcDz+AxSKSSt6EJu2+ZkBI0V+qU3GwfnUzadcCRC58xSLqjRMWr9Z0EElOrOVlhs7WQtQn8eb3Qvj24aiw9K4tE80WPSPo11Jn7mVp82+EAn8Cswdg9HTNErIBCVpq+tOBYaEosvwIDAQAB";
    private static final byte[] SALT = new byte[] {-46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64, 89};
    
    
	
	File extDir = Environment.getExternalStorageDirectory();
	
	File file;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		/*
		String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		mHandler = new Handler();
		// Construct the LicenseCheckerCallback. The library calls this when done.
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        // Construct the LicenseChecker with a Policy.
        mChecker = new LicenseChecker(
            this, new ServerManagedPolicy(this,
                new AESObfuscator(SALT, getPackageName(), deviceId)),
            BASE64_PUBLIC_KEY  // Your public licensing key.
            );
		
		doCheck();
		
		File file = new File(getFilesDir() + "/audio/");
		if(!file.exists()) {
			file.mkdirs();
		}
		*/
		context = this;
		sharedPref = this.getSharedPreferences("stevieb", Context.MODE_PRIVATE);
		
		makeSharedPrefs();
		
		
		RelativeLayout splashScreen = (RelativeLayout)findViewById(R.id.splashPage);
			
		
		if(!checkAllDownloaded()) {
		
			
			new Handler().postDelayed(new Runnable() {
						
	            @Override
	            public void run() {
	            	
	            	
	            	boolean notFirstTime = sharedPref.getBoolean("notInitial", false);
	            	
	            	if(!notFirstTime) {
	            		
	            		SharedPreferences.Editor editor = sharedPref.edit();
	            		editor.putBoolean("notInitial", true);
	            		editor.commit();
	            		editor = null;
		            	Intent intent = new Intent(context, DownloadList.class);
		            	startActivityForResult(intent, 1);
		            	
	            	}
	            	else {
	            		goToAudioBook();
	            	}
	            	
	            	//Intent i = new Intent(context, DownloadList.class);
	            	//startActivityForResult(i, 1);
	            	
	            	
	    	        
	            } 
	            
	        }, 3000);
	        
		
		}
		else {
			new Handler().postDelayed(new Runnable() {
											
	            @Override
	            public void run() {
	            	goToAudioBook();
	            }
            }, 3000);
			
		}
		
		
		
		
	}
	
	private void doCheck() {
		/*
        mCheckLicenseButton.setEnabled(false);
        setProgressBarIndeterminateVisibility(true);
        mStatusText.setText(R.string.checking_license);
        */
        mChecker.checkAccess(mLicenseCheckerCallback);
        
    }
	
	@Override
	protected void onDestroy() {
        super.onDestroy();
        mChecker.onDestroy();
        
    }
		
	
	
	public boolean checkAllDownloaded() {
		
		for(int i=0; i<totalChapters; i++) {
			
			File file = new File(getFilesDir() + "/audio/chapter" + Integer.toString(i+1) + ".mp3");
			if(!file.exists()) {
				return false;
			}
			
		}
		return true;
	}
	
	public boolean checkAllQueued() {
		
		for(int i=0; i<totalChapters; i++) {
			
			File file = new File(Environment.getExternalStorageDirectory() + "/StevieB/chapter" + Integer.toString(i+1) + ".mp3");
			Log.d("file", file.getPath());
			if(!file.exists()) {
				Log.d("Files Exists", "false");
				return false;
			}
			
		}
		Log.d("Files Exist", "true");
		return true;
	}

	public void goToAudioBook() {
		
		
		
		Intent i = new Intent(this, AudioBookActivity.class);
		startActivityForResult(i, 1);
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == 1) {
			
			if(resultCode == RESULT_OK) {
				finish();
			}
		}
		
	}
	
	public void makeSharedPrefs() {
		
		SharedPreferences.Editor editor = sharedPref.edit();
		
		for(int i=0; i<totalChapters; i++) {
			File file = new File(getFilesDir() + "/audio/chapter" + Integer.toString(i+1) + ".mp3");
			if(!file.exists()) {
				editor.putBoolean("ch" + Integer.toString(i+1) + "exists", false);
				editor.putLong("ch" + Integer.toString(i+1) + "progress" , 0);
				
			}
			else {
				editor.putBoolean("ch" + Integer.toString(i+1) + "exists", true);
			}
		}
		
		editor.commit();
		editor = null;
		
		for(int i=0; i<totalChapters; i++) {
			boolean ch = sharedPref.getBoolean("ch" + Integer.toString(i+1) + "exists", false);
			Log.d("ch" + Integer.toString(i+1), Boolean.toString(ch));
		}
		
	}
	
	public void makeNoSDAlert() {
		new AlertDialog.Builder(this)
	  	.setTitle("No SD Card")
	  	.setMessage("Your SD Card is missing.\nPlease insert card and try again.")
	  	.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
	  	}).show();
	}
	
	public void makeFreeSpaceAlert() {
		new AlertDialog.Builder(this)
	  	.setTitle("Not enough free space")
	  	.setMessage("Please clear some space and try again.")
	  	.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
	  	}).show();
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
	
	public boolean copyFiles() {
		
		final Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
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
				
			}
			
		});
		return true;
		
	}
	
	
	
	public void closeDownloads() {
		
			Log.d("allDLed", "true");
        	allTracksDownloaded = true;
        	makeSharedPrefs();
        	
        	
        	fileDownloader.removeProgressDialog();
	        fileDownloader.removeReceiver();
        	
        	
        	goToAudioBook();
			
		
	}
	
	public void getNextMissing() {
		
		String missing = "";
		
		for(int i=totalChapters; i>0; i--) {
			
			File file = new File(getFilesDir() + "/audio/chapter" + Integer.toString(i) + ".mp3");
			if(!file.exists()) {
				missing = "chapter" + Integer.toString(i) + ".mp3";
				
				
			}
			
		}
		
		if(!missing.equals("")) {
			Log.d("Missing", missing);
			fileDownloader.setFileString(missing);
			
			fileDownloader.initManager();
			//downloadNext(missing);
		}
		else {
			closeDownloads();
		}
	
	}
	
	@Override
	public void receiverReturn() {
		// TODO Auto-generated method stub
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
		getNextMissing();
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
	
	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
	    public void allow(int reason) {
	        if (isFinishing()) {
	            // Don't update UI if Activity is finishing.
	            return;
	        }
	        // Should allow user access.
	        //displayResult(getString(R.string.allow));
	    }

	    public void dontAllow(int reason) {
	        if (isFinishing()) {
	            // Don't update UI if Activity is finishing.
	            return;
	        }
	        //displayResult(getString(R.string.dont_allow));
	        
	        if (reason == Policy.RETRY) {
	            // If the reason received from the policy is RETRY, it was probably
	            // due to a loss of connection with the service, so we should give the
	            // user a chance to retry. So show a dialog to retry.
	            //showDialog(DIALOG_RETRY);
	            new AlertDialog.Builder(context)
	            	.setTitle("License Check Failed, Please Retry")
	            	.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							doCheck();
						}
					}).create();
	        } else {
	            // Otherwise, the user is not licensed to use this app.
	            // Your response should always inform the user that the application
	            // is not licensed, but your behavior at that point can vary. You might
	            // provide the user a limited access version of your app or you can
	            // take them to Google Play to purchase the app.
	            //showDialog(DIALOG_GOTOMARKET);
	        	new AlertDialog.Builder(context)
            	.setTitle("License Check Failed")
            	.setPositiveButton("Purchase This App", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "http://market.android.com/details?id=" + getPackageName()));
                        startActivity(marketIntent);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}).create();
	        }
	    }

		@Override
		public void applicationError(int errorCode) {
			// TODO Auto-generated method stub
			new AlertDialog.Builder(context)
        	.setTitle("License Check Error " + Integer.toString(errorCode))
        	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
			}).show();
			
		}
		/*
		private void displayResult(final String result) {
	        mHandler.post(new Runnable() {
	            public void run() {
	                mStatusText.setText(result);
	                setProgressBarIndeterminateVisibility(false);
	                mCheckLicenseButton.setEnabled(true);
	            }
	        });
	    }
	    */
	}
	
	
}
