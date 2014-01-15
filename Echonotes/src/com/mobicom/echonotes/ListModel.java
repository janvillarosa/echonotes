package com.mobicom.echonotes;

public class ListModel {

	private String noteName = "";
	private int numAnnotations;
	private String dateAndTime = "";
	private String color = "";

	public String getNoteName() {
		return noteName;
	}

	public void setNoteName(String noteName) {
		this.noteName = noteName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getNumAnnotations() {
		return numAnnotations;
	}

	public void setNumAnnotations(int numAnnotations) {
		this.numAnnotations = numAnnotations;
	}

	public String getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

}
