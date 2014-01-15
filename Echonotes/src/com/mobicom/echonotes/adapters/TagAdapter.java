package com.mobicom.echonotes.adapters;

import java.util.ArrayList;

import com.mobicom.echonotes.ListModel;
import com.mobicom.echonotes.R;
import com.mobicom.echonotes.activity.ListOfNotes;
import com.mobicom.echonotes.database.Tag;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class TagAdapter extends BaseAdapter implements OnClickListener {

        /*********** Declare Used Variables *********/
        private Activity activity;
        private ArrayList<Tag> data;
        private static LayoutInflater inflater = null;
        Tag tempValues = null;
        int i = 0;

        /************* CustomAdapter Constructor *****************/
        public TagAdapter(Activity a, ArrayList<Tag> d) {

                /********** Take passed values **********/
                activity = a;
                data = d;

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

        /********* Create a holder Class to contain inflated xml file elements *********/
        public static class ViewHolder {
                

                public ImageView color;
                public TextView text;

        }

        /****** Depends upon data size called for each row , Create each ListView row *****/
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                View vi = convertView;
                ViewHolder holder;

                if (convertView == null) {

                        /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                        vi = inflater.inflate(R.layout.navdrawer_tag_item, null);

                        /****** View Holder Object to contain tabitem.xml file elements ******/

                        holder = new ViewHolder();
                        holder.text = (TextView) vi.findViewById(R.id.tagText);
                        holder.color = (ImageView) vi.findViewById(R.id.colorView);

                        /************ Set holder with LayoutInflater ************/
                        vi.setTag(holder);
                } else
                        holder = (ViewHolder) vi.getTag();

                if (data.size() <= 0) {

                } else {
                        /***** Get each Model object from Arraylist ********/
                        tempValues = null;
                        tempValues = data.get(position);

                        /************ Set Model values in Holder elements ***********/
                        
                        holder.text.setText(tempValues.getTagName());
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
                        }

                        /******** Set Item Click Listener for LayoutInflater for each row *******/

                        vi.setOnClickListener(new OnItemClickListener(position));
                }
                return vi;
        }

        @Override
        public void onClick(View v) {
        }

        /********* Called when Item click in ListView ************/
        private class OnItemClickListener implements OnClickListener {
                private int mPosition;

                OnItemClickListener(int position) {
                        mPosition = position;
                }

                @Override
                public void onClick(View arg0) {

                        ListOfNotes sct = (ListOfNotes) activity;

                        /****
                         * Call onItemClick Method inside CustomListViewAndroidExample Class
                         * ( See Below )
                         ****/

                        sct.onTagItemClick(mPosition);
                }
        }
}