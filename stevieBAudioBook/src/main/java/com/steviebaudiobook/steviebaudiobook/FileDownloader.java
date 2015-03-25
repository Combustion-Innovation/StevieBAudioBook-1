package com.steviebaudiobook.steviebaudiobook;

import java.io.File;
import java.util.List;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class FileDownloader {
	
	private BroadcastReceiver receiver;
	private DownloadManager manager;
	private long enqueue;
	private Context contextmain;
	public Communicator comm;
	
	private ProgressDialog pd;
	
	private File file;
	private File fileDir;
	
	private String fileString;
	private String filePath;
	private String url;
	private String targetPath;
	private String sourcePath;
	
	private int fileSize;
	
	private boolean fileExists;
	
	public FileDownloader(Context context) {
		this.contextmain = context;
	}
	
	public void setReceiver() {
		fileExists = false;
		receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    Query query = new Query();
                    query.setFilterById(enqueue);
                    Cursor c = manager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
 
                        	//Do whatever
                        	comm.receiverReturn();
                        	c.close();
                        	Log.d("file", "downloaded");
                        	
                        	//removeProgressDialog();
                        	
                            context.unregisterReceiver(receiver);
                            
                            
                        }
                    }
                }
            }
        };
        contextmain.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
	
	
	public void setOpenReceiver() {
		fileExists = false;
		receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    
                    Query query = new Query();
                    query.setFilterById(enqueue);
                    Cursor c = manager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
 
                        	//Do whatever
                        	c.close();
                        	comm.receiverReturn();
                        	
                            
                           
                            
                        }
                    }
                }
            }
        };
        contextmain.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
	
	public void removeReceiver() {
		contextmain.unregisterReceiver(receiver);
	}
	
	public void initManager() {
		
		File path = new File(Environment.getExternalStorageDirectory() + this.filePath);
		if(!path.exists()) {
			path.mkdirs();
		}
		
		manager = (DownloadManager)contextmain.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(this.url + this.fileString));
        //Request request = new Request(Uri.parse("http://combustioninnovation.com/steviebmusic.com/audioBookFiles/" + trackName));
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false).setTitle(this.fileString)
		                .setDescription("Downloading, please wait...")
                .setDestinationInExternalPublicDir(this.filePath, this.fileString);
        		
        enqueue = manager.enqueue(request);
    }
	
	public void init1xManager(String file) {
		
		
		File path = new File(Environment.getExternalStorageDirectory() + this.filePath);
		if(!path.exists()) {
			path.mkdirs();
		}
		
		manager = (DownloadManager)contextmain.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(this.url + file));
        //Request request = new Request(Uri.parse("http://combustioninnovation.com/steviebmusic.com/audioBookFiles/" + trackName));
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false).setTitle(file)
		                .setDescription("Downloading, please wait...")
                .setDestinationInExternalPublicDir(this.filePath, file);
        		
        enqueue = manager.enqueue(request);
    }
	
	public boolean isDownloadManagerAvailable(Context context) {
	    try {
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	            return false;
	        }
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
	        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
	                PackageManager.MATCH_DEFAULT_ONLY);
	        return list.size() > 0;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public void makeProgressDialog() {
		pd = new ProgressDialog(this.contextmain);
		pd.setTitle("Please Wait");
		pd.setMessage("Files Downloading");
		pd.setCancelable(false);
		pd.show();
	}
	
	public void changeProgressDialogMessage(String string) {
		
		pd.setMessage(string);
		
	}
	
	public void removeProgressDialog() {
		pd.dismiss();
        pd = null;
	}
	
	public void unlockProgressDialog() {
		pd.setCancelable(true);
	}
	
	/*
	 * Getters/Setters
	 */
	public Context getContext() {
		return contextmain;
	}

	public void setContext(Context context) {
		this.contextmain = context;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFileDir() {
		return fileDir;
	}

	public void setFileDir(File fileDir) {
		this.fileDir = fileDir;
	}

	public String getFileString() {
		return fileString;
	}

	public void setFileString(String fileString) {
		this.fileString = fileString;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public boolean isFileExists() {
		return fileExists;
	}

	public void setFileExists(boolean fileExists) {
		this.fileExists = fileExists;
	}
	
	public void setCommunicator(Communicator c) {
		this.comm = c;
	}
	
	
	public interface Communicator {
		//Insert methods from calling class	
		public void receiverReturn();
		
	}
	
	
}
