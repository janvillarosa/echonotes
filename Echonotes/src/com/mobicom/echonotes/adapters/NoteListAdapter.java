package com.mobicom.echonotes.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobicom.echonotes.ListModel;
import com.mobicom.echonotes.R;
import com.mobicom.echonotes.activity.ListOfNotes;

public class NoteListAdapter extends BaseAdapter implements OnClickListener {

	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {

			ListOfNotes sct = (ListOfNotes) activity;

			sct.onItemClick(mPosition);
		}
	}
	public static class ViewHolder {

		public TextView text;
		public TextView text1;
		public ImageView color;
		public TextView text2;

	}
	/*********** Declare Used Variables *********/
	private Activity activity;
	private ArrayList<ListModel> data;
	private static LayoutInflater inflater = null;
	public Resources res;

	ListModel tempValues = null;

	int i = 0;

	/************* CustomAdapter Constructor *****************/
	public NoteListAdapter(Activity a, ArrayList<ListModel> d, Resources resLocal) {

		/********** Take passed values **********/
		activity = a;
		data = d;
		res = resLocal;

		/*********** Layout inflator to call external xml layout () ***********/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	/******** What is the size of Passed Arraylist Size ************/
	@Override
	public int getCount() {

		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.line_item, null);

			holder = new ViewHolder();
			holder.text = (TextView) vi.findViewById(R.id.noteNameTextView);
			holder.text1 = (TextView) vi
					.findViewById(R.id.numAnnotationTextView);
			holder.text2 = (TextView) vi.findViewById(R.id.dateTextView);
			holder.color = (ImageView) vi.findViewById(R.id.imageView1);

			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if (data.size() <= 0) {
			
			vi=inflater.inflate(R.layout.empty_view, null);
			System.out.print("Empty list");

		} else {
			tempValues = null;
			tempValues = data.get(position);
			
			System.out.print("NonEmpty list");

			holder.text.setText(tempValues.getNoteName());
			holder.text1.setText(tempValues.getNumAnnotations()+ " annotations");
			holder.text2.setText(tempValues.getDateAndTime());
			
			if(tempValues.getColor().equals("cyan")){
				holder.color.setImageResource(R.drawable.catmarker_cyan);
			}
			else if(tempValues.getColor().equals("green")){
				holder.color.setImageResource(R.drawable.catmarker_green);
			}
			else if(tempValues.getColor().equals("orange")){
				holder.color.setImageResource(R.drawable.catmarker_orange);
			}
			else if(tempValues.getColor().equals("purple")){
				holder.color.setImageResource(R.drawable.catmarker_purple);
			}
			else if(tempValues.getColor().equals("brown")){
				holder.color.setImageResource(R.drawable.catmarker_brown);
			}
			else if(tempValues.getColor().equals("red")){
				holder.color.setImageResource(R.drawable.catmarker_red);
			} else {
				holder.color.setImageResource(R.drawable.catmarker_red);
			}

			vi.setOnClickListener(new OnItemClickListener(position));
		}
		return vi;
	}

	@Override
	public void onClick(View v) {
	}
}
