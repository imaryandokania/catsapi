package com.ias.stc_vit;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import android.app.ProgressDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import me.ibrahimsn.particle.ParticleView;

public class MainActivity extends AppCompatActivity {
    private ImageView image;
    private ProgressDialog dialog;
    private String   catsapi;
    private RequestQueue requestQueue;
    private ParticleView particleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        particleView = findViewById(R.id.particleView);
        particleView.resume();
        image = findViewById(R.id.imageView);
        catsapi="https://api.thecatapi.com/v1/images/search";
        dialog = new ProgressDialog(MainActivity.this);
        Button buttonParse = findViewById(R.id.btnParse);
        requestQueue = Volley.newRequestQueue(this);
        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Loading Image...Please wait");
                dialog.show();
                 requestQueue = Volley.newRequestQueue(MainActivity.this);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                        Request.Method.GET,
                        catsapi,
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                                try{

                                    for(int i=0;i<response.length();i++){
                                        JSONObject student = response.getJSONObject(i);

                                        String url = student.getString("url");
                                        String breed = student.getString("breeds");
                                        Picasso.get()
                                                .load(url)
                                                .into(image, new com.squareup.picasso.Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        if (dialog.isShowing()) {
                                                            dialog.dismiss();
                                                        }

                                                    }

                                                    @Override
                                                    public void onError(Exception e) {

                                                    }

                                                });
                                     Log.i("see",url);
                                     Log.i("breed",breed);

                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){

                            Log.i("error","problem" );

                            }
                        }
                );
                requestQueue.add(jsonArrayRequest);
            }

        });


    }






}