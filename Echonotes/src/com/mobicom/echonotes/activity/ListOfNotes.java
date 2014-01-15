package com.mobicom.echonotes.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.mobicom.echonotes.ListModel;
import com.mobicom.echonotes.R;
import com.mobicom.echonotes.adapters.NoteListAdapter;
import com.mobicom.echonotes.adapters.TagAdapter;
import com.mobicom.echonotes.data.RecordingSession;
import com.mobicom.echonotes.database.Annotation;
import com.mobicom.echonotes.database.DatabaseHelper;
import com.mobicom.echonotes.database.Note;
import com.mobicom.echonotes.database.Tag;

public class ListOfNotes extends Activity {

	ListView list;
	NoteListAdapter adapter;
	DatabaseHelper db;
	public ArrayList<ListModel> noteListModelArray = new ArrayList<ListModel>();
	public ArrayList<Annotation> annotations = new ArrayList<Annotation>();
	private String[] drawerListViewItems;
	private ArrayList<Tag> tagsListViewItems;
	private DrawerLayout drawerLayout;
	private ListView drawerListView;
	private ListView tagsListView;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private SharedPreferences sharedPreferences;
	private Editor settingsEditor;
	private ImageButton newNote;

	private void openSettings() {
		Intent intent = new Intent(ListOfNotes.this, Preferences.class);
		startActivity(intent);
	}

	private void openSearch(String query) {
		Intent intent = new Intent(ListOfNotes.this,
				SearchResultsActivity.class);
		intent.putExtra("retrieve", "search");
		intent.putExtra("query", query);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_of_notes);
		db = new DatabaseHelper(getApplicationContext());
		sharedPreferences = getSharedPreferences("TagPreferences", MODE_PRIVATE);
		if (sharedPreferences.getBoolean("firstrun", true)) {
			settingsEditor = sharedPreferences.edit();

			settingsEditor.putString("tagPos0", "Home");
			settingsEditor.putString("tagPos1", "School");
			settingsEditor.putString("tagPos2", "Work");
			settingsEditor.putString("tagPos3", "Leisure");
			settingsEditor.putString("tagPos4", "Personal");
			settingsEditor.putString("tagPos5", "Miscellaneous");

			initializeTagList();
			tagsListViewItems = db.getAllTags();
			settingsEditor.putBoolean("firstrun", false);
			settingsEditor.commit();
			
		}
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View header = inflater.inflate(R.layout.list_of_notes_header,null);

		list = (ListView) findViewById(R.id.noteListView);
		initializeNoteList();
		list.addHeaderView(header);

		drawerListViewItems = getResources().getStringArray(R.array.items);
		drawerListView = (ListView) findViewById(R.id.buttonsListView_gui);

		tagsListViewItems = db.getAllTags();
		tagsListView = (ListView) findViewById(R.id.tagsListView_gui);
		tagsListView.setAdapter(new TagAdapter(this, tagsListViewItems));
		tagsListView.setClickable(true);
		tagsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parentView,
							View childView, int position, long id) {
						Intent intent = new Intent(ListOfNotes.this,
								SearchResultsActivity.class);
						intent.putExtra("retrieve", "tags");
						intent.putExtra("tagID", position);
						startActivity(intent);
					}
				});

		drawerListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.navdrawer_list_item, R.id.text1, drawerListViewItems));

		drawerListView.setClickable(true);
		drawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parentView,
							View childView, int position, long id) {
						switch (position) {
						case 0:
							Intent intent = new Intent(ListOfNotes.this,
									RecordNote.class);
							startActivity(intent);
						}
					}
				});

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close);

		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		newNote = (ImageButton) findViewById(R.id.newNoteButton);

		newNote.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					newNote.setImageResource(R.drawable.newnote_button_pressed);

				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					newNote.setImageResource(R.drawable.newnote_button);

				}
				return false;
			}
		});

		newNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ListOfNotes.this, RecordNote.class);
				startActivity(intent);
			}
		});
	}

	public void onRestore() {
		tagsListViewItems = db.getAllTags();
	}

	public void onResume() {
		super.onResume();
		initializeNoteList();
		tagsListViewItems = db.getAllTags();
		tagsListView.setAdapter(new TagAdapter(this, tagsListViewItems));
		tagsListView.setClickable(true);
		tagsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parentView,
							View childView, int position, long id) {
					}
				});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.closeDB();
	}

	private void initializeTagList() {
		for (int i = 1; i <= 6; i++) {
			String tagName = "";
			String tagColor = "";
			switch (i) {
			case 1:
				tagName = "Home";
				tagColor = "red";
				break;
			case 2:
				tagName = "School";
				tagColor = "orange";
				break;
			case 3:
				tagName = "Work";
				tagColor = "green";
				break;
			case 4:
				tagName = "Leisure";
				tagColor = "cyan";
				break;
			case 5:
				tagName = "Personal";
				tagColor = "purple";
				break;
			case 6:
				tagName = "Miscellaneous";
				tagColor = "brown";
				break;
			}
			Tag tag = new Tag();
			tag.setTagName(tagName);
			tag.setColor(tagColor);
			db.createTag(tag);
		}
	}

	private void initializeNoteList() {
		noteListModelArray.clear();
		ListModel noteListModel = new ListModel();
		Note note;

		List<Note> allNotes = db.getAllNotes();
		for (int i = 0; i < allNotes.size(); i++) {
			note = allNotes.get(i);

			noteListModel = new ListModel();

			annotations = db
					.getAnnotationsOfNote(allNotes.get(i).getNoteName());

			noteListModel.setNoteName(note.getNoteName());
			noteListModel.setNumAnnotations(annotations.size());
			noteListModel.setDateAndTime(note.getDateModified());

			ArrayList<Tag> tag = db.getTagofNote(i + 1);

			if (tag.size() != 0) {
				noteListModel.setColor(tag.get(0).getColor());
			}

			noteListModelArray.add(noteListModel);

		}

			adapter = new NoteListAdapter(this, noteListModelArray,
					getResources());
			list.setAdapter(adapter);

		list.setClickable(true);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentView, View childView,
					int position, long id) {
				Intent intent = new Intent(ListOfNotes.this, PlayNote.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_of_notes, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSubmitButtonEnabled(true);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			public boolean onQueryTextChange(String newText) {
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				openSearch(query);
				return true;
			}
		});
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_search:
			// openSearch();
			return true;
		case R.id.action_settings:
			openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		actionBarDrawerToggle.syncState();
	}

	public void setListData(ArrayList<RecordingSession> recordArrayList) {

		for (int i = 0; i < recordArrayList.size(); i++) {
			ListModel noteListModel = new ListModel();

			noteListModel.setNoteName(recordArrayList.get(i).getName());
			noteListModel.setNumAnnotations(recordArrayList.get(i)
					.getAnnotationTimer().size());
			noteListModel.setDateAndTime(recordArrayList.get(i)
					.getDateModified() + "");

			noteListModelArray.add(noteListModel);
		}

	}

	public void onItemClick(int mPosition) {
		ListModel tempValues = noteListModelArray.get(mPosition);

		Intent intent = new Intent(ListOfNotes.this, PlayNote.class);
		intent.putExtra("NOTE_NAME", tempValues.getNoteName());
		intent.putExtra("NUM_ANNOTATIONS", tempValues.getNumAnnotations());

		startActivity(intent);
	}

	public void onTagItemClick(int mPosition) {
		Intent intent = new Intent(ListOfNotes.this,
				SearchResultsActivity.class);
		intent.putExtra("retrieve", "tags");
		intent.putExtra("tagID", mPosition);
		startActivity(intent);
	}

}
