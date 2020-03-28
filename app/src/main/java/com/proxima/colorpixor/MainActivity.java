package com.proxima.colorpixor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    ProgressDialog progressDialog ;

    //ImageView to display image selected
    ImageView imageView;
    Tooltip tooltipNumber,tooltipChooseImage,tooltipUrl,tooltipGetUrl,tooltipUpload,tooltip;
    SharedPreferences sharedPreferences;
    LinearLayout linearLayout;
    int count=0;


    LinearLayout imageColor,layout;

    Bitmap scaled;

    CircleImageView[] imageColorView;
    TextView[] dynamicTxt;

    LinearLayout.LayoutParams params;
    LinearLayout.LayoutParams params1;

    EditText edtNumber,edtUrl;

    String url1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Uploading Image");
        linearLayout = findViewById(R.id.linearLayout);

        //initializing views
        imageView = (ImageView) findViewById(R.id.imageView);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        imageColor = findViewById(R.id.imageLayout);

        edtNumber = findViewById(R.id.edtNumber);
        edtUrl = findViewById(R.id.edtUrl);

        params = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = 300;
        params.height = 300;
        params.bottomMargin = 20;
        params.gravity = Gravity.CENTER;
        params1 = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.width = 300;
        params1.bottomMargin = 80;
        params1.gravity = Gravity.CENTER;
        sharedPreferences = getSharedPreferences("user",0);
        final boolean isNewUSer=sharedPreferences.getBoolean("newUser",true);
        if (isNewUSer)
        {
            tooltip = new Tooltip.Builder(linearLayout)
                    .setGravity(Gravity.CENTER)
                    .setBackgroundColor(getResources().getColor(R.color.white))
                    .setText("This is the first time tutorial for the app tap on upper half of the screen to proceed with the tutorial")
                    .setCornerRadius(50.0f)
                    .show();


        }


        //checking the permission
        //if the permission is not given we will open setting to add permission
        //else app will not open
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Grant Permission")
                    .setMessage("Please Allow Storage Permission")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0101);
                        }
                    })
                    .create()
                    .show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0101);
        }

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isNewUSer)
                {
                    if (count == 0)
                    {
                        tooltip.dismiss();
                        tooltipNumber = new Tooltip.Builder(edtNumber)
                                .setGravity(Gravity.BOTTOM)
                                .setBackgroundColor(getResources().getColor(R.color.white))
                                .setText("Enter number of colors you want to extract from the image here (This field can't be empty)")
                                .setCornerRadius(50.0f)
                                .show();
                        count++;
                    }
                    else if (count==1)
                    {
                        tooltipNumber.dismiss();
                        tooltipChooseImage = new Tooltip.Builder(findViewById(R.id.buttonChooseImage))
                                .setGravity(Gravity.BOTTOM)
                                .setBackgroundColor(getResources().getColor(R.color.white))
                                .setText("Click on this button to get image from your phone")
                                .setCornerRadius(50.0f)
                                .show();
                        count++;
                    }
                    else if (count == 2)
                    {
                        tooltipChooseImage.dismiss();
                        tooltipUrl = new Tooltip.Builder(edtUrl)
                                .setGravity(Gravity.BOTTOM)
                                .setBackgroundColor(getResources().getColor(R.color.white))
                                .setText("OR\n If you want to upload an image from internet enter its URL here")
                                .setCornerRadius(50.0f)
                                .show();
                        count++;
                    }
                    else if (count == 3)
                    {
                        tooltipUrl.dismiss();
                        tooltipGetUrl = new Tooltip.Builder(findViewById(R.id.btnUrlImage))
                                .setGravity(Gravity.BOTTOM)
                                .setBackgroundColor(getResources().getColor(R.color.white))
                                .setText("Click on this button to get image from the URL you entered")
                                .setCornerRadius(50.0f)
                                .show();
                        count++;
                    }
                    else if (count == 4)
                    {
                        tooltipGetUrl.dismiss();
                        tooltipUpload = new Tooltip.Builder(findViewById(R.id.buttonUploadImage))
                                .setGravity(Gravity.BOTTOM)
                                .setBackgroundColor(getResources().getColor(R.color.white))
                                .setText("Click on this button to extract the colors from the image")
                                .setCornerRadius(50.0f)
                                .show();
                        count++;
                    }
                    else if (count == 5) {
                        tooltipUpload.dismiss();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("newUser",false);
                        editor.commit();
                        count++;
                    }
                    else {


                    }
                }
                return false;
            }
        });


        findViewById(R.id.btnUrlImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtUrl.getText().toString().length()>0) {
                    imageColor.setVisibility(View.INVISIBLE);
                    layout.removeAllViews();
                    url1 = edtUrl.getText().toString();
                    new Background(url1, imageView).execute();
                }
                else {
                    Toast.makeText(MainActivity.this, "URL can't be empty", Toast.LENGTH_SHORT).show();
                }


            }
        });

        findViewById(R.id.buttonUploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.removeAllViews();
                if (scaled != null && edtNumber.getText().toString().length()>0)
                {
                    uploadBitmap(scaled);
                }
                else {
                    Toast.makeText(MainActivity.this, "No image selected or number of colors is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //adding click listener to button
        findViewById(R.id.buttonChooseImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageColor.setVisibility(View.INVISIBLE);

                //if the tags edittext is empty
                //we will throw input error
                //if everything is ok we will open image chooser
                if (edtNumber.getText().toString().length()>0) {

                    layout.removeAllViews();
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 100);
                }
                else {
                    Toast.makeText(MainActivity.this, "Number of Colors Cant be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageView.setVisibility(View.VISIBLE);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
                Bitmap compress = BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                scaled = Bitmap.createScaledBitmap(compress,800,800,true);
                //displaying selected image to imageview
                imageView.setImageBitmap(scaled);

                //calling the method uploadBitmap to upload image
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * The method is taking Bitmap as an argument
    * then it will return the byte[] array for the given bitmap
    * and we will send this array to the server
    * here we are using PNG Compression with 80% quality
    * you can give quality between 0 to 100
    * 0 means worse quality
    * 100 means best quality
    * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {

        //getting the tag from the edittext

        //our custom volley request
        progressDialog.setTitle("Extracting Colors");
        progressDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,"https://damp-wave-49064.herokuapp.com/home/"+edtNumber.getText().toString(),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        progressDialog.dismiss();
                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            Log.e("Json",obj.toString());
                            imageColorView = new CircleImageView[obj.length()];
                            dynamicTxt = new TextView[obj.length()];
                            imageColor.setVisibility(View.VISIBLE);
                            for (int i = 0 ; i < obj.length() ; i++)
                            {
                                imageColorView[i] = new CircleImageView(MainActivity.this);
                                dynamicTxt[i] = new TextView(MainActivity.this);
                                JSONObject jsonObject = obj.getJSONObject(i);
                                String colorCode = jsonObject.getString("id");
                                String colorName = jsonObject.getString("name");
                                String percentage = jsonObject.getString("percentage");
                                imageColorView[i].setBackgroundColor(Color.parseColor(colorCode));
                                imageColorView[i].setLayoutParams(params);
//                                imageColorView[i].setBackground(getDrawable(R.drawable.button_bg_round));
                                dynamicTxt[i].setText(colorName+"\n("+colorCode+")\n"+"("+percentage+"%)");
                                dynamicTxt[i].setLayoutParams(params1);
                                dynamicTxt[i].setGravity(Gravity.CENTER_HORIZONTAL);
                                dynamicTxt[i].setTextColor(getResources().getColor(R.color.text));
                                imageColor.removeAllViews();
                                layout.addView(imageColorView[i]);
                                layout.addView(dynamicTxt[i]);
                                imageColor.addView(layout);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error Uploading Image", Toast.LENGTH_SHORT).show();
                        Log.e("Error",error.toString());
                        progressDialog.dismiss();
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("files", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
        volleyMultipartRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    class Background extends AsyncTask<Void,Void, Bitmap>{
        private String url2;
        private ImageView imageView;
        public Background(String url, ImageView imageView) {
            this.url2 = url;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Getting Image");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null)
            {
                Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
            }
            else {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            }
            progressDialog.dismiss();

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (url2.length()>0)
            {
                try {
                    URL url = new URL(url2);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap image = BitmapFactory.decodeStream(input);
                    if (image!=null) {
                        scaled = Bitmap.createScaledBitmap(image, 800, 800, true);
                    }
                    //displaying selected image to imageview
                    return scaled;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else {
                Toast.makeText(MainActivity.this, "Please Enter a valid URL", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }
}
