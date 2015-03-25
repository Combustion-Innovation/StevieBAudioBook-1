package com.steviebaudiobook.steviebaudiobook;

import java.io.Serializable;

public class ABChapter implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int chapterNumber;
	public String title;
	public String subtitle;
	public String years;
	public String albumName;
	
	public int img;
	public int albumImg;
	
	public String audioPath;
	public long audioLength;
	public long audioProgress;
	public long fileSize;

	public boolean audioExists;
	
	
	public ABChapter(int chapterNumber, String title, String subtitle, String years, int img, int albumImg, String audioPath, String albumName) {
		
		this.chapterNumber = chapterNumber;
		this.title = title;
		this.subtitle = subtitle;
		this.years = years;
		this.img = img;
		this.albumImg = albumImg;
		this.audioPath = audioPath;
		this.albumName = albumName;
		
		
	}


	public int getImg() {
		return img;
	}


	public void setImg(int img) {
		this.img = img;
	}


	public int getAlbumImg() {
		return albumImg;
	}


	public void setAlbumImg(int albumImg) {
		this.albumImg = albumImg;
	}


	public int getChapterNumber() {
		return chapterNumber;
	}


	public void setChapterNumber(int chapterNumber) {
		this.chapterNumber = chapterNumber;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getSubtitle() {
		return subtitle;
	}


	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}


	public String getYears() {
		return years;
	}


	public void setYears(String years) {
		this.years = years;
	}


	public String getAudioPath() {
		return audioPath;
	}


	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}


	public long getAudioLength() {
		return audioLength;
	}


	public void setAudioLength(long l) {
		this.audioLength = l;
	}


	public long getAudioProgress() {
		return audioProgress;
	}


	public void setAudioProgress(long audioProgress) {
		this.audioProgress = audioProgress;
	}


	public boolean doesAudioExist() {
		return audioExists;
	}


	public void setAudioExist(boolean audioExists) {
		this.audioExists = audioExists;
	}


	public String getAlbumName() {
		return albumName;
	}


	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}


	public long getFileSize() {
		return fileSize;
	}


	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

}
