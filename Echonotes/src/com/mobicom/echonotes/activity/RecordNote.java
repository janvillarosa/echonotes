package com.mobicom.echonotes.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobicom.echonotes.R;
import com.mobicom.echonotes.data.RecordingSession;
import com.mobicom.echonotes.database.Annotation;
import com.mobicom.echonotes.database.DatabaseHelper;
import com.mobicom.echonotes.database.Note;

public class RecordNote extends Activity {

	private static String path;
	private boolean isRecording = false;

	private Uri fileUri;
	private MediaRecorder mRecorder = null;
	private ImageView startRecord, newPhoto, newText, imageAnnotation, saveText, cancelText;
	private EditText noteName, textAnnotation;
	RecordingSession currentNote;

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int MEDIA_TYPE_IMAGE = 1;
	private View textStub, imageStub, stub, annotationPanel;
	private Chronometer recordTime;
	private TextView numAnnotations, textAnnotationShow, currAnnotationCount, currImageAnnotationCount;
	private long timeStamp = 0;
	private NotificationManager notificationManager;
	private SharedPreferences sharedPreferences;
	private boolean recordOnStandby = false, showNotif = false;

	final Context context = this;

	private Thread recordingThread;

	private DatabaseHelper db;
	private long note_id;
	private long annotation_id;
	private String noteNameString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.recorder_screen);

		sharedPreferences = getSharedPreferences("TagPreferences", MODE_PRIVATE);

		startRecord = (ImageView) findViewById(R.id.startRecordImageView);
		newPhoto = (ImageView) findViewById(R.id.newPhotoImageView);
		noteName = (EditText) findViewById(R.id.noteNameEditText);
		newText = (ImageView) findViewById(R.id.newTextNoteImageView);
		recordTime = (Chronometer) findViewById(R.id.recordTimeChronometer);
		numAnnotations = (TextView) findViewById(R.id.numAnnotationsTextView);

		stub = ((ViewStub) findViewById(R.id.stub)).inflate();
		textStub = ((ViewStub) findViewById(R.id.annotationShowStub)).inflate();
		imageStub = ((ViewStub) findViewById(R.id.imageAnnotationShowStub))
				.inflate();
		annotationPanel = (View) findViewById(R.id.buttonLayout);
		annotationPanel.setVisibility(View.INVISIBLE);
		
		stub.setVisibility(View.INVISIBLE);
		textStub.setVisibility(View.INVISIBLE);
		imageStub.setVisibility(View.INVISIBLE);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		setOnTouch();
		setListeners();

		newPhoto.setClickable(false);
		newText.setClickable(false);

		currentNote = new RecordingSession();

		SharedPreferences settingPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (settingPref.getBoolean("RecordPreference", true)) {
			recordOnStandby = true;
		} else {
			recordOnStandby = false;
		}

		if (settingPref.getBoolean("NotifsPreference", true)) {
			showNotif = false;
		} else {
			showNotif = true;
		}

		db = new DatabaseHelper(getApplicationContext());

	}

	private void setOnTouch() {

		startRecord.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					startRecord
							.setImageResource(R.drawable.start_record_pressed);

				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					startRecord.setImageResource(R.drawable.start_record);

				}
				return false;
			}
		});
	}

	private void setTouchForAnnotations() {
		newPhoto.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					newPhoto.setImageResource(R.drawable.add_picture_annotation_pressed);

				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					newPhoto.setImageResource(R.drawable.add_picture_annotation);

				}
				return false;
			}
		});

		newText.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					newText.setImageResource(R.drawable.add_note_annotation_pressed);

				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					newText.setImageResource(R.drawable.add_note_annotation);

				}
				return false;
			}
		});
	}

	private void setListeners() {
		// RECORD BUTTON LISTENER
		startRecord.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isRecording) {
					if (noteName.length() != 0) {
						currentNote.setName(noteName.getText().toString());
						noteNameString = noteName.getText().toString();
					} else {
						currentNote.setName("Untitled Note");
						noteNameString = "Untitled Note";
					}
					annotationPanel.setVisibility(View.VISIBLE);
					annotationPanel.startAnimation(AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.slide_down));
					createDirectory();

					currentNote.setRecordingFilePath(path);

					recordingThread = new Thread(new Runnable() {
						@Override
						public void run() {
							startRecording();
						}
					});

					recordingThread.start();

					setTouchForAnnotations();

					newPhoto.setClickable(true);
					newText.setClickable(true);
					noteName.setFocusable(false);
					noteName.setClickable(false);

					recordTime.setBase(SystemClock.elapsedRealtime());
					recordTime.start();
					startRecord.setImageResource(R.drawable.stop_record);

					Note note = new Note(currentNote.getName(), currentNote
							.getRecordingFilePath(), db.getDateTime());
					note_id = db.createNote(note);

					if (showNotif)
						notifyUser();

				} else {
					stopRecording();
					saveRecording();
				}
			}
		});

		// NEW PHOTO ANNOTATION LISTENER
		newPhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				timeStamp = annotationTimestamp();
				Intent newPhotoIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				newPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(newPhotoIntent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

			}
		});

		// NEW TEXT ANNOTATION LISTENER
		newText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				stub.setVisibility(View.VISIBLE);
				stub.startAnimation(AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.slide_up));

				saveText = (ImageView) findViewById(R.id.saveTextButton);
				textAnnotation = (EditText) findViewById(R.id.textAnnotationEditText);

				timeStamp = annotationTimestamp();

				saveText.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							BufferedWriter out = new BufferedWriter(
									new FileWriter(path + "/" + timeStamp
											+ ".txt", true));
							out.write(textAnnotation.getText().toString());
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

						currentNote.getAnnotationTimer().add(
								annotationTimestamp());
						numAnnotations.setText(currentNote.getAnnotationTimer()
								.size() + " annotations");

						currentNote.getListOfTextAnnotations().add(
								path + "/" + timeStamp + ".txt");
						Annotation annotation = new Annotation("text",
								currentNote.getListOfTextAnnotations().get(
										currentNote.getListOfTextAnnotations()
												.size() - 1), "" + timeStamp);

						annotation_id = db.createAnnotation(annotation);
						db.createNoteAnnotation(note_id, annotation_id);
						stub.startAnimation(AnimationUtils.loadAnimation(
								getApplicationContext(), R.anim.slide_out_up));
						stub.setVisibility(View.GONE);

						textAnnotationShow = (TextView) findViewById(R.id.textAnnotationShowTextView);
						currAnnotationCount = (TextView) findViewById(R.id.annotationCountTextView);
						textAnnotationShow.setText(textAnnotation.getText()
								.toString());
						currAnnotationCount.setText("Annotation "
								+ currentNote.getAnnotationTimer().size());
						textAnnotation.setText("");

						textStub.setVisibility(View.VISIBLE);
						imageStub.setVisibility(View.GONE);

						textStub.startAnimation(AnimationUtils.loadAnimation(
								getApplicationContext(), R.anim.slide_up));
					}
				});

				cancelText = (ImageView) findViewById(R.id.cancelTextButton);

				cancelText.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						stub.setVisibility(View.GONE);

					}
				});

			}
		});
	}

	private void createDirectory() {
		File directory = new File(Environment.getExternalStorageDirectory()
				+ "/Echonotes/" + noteNameString);

		path = Environment.getExternalStorageDirectory() + "/Echonotes/"
				+ noteNameString;

		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				Log.d("Echonotes", "failed to create directory");
			}
		}
	}

	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private File getOutputMediaFile(int type) {

		// Create a media file name
		String timeStamp = Long.toString(annotationTimestamp());
		File mediaFile;

		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(path + "/IMG_" + timeStamp + ".jpg");

			currentNote.getListOfPicturePathAnnotations().add(
					mediaFile.getPath());

		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				currentNote.getAnnotationTimer().add(annotationTimestamp());

				numAnnotations.setText(currentNote.getAnnotationTimer().size()
						+ " annotations");
				Annotation annotation = new Annotation("image", currentNote
						.getListOfPicturePathAnnotations().get(
								currentNote.getListOfPicturePathAnnotations()
										.size() - 1), ""
						+ annotationTimestamp());
				annotation_id = db.createAnnotation(annotation);
				db.createNoteAnnotation(note_id, annotation_id);

				imageAnnotation = (ImageView) findViewById(R.id.imageAnnotationImageView);
				currImageAnnotationCount = (TextView) findViewById(R.id.imageAnnotationCountTextView);
				
				currImageAnnotationCount.setText("Annotation "
						+ currentNote.getAnnotationTimer().size());

				Bitmap myBitmap = BitmapFactory.decodeFile(currentNote
						.getListOfPicturePathAnnotations().get(
								currentNote.getListOfPicturePathAnnotations()
										.size() - 1));
				imageAnnotation.setImageBitmap(myBitmap);

				textStub.setVisibility(View.GONE);
				imageStub.setVisibility(View.VISIBLE);

				imageStub.startAnimation(AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.slide_up));

			} else if (resultCode == RESULT_CANCELED) {
			}
		}
	}

	public long annotationTimestamp() {
		return SystemClock.elapsedRealtime() - recordTime.getBase();

	}

	protected void onPause() {
		try {
			super.onPause();
			if (!recordOnStandby) {
				stopRecording();
				saveRecording();
			}
		} catch (Exception e) {

		}
	}

	private void saveRecording() {
		recordTime.stop();
		startRecord.setImageResource(R.drawable.start_record);

		try {
			recordingThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		final String[] tags = { "0", "0", "0", "0", "0", "0" };

		for (int i = 0; i < 6; i++) {
			tags[i] = sharedPreferences.getString("tagPos" + i, "Tag " + i);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle("Add to a category");
		builder.setItems(tags, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				currentNote.setCategory(tags[which]);
				db.createNoteTag(note_id, which + 1);
				currentNote.writeMetadata();

				if (showNotif)
					notificationManager.cancelAll();

				finish();

			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void startRecording() {

		File recordingFile = new File(currentNote.getRecordingFilePath() + "/main_recording.3gpp");

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(recordingFile.getPath());
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		mRecorder.start();
		isRecording = true;
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		isRecording = false;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void notifyUser() {
		Notification n = new Notification.Builder(this)
				.setContentTitle("Echonotes is recording.")
				.setContentText("Touch to go back to your recording")
				.setSmallIcon(R.drawable.ic_stat_device_access_mic)
				.setAutoCancel(true).build();

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n);
	}

}