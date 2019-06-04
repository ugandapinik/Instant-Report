package com.wohhie.www.myworksafe;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wohhie.www.myworksafe.adapter.UploadListAdapter;
import com.wohhie.www.myworksafe.model.Incident;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ReportIncidentActivity extends AppCompatActivity implements LocationListener {

    private EditText incidentNotes;
    private EditText incidentLatitude;
    private EditText incidentLongitude;
    private Spinner incidentType;
    private Spinner incidentLevel;
    private EditText incidentDate;
    private EditText incidentTime;
    private EditText input_address;


    // upload files and display in the recycler view
    private ImageButton selectFiles;
    private RecyclerView uploadedList;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    private UploadListAdapter uploadListAdapter;


    private Button incidentBtn;
    DatePickerDialog.OnDateSetListener dateSetListener;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private Calendar calendar;


    private LocationManager locationManager;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;



    // attributes for uploading images and files
    private Button btnChoose, btnUpload;
    private ImageView displayImage;
    private Uri filePath;
    private Uri photoUri;
    private Uri fileUri;
    private Uri videoUri;
    private final int PICK_IMAGE_REQUEST = 71;
    public static final int RESULT_LOAD_IMAGE = 1;





    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    TextView tvLatitude, tvLongitude, tvTime;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_incident);

        // attach this activity with butterknife
        //ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Report Incident");
        toolbar.setTitleTextColor(Color.WHITE);


        incidentNotes = findViewById(R.id.input_notes);
        incidentType = findViewById(R.id.input_type);
        incidentLevel = findViewById(R.id.input_level);
        incidentDate = findViewById(R.id.input_date);
        incidentTime = findViewById(R.id.input_time);
        incidentLatitude = findViewById(R.id.input_latitude);
        incidentLongitude = findViewById(R.id.input_longitude);
        incidentBtn = findViewById(R.id.btn_incident);
        input_address = findViewById(R.id.input_address);


        // Uploading file and import information
        selectFiles = findViewById(R.id.btn_uploadFiles);
        uploadedList = findViewById(R.id.recyclerView);
        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);
        uploadedList.setLayoutManager(new LinearLayoutManager(this));
        uploadedList.setHasFixedSize(true);
        uploadedList.setAdapter(uploadListAdapter);




        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            showSettingsAlert();
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }

            // get location
            getLocation();
        }











        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("incident");
        storage = FirebaseStorage.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();


        // intialize component
        intializeComponent();


        // getting current date time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        incidentDate.setText(currentDate.toString());
        incidentTime.setText(mdformat.format(calendar.getTime()));


        // intilize component
        incidentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIncldent();
            }
        });



        selectFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent();
                 intent.setType("*/*");
                 intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, true);
                 intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(Intent.createChooser(intent, "SELECT PICTURES"), RESULT_LOAD_IMAGE);


            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            if (data.getClipData() != null){

                // getting the total number of data
                int totalNumberSelected = data.getClipData().getItemCount();
                Log.e("Tota", totalNumberSelected + "");
                for (int i = 0; i < totalNumberSelected; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    String fileName = getFileName(fileUri);
                    Log.e("Files", fileName);
                    fileNameList.add(fileName);
                    uploadListAdapter.notifyDataSetChanged();

                    final StorageReference fileUpload = storageReference.child("images/").child(fileName);
                    fileUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //showMessage("Uploaded");
                            Log.e(TAG, "Downloaded URL: " + taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                        }
                    });
                }


                //showMessage("SELECT MULTIPLE FILES");

            }else{

            }
        }
    }



    public String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try{
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }
            }finally {
                cursor.close();
            }
        }


        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf("/");
            if (cut != -1){
                result = result.substring(cut + 1);

            }
        }

        return result;
    }




    private void uploadImage() {
        Log.d("FilePath", filePath.toString());

        if(filePath != null) {

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Toast.makeText(ReportIncidentActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //Toast.makeText(ReportIncidentActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());

                        }
                    });
        }
    }


    private void intializeComponent() {
        incidentLatitude.setText("48.941460");
        incidentLongitude.setText("-57.936520");
    }


    /**
     * Getting the address of the location
     *
     * @param lat
     * @param lng
     */
    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(ReportIncidentActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String   add = obj.getAddressLine(0);
            String  currentAddress = obj.getSubAdminArea() + ","
                    + obj.getAdminArea();

            Log.e("Current Address: ", obj.getAddressLine(0));
            double   latitude = obj.getLatitude();
            double longitude = obj.getLongitude();
            String currentCity= obj.getSubAdminArea();
            String currentState= obj.getAdminArea();
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();


            input_address.setText(obj.getAddressLine(0));


            System.out.println("obj.getCountryName()"+obj.getCountryName());
            System.out.println("obj.getCountryCode()"+obj.getCountryCode());
            System.out.println("obj.getAdminArea()"+obj.getAdminArea());
            System.out.println("obj.getPostalCode()"+obj.getPostalCode());
            System.out.println("obj.getSubAdminArea()"+obj.getSubAdminArea());
            System.out.println("obj.getLocality()"+obj.getLocality());
            System.out.println("obj.getSubThoroughfare()"+obj.getSubThoroughfare());


            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




































    private void addIncldent() {
        String notes = incidentNotes.getText().toString().trim();
        String level = incidentLevel.getSelectedItem().toString();
        String type = incidentType.getSelectedItem().toString();
        String latitude = incidentLatitude.getText().toString().trim();
        String longitude = incidentLongitude.getText().toString().trim();

        String dateTime = incidentDate.getText().toString() + " " + incidentTime.getText().toString();


        if (!TextUtils.isEmpty(notes)){
            // creating unique key
            String id = databaseReference.push().getKey();

            Incident incident = new Incident();
            incident.setNote(notes);
            incident.setDateTime(dateTime);
            incident.setLevel(level);
            incident.setIncidentType(type);
            incident.setLatitude(latitude);
            incident.setLongitude(longitude);
            incident.setUserId(1002);
            incident.setStatus("new");
            incident.setFileModelList(fileNameList);


            databaseReference.child(id).setValue(incident);
            final ProgressDialog dialog = ProgressDialog.show(ReportIncidentActivity.this, "",
                    "Loading. Please wait...", true);
            dialog.show();
            new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {
                    // You don't need anything here
                }

                public void onFinish() {
                    dialog.dismiss();
                    showMessage("Incident Created");
                }
            }.start();


        }else{
            showMessage("message");
        }

    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }






    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsRejected.toArray(
                                                new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                    }
                                }
                            });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }



    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ReportIncidentActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        incidentLatitude.setText(Double.toString(loc.getLatitude()));
        incidentLongitude.setText(Double.toString(loc.getLongitude()));
        getAddress(loc.getLatitude(), loc.getLongitude());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}
