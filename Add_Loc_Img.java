package com.example.girlsafty;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Add_Loc_Img extends AppCompatActivity implements LocationListener {

    TextView tv_lat,tv_log,tv_loc;
    Button btn_pic,btn_upload;
    ImageView img_open_time,img_close_time;
    private ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/TEST_CAM_PIC";
    private int GALLERY = 1, CAMERA = 2;
    Bitmap bitmap;
    TextView txt_date;
    ProgressBar progressBar;
    Toolbar toolbar;
    ProgressDialog progressDoalog;
    Button btn_take_pic,upload;
    String URL= "https://codingseekho.in/APP/CAR_SERVICES/upload_loc_img.php";
    private static final int PERMISSION_REQUEST_CODE = 1;
    ByteArrayOutputStream bytes;
    LocationManager locationManager;
    EditText et_m_name;
    private  static final int CAMERA_REQUEST=123;
    ImageView photo;
    String str_lat,str_log,str_add,str_mname;

    List<Address> address;

    float lat=0,log = 0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Girl Safety Alert");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        progressBar=(ProgressBar)findViewById(R.id.pg);


        upload = (Button) findViewById(R.id.upload);
        btn_take_pic = (Button) findViewById(R.id.btn_take_pic);
        imageview = (ImageView) findViewById(R.id.iv_pic);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        }
        getLocation();

        btn_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
            }
        });

    }
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                check_permission();
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, (LocationListener) this);
        }
        catch (SecurityException e) {
            e.printStackTrace(); }
    }
    @Override
    public void onLocationChanged(Location location) {
        try {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            StringBuilder stringBuilder=new StringBuilder();
            //float lat=0,log = 0;

            // Toast.makeText(this, "Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            lat=(float) (location.getLatitude());
            log=(float) (location.getLongitude());

            address=geocoder.getFromLocation(lat,log,1);

            Address address1=address.get(0);
            // for (int i=0; i < address1.getMaxAddressLineIndex(); i++) {

            stringBuilder.append(address1.getAddressLine(0)).append("\n");
            stringBuilder.append(address1.getLocality()).append("\n");
            stringBuilder.append(address1.getPostalCode()).append("\n");
            stringBuilder.append(address1.getCountryName());
            //  result = stringBuilder.toString();
            //}
            tv_lat.setText(String.valueOf(lat));
            tv_log.setText(String.valueOf(log));
            tv_loc.setText(tv_loc.getText() + "\n"+address.get(0).getAddressLine(0));

        }catch(Exception e)
        { }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void check_permission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                takePhotoFromCamera();

            } else {
                requestPermission(); // Code for permission
            }
        } else {
            takePhotoFromCamera();
            Toast.makeText(Add_Loc_Img.this, "Below 23 API Oriented Device No Permission ....", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Add_Loc_Img.this, Manifest.permission.CAMERA);
        int result1 = ContextCompat.checkSelfPermission(Add_Loc_Img.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(Add_Loc_Img.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED&& result1 == PackageManager.PERMISSION_GRANTED&& result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Add_Loc_Img.this, Manifest.permission.CAMERA)&& ActivityCompat.shouldShowRequestPermissionRationale(Add_Loc_Img.this, Manifest.permission.READ_EXTERNAL_STORAGE)&& ActivityCompat.shouldShowRequestPermissionRationale(Add_Loc_Img.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getApplicationContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Add_Loc_Img.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(Add_Loc_Img.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Add_Loc_Img.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(Add_Loc_Img.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }
    public String saveImage(Bitmap myBitmap) {
        bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {,img_close_time;
    private ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/TEST_CAM_PIC";
    private int GALLERY = 1, CAMERA = 2;
    Bitmap bitmap;
    TextView txt_date;
    ProgressBar progressBar;
    Toolbar toolbar;
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

   /* public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }*/

    public String encode_img(Bitmap bm){

        byte[] imagebyte = bytes.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }
    private void UploadImage(){
        progressDoalog = new ProgressDialog(Add_Loc_Img.this);
        progressDoalog.setMessage("Uploading please wait....");
        progressDoalog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDoalog.dismiss();
                String s = response.trim();
                Toast.makeText(Add_Loc_Img.this, ""+s, Toast.LENGTH_SHORT).show();
                if(s.equalsIgnoreCase("Loi")){
                    Toast.makeText(Add_Loc_Img.this, "Loi", Toast.LENGTH_SHORT).show();
                }else{
                    refresh();
                    Toast.makeText(Add_Loc_Img.this, "Success", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Add_Loc_Img.this, error+"", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String image = encode_img(bitmap);
                //  String image = ""+bitmap;

                str_lat=String.valueOf(lat);
                str_log=String.valueOf(log);
                str_add=address.get(0).getAddressLine(0);
                Map<String ,String> params = new HashMap<String,String>();

                params.put("IMG",image);
                params.put("name","ALERT");
                params.put("str_lat",str_lat);
                params.put("str_log",str_log);
                params.put("str_add",str_add);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void refresh()
    {
        Intent refresh = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(refresh);
        finish();
    }

}
