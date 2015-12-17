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

public class SelectUserAdapter extends BaseAdapter
{
    public List<SelectUser> _data;
    private ArrayList<SelectUser> arrayList;
    Context _c;
    ViewHolder v;
//    RoundImage roundedImage;

    public SelectUserAdapter(List<SelectUser> selectUsers,Context context)
    {
        _data=selectUsers;
        _c=context;
        this.arrayList=new ArrayList<SelectUser>();
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
            view = li.inflate(R.layout.vz_frnds, null);
            Log.e("inside", "here---------------In View1");


        }
        else
        {
            view=convertView;
            Log.e("Inside","here------------------In View2");
        }
        v=new ViewHolder();
        v.title=(TextView)view.findViewById(R.id.name);
        v.phone=(TextView)view.findViewById(R.id.number);
     v.imageView=(ImageView)view.findViewById(R.id.contactImage);

        //Loading font face
//        Typeface typeface=new Typeface.createFromAsset(_c.getAssets(),"fonts/Helvetica.ttf");
//        v.title.setTypeFace(typeface);

        final  SelectUser data=(SelectUser) _data.get(i);
        v.title.setText(data.getName());
        v.phone.setText(data.getPhone());

        //set Image if exxists
        try {
            if (data.getThumb() != null) {
                v.imageView.setImageBitmap(data.getThumb());
            } else {
                v.imageView.setImageResource(R.drawable.simple_profile_placeholder1);
            }
            //setting round image
//            Bitmap bm = BitmapFactory.decodeResource(view.getResources(), R.drawable.contact);
            //Load default image
//            roundImage = new RoundImage(bm);
//            v.imageView.setImageDrawable(roundedImage);
        }catch (OutOfMemoryError e)
        {
          //  v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.contact));
            e.printStackTrace();
        }
        Log.e("Image Thumb", "---------" + data.getThumb());
        view.setTag(data);

       // view.setBackgroundColor(Color.parseColor("#88e0e7e0"));


        return view;
    }
    static class ViewHolder
    {
        ImageView imageView;
        TextView title,phone;
    }



}