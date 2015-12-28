package com.bitjini.vzcards;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReferUserAdapter extends BaseAdapter
{
    public List<ReferalUsers> _data;
    Context _c;
    ViewHolder v;
//    RoundImage roundedImage;

    public ReferUserAdapter(ArrayList<ReferalUsers> ReferUsers,Context context)
    {
        _data=ReferUsers;
        _c=context;

    }
    @Override
    public int getCount()
    {
        return _data.size();
    }
    @Override
    public Object getItem(int i)
    {
        return _data.get(i);
    }
    @Override
    public long getItemId(int i)
    {
        return i;

    }
    // @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i,View convertView,ViewGroup viewGroup)
    {
        View view=convertView;
        if(view==null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.referral, null);
            Log.e("inside", "here---------------In View1");
            Log.e("position..",""+ i);


        }
        else
        {
            view=convertView;
            Log.e("Inside","here------------------In View2");
        }
        v=new ViewHolder();
        v.name=(TextView)view.findViewById(R.id.referralName);
        v.referredName=(TextView)view.findViewById(R.id.referred);


        v.name.setText(_data.get(i).getName());
        v.referredName.setText(_data.get(i).getReferredName());



        return view;
    }
    static class ViewHolder
    {

        TextView name,referredName;
    }



}