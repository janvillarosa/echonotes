package com.mobicom.echonotes;

import java.io.File;
import java.io.IOException;

public class NoteModifier {
	
	
	public void deleteNote(String filepath){
		
		File directory = new File(filepath);
		 
    	//make sure directory exists
    	if(!directory.exists()){
 
           System.out.println("Directory does not exist.");
           System.exit(0);
 
        }else{
 
           try{
 
               delete(directory);
 
           }catch(IOException e){
               e.printStackTrace();
               System.exit(0);
           }
        }
	}
	
	public static void delete(File file)
	    	throws IOException{
	 
	    	if(file.isDirectory()){
	 
	    		if(file.list().length==0){
	 
	    		   file.delete();
	 
	    		}else{
	 
	        	   String files[] = file.list();
	 
	        	   for (String temp : files) {
	        	      File fileDelete = new File(file, temp);
	 
	        	     delete(fileDelete);
	        	   }
	 
	        	   if(file.list().length==0){
	           	     file.delete();
	        	   }
	    		}
	 
	    	}else{
	    		file.delete();
	    	}
	    }

}
