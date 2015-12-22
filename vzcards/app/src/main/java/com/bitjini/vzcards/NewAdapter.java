package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * Created by bitjini on 21/12/15.
 */
public class NewAdapter extends BaseExpandableListAdapter {
    public List<ReferalUsers> _data;
    Context _c;
    RecyclerView.ViewHolder v;
//    RoundImage roundedImage;
public ArrayList<ReferalUsers> groupItem, tempChild;
    public ArrayList<ItemDetail> ChildItem = new ArrayList<ItemDetail>();

    public NewAdapter(ArrayList<ReferalUsers> group,ArrayList<ItemDetail> child,Context context)
    {
        groupItem=group;
        ChildItem=child;
        _c=context;

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)_c.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.child_row, parent, false);
        }

        TextView itemName = (TextView) v.findViewById(R.id.textView1);
//
         ItemDetail det = ChildItem.get(childPosition);

//        v.name.setText(_data.get(groupPosition).getName().get(childPosition));
//        v.referredName.setText(_data.get(i).getReferredName());
        itemName.setText(det.getDesc());


        return v;

    }
    @Override
    public int getChildrenCount(int groupPosition) {
        return ChildItem.size();
    }
    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }
    @Override
    public int getGroupCount() {
        return groupItem.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition)
    {
        super.onGroupCollapsed(groupPosition);
    }
    @Override
    public void onGroupExpanded(int groupPosition)
    {
        super.onGroupExpanded(groupPosition);
    }
    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)_c.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.referral, parent, false);
        }

        TextView name = (TextView) v.findViewById(R.id.referralName);
        TextView referredName=(TextView)v.findViewById(R.id.referred);

        ReferalUsers cat = groupItem.get(groupPosition);

        name.setText(cat.getName());
        referredName.setText(cat.getReferredName());


        return v;

    }
     @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
