package com.bitjini.vzcards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

                SelectUser selectUser = new SelectUser();
                selectUser.setItemName(itemName);
                selectUser.setItem_description(description);
                selectUser.setDate_validity(date_validity);
                selectUser.setItem_photo(item_photo);
                selectUser.setConnections(arr2);
                selectUsers.add(selectUser);
//                Log.e("arraylist :", "" + selectUsers);
//                Log.e(" connections :", "" + arr2);

//                try {
//                    if (arr2.length() > 0) {
//                        for (int i2 = 0; i2 < arr2.length(); i2++) {
//                            JSONObject c2 = arr2.getJSONObject(i2);
//                            if (arr2.get(i2) == 0) {
//                                connectorDetails.add(null);
//                                Log.w("connectorDetails null", "" + connectorDetails);
//
//                            }
//                            JSONObject reffered_phone_details = c2.getJSONObject("reffered_phone_details");
//                            String referedFname = reffered_phone_details.getString("firstname");
//                            String referedLname = reffered_phone_details.getString("lastname");
//                            String referedphoto = reffered_phone_details.getString("photo");
//
//                            SelectUser userConnectorDetails = new SelectUser();
//                            userConnectorDetails.setReferredFname(referedFname);
//                            userConnectorDetails.setReferredLname(referedLname);
//                            userConnectorDetails.setReferredPhoto(referedphoto);
//
//
//                            JSONObject connecter_details = c2.getJSONObject("connecter_details");
//
//
//                            String fname = connecter_details.getString("firstname");
//                            String lastname = connecter_details.getString("lastname");
//                            String photo = connecter_details.getString("photo");
//                            userConnectorDetails.setfName(fname);
//                            userConnectorDetails.setLname(lastname);
//                            userConnectorDetails.setPhoto(photo);
//
//                            connectorDetails.add(userConnectorDetails);
//                            Log.e("connectorDetails has", "" + connectorDetails);
//                        }
//                    }
//                }catch (IndexOutOfBoundsException e)
//                {
//                    e.printStackTrace();
//                }

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


            v.item_photo = (ImageView) view.findViewById(R.id.feedImage);

            final SelectUser data = (SelectUser) arrayList.get(i);
            v.txtItem.setText(data.getItemName());
            v.txtDescription.setText(data.getItem_description());
            v.txtDate.setText(data.getDate_validity());

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
            TextView txtItem, txtDescription, txtDate;
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

    }