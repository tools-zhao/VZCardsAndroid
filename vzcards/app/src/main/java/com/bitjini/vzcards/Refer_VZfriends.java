package com.bitjini.vzcards;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bitjini on 18/12/15.
 */
public class Refer_VZfriends extends Activity implements SearchView.OnQueryTextListener {


    String VZFRIENDS_URL = "http://vzcards-api.herokuapp.com/get_my_friends/?access_token=";
    VerifyScreen p = new VerifyScreen();

    Context c;
    View v;
    // ArrayList
    ArrayList<SelectUser> selectUsers;
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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vzfriends_list);

        selectUsers = new ArrayList<SelectUser>();

        try {

            Log.e("token =",""+p.token_sharedPreference);
            String received = new HttpAsyncTask(getApplicationContext()).execute(VZFRIENDS_URL + p.token_sharedPreference).get();

            JSONObject jsonObject = new JSONObject(received);
            String response = jsonObject.getString("response");

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

                Log.e("phone ", "" + phone);

                SelectUser selectUser = new SelectUser();
                selectUser.setfName(firstname);
                selectUser.setLname(lastname);
                selectUser.setPhone(phone);
                selectUser.setPhoto(photo);
                selectUsers.add(selectUser);
            }

            Log.e(" received :", "" + response);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//            resolver = c.getContentResolver();
        listView = (ListView)findViewById(R.id.contactList);
        mSearchView = (SearchView)findViewById(R.id.searchview);
//        displayText = (TextView) findViewById(R.id.resultText);

        listView.setTextFilterEnabled(true);
        setupSearchView();
        adapter = new VZFriends_Adapter(selectUsers, Refer_VZfriends.this);
        listView.setAdapter(adapter);
        filter = adapter.getFilter();
        listView.setFastScrollEnabled(true);

    }


    private void setupSearchView()
    {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search");
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {

        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }




    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }


    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.vz_frnds, null);

        Bitmap image = null;
        SelectUser data = (SelectUser) parent.getItemAtPosition(position);

        String name = data.getName();
        String phoneNo = data.getPhone();
//                image = data.getThumb();
//
//
//                if (image== null) {

        Drawable d = getResources().getDrawable(R.drawable.simple_profile_placeholder1);
        ImageView contactimage = (ImageView) view.findViewById(R.id.contactImage);
        contactimage.setImageDrawable(d);
        contactimage.buildDrawingCache();
        image = contactimage.getDrawingCache();
//                }

        //dynamically increase the size of the imageview
//                int width = image.getWidth();
//                int height = image.getHeight();
//                int newWidth = 300;
//                int newHeight = 240;
//                float scaleWidth = ((float) newWidth) / width;
//                float scaleHeight = ((float) newHeight) / height;
//                Matrix matrix = new Matrix();
//                matrix.postScale(scaleWidth, scaleHeight);
//                Bitmap newbm = Bitmap.createBitmap(image, 0, 0, width, height, matrix,true);

        //Passing data to nextscreen
        Intent nextScreenIntent = new Intent(c, DisplayContact.class);
        nextScreenIntent.putExtra("name", name);
        nextScreenIntent.putExtra("phoneNo", phoneNo);

        Bundle extras = new Bundle();
        extras.putParcelable("photo", image);

        nextScreenIntent.putExtras(extras);


        Log.e("n", name + "." + phoneNo);
        startActivity(nextScreenIntent);
    }


    class VZFriends_Adapter extends BaseAdapter implements Filterable {

        private ArrayList<SelectUser> arrayList = new ArrayList<>();
        private ArrayList<SelectUser> arrayListFilter = new ArrayList<>();
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
                Log.e("inside", "here---------------In View1");


            } else {
                view = convertView;
                Log.e("Inside", "here------------------In View2");
            }
            v = new ViewHolder();
            v.fname = (TextView) view.findViewById(R.id.name);

            v.phone = (TextView) view.findViewById(R.id.number);
            v.imageView = (ImageView) view.findViewById(R.id.contactImage);

            final SelectUser data = (SelectUser) arrayList.get(i);
            v.fname.setText(data.getfName() + " " + data.getLname());

            v.phone.setText(data.getPhone());

            //set Image if exxists
            try {
                if (data.getPhoto() != null) {

                    Picasso.with(_c).load(data.getPhoto()).into(v.imageView);
//                    v.imageView.setTag(data.getPhoto());
//                    new DownloadImagesTask(_c).execute(v.imageView);// Download item_photo from AsynTask

                } else {
                    v.imageView.setImageResource(R.drawable.simple_profile_placeholder1);
                }
                //setting round image
//            Bitmap bm = BitmapFactory.decodeResource(view.getResources(), R.drawable.contact);
                //Load default image
//            roundImage = new RoundImage(bm);
//            v.imageView.setImageDrawable(roundedImage);
            } catch (ArrayIndexOutOfBoundsException ae) {
                ae.printStackTrace();

            } catch
                    (OutOfMemoryError e) {
                //  v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.contact));
                e.printStackTrace();
            }
            Log.e("Image Thumb", "---------" + data.getThumb());
            view.setTag(data);

            // view.setBackgroundColor(Color.parseColor("#88e0e7e0"));


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
                                if (g.getName().toLowerCase()
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
            TextView fname, phone;
        }


    }
}

