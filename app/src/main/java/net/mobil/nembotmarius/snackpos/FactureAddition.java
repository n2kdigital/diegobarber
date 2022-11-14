package net.mobil.nembotmarius.snackpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.mobil.nembotmarius.snackpos.expandablelistview.ExpandableListview;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FactureAddition extends AppCompatActivity {

    String NoFact = "";
    ImageButton imageButton1;
    ImageButton imageButton2;
    TextView textView5;

    int opt=0;

    public interface CallBackListener<String> {
        void onPostTask(String result);
    }
    Outils o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facture_addition);

        final Intent intentsalesinvoice = new Intent(this, SalesInvoice.class);
        final Intent intentsynchrodata = new Intent(FactureAddition.this, SynchroData.class);

        final CallBackListener<String> callbacklistener = new CallBackListener<String>() {
            @Override
            public void onPostTask(String result) {
                String jsonid = result.replace("\"{","{").replace("}\"","}").replace("\\","");
                JSONObject jo_inside = null;
                try {
                    jo_inside = new JSONObject(jsonid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!jo_inside.isNull("msg")){
                    String msg = null;
                    try {
                        msg = jo_inside.getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(msg.equals("Ok")){
                        if(opt==1){
                            String Produit = "";
                            try {
                                Produit = jo_inside.getString("Produit");
                                o.wrtieFileOnInternalStorage(FactureAddition.this,"com.nembotmarius.productinvoice.json", Produit);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(intentsalesinvoice);
                        }else{
                            startActivity(intentsynchrodata);
                        }
                    }else{
                        Toast.makeText(FactureAddition.this,"Erreur Lors de la mise à jour",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        imageButton1 = (ImageButton)findViewById(R.id.imageButton1);
        imageButton2 = (ImageButton)findViewById(R.id.imageButton2);
        textView5 = (TextView)findViewById(R.id.textView5);

        o = new Outils();
        o.readConfig(this.getApplicationContext());

        Intent intent = getIntent();
        NoFact = intent.getStringExtra("nofact");
        intentsalesinvoice.putExtra("nofact", NoFact);

        textView5.setText("Facture No : "+NoFact);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Requestdatareceipt(callbacklistener).execute(NoFact);
                opt=0;
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(o.Droits.equals("2")){
                    new Requestdatareceipt2(callbacklistener).execute(NoFact);
                    opt=1;
                }else{
                    opt=0;
                    startActivity(intentsalesinvoice);
                }

            }
        });

        if(o.Droits.equals("2")){
            imageButton1.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SynchroData.class));
    }

    private class Requestdatareceipt extends AsyncTask<String, Void, String> {
        private CallBackListener<String> callbacklistener;

        protected Requestdatareceipt(CallBackListener<String> callbacklistener){
            this.callbacklistener = callbacklistener;
        }

        protected String doInBackground(String... params) {
            int count = params.length;
            String NoFact = "";
            if(count>0){
                NoFact = params[0];
            }

            String address = "&NoFact=" + NoFact;
            String url = o.getUrl("json/docventesfac.php", address);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if(response.code()==200){
                    String sbody = response.body().string();
                    return sbody;
                }else{
                    return "Loading Fail.";
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            callbacklistener.onPostTask(result);
        }
    }

    private class Requestdatareceipt2 extends AsyncTask<String, Void, String> {
        private CallBackListener<String> callbacklistener;

        protected Requestdatareceipt2(CallBackListener<String> callbacklistener){
            this.callbacklistener = callbacklistener;
        }

        protected String doInBackground(String... params) {
            int count = params.length;
            String NoFact = "";
            if(count>0){
                NoFact = params[0];
            }

            String address = "&NoFact=" + NoFact;
            String url = o.getUrl("json/produitvendu.php", address);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if(response.code()==200){
                    String sbody = response.body().string();
                    return sbody;
                }else{
                    return "Loading Fail.";
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            callbacklistener.onPostTask(result);
        }
    }
}