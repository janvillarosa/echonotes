package com.mobicom.echonotes.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "notesManager";

	// Table Names
	private static final String TABLE_NOTES = "notes";
	private static final String TABLE_TAGS = "tags";
	private static final String TABLE_ANNOTATIONS = "annotations";
	private static final String TABLE_NOTES_TAGS = "notes_tags";
	private static final String TABLE_NOTES_ANNOTATIONS = "notes_annotations";

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_FILE_PATH = "file_path";
	private static final String KEY_NOTE_ID = "note_id";

	// NOTES Table - column names
	private static final String KEY_DATE_MODIFIED = "date_modified";

	// TAGS Table - column names
	private static final String KEY_COLOR = "color";

	// ANNOTATIONS Table - column names
	private static final String KEY_TYPE = "type";
	private static final String KEY_TIME = "time_stamp";

	// NOTES_TAGS Table - column names
	private static final String KEY_TAG_ID = "tag_id";

	// NOTES_ANNOTATIONS TABLE
	private static final String KEY_ANNOTATION_ID = "annotation_id";

	// Table Create Statements
	// Notes table create statement
	private static final String CREATE_TABLE_NOTES = "CREATE TABLE "
			+ TABLE_NOTES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
			+ " TEXT," + KEY_FILE_PATH + " TEXT," + KEY_DATE_MODIFIED + " TEXT"
			+ ")";

	// Categories table create statement
	private static final String CREATE_TABLE_TAGS = "CREATE TABLE "
			+ TABLE_TAGS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
			+ " TEXT," + KEY_COLOR + " TEXT" + ")";

	// Annotations table create statement
	private static final String CREATE_TABLE_ANNOTATIONS = "CREATE TABLE "
			+ TABLE_ANNOTATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_TIME + " TEXT," + KEY_TYPE + " INTEGER," + KEY_FILE_PATH
			+ " TEXT" + ")";

	// Notes and Tags table create statement
	private static final String CREATE_TABLE_NOTES_TAGS = "CREATE TABLE "
			+ TABLE_NOTES_TAGS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_NOTE_ID + " TEXT," + KEY_TAG_ID + " TEXT" + ")";

	// Notes and Annotations table create statement
	private static final String CREATE_TABLE_NOTES_ANNOTATIONS = "CREATE TABLE "
			+ TABLE_NOTES_ANNOTATIONS
			+ "("
			+ KEY_ID
			+ " INTEGER PRIMARY KEY,"
			+ KEY_NOTE_ID + " TEXT," + KEY_ANNOTATION_ID + " TEXT" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_NOTES);
		db.execSQL(CREATE_TABLE_TAGS);
		db.execSQL(CREATE_TABLE_ANNOTATIONS);
		db.execSQL(CREATE_TABLE_NOTES_TAGS);
		db.execSQL(CREATE_TABLE_NOTES_ANNOTATIONS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOTATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES_TAGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES_ANNOTATIONS);
		// create new tables
		onCreate(db);
	}

	/* CREATE TABLES */

	public long createTag(Tag tag) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, tag.getTagName());
		values.put(KEY_COLOR, tag.getColor());

		// insert row
		long tag_id = db.insert(TABLE_TAGS, null, values);

		return tag_id;
	}

	public long createNote(Note note) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, note.getNoteName());
		values.put(KEY_DATE_MODIFIED, getDateTime());
		values.put(KEY_FILE_PATH, note.getRecordingFilePath());

		// insert row
		long tag_id = db.insert(TABLE_NOTES, null, values);

		return tag_id;
	}

	public long createAnnotation(Annotation annotation) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIME, annotation.getAnnotationTimeStamp());
		values.put(KEY_TYPE, annotation.getAnnotationType());
		values.put(KEY_FILE_PATH, annotation.getAnnotationFilePath());

		long tag_id = db.insert(TABLE_ANNOTATIONS, null, values);

		return tag_id;
	}

	public long createNoteTag(long noteId, long tagId) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NOTE_ID, noteId);
		values.put(KEY_TAG_ID, tagId);

		// insert row
		long tag_id = db.insert(TABLE_NOTES_TAGS, null, values);

		return tag_id;
	}

	public long createNoteAnnotation(long noteId, long annotationId) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NOTE_ID, noteId);
		values.put(KEY_ANNOTATION_ID, annotationId);

		// insert row
		long tag_id = db.insert(TABLE_NOTES_ANNOTATIONS, null, values);

		return tag_id;
	}

	/* UPDATE DB */

	public int updateNote(Note note) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, note.getNoteName());
		values.put(KEY_DATE_MODIFIED, getDateTime());
		values.put(KEY_FILE_PATH, note.getRecordingFilePath());

		// updating row
		return db.update(TABLE_NOTES, values, KEY_ID + " = ?",
				new String[] { String.valueOf(note.getNoteId()) });
	}

	public int updateAnnotation(Annotation annotation) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIME, annotation.getAnnotationTimeStamp());
		values.put(KEY_TYPE, annotation.getAnnotationType());
		values.put(KEY_FILE_PATH, annotation.getAnnotationFilePath());

		// updating row
		return db.update(TABLE_NOTES, values, KEY_ID + " = ?",
				new String[] { String.valueOf(annotation.getAnnotationId()) });
	}

	public int updateTag(Tag tag) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, tag.getTagName());
		values.put(KEY_COLOR, tag.getColor());

		// updating row
		return db.update(TABLE_TAGS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(tag.getTagId()) });
	}

	/* GET DATA */

	public ArrayList<Tag> getAllTags() {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		String selectQuery = "SELECT  * FROM " + TABLE_TAGS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Tag t = new Tag();
				t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				t.setTagName(c.getString(c.getColumnIndex(KEY_NAME)));
				t.setColor(c.getString(c.getColumnIndex(KEY_COLOR)));

				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public ArrayList<Note> getAllNotes() {
		ArrayList<Note> notes = new ArrayList<Note>();
		String selectQuery = "SELECT  * FROM " + TABLE_NOTES + 
							 "ORDER BY " + DATE_MODIFIED + " DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Note n = new Note();
				n.setNoteId(c.getInt((c.getColumnIndex(KEY_ID))));
				n.setNoteName(c.getString(c.getColumnIndex(KEY_NAME)));
				n.setDateModified(c.getString(c
						.getColumnIndex(KEY_DATE_MODIFIED)));
				n.setRecordingFilePath(c.getString(c
						.getColumnIndex(KEY_FILE_PATH)));

				// adding to tags list
				notes.add(n);
			} while (c.moveToNext());
		}
		return notes;
	}

	public ArrayList<Annotation> getAllAnnotations() {
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		String selectQuery = "SELECT  * FROM " + TABLE_ANNOTATIONS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Annotation a = new Annotation();
				a.setAnnotationId(c.getInt((c.getColumnIndex(KEY_ID))));
				a.setAnnotationTimeStamp(c.getString(c.getColumnIndex(KEY_TIME)));
				a.setAnnotationType(c.getString(c.getColumnIndex(KEY_TYPE)));
				a.setAnnotationFilePath(c.getString(c
						.getColumnIndex(KEY_FILE_PATH)));

				// adding to tags list
				annotations.add(a);
			} while (c.moveToNext());
		}
		return annotations;
	}

	public ArrayList<Annotation> getAnnotationsOfNote(String note_name) {
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		String selectQuery = "SELECT  * FROM " + TABLE_NOTES + " tn, "
				+ TABLE_ANNOTATIONS + " ta, " + TABLE_NOTES_ANNOTATIONS
				+ " tna WHERE " + KEY_NAME + " = '" + note_name + "'"
				+ " AND ta." + KEY_ID + " = " + "tna." + KEY_ANNOTATION_ID
				+ " AND tn." + KEY_ID + " = " + "tna." + KEY_NOTE_ID;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Annotation a = new Annotation();
				a.setAnnotationId(c.getInt((c.getColumnIndex(KEY_ID))));
				a.setAnnotationTimeStamp(c.getString(c.getColumnIndex(KEY_TIME)));
				a.setAnnotationType(c.getString(c.getColumnIndex(KEY_TYPE)));
				a.setAnnotationFilePath(c.getString(c
						.getColumnIndex(KEY_FILE_PATH)));

				// adding to tags list
				annotations.add(a);
			} while (c.moveToNext());
		}
		return annotations;
	}

	public ArrayList<Note> getNotesofTag(int tagID) {
		ArrayList<Note> notes = new ArrayList<Note>();
		String selectQuery = "SELECT " + "tn." + KEY_NAME + "," + "tn."
				+ KEY_ID + ", tn." + KEY_DATE_MODIFIED + "," + KEY_FILE_PATH
				+ " FROM " + TABLE_NOTES + " AS tn, " + TABLE_TAGS + " AS tt, "
				+ TABLE_NOTES_TAGS + " AS tnt WHERE tnt." + KEY_TAG_ID + " = "
				+ tagID + " AND tt." + KEY_ID + " = " + "tnt." + KEY_TAG_ID
				+ " AND tn." + KEY_ID + " = " + "tnt." + KEY_NOTE_ID;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Note n = new Note();
				n.setNoteId(c.getInt((c.getColumnIndex("" + KEY_ID))));
				n.setNoteName(c.getString(c.getColumnIndex("" + KEY_NAME)));
				n.setDateModified(c.getString(c.getColumnIndex(""
						+ KEY_DATE_MODIFIED)));
				n.setRecordingFilePath(c.getString(c
						.getColumnIndex(KEY_FILE_PATH)));

				// adding to tags list
				notes.add(n);
			} while (c.moveToNext());
		}
		return notes;
	}

	public ArrayList<Tag> getTagofNote(int noteID) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		String selectQuery = "SELECT  * FROM " + TABLE_TAGS + " tt, "
				+ TABLE_NOTES + " tn, " + TABLE_NOTES_TAGS + " tnt WHERE tnt."
				+ KEY_NOTE_ID + " = " + noteID + " AND tn." + KEY_ID + " = "
				+ "tnt." + KEY_NOTE_ID + " AND tt." + KEY_ID + " = " + "tnt."
				+ KEY_TAG_ID;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Tag t = new Tag();
				t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				t.setTagName(c.getString(c.getColumnIndex(KEY_NAME)));
				t.setColor(c.getString(c.getColumnIndex(KEY_COLOR)));

				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	public ArrayList<Note> getNotesLike(String noteName) {
		ArrayList<Note> notes = new ArrayList<Note>();
		String selectQuery = "SELECT  * FROM " + TABLE_NOTES + " WHERE "
				+ KEY_NAME + " LIKE " + "'%" + noteName + "%'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Note n = new Note();
				n.setNoteId(c.getInt((c.getColumnIndex(KEY_ID))));
				n.setNoteName(c.getString(c.getColumnIndex(KEY_NAME)));
				n.setDateModified(c.getString(c
						.getColumnIndex(KEY_DATE_MODIFIED)));
				n.setRecordingFilePath(c.getString(c
						.getColumnIndex(KEY_FILE_PATH)));

				// adding to tags list
				notes.add(n);
			} while (c.moveToNext());
		}
		return notes;
	}

	public Note getNote(String noteName) {
		String selectQuery = "SELECT  * FROM " + TABLE_NOTES + " WHERE "
				+ KEY_NAME + " = '" + noteName + "'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		Note n = new Note();

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				n.setNoteId(c.getInt((c.getColumnIndex(KEY_ID))));
				n.setNoteName(c.getString(c.getColumnIndex(KEY_NAME)));
				n.setDateModified(c.getString(c
						.getColumnIndex(KEY_DATE_MODIFIED)));
				n.setRecordingFilePath(c.getString(c
						.getColumnIndex(KEY_FILE_PATH)));

			} while (c.moveToNext());
		}
		return n;
	}

	public ArrayList<Tag> getTag(String tagName) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		String selectQuery = "SELECT  * FROM " + TABLE_TAGS + " WHERE "
				+ KEY_NAME + " = " + tagName;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Tag t = new Tag();
				t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				t.setTagName(c.getString(c.getColumnIndex(KEY_NAME)));
				t.setColor(c.getString(c.getColumnIndex(KEY_COLOR)));

				// adding to tags list
				tags.add(t);
			} while (c.moveToNext());
		}
		return tags;
	}

	// DELETE ENTRIES

	public void deleteNote(long note_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NOTES, KEY_ID + " = ?",
				new String[] { String.valueOf(note_id) });

	}

	public void deleteTag(long tag_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TAGS, KEY_ID + " = ?",
				new String[] { String.valueOf(tag_id) });
		db.delete(TABLE_NOTES_TAGS, KEY_TAG_ID + " = ?",
				new String[] { String.valueOf(tag_id) });
	}

	public void deleteAnnotation(long annotation_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ANNOTATIONS, KEY_ID + " = ?",
				new String[] { String.valueOf(annotation_id) });
		db.delete(TABLE_NOTES_ANNOTATIONS, KEY_ANNOTATION_ID + " = ?",
				new String[] { String.valueOf(annotation_id) });

	}

	public void deleteAnnotationOfNote(String note_name) {
		String selectQuery = "DELETE FROM " + TABLE_ANNOTATIONS + " a "+
		"INNER JOIN " + TABLE_NOTES_ANNOTATIONS +" b on b." + KEY_ANNOTATION_ID + 
		" = a." + KEY_ID + " AND b." + KEY_NOTE_ID + " = \"" + note_name + "\"";
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(selectQuery);
		

	}

	public void deleteAllNotes() {
		ArrayList<Note> allNotes = getAllNotes();

		// delete all notes
		for (Note note : allNotes) {
			// delete note
			deleteNote(note.getNoteId());
		}
	}

	public void deleteAllAnnotations() {
		ArrayList<Annotation> allAnnotations = getAllAnnotations();

		// delete all annotations
		for (Annotation annotation : allAnnotations) {
			// delete annotation
			deleteAnnotation(annotation.getAnnotationId());
		}
	}

	public void deleteAllTags() {
		ArrayList<Tag> allTags = getAllTags();

		// delete all annotations
		for (Tag tag : allTags) {
			// delete annotation
			deleteTag(tag.getTagId());
		}
	}

	public String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}
