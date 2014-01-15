package com.mobicom.echonotes.database;

public class Annotation {

	long annotationId;
	String annotationType;
	String timeStamp;
	String annotationFilePath;
	
	public Annotation(){
		
	}
	
	public Annotation(String annotationType, String annotationFilePath, String timeStamp){
		this.annotationType = annotationType;
		this.annotationFilePath = annotationFilePath;
		this.timeStamp=timeStamp;
	}
	
	public Annotation(long id, String annotationType, String annotationFilePath, String timeStamp){
		this.annotationId = id;
		this.annotationType = annotationType;
		this.annotationFilePath = annotationFilePath;
	}
	
	public void setAnnotationId(long annotationId){
		this.annotationId = annotationId;
	}
	
	public void setAnnotationType(String annotationType){
		this.annotationType = annotationType;
	}
	
	public void setAnnotationFilePath(String annotationFilePath){
		this.annotationFilePath = annotationFilePath;
	}
	
	public void setAnnotationTimeStamp(String timeStamp){
		this.timeStamp = timeStamp;
	}
	
	public long getAnnotationId(){
		return this.annotationId;
	}
	
	public String getAnnotationType(){
		return this.annotationType;
	}
	
	public String getAnnotationFilePath(){
		return this.annotationFilePath;
	}
	
	public String getAnnotationTimeStamp(){
		return this.timeStamp;
	}
}

