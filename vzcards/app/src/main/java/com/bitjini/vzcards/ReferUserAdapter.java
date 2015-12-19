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
    private ArrayList<ReferalUsers> arrayList;
    Context _c;
    ViewHolder v;
//    RoundImage roundedImage;

    public ReferUserAdapter(ArrayList<ReferalUsers> ReferUsers,Context context)
    {
        _data=ReferUsers;
        _c=context;
        this.arrayList=new ArrayList<ReferalUsers>();
        this.arrayList.addAll(_data);
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


        }
        else
        {
            view=convertView;
            Log.e("Inside","here------------------In View2");
        }
        v=new ViewHolder();
        v.name=(TextView)view.findViewById(R.id.referralName);
        v.referredName=(TextView)view.findViewById(R.id.referred);



        final  ReferalUsers data=(ReferalUsers) _data.get(i);
        v.name.setText(data.getName());
        v.referredName.setText(data.getReferredName());



        return view;
    }
    static class ViewHolder
    {

        TextView name,referredName;
    }



}