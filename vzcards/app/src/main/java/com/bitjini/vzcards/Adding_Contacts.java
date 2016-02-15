//package com.bitjini.vzcards;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by bitjini on 14/1/16.
// */
//public class Adding_Contacts  extends Activity implements AdapterView.OnItemClickListener{
//    ArrayList<String> label;
//    DataBaseHandler dbLabels,dbValues;
//    List<Database_Contact> list,list2;
//    ProfileAdapter myAdapter;
//    ListView listView;
//    ArrayList<Database_Contact> selectUsers = new ArrayList<Database_Contact>();
//    Database_Contact cat;
//    int clickCount = 0;
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.connect_tickets);
//        listView=(ListView) findViewById(R.id.list);
//        label = new ArrayList<String>();
//        label.add("Fname");
//        label.add("Lname");
//        label.add("Industry");
//        label.add("Company");
//        label.add("Address");
//       dbLabels=new DataBaseHandler(this);
//        dbValues=new DataBaseHandler(this);
////        db.onUpgrade(db.getWritableDatabase(), 1, 2);
////        Inserting Contacts
//        Log.d(" Insert :","Inserting..");
//         dbValues.addContact(new Database_Contact("Jack","M","IT","bitjini","git"));
//        dbLabels.addContact(new Database_Contact("Fname","LName","Industry","Company","Address"));
//
//int id=0;
//        //Reading all contacts
//        Log.d("Reading :", "Reading all contacts..");
//       list=dbValues.getAllContacts();
//        list2=dbValues.getAllContacts();
//        List values=new ArrayList();
//
//        List labels=new ArrayList();
//        for(int i=0;i<list.size();i++)
//        {
//           values.add(list.get(i).getName());
//            values.add(list.get(i).getLname());
//            values.add(list.get(i).getIndustry());
//            values.add(list.get(i).getCompany());
//            values.add(list.get(i).getAddress());
//
//        }
//        for(int i=0;i<list2.size();i++)
//        {
//            labels.add(list2.get(i).getName());
//            labels.add(list2.get(i).getLname());
//            labels.add(list2.get(i).getIndustry());
//            labels.add(list2.get(i).getCompany());
//            labels.add(list2.get(i).getAddress());
//        }
////        for(int i=0;i<list.size();i++) {
////            Database_Contact db = new Database_Contact();
////            db.setLabel(labels.get(i));
////            db.setValues(values.get(i));
////            selectUsers.add(db);
////        }
//        myAdapter= new ProfileAdapter(this, selectUsers);
//        listView.setAdapter(myAdapter);
//       listView.setOnItemClickListener(this);
//
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> arg0, View view, int i, long l) {
//
//        // start Database Activity with extras the contact id
//        Intent intent=new Intent(this,Database_Activity.class);
//        intent.putExtra("contact", list.get(i).get_id());
//        startActivityForResult(intent,1);
//
//    }
////    @Override
//    protected  void onActivityResult(int requestCode,int resultCode,Intent data){
//        // get All contacts again, because something changed
//
//        list=dbLabels.getAllContacts();
//        List listName=new ArrayList();
//
//        for(int i=0;i<list.size();i++)
//        {
//            listName.add(i,list.get(i).getName());
//        }
//        myAdapter= new ProfileAdapter(this, selectUsers);
//      listView.setOnItemClickListener(this);
//        listView.setAdapter(myAdapter);
//    }
//    // Adopter class : handle list items
//    class ProfileAdapter extends BaseAdapter {
//
//        ViewHolder holder = new ViewHolder();
//        Context _c;
//        public ArrayList<Database_Contact> groupItem;
//
//        int flag=0;
//        public ProfileAdapter(Context context, ArrayList<Database_Contact> group) {
//            groupItem = group;
//
//            _c = context;
//        }
//
//        @Override
//        public int getCount() {
//            return groupItem.size();
//        }
//
//        @Override
//        public Database_Contact getItem(int i) {
//            return groupItem.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//
//        }
//
//        @Override
//        public View getView(final int position, final View convertView, ViewGroup parent) {
//
//
//            View v = convertView;
//
//            if (v == null) {
//                LayoutInflater inflater = (LayoutInflater) _c.getSystemService
//                        (Context.LAYOUT_INFLATER_SERVICE);
//                v = inflater.inflate(R.layout.db_items, parent, false);
//
//                v.setTag(holder);
//            } else {
//                holder = (ViewHolder) v.getTag();
//            }
//
//
//            holder.mLable = (TextView) v.findViewById(R.id.labels);
//            holder.mValues = (EditText) v.findViewById(R.id.values1);
//
////            holder.mValues.setTag(position);
//            flag=position;
//            cat = groupItem.get(flag);
//
//            holder.mLable.setText(cat.getLabel());
//            holder.mValues.setText(cat.getValues());
//
//            holder.mValues.setTag(flag);
//            Log.e("position...",""+groupItem.get(flag).getValues()+"   "+position);
//
//            if(clickCount==0)
//            {
//                actv(false);
//
//            }else {
//                holder.mValues.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
////                        final int position2 = holder.mValues.getId();
////                        final EditText repsText = (EditText) holder.mValues;
////                        if (repsText.getText().toString().length() > 0) {
////                      }
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//
//                        cat.setValues(editable.toString());
//
//
//
//                    }
//
//                });
//            }
//            if(clickCount==1) {
//                actv(true);
//            }
//            return v;
//        }
//
//        protected void actv(final boolean active) {
//
//
//
//            holder.mValues.setEnabled(active);
//            if (active) {
//                holder.mValues.requestFocus();
//
//            }
//        }
//    }
//
//    class ViewHolder {
//
//        TextView mLable;
//        EditText mValues;
////        public MutableWatcher mWatcher;
//    }
//}
