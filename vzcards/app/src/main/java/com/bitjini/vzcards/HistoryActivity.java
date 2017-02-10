package com.bitjini.vzcards;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTimeUtils;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static com.bitjini.vzcards.BaseURLs.HISTORY_URL;
import static com.bitjini.vzcards.BaseURLs.URL_REMOVE_TICKET;
import static com.bitjini.vzcards.Constants.token_sharedPreference;

/**
 * Created by VEENA on 12/7/2015.
 */
public class HistoryActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

     private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<SelectUser> arrayList = new ArrayList<>();
    MyClassAdapter childAdapter;
    // ArrayList
    ArrayList<SelectUser> selectUsers=new ArrayList<>();
    ArrayList<SelectUser> connectorDetails=new ArrayList<>();
    History_Adapter adapter;
    ListView listView;
    ProgressDialog progressDialog;
    int count=0;
    ImageView progressContainer;
    ProgressBar progressBar;

    View footer;
    int countOfFeeds=0;
    int currentPage=1;
    int totalPage=0;
    boolean isLoading=false;
    TextView emptyMsg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View history = inflater.inflate(R.layout.history_listview, container, false);
        emptyMsg=(TextView) history.findViewById(R.id.emptyFeeds);
          progressContainer = (ImageView) history.findViewById(R.id.progress);
//        progressBar = (ProgressBar)history.findViewById(R.id.progress1);
        swipeRefreshLayout = (SwipeRefreshLayout) history.findViewById(R.id.pullToRefresh);

        listView = (ListView) history.findViewById(R.id.historyList);
        LayoutInflater inflater2 = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (View) inflater2.inflate(R.layout.loading_layout, null);

        // the refresh listner. this would be called when the layout is pulled down
        swipeRefreshLayout.setOnRefreshListener(this);

        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.pink,
                R.color.colorPrimary
                ,R.color.red);
        // check if you are connected or not
        if (isConnected()) {
            Log.e("", "You are conncted");
        } else {
            Log.e("", "You are NOT conncted");
            Toast.makeText(getActivity(),"Check your Network Connectivity",Toast.LENGTH_LONG).show();
        }

        if(getActivity()!=null) {
        getHistoryContents(HISTORY_URL + token_sharedPreference);

            adapter = new History_Adapter(selectUsers, getActivity(), R.layout.history_layout);

            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(getActivity(),"you clicked :"+position,Toast.LENGTH_LONG).show();
                SelectUser data = (SelectUser) arrayList.get(position);
                int height = 0;
                View toolbar=(View) view.findViewById(R.id.toolbar);
                View viewline=(View)view.findViewById(R.id.viewLine);

                if (toolbar.getVisibility() == View.VISIBLE) {
                    MyCustomAnimation a = new MyCustomAnimation(toolbar, 500, MyCustomAnimation.COLLAPSE);

                    toolbar.startAnimation(a);
                    toolbar.setClickable(true);

                    if (Integer.parseInt(data.getQuestion()) == 1) {

                        viewline.setBackgroundColor(Color.parseColor("#f27166"));

                    }
                    if (Integer.parseInt(data.getQuestion()) == 0) {

                        viewline.setBackgroundColor(Color.parseColor("#add58a"));

                    }

                } else {
                    MyCustomAnimation a = new MyCustomAnimation(toolbar, 500, MyCustomAnimation.EXPAND);
                    a.setHeight(height);
                    toolbar.startAnimation(a);
                    toolbar.setClickable(true);

                    viewline.setBackgroundColor(Color.parseColor("#771d1e10"));
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
                                 int visibleItemCount, final int totalItemCount) {
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
                Log.i("Main",totalItemCount+"");

                int lastIndexInScreen = visibleItemCount + firstVisibleItem;

                if (lastIndexInScreen>= totalItemCount && 	!isLoading) {


                    // It is time to load more items
                    isLoading = true;
                    totalPage=(int) Math.ceil((double)countOfFeeds / 10.0);
                    Log.e("totalPage ",""+totalPage);

                    loadMore();

                }
            }
        });

        progressContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.GONE);

                swipeRefreshLayout.setRefreshing(true);

                        refreshContent();
            }
        });
        // on configuration changes (screen rotation) we want fragment member variables to preserved
        setRetainInstance(true);
        return history;
    }

    public void getHistoryContents(String url) {


        try {
            count = 1;
            String result = new HttpAsyncTask(getActivity()).execute(url).get();
            Log.e("received History", "" + result);


            listView.setVisibility(View.VISIBLE);

            Log.e("received History", "" + result);


            JSONObject jsonObject = new JSONObject(result);

            countOfFeeds = jsonObject.getInt("count");
            if (countOfFeeds == 0) {
                emptyMsg.setVisibility(View.VISIBLE);
                emptyMsg.setText("Hey, you have not added any tickets.\nPlease \"Add\" tickets.");
                listView.setVisibility(View.GONE);

            } else {
                emptyMsg.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                String response = jsonObject.getString("response");
                // Getting JSON Array node
                JSONArray arr = jsonObject.getJSONArray("response");

                // looping through All Contacts
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject c = arr.getJSONObject(i);
                    // Connection Node in an array
                    JSONArray arr2 = c.getJSONArray("connections");
                    JSONArray connection = arr.getJSONObject(i).getJSONArray("connections");
                    String question = "", description = "", ticket_id = "", itemName = "", date_validity = "", vz_id = "", item_photo = "", date_created = "";

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

                    swipeRefreshLayout.setRefreshing(false);
                }

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
                connectorDetails.clear();

                swipeRefreshLayout.setRefreshing(true);
                currentPage=1;
                totalPage=0;
                countOfFeeds=0;
                isLoading = false;


                if(getActivity()!=null) {
                    getHistoryContents(HISTORY_URL + token_sharedPreference);

                    adapter = new History_Adapter(selectUsers, getActivity(), R.layout.history_layout);

                    listView.setAdapter(adapter);

                    listView.setVisibility(View.VISIBLE);
                }
                //do processing to get new data and set your listview's adapter, maybe  reinitialise the loaders you may be using or so
                //when your data has finished loading, set the refresh state of the view to false
//                swipeRefreshLayout.setRefreshing(false);

            }
        }, 2000);

    }
    public void loadMore(){

        listView.addFooterView(footer);

        new Handler().postDelayed(new Runnable() {

            @Override public void run() {
//
        currentPage++;
        if(currentPage<=totalPage) {

            Log.e("currentpage=",""+currentPage);

            getHistoryContents(HISTORY_URL + token_sharedPreference +"&page="+currentPage);

//            // Notify the ListView of data changed
//
            adapter.notifyDataSetChanged();

            isLoading = false;
            listView.removeFooterView(footer);


        }
        else {
            listView.removeFooterView(footer);
        }
            }
        }, 2000);

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    private class History_Adapter extends BaseAdapter {

        private ArrayList<SelectUser> arrayListFilter = null;
        Context _c;
        ViewHolder v;
        int textViewResourceId;

        public History_Adapter(ArrayList<SelectUser> arrayList1, Context context, int textViewResourceId1) {
            super();

            this._c = context;
            textViewResourceId = textViewResourceId1;
           arrayList= arrayList1;
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
            View view= null;
            convertView = null;
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.history_layout, viewGroup,false);


            } else {
                view = convertView;
               }
            v = new ViewHolder();

            v.txtItem = (TextView) view.findViewById(R.id.itemName);
            v.txtDescription = (TextView) view.findViewById(R.id.desc);
            v.txtDate = (TextView) view.findViewById(R.id.days);
            v.txtcount=(TextView) view.findViewById(R.id.refCount);
            v.btnRemove=(Button) view.findViewById(R.id.remove);
            v.tickImage=(ImageView)view.findViewById(R.id.tick);

            v.viewLine=(View)view.findViewById(R.id.viewLine);
            v.viewline2=(View)view.findViewById(R.id.viewLine2);
            v.item_photo = (ImageView) view.findViewById(R.id.feedImage);

            final SelectUser data = (SelectUser) arrayList.get(i);
            v.txtItem.setText(data.getItemName());
            v.txtDescription.setText(data.getItem_description());
            v.txtDate.setText(data.getDate_created());

//            Log.e("question :",""+Integer.parseInt(data.getQuestion()));
            // check if it is has change the color to green=0
            if (Integer.parseInt(data.getQuestion()) == 0) {
                v.viewLine.setBackgroundColor(Color.parseColor("#add58a")); //Green

            }
            // check if it is has change the color to red=1
            if (Integer.parseInt(data.getQuestion()) == 1) {
                v.viewLine.setBackgroundColor(Color.parseColor("#f27166"));// Red

            }
//            Log.e("pic image :",""+data.getItem_photo());
            //set Image if exxists
            try {
                if (data.getItem_photo().isEmpty()) {
//
                    v.item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
                } else {

//                    v.item_photo.setTag(data.getItem_photo());
//                    new DownloadImageProgress(_c).execute(String.valueOf(v.item_photo));// Download item_photo from AsynTask
                    Picasso.with(_c).load(data.getItem_photo()).resize(200, 200).into( v.item_photo);
                }

            } catch (ArrayIndexOutOfBoundsException ae) {
                ae.printStackTrace();

            } catch (OutOfMemoryError e) {
                //  v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.contact));
                e.printStackTrace();
            }


//            Log.e("get connect details", "" + data.getConnections());
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
                                    progressDialog=new ProgressDialog(getActivity());
                                    if(progressDialog!=null){
                                    progressDialog.setMessage("Deleting ticket..");
                                    progressDialog.show();
                                    progressDialog.setCancelable(false);}
                                    new HttpAsyncTask(getActivity()){
                                       @Override
                                        public void onPostExecute(String result)
                                       {
                                           if(progressDialog!=null & progressDialog.isShowing())
                                           {
                                               progressDialog.dismiss();
                                               progressDialog=null;
                                           }

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
                                    }.execute(URL_REMOVE_TICKET+ticketId+"?access_token="+token_sharedPreference);

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
            connectorDetails=new ArrayList<>();

            if (data.getConnections().length()==0) {
//                Log.e("data.getConnection :",""+data.getConnections().length());
            }else{
                v.tickImage.setImageResource(R.drawable.tickxx);
                try {
                JSONArray array = data.getConnections();
                for (int i2 = 0; i2 < array.length(); i2++) {
                    JSONObject c2 = array.getJSONObject(i2);

                    Log.e("count :",""+array.length());

                    if(array.length()>1)
                    {
                        v.txtcount.setText(String.valueOf(array.length())+" Referrals");

                    }else
                    {
                        v.txtcount.setText(String.valueOf(array.length())+" Referral");
                    }

                    String referedFname="",referedLname="",referedphoto="";

                    String refPhoneDetails  = c2.getString("reffered_phone_details");

                    // check if response in valid json object or string
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
                        String phone = c2.getString("reffered_phone_details");
                        referedFname = c2.getString("reffered_ticket_details");

                        Log.e("ref phone ",""+phone);
                        Log.e("ref fname ",""+referedFname);
                    }

                    JSONObject connecter_details = c2.getJSONObject("connecter_details");

                    String fname = connecter_details.getString("firstname");
                    String lastname = connecter_details.getString("lastname");
                    String photo = connecter_details.getString("photo");

                    String question=data.getQuestion();
                    SelectUser userConnectorDetails = new SelectUser();
                    userConnectorDetails.setfName(fname);
                    userConnectorDetails.setLname(lastname);
                    userConnectorDetails.setPhoto(photo);

                    userConnectorDetails.setQuestion(question);
                    userConnectorDetails.setReferredFname(referedFname);
                    userConnectorDetails.setReferredLname(referedLname);
                    userConnectorDetails.setReferredPhoto(referedphoto);
                    connectorDetails.add(userConnectorDetails);

                    Log.e("connectorDetails has", "" + userConnectorDetails.getReferredFname());

                }
                    Log.e("conectDetails.length()",""+connectorDetails.size());

                    for(SelectUser u:connectorDetails)
                    {
                        Log.e("number of ref's ",""+u.getReferredFname());
                    }
                } catch (JSONException e) {
                e.printStackTrace();
            }
                // Resets the toolbar to be closed
                View toolbar = (View) view.findViewById(R.id.toolbar);
                ListView list = (ListView) view.findViewById(R.id.referralList);

//                Log.e("arraylist :", "" + connectorDetails);
                if(getActivity()!=null) {
                    childAdapter = new MyClassAdapter(getActivity(), connectorDetails, R.layout.history_referrals);
                    list.setAdapter(childAdapter);
                    if (Integer.parseInt(data.getQuestion()) == 0) {
                        v.viewline2.setBackgroundColor(Color.parseColor("#add58a")); //Green

                    }
                    // check if it is has change the color to red=1
                    if (Integer.parseInt(data.getQuestion()) == 1) {
                        v.viewline2.setBackgroundColor(Color.parseColor("#f27166"));// Red

                    }
                }
                Utility.setListViewHeightBasedOnChildren(list);
                toolbar.setVisibility(View.GONE);

            }

//            connectorDetails.clear();
//

            return view;

        }
    }


        class ViewHolder {
            ImageView item_photo,tickImage;
            TextView txtItem, txtDescription, txtDate ,txtcount;
            Button btnRemove;
            View viewLine,viewline2;
        }

        public class MyClassAdapter extends BaseAdapter {

            Context _c;
            public ArrayList<SelectUser> itemList=new ArrayList<>();
            int textViewResourceId;


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

                View v = null;
                convertView = null;
                if (convertView == null) {

                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.history_referrals,parent, false);
                }
                TextView name = (TextView) v.findViewById(R.id.referralName);
                TextView referredName = (TextView) v.findViewById(R.id.referred);
                TextView itemName = (TextView) v.findViewById(R.id.itemName);
                ImageView referredPhoto = (ImageView) v.findViewById(R.id.referdPhoto);
                ImageView photo = (ImageView) v.findViewById(R.id.photo);



                SelectUser cat = itemList.get(position);
                Log.e("position",""+position);

                Log.e("js connectorDetails", "" + cat.getfName()+" " + position);
                Log.e("js connectorDetails", "" + cat.getReferredFname() +" "+position);

                name.setText(cat.getfName() + " " + cat.getLname());
                referredName.setText(cat.getReferredFname() + " " + cat.getReferredLname());


                try {
                    if (!cat.getPhoto().isEmpty()) {
//                        photo.setTag(cat.getPhoto());
//                        new DownloadImagesTask(getActivity()).execute(photo);// Download item_photo from AsynTask
                        Picasso.with(_c).load(cat.getPhoto()).centerCrop().resize(150,150).into( photo);


                    } else {
                        photo.setImageResource(R.drawable.profile_pic_placeholder);
                    }
                    if (!cat.getReferredPhoto().isEmpty()) {

                        Picasso.with(_c).load(cat.getReferredPhoto()).centerCrop().resize(150,150).into(referredPhoto);
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

    public String getDateDifference(String date_created) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String time = null;
        String created_date = date_created.replaceAll("[^0-9-:]", " ");
        String output = created_date.substring(0, 19);
        Log.e(" date_created rep  :", "" + output);

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        DateTimeUtils obj = null;
        int days;
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+"));
        String result = sdf.format(calendar2.getTime());

        System.out.println(result);

        Period period = null;
        long elapsed = 0;
        try {


            Date date = sdf.parse(date_created);
            calendar1.setTime(date);

            // set the current date to calender2 object

            Date date2 = sdf.parse(result);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date2);

            Log.e("date1 =", "" + date);
            Log.e("date2 =", "" + date2);


//    }       obj.printDifference(date, date2);


//        return "Added "+period.getMonths() +" "+time;

//		/*
//		 * Use getTimeInMillis() method to get the Calendar's time value in
//		 * milliseconds. This method returns the current time as UTC
//		 * milliseconds from the epoch
//		 */

            Interval interval =
                    new Interval(date.getTime(), date2.getTime());
            period = interval.toPeriod();
            Log.e("period", "" + period);
            System.out.printf(
                    "%d years, %d months, %d days, %d hours, %d minutes, %d seconds%n",
                    period.getYears(), period.getMonths(), period.getDays(),
                    period.getHours(), period.getMinutes(), period.getSeconds());

        long miliSecondForDate1 = calendar1.getTimeInMillis();
        long miliSecondForDate2 = calendar2.getTimeInMillis();

        // Calculate the difference in millisecond between two dates
        long diffInMilis = miliSecondForDate2-miliSecondForDate1;
//
//		/*
//		 * Now we have difference between two date in form of millsecond we can
//		 * easily convert it Minute / Hour / Days by dividing the difference
//		 * with appropriate value. 1 Second : 1000 milisecond 1 Hour : 60 * 1000
//		 * millisecond 1 Day : 24 * 60 * 1000 milisecond
//		 */


            long diffInSecond = diffInMilis / 1000;
            long diffInMinute = diffInMilis / (60 * 1000);
            long diffInHour = diffInMilis / (60 * 60 * 1000);
            long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);


            if (diffInSecond <= 60) {
                elapsed = diffInSecond;
                time = "seconds ago";
                System.out.println("Difference in Seconds : " + elapsed);
            }
            if (diffInSecond > 60 && diffInMinute < 60) {

                elapsed = diffInMinute;

                if (elapsed > 1) {
                    time = "mins ago";
                } else {
                    time = "min ago";
                }
                System.out.println("Difference in Minute : " + elapsed);
            }
            if (diffInMinute > 60 && diffInHour < 24) {
                elapsed = diffInHour;
                if (elapsed > 1) {
                    time = "hrs ago";
                } else {
                    time = "hour ago";
                }
                System.out.println("Difference in Hours : " + elapsed);
            }
            if (diffInHour > 24 && diffInDays < 30) {

                elapsed = diffInDays;
                if (elapsed > 1) {
                    time = "days ago";
                } else {
                    time = "day ago";
                }
                System.out.println("Difference in Days : " + elapsed);
            }
            if (diffInDays > 30) {
                elapsed=period.getMonths();
                System.out.println("Difference in Days : " + period.getMonths());
                if (elapsed > 1) {
                    time = "months ago";
                } else {
                    time = "month ago";
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Added " + elapsed + " " + time;


    }
    }

