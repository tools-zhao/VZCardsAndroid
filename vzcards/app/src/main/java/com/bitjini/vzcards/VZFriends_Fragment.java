package com.bitjini.vzcards;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncContext;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bitjini on 18/12/15.
 */
public class VZFriends_Fragment extends Fragment implements View.OnClickListener,SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    String VZFRIENDS_URL = "https://vzcards-api.herokuapp.com/get_my_friends/?access_token=";

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    VerifyScreen p = new VerifyScreen();

    Context c;
    View v;
    // ArrayList
    ArrayList<SelectUser> selectUsers = new ArrayList<SelectUser>();
    ArrayList<SelectUser> phoneList = new ArrayList<SelectUser>();
    VZFriends_Adapter adapter;
    List<SelectUser> temp;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones, email;
    SearchView mSearchView;
    private TextView displayText;
    Filter filter;
    // Pop up
    ContentResolver resolver;
    ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    View footer;
    int countOfFrnds=0;
    int currentPage=1;
    int totalPage=0;
    boolean isLoading=false;
    View vzfrnds;
    int progressCount=0;

    TextView emptyMsg;
    Button inviteButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         vzfrnds = inflater.inflate(R.layout.contact_listview, container, false);
        emptyMsg=(TextView) vzfrnds.findViewById(R.id.emptyFeeds);


        getActivity();
        progressBar = (ProgressBar) vzfrnds.findViewById(R.id.progress1);
        swipeRefreshLayout = (SwipeRefreshLayout) vzfrnds.findViewById(R.id.pullToRefresh);
        inviteButton=(Button)vzfrnds.findViewById(R.id.invite);

        // the refresh listner. this would be called when the layout is pulled down
        swipeRefreshLayout.setOnRefreshListener(this);
        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.pink,
                R.color.colorPrimary
                ,R.color.red);
        c = vzfrnds.getContext();


        listView = (ListView)vzfrnds.findViewById(R.id.contactList);
        LayoutInflater inflater2 = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (View) inflater2.inflate(R.layout.loading_layout, null);

        mSearchView = (SearchView) vzfrnds.findViewById(R.id.searchview);
        if(getActivity()!=null) {
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
            animation.setDuration(2000); //in milliseconds
            animation.setRepeatCount(5);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
            getVzFrnds(VZFRIENDS_URL + p.token_sharedPreference);


            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            Collections.sort(selectUsers, new SortBasedOnName());// sort in alphabetical order

            adapter = new VZFriends_Adapter(selectUsers, getActivity());
            listView.setAdapter(adapter);

            listView.setTextFilterEnabled(true);
            filter = adapter.getFilter();
        }


        setupSearchView();


        listView.setFastScrollEnabled(true);
        listView.setOnItemClickListener(this);
        inviteButton.setOnClickListener(this);

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
                Log.i("Main",totalItemCount+"");

                int lastIndexInScreen = visibleItemCount + firstVisibleItem;

                if (lastIndexInScreen>= totalItemCount && 	!isLoading) {


                    // It is time to load more items
                    isLoading = true;
                    totalPage=(int) Math.ceil((double)countOfFrnds / 10.0);
                    Log.e("totalPage ",""+totalPage);

                    loadMore();

                }
            }
        });
        Button profilebtn = (Button) vzfrnds.findViewById(R.id.profilebtn);
        Button referral = (Button) vzfrnds.findViewById(R.id.referralbtn);

        profilebtn.setOnClickListener(this);

        referral.setOnClickListener(this);
        return vzfrnds;

    }

    public void getVzFrnds(String url)
    {
        try {
            progressCount=1;

            String received=new HttpAsyncTask(getActivity()).execute(url).get();


                    JSONObject jsonObject = new JSONObject(received);
                    countOfFrnds=jsonObject.getInt("count");
            if(countOfFrnds==0)
            {
                emptyMsg.setVisibility(View.VISIBLE);
                emptyMsg.setText( "Hey, you have no VZFriends.\nPlease invite friends to VZCards.");
                listView.setVisibility(View.GONE);

            }else {
                emptyMsg.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                Log.e("count of frnds", "" + countOfFrnds);
                String response = jsonObject.getString("response");
                Log.e("response of frnds", "" + response);
                // Getting JSON Array node
                JSONArray arr = jsonObject.getJSONArray("response");

                // looping through All Contacts
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject c = arr.getJSONObject(i);
                    // Feed node is JSON Object
                    String phone = c.getString("phone");
                    String firstname = c.getString("firstname");
                    String lastname = c.getString("lastname");
                    String photo = c.getString("photo");

                    String company = c.getString("company");
                    String pin_code = c.getString("pin_code");
                    String industry = c.getString("industry");
                    String address1 = c.getString("address_line_1");
                    String address2 = c.getString("address_line_2");
                    String city = c.getString("city");
                    String title = c.getString("title");
                    String company_photo = c.getString("company_photo");
                    String email = c.getString("email");


                    SelectUser selectUser = new SelectUser();

                    SyncContacts sync = new SyncContacts(getActivity());
//                Log.e("list:",""+sync.phoneList12);
                    for (SelectUser list : sync.phoneList12) {

                        if (phone.equals(list.getPhone())) {
                            selectUser.setfName(list.getName());
                            Log.e("list name",""+list.getfName());
                        }

                    }

                    selectUser.setFirstName(firstname);
                    selectUser.setLastName(lastname);
                    selectUser.setSyncPhone(phone);
                    selectUser.setPhoto(photo);
                    selectUser.setEmail(email);
                    selectUser.setCompany(company);
                    selectUser.setPin_code(pin_code);
                    selectUser.setIndustry(industry);
                    selectUser.setAddress1(address1);
                    selectUser.setAddress2(address2);
                    selectUser.setCity(city);
                    selectUser.setTitle(title);
                    selectUser.setComany_photo(company_photo);


                    selectUsers.add(selectUser);


                }

            }
//            Log.e(" received :", "" + response);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }






    public void onRefresh() {
        // TODO Auto-generated method stub

        refreshContent();

    }
    private void refreshContent(){

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {

                selectUsers.clear();

                currentPage=1;
                totalPage=0;
                countOfFrnds=0;
                isLoading = false;
                getVzFrnds(VZFRIENDS_URL + p.token_sharedPreference);
                if(getActivity()!=null) {
                    Collections.sort(selectUsers, new SortBasedOnName());// sort in alphabetical order

                    adapter = new VZFriends_Adapter(selectUsers, getActivity());
                    listView.setAdapter(adapter);
                    filter = adapter.getFilter();
                }
                //do processing to get new data and set your listview's adapter, maybe  reinitialise the loaders you may be using or so
                //when your data has finished loading, set the refresh state of the view to false
                swipeRefreshLayout.setRefreshing(false);

            }
        }, 5000);

    }
    public void loadMore(){

        listView.addFooterView(footer);

        new Handler().postDelayed(new Runnable() {

            @Override public void run() {
//
                currentPage++;
                if(currentPage<totalPage) {

                    Log.e("currentpage=",""+currentPage);

                    getVzFrnds("https://vzcards-api.herokuapp.com/get_my_friends/?access_token=" + p.token_sharedPreference +"&page="+currentPage);

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
    private void setupSearchView()
    {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
//        mSearchView.setQueryHint("Search");
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {

//        if (TextUtils.isEmpty(newText)) {
//            listView.clearTextFilter();
//        } else {
//            listView.setFilterText(newText);
//        }
        filter.filter(newText);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }


    @Override
    public void onStop() {
        super.onStop();
//        phones.close();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        SelectUser data = (SelectUser) parent.getItemAtPosition(position);
        Intent nextScreenIntent = new Intent(getActivity(), Friends_Profile.class);

        nextScreenIntent.putExtra("phoneName",data.getfName());
        nextScreenIntent.putExtra("fname", data.getFirstName());
        nextScreenIntent.putExtra("lname", data.getLastName());
        nextScreenIntent.putExtra("photo", data.getPhoto());
        nextScreenIntent.putExtra("phone", data.getSyncPhone());
        nextScreenIntent.putExtra("company", data.getCompany());
        nextScreenIntent.putExtra("pin_code", data.getPin_code());
        nextScreenIntent.putExtra("industry", data.getIndustry());
        nextScreenIntent.putExtra("address_line_1", data.getAddress1());
        nextScreenIntent.putExtra("address_line_2", data.getAddress2());
        nextScreenIntent.putExtra("city", data.getCity());
        nextScreenIntent.putExtra("company_photo", data.getComany_photo());
        nextScreenIntent.putExtra("email", data.getEmail());
        nextScreenIntent.putExtra("title",data.getTitle());
        startActivity(nextScreenIntent);
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        Log.e("getCompany :",""+data.getCompany());
        //Inflate the fragment
//        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.slide_out).add(R.id.vzfrnds_list_frame, ldf).addToBackStack(ldf.toString())
//                .commit();
    }



    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profilebtn:
                Fragment profilefragment = new MyProfile_Fragment();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.vzfrnds_list_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(contentView.getId(), profilefragment)
                        .commit();


                break;
            case R.id.referralbtn:

                Fragment fragment = new Referral_Fragment();
                // get the id of fragment
                FrameLayout contentView2 = (FrameLayout) getActivity().findViewById(R.id.vzfrnds_list_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager1 = getFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(contentView2.getId(), fragment)
                        .commit();


                break;
            case R.id.invite:
                Fragment inviteContacts = new InviteContacts();

                // get the id of fragment
                FrameLayout contentView3 = (FrameLayout) getActivity().findViewById(R.id.vzfrnds_list_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(contentView3.getId(), inviteContacts).addToBackStack(null)
                        .commit();
            default:
                break;
        }


    }

    class VZFriends_Adapter extends BaseAdapter implements Filterable {

        private ArrayList<SelectUser> arrayList = new ArrayList<>();
        private ArrayList<SelectUser> arrayListFilter = null;
        Context _c;
        ViewHolder v;

        public VZFriends_Adapter(ArrayList<SelectUser> arrayList, Context context) {
            super();

            this._c = context;

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
                view = li.inflate(R.layout.vz_frnds, null);

            } else {
                view = convertView;

            }
            v = new ViewHolder();
            v.fname = (TextView) view.findViewById(R.id.name);
            v.company=(TextView) view.findViewById(R.id.companyName);
            v.phone = (TextView) view.findViewById(R.id.number);
            v.imageView = (ImageView) view.findViewById(R.id.contactImage);

            final SelectUser data = (SelectUser) arrayList.get(i);
            v.fname.setText(data.getfName());
            Log.e("name=",""+data.getfName());
//            v.company.setText(data.getCompany());
            v.phone.setText(data.getSyncPhone());

            //set Image if exxists
            try {
                if (!data.getPhoto().isEmpty()) {
                    Picasso.with(_c).load(data.getPhoto()).placeholder(R.drawable.progress_animation).into(v.imageView);
//                v.imageView.setTag(data.getPhoto());
//                new DownloadImagesTask(_c).execute(v.imageView);// Download item_photo from AsynTask

                } else {
                    v.imageView.setImageResource(R.drawable.simple_profile_placeholder1);
                }
            } catch (ArrayIndexOutOfBoundsException | OutOfMemoryError ae) {
                ae.printStackTrace();

            }
            view.setTag(data);

            return view;
        }

        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    final FilterResults oReturn = new FilterResults(); // Holds the results of a filtering operation for  publishing

                    final ArrayList<SelectUser> results = new ArrayList<SelectUser>();


                    if (arrayListFilter == null)
                        arrayListFilter = arrayList;

                    /**
                     *
                     * If constraint(CharSequence that is received) is null returns
                     * the arraylist(Original) values else does the Filtering
                     * and returns FilteredArrList(Filtered)
                     *
                     **/

                    if (constraint != null) {

                        if (arrayListFilter != null && arrayListFilter.size() > 0) {
                            for (final SelectUser g : arrayListFilter) {
                                if (g.getfName().toLowerCase()
                                        .contains(constraint.toString()))
                                    results.add(g);
                            }
                        }
                        // set the Filtered result to return
                        oReturn.values = results;
                    }
                    return oReturn;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {
                    // has the filtered values
                    arrayList = (ArrayList<SelectUser>) results.values;
                    // notifies the data with new filtered values. Only filtered values will be shown on the list
                    notifyDataSetChanged();
                }
            };
        }


        class ViewHolder {
            ImageView imageView;
            TextView fname, phone, company;
        }


    }

}