package com.example.girlsafty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements LocationListener  {

    TextView tv_lat,tv_log,tv_loc;
    Button btn_pic,btn_upload;
    EditText et_m_name;
    private  static final int CAMERA_REQUEST=123;
    ImageView photo;
    String str_lat,str_log,str_add,str_mname;
    List<Address> address;
    ProgressDialog progressDoalog;
    float lat=0,log = 0;
    String URL= "https://codingseekho.in/APP/CAR_SERVICES/upload_loc_img.php";
    String json_url="http://192.168.29.252/gril_safety_app/add_location.php?";
    LocationManager locationManager;
    Bitmap bitmap;
    ByteArrayOutputStream bytes;
    private static final String IMAGE_DIRECTORY = "/TEST_CAM_PIC";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        tv_lat=(TextView)findViewById(R.id.latitude_textview);
        tv_log=(TextView)findViewById(R.id.longitude_textview);
        tv_loc=(TextView)findViewById(R.id.location_tv);
       // et_m_name=(EditText)findViewById(R.id.m_name);
       // btn_pic=(Button)findViewById(R.id.btn_photo);
        btn_upload=(Button)findViewById(R.id.upload);
        photo=(ImageView)findViewById(R.id.iv_pic);

        //  Toast.makeText(getApplicationContext(),"Current Location",Toast.LENGTH_LONG).show();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        }
        getLocation();

    }
    public void btnClick(View v){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        startActivityForResult(intent,CAMERA_REQUEST);

    }
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            photo.setImageBitmap(bitmap);
            saveImage(bitmap);
        }
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

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });

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

        try {
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
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Uploading please wait....");
        progressDoalog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDoalog.dismiss();

                Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
//                if(s.equalsIgnoreCase("Loi")){
//                    Toast.makeText(MainActivity.this, "Loi", Toast.LENGTH_SHORT).show();
//                }else{
//                    refresh();
//                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error+"", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s)
    {
        Toast.makeText(this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();

    }
}

