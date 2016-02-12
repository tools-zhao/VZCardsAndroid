package com.bitjini.vzcards;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VEENA on 12/7/2015.
 */
public class MyProfile_Fragment extends Fragment implements View.OnClickListener {

    public static final String mypreference = "mypref.txt";
    public static final String TASKS = "key";
    public static final String IMAGE = "image";
    private static final String SAVED_STATE_KEY = "saved_state_key";

    Context _c = getActivity();
    private final int SELECT_PHOTO = 1;
    private ImageView imageProfile, imageCompany;
    private Uri outputFileUri;
    ImageView currentImageView = null;
    View profile;
    SharedPreferences data ;

    ArrayList<String> label;
    ArrayList<String> values;
    int clickCount = 0;
    Button editbtn, profilebtn, vzfrndsbtn, referralbtn;
    ListView listView;
    EditTextAdapter editTextAdapter;
    ArrayList<ListItem> arrayList = new ArrayList<ListItem>();
    ArrayList<ListItem> adapterArrayList = new ArrayList<ListItem>();

    public ArrayList<ListItem> groupItem = new ArrayList<ListItem>();
    String json,json2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        profile = inflater.inflate(R.layout.profile_layout, container, false);

        editbtn = (Button) profile.findViewById(R.id.edit);
        listView = (ListView) profile.findViewById(R.id.profileList);
      data = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if(data.contains(TASKS)) {
            LoadPreferences();
        }
        //Picking Profile picture
        imageProfile = (ImageView) profile.findViewById(R.id.profilePic);
        imageCompany = (ImageView) profile.findViewById(R.id.btn_pick);

        //image listeners
        imageCompany.setOnClickListener(this);
        imageProfile.setOnClickListener(this);


        profilebtn = (Button) profile.findViewById(R.id.profilebtn);
        referralbtn = (Button) profile.findViewById(R.id.referralbtn);
        vzfrndsbtn = (Button) profile.findViewById(R.id.vzfrnds);

        label = new ArrayList<String>();
        label.add("Fname");
        label.add("Lname");
        label.add("Industry");
        label.add("Company");
        label.add("Address");

        values = new ArrayList<String>();
        values.add("veena");
        values.add("M");
        values.add("IT");
        values.add("BITJIN");
        values.add("GIT");

        for (int i = 0; i < label.size(); i++) {
            ListItem item = new ListItem();
            item.setLabel(label.get(i));
            item.setValue(values.get(i));
            arrayList.add(item);
        }

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clickCount == 0) {

                    editbtn.setText("Save");

                    editTextAdapter.actv(true);
                    editTextAdapter.notifyDataSetChanged();
//                    listView.setAdapter(editTextAdapter);
//                    LoadPreferences();
//                    Log.e("selected position of textview", "" + position);
                    Toast.makeText(getActivity(), "click 0", Toast.LENGTH_LONG).show();
                    clickCount = 1;

                } else if (clickCount == 1) {

                    editbtn.setText("Edit");

                    editTextAdapter.actv(false);
                  json2= new Gson().toJson(groupItem);// updated array

                    SavePreferences(TASKS, json2);
                    LoadPreferences();
                    editTextAdapter.notifyDataSetChanged();
//                    listView.setAdapter(editTextAdapter);
                    Toast.makeText(getActivity(), "click 1", Toast.LENGTH_LONG).show();
                    clickCount = 0;

                }

            }
        });

        if(!groupItem.isEmpty())
        json2= new Gson().toJson(groupItem);// updated array
        Log.e("updated array", "" + json2);
        json = new Gson().toJson(arrayList); //default array



        //converting arrayList to json to Save the values in sharedpreference by calling SavePrefernces
        // Check if the updated array is equal to default array if false load default array else load updated array
        if (json.equals(json2) || json2==null) {

            SavePreferences(TASKS, json);
        } else {


            SavePreferences(TASKS, json2);
        }
        LoadPreferences();


        vzfrndsbtn.setOnClickListener(this);
        referralbtn.setOnClickListener(this);
        return profile;
    }

    protected void SavePreferences(String key, String value) {
// TODO Auto-generated method stub
       data = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
        editor.commit();
        System.out.println(value);

    }


    // To retrive saved values in shared preference Now convert the JSON string back to your java object

    protected void LoadPreferences() {

        SharedPreferences prefs1 = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs1.getString(TASKS, null);
        Type type = new TypeToken<ArrayList<ListItem>>() {
        }.getType();
        adapterArrayList = gson.fromJson(json, type);

        // send the adapterArraylist to the adapter and set it to listview
        editTextAdapter = new EditTextAdapter(getActivity(), adapterArrayList, R.layout.profile_layout);
        listView.setAdapter(editTextAdapter);


    }


    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "amfb" + File.separator);
        root.mkdir();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        //FileSystem
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, SELECT_PHOTO);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PHOTO) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                    //Bitmap factory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // downsizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);

//            v.imageView.setImageDrawable(roundedImage);
                    currentImageView.setImageBitmap(bitmap);
//                    imageProfile.setImageBitmap(bitmap);
//                    imageCompany.setImageBitmap(bitmap);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    Log.d("ImageURI", selectedImageUri.getLastPathSegment());
                    // /Bitmap factory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // downsizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    try {//Using Input Stream to get uri
                        InputStream input = getActivity().getContentResolver().openInputStream(selectedImageUri);
                        final Bitmap bitmap = BitmapFactory.decodeStream(input);
//                        imageProfile.setImageBitmap(bitmap);

                        currentImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            // user cancelled Image capture
            Toast.makeText(getActivity(),
                    "User cancelled image capture", Toast.LENGTH_SHORT)
                    .show();
        } else {
            // failed to capture image
            Toast.makeText(getActivity(),
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {

            //setting profile picture
            case R.id.profilePic:
                currentImageView = (ImageView) v;
                openImageIntent();
                break;

            //setting company picture
            case R.id.btn_pick:
                currentImageView = (ImageView) v;
                openImageIntent();
                break;

            //redirecting to VZFriends_Fragment
            case R.id.vzfrnds:
                Fragment newfragment = new VZFriends_Fragment();
                // get the id of fragment
                FrameLayout contentView2 = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager1 = getFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(contentView2.getId(), newfragment).addToBackStack(contentView2.toString())
                        .commit();
                break;

            //redirecting to Referral_Fragmen
            case R.id.referralbtn:
                Fragment fragment = new Referral_Fragment();
                // get the id of fragment
                FrameLayout contentView3 = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(contentView3.getId(), fragment).addToBackStack(contentView3.toString())
                        .commit();


                break;
            default:
                break;

        }
    }

    /**
     * The object we have a list of, probably more complex in your app
     */
    static class ListItem {
        public String value;
        public String label;

        ListItem() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        ListItem(String label, String value) {
            this.value = value;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    //    /**
//     * ViewHolder which also tracks the TextWatcher for an EditText
//     */
    static class ViewHolder {
        public TextView textView;
        public EditText editText;
        public TextWatcher textWatcher;
    }

    class EditTextAdapter extends BaseAdapter {
        ViewHolder holder = new ViewHolder();
        Context _c;

        EditTextAdapter(Context context, ArrayList<ListItem> groupItem, int resource) {

            this._c = context;
            MyProfile_Fragment.this.groupItem = groupItem;
        }

        @Override
        public int getCount() {
            return groupItem.size();
        }

        @Override
        public ListItem getItem(int i) {
            return groupItem.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = null;
            convertView=null;
            if (convertView == null) {
                // Not recycled, inflate a new view
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = li.inflate(R.layout.profile_listitems, null);
//                rowView = getLayoutInflater().inflate(R.layout.profile_listitems, null);


                rowView.setTag(holder);
            }
            holder.textView = (TextView) rowView.findViewById(R.id.labels);
            holder.editText = (EditText) rowView.findViewById(R.id.values1);
            ViewHolder holder = (ViewHolder) rowView.getTag();
            // Remove any existing TextWatcher that will be keyed to the wrong ListItem
            if (holder.textWatcher != null)
                holder.editText.removeTextChangedListener(holder.textWatcher);

            final ListItem listItem = groupItem.get(position);

            // Keep a reference to the TextWatcher so that we can remove it later
            holder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listItem.value= s.toString();
                    System.out.println(listItem.value +""+groupItem.get(position));

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            holder.editText.addTextChangedListener(holder.textWatcher);

            holder.editText.setText(listItem.value);

            holder.textView.setText(listItem.getLabel().toString());
//              holder.editText.setEnabled(false);

            if (clickCount == 0) {
                actv(false);
            }
            return rowView;
        }

    protected void actv(final boolean active) {
        holder.editText.setEnabled(active);
            if (active) {
                holder.editText.requestFocus();

            }
        }
    }
}


