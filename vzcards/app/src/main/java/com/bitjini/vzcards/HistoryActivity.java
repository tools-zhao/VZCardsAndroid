package com.bitjini.vzcards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Created by VEENA on 12/7/2015.
 */
public class HistoryActivity extends Fragment {

    String HISTORY_URL = "http://vzcards-api.herokuapp.com/history/?access_token=";
    VerifyScreen p = new VerifyScreen();
    // ArrayList
    ArrayList<SelectUser> selectUsers=new ArrayList<>();
    ArrayList<SelectUser> connectorDetails=new ArrayList<>();
    History_Adapter adapter;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View history = inflater.inflate(R.layout.history_listview, container, false);
        try {
            String result= new HttpAsyncTask(getActivity()).execute(HISTORY_URL + p.token_sharedPreference).get();
            Log.e("received History", "" + result);


            JSONObject jsonObject = new JSONObject(result);

            String response = jsonObject.getString("response");
            // Getting JSON Array node
            JSONArray arr = jsonObject.getJSONArray("response");

            // looping through All Contacts
            for (int i = 0; i < arr.length(); i++) {
                JSONObject c = arr.getJSONObject(i);
                // Connection Node in an array
                JSONArray   arr2 = c.getJSONArray("connections");
                JSONArray connection = arr.getJSONObject(i).getJSONArray("connections");
//                JSONObject details=connection.getJSONObject(0).getJSONObject("connecter_details");
                Log.e(" connec json arr:",""+connection);
//                Log.e(" details json obj:",""+details);

//                Log.e(" connectorDetails in:", ""+i+ " "+arr2+" "+ connectorDetails);
                // ticket_details Node in an json object
                JSONObject ticket_details = c.getJSONObject("ticket_details");

                String question = ticket_details.getString("question");
                String description = ticket_details.getString("description");
                String ticket_id = ticket_details.getString("ticket_id");
                String itemName = ticket_details.getString("item");
                String date_validity = ticket_details.getString("date_validity");
                String vz_id = ticket_details.getString("vz_id");
                String item_photo = ticket_details.getString("item_photo");
                String date_created = ticket_details.getString("date_created");
//                Log.e(" description :", "" + description);

                String days= String.valueOf(getDateDifference(date_created));
                SelectUser selectUser = new SelectUser();
                selectUser.setItemName(itemName);
                selectUser.setDate_created(days);
                selectUser.setItem_description(description);
                selectUser.setDate_validity(date_validity);
                selectUser.setItem_photo(item_photo);
                selectUser.setConnections(arr2);
                selectUsers.add(selectUser);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        listView = (ListView) history.findViewById(R.id.historyList);
        Log.e("arraylist :",""+selectUsers);
        adapter = new History_Adapter(selectUsers, getActivity(),R.layout.history_layout);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getActivity(),"you clicked :"+position,Toast.LENGTH_LONG).show();

                int height = 0;
                View toolbar=(View) view.findViewById(R.id.toolbar);
                if (toolbar.getVisibility() == View.VISIBLE) {
                    MyCustomAnimation a = new MyCustomAnimation(toolbar, 500, MyCustomAnimation.COLLAPSE);

                    toolbar.startAnimation(a);
                    toolbar.setClickable(true);
                } else {
                    MyCustomAnimation a = new MyCustomAnimation(toolbar, 500, MyCustomAnimation.EXPAND);
                    a.setHeight(height);
                    toolbar.startAnimation(a);
                    toolbar.setClickable(true);
                }
            }
        });



        return history;
    }
    private class History_Adapter extends BaseAdapter {

        private ArrayList<SelectUser> arrayList = new ArrayList<>();
        private ArrayList<SelectUser> arrayListFilter = null;
        Context _c;
        ViewHolder v;
        int textViewResourceId;

        public History_Adapter(ArrayList<SelectUser> arrayList, Context context, int textViewResourceId1) {
            super();

            this._c = context;
            textViewResourceId = textViewResourceId1;
            this.arrayList = arrayList;
        }


        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;

        }

        // @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = convertView;
            if (view == null) {
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.history_layout, null);
                Log.e("inside", "here---------------In View1");


            } else {
                view = convertView;
                Log.e("Inside", "here------------------In View2");
            }
            v = new ViewHolder();
            v.txtItem = (TextView) view.findViewById(R.id.itemName);
            v.txtDescription = (TextView) view.findViewById(R.id.desc);
            v.txtDate = (TextView) view.findViewById(R.id.days);
            v.txtcount=(TextView) view.findViewById(R.id.refCount);

            v.item_photo = (ImageView) view.findViewById(R.id.feedImage);

            final SelectUser data = (SelectUser) arrayList.get(i);
            v.txtItem.setText(data.getItemName());
            v.txtDescription.setText(data.getItem_description());
            v.txtDate.setText(data.getDate_created());

            //set Image if exxists
            try {
                if (data.getItem_photo() != null) {
                    v.item_photo.setTag(data.getItem_photo());
                    new DownloadImagesTask(_c).execute(v.item_photo);// Download item_photo from AsynTask

                } else {
                    v.item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
                }

            } catch (ArrayIndexOutOfBoundsException ae) {
                ae.printStackTrace();

            } catch (OutOfMemoryError e) {
                //  v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.contact));
                e.printStackTrace();
            }

            Log.e("get connect details", "" + data.getConnections());
            view.setTag(data);
            if (data.getConnections().length()==0) {
                Log.e("data.getConnection :",""+data.getConnections().length());
            }else{
                try {
                JSONArray array = data.getConnections();
                for (int i2 = 0; i2 < array.length(); i2++) {
                    JSONObject c2 = array.getJSONObject(i2);

                    Log.e("count :",""+array.length());


                    if(array.length()>1)
                    {
                        v.txtcount.setText(String.valueOf(array.length())+" referrals");

                    }else
                    {
                        v.txtcount.setText(String.valueOf(array.length())+" referral");
                    }
                    JSONObject reffered_phone_details = c2.getJSONObject("reffered_phone_details");
                    String referedFname = reffered_phone_details.getString("firstname");
                    String referedLname = reffered_phone_details.getString("lastname");
                    String referedphoto = reffered_phone_details.getString("photo");

                    SelectUser userConnectorDetails = new SelectUser();
                    userConnectorDetails.setReferredFname(referedFname);
                    userConnectorDetails.setReferredLname(referedLname);
                    userConnectorDetails.setReferredPhoto(referedphoto);


                    JSONObject connecter_details = c2.getJSONObject("connecter_details");


                    String fname = connecter_details.getString("firstname");
                    String lastname = connecter_details.getString("lastname");
                    String photo = connecter_details.getString("photo");
                    userConnectorDetails.setfName(fname);
                    userConnectorDetails.setLname(lastname);
                    userConnectorDetails.setPhoto(photo);

                    connectorDetails.add(userConnectorDetails);
                    Log.e("connectorDetails has", "" + connectorDetails);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
                // Resets the toolbar to be closed
                View toolbar = (View) view.findViewById(R.id.toolbar);
                ListView list = (ListView) view.findViewById(R.id.referralList);
                Log.e("arraylist :", "" + connectorDetails);

                MyClassAdapter adapter = new MyClassAdapter(getActivity(), R.layout.referral, connectorDetails);
                list.setAdapter(adapter);

                toolbar.setVisibility(View.GONE);

            }




            return view;

        }
    }


        class ViewHolder {
            ImageView item_photo;
            TextView txtItem, txtDescription, txtDate ,txtcount;
        }

        public class MyClassAdapter extends ArrayAdapter<SelectUser> {

            private TextView itemView;
            private ImageView imageView;


            public MyClassAdapter(Context context, int textViewResourceId, ArrayList<SelectUser> items) {
                super(context, textViewResourceId, items);
            }

            public View getView(int position, View convertView, ViewGroup parent) {

                View v = convertView;

                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.referral, null);
                }
                TextView name = (TextView) v.findViewById(R.id.referralName);
                TextView referredName = (TextView) v.findViewById(R.id.referred);
                TextView itemName = (TextView) v.findViewById(R.id.itemName);
                ImageView referredPhoto = (ImageView) v.findViewById(R.id.referdPhoto);
                ImageView photo = (ImageView) v.findViewById(R.id.photo);


                SelectUser cat = connectorDetails.get(position);

                name.setText(cat.getfName() + " " + cat.getLname());
                referredName.setText(cat.getReferredFname() + " " + cat.getReferredLname());
                try {
                    if (cat.getPhoto() != null) {
                        photo.setTag(cat.getPhoto());
                        new DownloadImagesTask(getActivity()).execute(photo);// Download item_photo from AsynTask
//                    pr.bm=null; pr.output=null;
//                    pr.DownloadFullFromUrl(cat.getPhoto());
//                    pr.getRoundedCornerBitmap(pr.bm, 100);
//                    photo.setImageBitmap(pr.output);

                    } else {
                        photo.setImageResource(R.drawable.profile_pic_placeholder);
                    }
                    if (cat.getReferredPhoto() != null) {
//                    pr.bm=null; pr.output=null;
//                    pr.DownloadFullFromUrl(cat.getReferedPhoto());
//                    pr.getRoundedCornerBitmap(pr.bm, 100);
//                    referredPhoto.setImageBitmap(pr.output);
                        referredPhoto.setTag(cat.getReferredPhoto());
                        new DownloadImagesTask(getActivity()).execute(referredPhoto);// Download item_photo from AsynTask

                    } else {
                        photo.setImageResource(R.drawable.profile_pic_placeholder);
                    }

                } catch (ArrayIndexOutOfBoundsException ae) {
                    ae.printStackTrace();

                } catch (OutOfMemoryError e) {
                    //  v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.contact));
                    e.printStackTrace();
                }

                return v;
            }
        }

    public String getDateDifference(String date_created)  {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String time = null;
        String created_date=date_created.replaceAll("[^0-9-:]", " ");
        String output = created_date.substring(0, 19);
        Log.e(" date_created rep  :", "" + output);
         // Create Calendar instance
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2= Calendar.getInstance();

        String result = sdf.format(calendar2.getTime());
        System.out.println(result);


        try {
            // set the created date to calender1 object
            String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
             Date date = new SimpleDateFormat(pattern).parse(date_created);
             calendar1.setTime(date);

            // set the current date to calender2 object
             Date date2 = new SimpleDateFormat(pattern).parse(result);
             calendar2.setTime(date2);

            Log.e("date1 =",""+date);
            Log.e("date2 =",""+date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
		/*
		 * Use getTimeInMillis() method to get the Calendar's time value in
		 * milliseconds. This method returns the current time as UTC
		 * milliseconds from the epoch
		 */
        long miliSecondForDate1 = calendar1.getTimeInMillis();
        long miliSecondForDate2 = calendar2.getTimeInMillis();

        // Calculate the difference in millisecond between two dates
        long diffInMilis = miliSecondForDate2-miliSecondForDate1;

		/*
		 * Now we have difference between two date in form of millsecond we can
		 * easily convert it Minute / Hour / Days by dividing the difference
		 * with appropriate value. 1 Second : 1000 milisecond 1 Hour : 60 * 1000
		 * millisecond 1 Day : 24 * 60 * 1000 milisecond
		 */
        long elapsed = 0;

        long diffInSecond = diffInMilis / 1000;
        long diffInMinute = diffInMilis / (60 * 1000);
        long diffInHour = diffInMilis / (60 * 60 * 1000);
        long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);


        if(diffInSecond<=60)
        {
            elapsed=diffInSecond;
            time="seconds";
            System.out.println("Difference in Seconds : " + elapsed);
        }
        if(diffInSecond>60 && diffInMinute<60){

            elapsed=diffInMinute;
            time="mins";
            System.out.println("Difference in Minute : " + elapsed);
        }
        if(diffInMinute>60 && diffInHour<24)
        {
            elapsed=diffInHour;
            time="hrs";
            System.out.println("Difference in Hours : " + elapsed);
        }
        if(diffInHour>24 && diffInDays<30) {

            elapsed=diffInDays;
            time="days";
            System.out.println("Difference in Days : " + elapsed);
        }
        if(diffInDays>30) {

            elapsed= Long.parseLong(output);
            System.out.println("Difference in Days : " + elapsed);
        }


   return elapsed +" "+time;



    }
    }