package com.bitjini.vzcards;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bitjini on 23/8/16.
 */
public class MyAdapter extends BaseAdapter {
    ArrayList<ListItem> groupItem=new ArrayList<>();
    Context _c;
    public MyAdapter( ArrayList<ListItem> groupItem,Context context, int resource) {
        this._c = context;
       this.groupItem = groupItem;
    }
    @Override
    public int getCount() {
        return groupItem.size();
    }
    @Override
    public ListItem getItem(int i) {
        return groupItem.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = null;
            convertView = null;
            if (convertView == null) {
                // Not recycled, inflate a new view
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = li.inflate(R.layout.frnds_profile_details, null);


            }
//            holder.textView = (TextView) rowView.findViewById(R.id.labels);
            TextView editText = (TextView) rowView.findViewById(R.id.values1);

            final ListItem listItem = groupItem.get(position);
            if(position==0 )
            {
                editText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                editText.setTextSize(18);
                editText.setTypeface(null, Typeface.BOLD);

            }

            editText.setText(listItem.value);

//            holder.textView.setText(listItem.getLabel().toString());
//              holder.editText.setEnabled(false);

            return rowView;
        }

//        protected void actv(final boolean active) {
//            holder.editText.setEnabled(active);
//            if (active) {
//                holder.editText.requestFocus();
//            }
//        }
}