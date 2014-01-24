package com.mobicom.echonotes.activity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobicom.echonotes.NoteModifier;
import com.mobicom.echonotes.R;
import com.mobicom.echonotes.database.Annotation;
import com.mobicom.echonotes.database.DatabaseHelper;
import com.mobicom.echonotes.database.Note;

public class PlayNote extends Activity {

	private MediaPlayer mPlayer;
	private ImageView playButton, image, nextAnnotation, previousAnnotation;
	private TextView noteNameTextView, numAnnotationsTextView,
			durationTextView, currImageAnnotationCount, currAnnotationCount;
	private SeekBar seekbar;
	private int numAnnotations, annotationIterator = 0;
	private boolean mStartPlaying = true;
	private Handler handler = new Handler();
	private boolean playStart = false;
	private ArrayList<Annotation> annotations;
	private View textStub, imageStub;
	private Chronometer playTime;
	private long timeSinceStop = 0;
	private DatabaseHelper db;
	private Note currentNote;

	private Runnable moveSeekBarRunnable = new Runnable() {
		@Override
		public void run() {
			seekbar.setMax(mPlayer.getDuration());
			seekbar.setProgress(mPlayer.getCurrentPosition());
			skippingEnbled();
			showAnnotations();

			if (mPlayer.getCurrentPosition() >= mPlayer.getDuration() - 50) {
				playButton.setImageResource(R.drawable.ic_action_play);
				timeSinceStop = 0;
				playTime.stop();
			}
			handler.postDelayed(moveSeekBarRunnable, 10);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				image.setImageBitmap(thumbnail);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DatabaseHelper(getApplicationContext());
		setContentView(R.layout.player_screen);

		playButton = (ImageView) findViewById(R.id.playRecordingImageView);
		nextAnnotation = (ImageView) findViewById(R.id.nextAnnotation);
		previousAnnotation = (ImageView) findViewById(R.id.previousAnnotation);

		seekbar = (SeekBar) findViewById(R.id.seekBar1);
		noteNameTextView = (TextView) findViewById(R.id.noteNameTextView);
		numAnnotationsTextView = (TextView) findViewById(R.id.numAnnotations);
		durationTextView = (TextView) findViewById(R.id.durationTextView);
		playTime = (Chronometer) findViewById(R.id.playTimeChronometer);
		mPlayer = new MediaPlayer();

		textStub = ((ViewStub) findViewById(R.id.playerTextStub)).inflate();
		imageStub = ((ViewStub) findViewById(R.id.playerImageStub)).inflate();
		textStub.setVisibility(View.GONE);
		imageStub.setVisibility(View.GONE);

		Bundle extras;

		if (savedInstanceState == null) {
			extras = getIntent().getExtras();
			if (extras == null) {
			} else {
				numAnnotations = extras.getInt("NUM_ANNOTATIONS");
				noteNameTextView.setText(extras.getString("NOTE_NAME"));
				this.setTitle(extras.getString("NOTE_NAME"));
			}
		} else {
			noteNameTextView.setText((String) savedInstanceState
					.getSerializable("NOTE_NAME"));
			numAnnotations = (Integer) savedInstanceState
					.getSerializable("NUM_ANNOTATIONS");
			numAnnotationsTextView.setText(numAnnotations + " annotations");
			this.setTitle((String) savedInstanceState
					.getSerializable("NOTE_NAME"));
			;
		}

		annotations = db.getAnnotationsOfNote(noteNameTextView.getText()
				.toString());
		numAnnotationsTextView.setText(numAnnotations + " annotations");
		
		currentNote = db.getNote(noteNameTextView.getText().toString());

		mPlayer = new MediaPlayer();

		try {
			mPlayer.setDataSource(currentNote.getRecordingFilePath()+"/main_recording.3gpp");

			mPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (annotations.size() == 0) {
			nextAnnotation.setClickable(false);
			nextAnnotation
					.setImageResource(R.drawable.ic_action_next_item_pressed);
			previousAnnotation.setClickable(false);
			previousAnnotation
					.setImageResource(R.drawable.ic_action_previous_item_pressed);
		} else if (annotations.size() == 1) {
			previousAnnotation.setClickable(false);
			previousAnnotation
					.setImageResource(R.drawable.ic_action_previous_item_pressed);
		}
		skippingEnbled();

		int minutes = mPlayer.getDuration() / 1000 / 60;
		int seconds = mPlayer.getDuration() / 1000 % 60;
		String minutesFormatted = "" + minutes;
		String secondsFormatted = "" + seconds;

		if (minutes < 10) {
			minutesFormatted = "0" + minutesFormatted;
		}

		if (seconds < 10) {
			secondsFormatted = "0" + secondsFormatted;
		}
		durationTextView.setText(minutesFormatted + ":" + secondsFormatted);
		playTime.setBase(SystemClock.elapsedRealtime());

		setListeners();
		setOnTouch();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.player_screen, menu);
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();
		stopPlaying();
		handler.removeCallbacks(moveSeekBarRunnable);
		moveSeekBarRunnable = null;
		handler = null;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		/* FOR EDITING NOTES. WILL BE IMPLEMENTED IN THE FUTURE
		 * case R.id.action_newtext:
			return true;
		case R.id.action_newphoto:
			return true;
		*/
		case R.id.action_settings:
			openSettings();
			return true;
		case R.id.action_delete:
			deleteNote();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void deleteNote(){
		db.deleteNote(currentNote.getNoteId());
		db.deleteAnnotationOfNote(currentNote.getNoteName());
		NoteModifier deleter = new NoteModifier();
		deleter.deleteNote(currentNote.getRecordingFilePath());
		finish();
		
	}
	
	

	protected void onPause() {
		super.onPause();
		pausePlaying();

	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			pausePlaying();
		}
	}

	private void openSettings() {
		Intent intent = new Intent(PlayNote.this, Preferences.class);
		startActivity(intent);
	}

	private void pausePlaying() {
		mPlayer.pause();
	}

	private void setListeners() {
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (fromUser) {
					mPlayer.seekTo(progress);
					seekbar.setProgress(progress);
					showAnnotations();

					if (seekbar.getMax() == seekbar.getProgress()) {
						playButton.setImageResource(R.drawable.ic_action_play);
						timeSinceStop = 0;
						playTime.stop();
					}
				} else {
					seekbar.setProgress(mPlayer.getCurrentPosition());
					showAnnotations();

					if (seekbar.getMax() == seekbar.getProgress()) {
						playButton.setImageResource(R.drawable.ic_action_play);
						timeSinceStop = 0;
						playTime.stop();
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
		});

		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onPlay(mStartPlaying);
				if (mStartPlaying) {
					playTime.setBase(SystemClock.elapsedRealtime()
							+ timeSinceStop);
					playTime.start();
					playButton.setImageResource(R.drawable.ic_action_pause);
				} else {
					playButton.setImageResource(R.drawable.ic_action_play);
					timeSinceStop = playTime.getBase()
							- SystemClock.elapsedRealtime();
					playTime.stop();
				}
				mStartPlaying = !mStartPlaying;
			}
		});

		nextAnnotation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (annotations.size() != 0) {
					int timeStamp = Integer.parseInt(annotations.get(
							annotationIterator).getAnnotationTimeStamp());
					mPlayer.seekTo(timeStamp);
					seekbar.setProgress(timeStamp);
				} else {
					nextAnnotation.setClickable(false);
					nextAnnotation
							.setImageResource(R.drawable.ic_action_next_item_pressed);
				}
			}
		});
		previousAnnotation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				annotationIterator -= 2;
				int timeStamp = Integer.parseInt(annotations.get(
						annotationIterator).getAnnotationTimeStamp());
				mPlayer.seekTo(timeStamp);
				seekbar.setProgress(timeStamp);
			}
		});
	}

	private void setOnTouch() {
		previousAnnotation.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					previousAnnotation
							.setImageResource(R.drawable.ic_action_previous_item_pressed);

				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					previousAnnotation
							.setImageResource(R.drawable.ic_action_previous_item);

				}
				return false;
			}
		});

		nextAnnotation.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					nextAnnotation
							.setImageResource(R.drawable.ic_action_next_item_pressed);

				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					nextAnnotation
							.setImageResource(R.drawable.ic_action_next_item);

				}
				return false;
			}
		});
	}

	private void showAnnotations() {
		try {
			Annotation annotationInView = annotations.get(annotationIterator);

			if (mPlayer.getCurrentPosition() >= Integer
					.parseInt(annotationInView.getAnnotationTimeStamp())) {
				if (annotationInView.getAnnotationType().equals("text")) {

					String temp = "";
					String curr;

					try {
						BufferedReader reader = new BufferedReader(
								new FileReader(
										annotationInView
												.getAnnotationFilePath()));

						while ((curr = reader.readLine()) != null) {
							temp += curr;
							temp += "\n";
						}
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					int tempCount = annotationIterator + 1;

					TextView textAnnotation = (TextView) findViewById(R.id.textAnnotationShowTextView);
					textAnnotation.setText(temp);
					currAnnotationCount = (TextView) findViewById(R.id.annotationCountTextView);
					currAnnotationCount.setText("Annotation " + tempCount);

					textStub.setVisibility(View.VISIBLE);
					imageStub.setVisibility(View.GONE);
					textStub.startAnimation(AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.slide_up));
					annotationIterator++;
					skippingEnbled();
				} else {

					int tempCount = annotationIterator + 1;

					ImageView imageAnnotation = (ImageView) findViewById(R.id.imageAnnotationImageView);
					currImageAnnotationCount = (TextView) findViewById(R.id.imageAnnotationCountTextView);
					currImageAnnotationCount.setText("Annotation " + tempCount);

					Bitmap myBitmap = BitmapFactory.decodeFile(annotations.get(
							annotationIterator).getAnnotationFilePath());
					imageAnnotation.setImageBitmap(myBitmap);

					imageStub.setVisibility(View.VISIBLE);
					textStub.setVisibility(View.GONE);
					imageStub.startAnimation(AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.slide_up));
					annotationIterator++;
					skippingEnbled();
				}
			}
		} catch (Exception e) {

		}
	}
	
	private void skippingEnbled() {
		if (annotationIterator == annotations.size() && annotations.size() == 1) {
			nextAnnotation.setClickable(false);
			nextAnnotation
					.setImageResource(R.drawable.ic_action_next_item_pressed);
		} else if (annotationIterator == 2 && annotations.size() == 2) {
			previousAnnotation.setClickable(true);
			previousAnnotation
					.setImageResource(R.drawable.ic_action_previous_item);
			nextAnnotation.setClickable(false);
			nextAnnotation
					.setImageResource(R.drawable.ic_action_next_item_pressed);
		} else if (annotationIterator == 0 || annotationIterator == 1) {
			previousAnnotation.setClickable(false);
			previousAnnotation
					.setImageResource(R.drawable.ic_action_previous_item_pressed);
		} else if (annotationIterator == annotations.size()) {
			nextAnnotation.setClickable(false);
			nextAnnotation
					.setImageResource(R.drawable.ic_action_next_item_pressed);
		} else {
			previousAnnotation.setClickable(true);
			nextAnnotation.setClickable(true);
			previousAnnotation
					.setImageResource(R.drawable.ic_action_previous_item);
			nextAnnotation.setImageResource(R.drawable.ic_action_next_item);
		}
	}
	
	private void startPlaying() {
		if (!playStart) {
			seekbar.setProgress(0);
			seekbar.setMax(mPlayer.getDuration());
			handler.post(moveSeekBarRunnable);

			mPlayer.start();
			playStart = true;
		} else {
			mPlayer.start();
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

}