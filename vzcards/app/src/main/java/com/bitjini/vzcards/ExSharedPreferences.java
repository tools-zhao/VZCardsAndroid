//package com.example.bitjini.myapplication;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SharedPrefList extends Activity {
//    public static final String mypreference = "mypref.txt";
//    public static final String TASKS = "key";
//    private static final String SAVED_STATE_KEY = "saved_state_key";
//    SharedPreferences sharedpreferences;
//    ArrayList<String> label;
//    ArrayList<String> values;
//    int clickCount = 0;
//    Button editbtn;
//    ListView listView;
//    EditTextAdapter editTextAdapter;
//    ArrayList<ListItem> arrayList = new ArrayList<ListItem>();
//    ArrayList<ListItem> adapterArrayList = new ArrayList<ListItem>();
//
//    public ArrayList<ListItem> groupItem=new ArrayList<ListItem>();
//    String json;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.profile_layout);
//
//        listView=(ListView)findViewById(R.id.list);
//        LoadPreferences();
//        editbtn = (Button)findViewById(R.id.edit);
//
//
//
//        label = new ArrayList<String>();
//        label.add("Fname");
//        label.add("Lname");
//        label.add("Industry");
//        label.add("Company");
//        label.add("Address");
//
//        values = new ArrayList<String>();
//        values.add("veena");
//        values.add("M");
//        values.add("IT");
//        values.add("BITJIN");
//        values.add("GIT");
//
//        for (int i = 0; i < label.size(); i++) {
//            ListItem item = new ListItem();
//            item.setLabel(label.get(i));
//            item.setValue(values.get(i));
//            arrayList.add(item);
//        }
//
//        editbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (clickCount == 0) {
//
//                    editbtn.setText("Save");
//
//                    editTextAdapter.actv(true);
//                    editTextAdapter.notifyDataSetChanged();
//                    listView.setAdapter(editTextAdapter);
////                    Log.e("selected position of textview", "" + position);
//                    Toast.makeText(SharedPrefList.this, "click 0", Toast.LENGTH_LONG).show();
//                    clickCount = 1;
//
//                } else if (clickCount == 1) {
//
//                    editbtn.setText("Edit");
//
//                    editTextAdapter.actv(false);
//                    json = new Gson().toJson(groupItem);
//
//                    SavePreferences(TASKS,json);
//                    LoadPreferences();
//                    editTextAdapter.notifyDataSetChanged();
//                    listView.setAdapter(editTextAdapter);
//                    Toast.makeText(SharedPrefList.this, "click 1", Toast.LENGTH_LONG).show();
//                    clickCount = 0;
//
//                }
//
//            }
//        });
//        Log.e("updated array",""+groupItem);
//        //converting arrayList to json to Save the values in sharedpreference by calling SavePrefernces
//        // Check if the updated array is equal to default array if false load default array else load updated array
//        if(groupItem==arrayList) {
//            json = new Gson().toJson(arrayList); //default array
//
//            SavePreferences(TASKS, json);
//        }else{
//            json = new Gson().toJson(groupItem);// updated array
//
//            SavePreferences(TASKS, json);
//        }
//        LoadPreferences();
//
//    }
//
//
//    protected void SavePreferences(String key, String value) {
//        // TODO Auto-generated method stub
//        SharedPreferences data = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = data.edit();
//        editor.putString(key, value);
//        editor.commit();
//        System.out.println(value);
//
//
//
//    }
//
//    // To retrived saved values in shared preference Now convert the JSON string back to your java object
//
//    protected void LoadPreferences(){
//
//        SharedPreferences prefs1 = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = prefs1.getString(TASKS, null);
//        Type type = new TypeToken<ArrayList<ListItem>>() {}.getType();
//        adapterArrayList = gson.fromJson(json, type);
//
//        // send the adapterArraylist to the adapter and set it to listview
//        editTextAdapter = new EditTextAdapter(this,adapterArrayList,R.layout.profile_layout);
//        listView.setAdapter(editTextAdapter);
//
//
//    }
//
//
//
////    /**
////     * The object we have a list of, probably more complex in your app
////     */
////    static class ListItem {
////        public String value;
////        public String label;
////        ListItem(){}
////
////        public String getValue() {
////            return value;
////        }
////
////        public void setValue(String value) {
////            this.value = value;
////        }
////
////        ListItem(String label,String value) {
////            this.value = value;
////            this.label=label;
////        }
////
////        public String  getLabel() {
////            return label;
////        }
////
////        public void setLabel(String label) {
////            this.label = label;
////        }
////    }
////
////    //    /**
//////     * ViewHolder which also tracks the TextWatcher for an EditText
//////     */
////    static class ViewHolder {
////        public TextView textView;
////        public EditText editText;
////        public TextWatcher textWatcher;
////    }
////
////    class EditTextAdapter extends BaseAdapter {
////        ViewHolder holder=new ViewHolder();
////
////        Context _c;
////
////        EditTextAdapter(Context context, ArrayList<ListItem> groupItem,int resource) {
////
////            this._c=context;
////            SharedPrefList.this.groupItem=groupItem;
////        }
////
////        @Override
////        public int getCount() {
////            return groupItem.size();
////        }
////
////        @Override
////        public ListItem getItem(int i) {
////            return groupItem.get(i);
////        }
////
////        @Override
////        public long getItemId(int i) {
////            return i;
////        }
////
////        @Override
////        public View getView(int position, View convertView, ViewGroup parent) {
////            View rowView = convertView;
////            if (rowView == null) {
////                // Not recycled, inflate a new view
////                rowView = getLayoutInflater().inflate(R.layout.profile_listitems, null);
////
////
////                rowView.setTag(holder);
////            }
////            holder.textView = (TextView) rowView.findViewById(R.id.labels);
////            holder.editText = (EditText) rowView.findViewById(R.id.values1);
////            ViewHolder holder = (ViewHolder) rowView.getTag();
////            // Remove any existing TextWatcher that will be keyed to the wrong ListItem
////            if (holder.textWatcher != null)
////                holder.editText.removeTextChangedListener(holder.textWatcher);
////
////            final ListItem listItem = groupItem.get(position);
////
////            // Keep a reference to the TextWatcher so that we can remove it later
////            holder.textWatcher = new TextWatcher() {
////                @Override
////                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////                }
////
////                @Override
////                public void onTextChanged(CharSequence s, int start, int before, int count) {
////                    listItem.value = s.toString();
////                }
////
////                @Override
////                public void afterTextChanged(Editable s) {
////                }
////            };
////            holder.editText.addTextChangedListener(holder.textWatcher);
////
////            holder.editText.setText(listItem.value);
////
////            holder.textView.setText(listItem.getLabel().toString());
////
////
////            if(clickCount==0)
////            {
////                actv(false);
////            }
////            return rowView;
////        }
////        protected void actv(final boolean active) {
////            holder.editText.setEnabled(active);
////            if (active) {
////                holder.editText.requestFocus();
////
////            }
////        }
////    }
////
////
//
//}