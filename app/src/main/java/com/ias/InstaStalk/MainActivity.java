package com.ias.InstaStalk;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import android.app.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import me.ibrahimsn.particle.ParticleView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageView image;
    private ImageView badge;
    private int requestCode;
    private Boolean b=false;
    private int grantResults[];
    private String d;
    private String name;
    private ProgressDialog dialog;
    Intent galleryIntent;
    private String catsapi;
    private RequestQueue requestQueue;
    private ParticleView particleView;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        galleryIntent = new Intent(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        particleView = findViewById(R.id.particleView);
        particleView.resume();
        image = findViewById(R.id.imageView);
        catsapi="https://www.instagram.com/imaryandokania/?__a=1";
        dialog = new ProgressDialog(MainActivity.this);
        Button buttonParse = findViewById(R.id.btnParse);
        TextView t=(TextView)findViewById(R.id.textView2);
        TextView t3=(TextView)findViewById(R.id.textView4);
        badge=(ImageView)findViewById(R.id.imageView2);
       // badge.setVisibility(View.INVISIBLE);
        TextView t2=(TextView)findViewById(R.id.textView3);
        EditText eText = (EditText) findViewById(R.id.editTextTextPersonName);
       // requestQueue = Volley.newRequestQueue(this);
        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Your answer is correct!");
                image.setImageDrawable(null);
                badge.setImageDrawable(null);
                closeKeyBoard();
                dialog.setMessage("Connecting to Instagram Server");
                dialog.show();
                d=eText.getText().toString();
                eText.setText("");
                String url = "https://www.instagram.com/"+d+"/?__a=1";
                StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject graphql = jsonObject.getJSONObject("graphql");
                            JSONObject user = graphql.getJSONObject("user");
                            String verify=user.getString("is_verified");
                            JSONObject f1=user.getJSONObject("edge_followed_by");
                            JSONObject f2=user.getJSONObject("edge_follow");
                            String followers=f1.getString("count");
                            String following=f2.getString("count");
                            String bio=user.getString("biography");
                            name=user.getString("full_name");
                            String pic = user.getString("profile_pic_url_hd");

                            Picasso.get()
                                    .load(pic)
                                    .transform(new RoundedCornersTransformation(10, 10))
                                    .into(image, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            if (dialog.isShowing()) {

                                                dialog.dismiss();
                                                t.setText("Name:"+name);
                                                t2.setText("Bio:"+bio.trim());
                                                t3.setText("Followers: "+followers+"            "+"Following: "+following);
                                                Log.i("veified",verify);
                                                if(verify.equals("true"))
                                                {
                                                    badge.setImageResource(R.drawable.bag);

                                                }

                                            }

                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }

                                    });
                            Log.i("see",name);
                        }
                        catch (JSONException e) {
                           e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.setMessage("ROLLBACK");
                       Toast.makeText(MainActivity.this,"Enter valid ID", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        });

image.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      dialog.setMessage("Saving to Gallery");
      dialog.show();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            onRequestPermissionsResult(requestCode, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, grantResults);

        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bm=view.getDrawingCache();
        String imgSaved=MediaStore.Images.Media.insertImage(getContentResolver(), bm, name , d);
        view.setDrawingCacheEnabled(false);
        bm.recycle();
        if(imgSaved!=null)
        {
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            }, 2000);
        }
        else
        {
            dialog.setMessage("Error");
            dialog.cancel();

        }

    }
});
    }
    public void onRequestPermissionsResult ( int requestCode,
                                             String permissions[], int[] grantResults){
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    b=true;

                    Log.d("permission", "granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.uujm
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();

                    //app cannot function without this permission for now so close it...
                    onDestroy();
                }
                return;
            }

//
        }
    }
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}