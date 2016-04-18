package com.bitjini.vzcards;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
public class HistoryActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    String HISTORY_URL = "http://vzcards-api.herokuapp.com/history/?access_token=";
    private SwipeRefreshLayout swipeRefreshLayout;

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


        swipeRefreshLayout = (SwipeRefreshLayout) history.findViewById(R.id.pullToRefresh);
        // the refresh listner. this would be called when the layout is pulled down
        swipeRefreshLayout.setOnRefreshListener(this);
        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.pink,
                R.color.colorPrimary
                ,R.color.red);

        listView = (ListView) history.findViewById(R.id.historyList);

        getHistoryContents();
        adapter = new History_Adapter(selectUsers, getActivity(), R.layout.history_layout);

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

       // to avoid triggering of swipe to refresh on scrolling of listview
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(listView != null && listView.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });



        return history;
    }
    public void getHistoryContents()
    {
        try {
            String result = new HttpAsyncTask(getActivity()).execute(HISTORY_URL + p.token_sharedPreference).get();
            Log.e("received History", "" + result);


            JSONObject jsonObject = new JSONObject(result);

            String response = jsonObject.getString("response");
            // Getting JSON Array node
            JSONArray arr = jsonObject.getJSONArray("response");

            // looping through All Contacts
            for (int i = 0; i < arr.length(); i++) {
                JSONObject c = arr.getJSONObject(i);
                // Connection Node in an array
                JSONArray arr2 = c.getJSONArray("connections");
                JSONArray connection = arr.getJSONObject(i).getJSONArray("connections");
                String question="",description="",ticket_id="",itemName="",date_validity="",vz_id="",item_photo="",date_created="";

                // ticket_details Node in an json object
                JSONObject ticket_details = c.getJSONObject("ticket_details");

                 question = ticket_details.getString("question");
                 description = ticket_details.getString("description");
                 ticket_id = ticket_details.getString("ticket_id");
                 itemName = ticket_details.getString("item");
                 date_validity = ticket_details.getString("date_validity");
                 vz_id = ticket_details.getString("vz_id");
                 item_photo = ticket_details.getString("item_photo");
                 date_created = ticket_details.getString("date_created");
//                Log.e(" description :", "" + description);

                String days = String.valueOf(getDateDifference(date_created));
                SelectUser selectUser = new SelectUser();
                selectUser.setItemName(itemName);
                selectUser.setDate_created(days);
                selectUser.setTicket_id(ticket_id);
                selectUser.setItem_description(description);
                selectUser.setDate_validity(date_validity);
                selectUser.setItem_photo(item_photo);
                selectUser.setQuestion(question);
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


    }




    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub

        refreshContent();

    }
    private void refreshContent(){

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {

                selectUsers.clear();
                getHistoryContents();

                adapter = new History_Adapter(selectUsers, getActivity(), R.layout.history_layout);

                listView.setAdapter(adapter);
                //do processing to get new data and set your listview's adapter, maybe  reinitialise the loaders you may be using or so
                //when your data has finished loading, set the refresh state of the view to false
                swipeRefreshLayout.setRefreshing(false);

            }
        }, 5000);

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


            } else {
                view = convertView;
               }
            v = new ViewHolder();
            v.txtItem = (TextView) view.findViewById(R.id.itemName);
            v.txtDescription = (TextView) view.findViewById(R.id.desc);
            v.txtDate = (TextView) view.findViewById(R.id.days);
            v.txtcount=(TextView) view.findViewById(R.id.refCount);
            v.btnRemove=(Button) view.findViewById(R.id.remove);

            v.viewLine=(View)view.findViewById(R.id.viewLine);
            v.item_photo = (ImageView) view.findViewById(R.id.feedImage);

            final SelectUser data = (SelectUser) arrayList.get(i);
            v.txtItem.setText(data.getItemName());
            v.txtDescription.setText(data.getItem_description());
            v.txtDate.setText(data.getDate_created());

            Log.e("question :",""+Integer.parseInt(data.getQuestion()));
            // check if it is has change the color to green=0
            if (Integer.parseInt(data.getQuestion()) == 0) {
                v.viewLine.setBackgroundColor(Color.parseColor("#add58a")); //Green

            }
            // check if it is has change the color to red=1
            if (Integer.parseInt(data.getQuestion()) == 1) {
                v.viewLine.setBackgroundColor(Color.parseColor("#f27166"));// Red

            }
            Log.e("pic image :",""+data.getItem_photo());
            //set Image if exxists
            try {
                if (data.getItem_photo().isEmpty()) {
//
                    v.item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
                } else {

                    v.item_photo.setTag(data.getItem_photo());
//                    new DownloadImageProgress(_c).execute(String.valueOf(v.item_photo));// Download item_photo from AsynTask
                    Picasso.with(_c).load(data.getItem_photo()).resize(250, 250).into( v.item_photo);
                }

            } catch (ArrayIndexOutOfBoundsException ae) {
                ae.printStackTrace();

            } catch (OutOfMemoryError e) {
                //  v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.contact));
                e.printStackTrace();
            }


            Log.e("get connect details", "" + data.getConnections());
            view.setTag(data);
            v.btnRemove.setTag(i);

            v.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Delete");
                    alertDialogBuilder.setMessage("Do you want to Delete this item");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                   String ticketId=data.getTicket_id();
                                    // remove ticket details
                                    new HttpAsyncTask(getActivity()){
                                       @Override
                                        public void onPostExecute(String result)
                                       {

                                            if(responseCode==200){
                                                Integer index = (Integer) v.getTag();
                                                arrayList.remove(index.intValue());
                                                notifyDataSetChanged();
                                                Toast.makeText(getActivity(),"Deleted Successfully",Toast.LENGTH_LONG).show();

                                            }
                                           else {
                                                Toast.makeText(getActivity(),"Delete Failure",Toast.LENGTH_LONG).show();
                                            }
                                       }
                                    }.execute("https://vzcards-api.herokuapp.com/remove_ticket/ticket_id="+ticketId+"?access_token="+ p.token_sharedPreference);

                                }
                            });
                    alertDialogBuilder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
            if (data.getConnections().length()==0) {
//                Log.e("data.getConnection :",""+data.getConnections().length());
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

                    String referedFname="",referedLname="",referedphoto="";

                    String refPhoneDetails  = c2.getString("reffered_phone_details");

                    JsonParser parser1 = new JsonParser();
                    JsonElement jsonObject1 =  parser1.parse(refPhoneDetails);
//                        Log.e("json object1 1=",""+jsonObject1);
                    if(jsonObject1.isJsonObject())
                    {

                        JSONObject reffered_phone_details = c2.getJSONObject("reffered_phone_details");
                        referedFname = reffered_phone_details.getString("firstname");
                         referedLname = reffered_phone_details.getString("lastname");
                         referedphoto = reffered_phone_details.getString("photo");




                    }
                    //you have a string
                    else
                    {
                        refPhoneDetails = c2.getString("reffered_phone_details");
                        referedFname = c2.getString("reffered_ticket_details");


                        Log.e("ref fname ",""+referedFname);
                    }

                    JSONObject connecter_details = c2.getJSONObject("connecter_details");


                    String fname = connecter_details.getString("firstname");
                    String lastname = connecter_details.getString("lastname");
                    String photo = connecter_details.getString("photo");

                    SelectUser userConnectorDetails = new SelectUser();
                    userConnectorDetails.setfName(fname);
                    userConnectorDetails.setLname(lastname);
                    userConnectorDetails.setPhoto(photo);

                    userConnectorDetails.setReferredFname(referedFname);
                    userConnectorDetails.setReferredLname(referedLname);
                    userConnectorDetails.setReferredPhoto(referedphoto);
                    connectorDetails.add(userConnectorDetails);

//                    Log.e("connectorDetails has", "" + connectorDetails);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
                // Resets the toolbar to be closed
                View toolbar = (View) view.findViewById(R.id.toolbar);
                ListView list = (ListView) view.findViewById(R.id.referralList);
//                Log.e("arraylist :", "" + connectorDetails);

                MyClassAdapter adapter = new MyClassAdapter(getActivity(), connectorDetails,R.layout.referral);
                String json2 = new Gson().toJson(connectorDetails);// updated array
                Log.e("updated array", "" + json2);
                list.setAdapter(adapter);

                toolbar.setVisibility(View.GONE);

            }




            return view;

        }
    }


        class ViewHolder {
            ImageView item_photo;
            TextView txtItem, txtDescription, txtDate ,txtcount;
            Button btnRemove;
            View viewLine;
        }

        public class MyClassAdapter extends BaseAdapter {
            Context _c;

            int textViewResourceId;
            ArrayList<SelectUser> itemList=new ArrayList<>();

            public MyClassAdapter(Context context, ArrayList<SelectUser> group, int textViewResourceId1) {
                itemList = group;
                textViewResourceId = textViewResourceId1;
                _c = context;
            }

            @Override
            public int getCount() {
                return itemList.size();
            }

            @Override
            public Object getItem(int i) {
                return itemList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;

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


                SelectUser cat = itemList.get(position);
                Log.e("size connectorDetails", "" + itemList.size());

                name.setText(cat.getfName() + " " + cat.getLname());
                referredName.setText(cat.getReferredFname() + " " + cat.getReferredLname());
                Log.e("js connectorDetails", "" + cat.getfName() +" "+ itemList.get(position));
                Log.e("js connectorDetails", "" + cat.getReferredFname()+" "+ itemList.get(position));
                try {
                    if (!cat.getPhoto().isEmpty()) {
//                        photo.setTag(cat.getPhoto());
//                        new DownloadImagesTask(getActivity()).execute(photo);// Download item_photo from AsynTask
                        Picasso.with(_c).load(cat.getPhoto()).into( photo);


                    } else {
                        photo.setImageResource(R.drawable.profile_pic_placeholder);
                    }
                    if (!cat.getReferredPhoto().isEmpty()) {

                        Picasso.with(_c).load(cat.getReferredPhoto()).into(referredPhoto);
//                        referredPhoto.setTag(cat.getReferredPhoto());
//                        new DownloadImagesTask(getActivity()).execute(referredPhoto);// Download item_photo from AsynTask

                    } else {
                        referredPhoto.setImageResource(R.drawable.profile_pic_placeholder);
                    }

                } catch (ArrayIndexOutOfBoundsException ae) {
                    ae.printStackTrace();

                } catch (OutOfMemoryError e) {
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