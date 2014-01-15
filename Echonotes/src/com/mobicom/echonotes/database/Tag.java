package com.mobicom.echonotes.database;

public class Tag {

	long tagId;
	String tagName;
	String color;
	
	public Tag(){
		
	}
	
	public Tag(String tagName, String color){
		this.tagName = tagName;
	}
	
	public Tag(long id, String tagName, String color){
		this.tagId = id;
		this.tagName = tagName;
	}
	
	public void setId(long tagId){
		this.tagId = tagId;
	}
	
	public void setTagName(String tagName){
		this.tagName = tagName;
	}
	public void setColor(String color){
		this.color = color;
	}
	
	public String getColor(){
		return this.color;
	}
	
	public long getTagId(){
		return this.tagId;
	}
	
	public String getTagName(){
		return this.tagName;
	}
	
}

