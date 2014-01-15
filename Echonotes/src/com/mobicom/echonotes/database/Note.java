package com.mobicom.echonotes.database;

public class Note {
	
	long noteId;
	String noteName;
	String recordingFilePath;
	String dateModified;
	
	public Note(){
		
	}
	
	public Note(String noteName, String recordingFilePath, String dateModified){
		this.noteName = noteName;
		this.recordingFilePath = recordingFilePath;
		this.dateModified = dateModified;
	}
	

	public Note(long noteId,String noteName, String recordingFilePath, String dateModified){
		this.noteId = noteId;
		this.noteName = noteName;
		this.recordingFilePath = recordingFilePath;
		this.dateModified = dateModified;
	}
	
	public void setNoteId(long noteId){
		this.noteId = noteId;
	}
	
	public void setNoteName(String noteName){
		this.noteName = noteName;
	}
	
	public void setRecordingFilePath(String recordingFilePath){
		this.recordingFilePath = recordingFilePath;
	}
	
	public void setDateModified(String dateModified){
		this.dateModified = dateModified;
	}
	
	public long getNoteId(){
		return this.noteId;
	}
	
	public String getNoteName(){
		return this.noteName;
	}
	
	public String getRecordingFilePath(){
		return this.recordingFilePath;
	}
	
	public String getDateModified(){
		return this.dateModified;
	}
}
