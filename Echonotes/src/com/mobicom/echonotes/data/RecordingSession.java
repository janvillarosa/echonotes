package com.mobicom.echonotes.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;

public class RecordingSession {
	
	private ArrayList<String> listOfTextAnnotations;
	private ArrayList<String> listOfPicturePathAnnotations;
	private ArrayList<Long> annotationTimer;
	private String recordingFilePath;
	private String name;
	private String dateModified;
	private String category;
	
	
	public RecordingSession() {
		listOfTextAnnotations = new ArrayList();
		listOfPicturePathAnnotations = new ArrayList();
		annotationTimer = new ArrayList();
		
	}


	public ArrayList<String> getListOfTextAnnotations() {
		return listOfTextAnnotations;
	}


	public void setListOfTextAnnotations(ArrayList<String> listOfTextAnnotations) {
		this.listOfTextAnnotations = listOfTextAnnotations;
	}


	public ArrayList<String> getListOfPicturePathAnnotations() {
		return listOfPicturePathAnnotations;
	}


	public void setListOfPicturePathAnnotations(
			ArrayList<String> listOfPicturePathAnnotations) {
		this.listOfPicturePathAnnotations = listOfPicturePathAnnotations;
	}


	public String getRecordingFilePath() {
		return recordingFilePath;
	}


	public void setRecordingFilePath(String recordingFilePath) {
		this.recordingFilePath = recordingFilePath;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDateModified() {
		return dateModified;
	}


	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}


	public String getCategory() {
		return category;
	}

	public ArrayList<Long> getAnnotationTimer() {
		return annotationTimer;
	}


	public void setAnnotationTimer(ArrayList<Long> annotationCounter) {
		this.annotationTimer = annotationCounter;
	}


	public void setCategory(String category) {
		this.category = category;
	}
	
	public void writeMetadata(){
		
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getPath()+"/Echonotes/notes.txt", true));
			out.write(name+"\n");
			out.write(recordingFilePath+"\n");
			out.write(dateModified+"\n");
			out.write(category+"\n");
			for(int i=0;i<listOfTextAnnotations.size();i++){
				out.write(listOfTextAnnotations.get(i)+"\n");
			}
			for(int i=0;i<listOfPicturePathAnnotations.size();i++){
				out.write(listOfPicturePathAnnotations.get(i)+"\n");
			}
			for(int i=0;i<annotationTimer.size();i++){
				out.write(annotationTimer.get(i)+"\n");
			}
			out.close();
			
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getPath()+"/Echonotes/categories.txt", true));
			out.write(category+name+"\n");
			out.close();
		}catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
