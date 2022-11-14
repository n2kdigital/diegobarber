package net.mobil.nembotmarius.snackpos;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.mobil.nembotmarius.snackpos.expandablelistview.ExpandableListview;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SynchroData extends AppCompatActivity {

    Outils o;
    String option = "";
    TextView txtinfodata;

    Button btnsynchrocontinue;
    Button btnsynchrorefreshclient;
    Button btnsynchrorefreshproduct;
    Button btnlistcommande;

    public interface CallBackListener<String> {
        void onPostTask(String result) throws JSONException;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchro_data);

        txtinfodata = (TextView) findViewById(R.id.txtinfodata);
        btnsynchrocontinue = (Button) findViewById(R.id.btnsynchrocontinue);
        btnsynchrorefreshclient = (Button) findViewById(R.id.btnsynchrorefreshclient);
        btnsynchrorefreshproduct = (Button) findViewById(R.id.btnsynchrorefreshproduct);
        btnlistcommande = (Button) findViewById(R.id.btnlistcommande);

        final Intent intentsalesinvoice = new Intent(this, SalesInvoice.class);
        final Intent intentlisteventes = new Intent(this, ListVentesServ.class);
        final Intent intentexpandablelistview = new Intent(this, net.mobil.nembotmarius.snackpos.expandablelistview.ExpandableListview.class);

        final CallBackListener<String> callbacklistener = new CallBackListener<String>() {
            @Override
            public void onPostTask(String result) {
                disablefunc(true);
                if(option.equals("100")){
                    try {
                        String jsonid = result;
                        JSONObject jo_inside = new JSONObject(jsonid);
                        String strCode = jo_inside.getString("code");
                        String strBody = jo_inside.getString("body").trim();
                        if(strCode.equals("200")){
                            txtinfodata.setText("Sales Synchronization Complete");
                        }else{
                            Toast.makeText(SynchroData.this,"Error went validating operation",Toast.LENGTH_SHORT).show();
                            txtinfodata.setText("Sales Synchronization Error");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(option.equals("200")){
                    txtinfodata.setText(result);
                }else if(option.equals("300")){
                    txtinfodata.setText(result);
                    txtinfodata.setText(result);
                }else if(option.equals("600")){
                    txtinfodata.setText(result);
                }

            }
        };

        o = new Outils();
        o.readConfig(this.getApplicationContext());

        btnsynchrorefreshproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = "600";
                txtinfodata.setText("Loading Products...");
                disablefunc(false);
                new Requestdata(callbacklistener).execute(o.Depot);
            }
        });



        btnsynchrorefreshclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentlisteventes);
            }
        });

        btnsynchrocontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentsalesinvoice);
            }
        });

        btnlistcommande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentexpandablelistview);
            }
        });

        if(o.Droits.equals("2")){
            btnsynchrorefreshproduct.setVisibility(View.INVISIBLE);
            btnsynchrocontinue.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void disablefunc(boolean state){
        btnsynchrocontinue.setEnabled(state);
        btnsynchrorefreshclient.setEnabled(state);
        btnsynchrorefreshproduct.setEnabled(state);
    }


    private class Requestdata extends AsyncTask<String, Void, String> {
        private CallBackListener<String> callbacklistener;

        protected Requestdata(CallBackListener<String> callbacklistener){
            this.callbacklistener = callbacklistener;
        }

        protected String doInBackground(String... params) {
            int count = params.length;
            String nDepot = "";
            if(count>0){
                nDepot = params[0];
            }

            String url = o.getUrl("json/produit.php");
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if(response.code()==200){
                    String sbody = response.body().string().trim();
                    JSONObject jo_inside = null;

                    String Produit = "";
                    try {
                        jo_inside = new JSONObject(sbody);
                        Produit = jo_inside.getString("Produit");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    o.wrtieFileOnInternalStorage(SynchroData.this,"com.nembotmarius.product.json", Produit);

                    Log.i("Reponse", sbody);
                    return "Loading Complete.";
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
            try {
                callbacklistener.onPostTask(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Requestdataclient extends AsyncTask<String, Void, String> {
        private CallBackListener<String> callbacklistener;

        protected Requestdataclient(CallBackListener<String> callbacklistener){
            this.callbacklistener = callbacklistener;
        }

        protected String doInBackground(String... params) {
            int count = params.length;
            String codedep = "";
            if(count>0){
                codedep = params[0];
            }

            String url = o.getUrl("json/clients.php");
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if(response.code()==200){
                    String sbody = response.body().string();
                    o.wrtieFileOnInternalStorage(SynchroData.this.getApplicationContext(),"com.nembotmarius.customer.json", sbody);
                    Log.i("Reponse", sbody);
                    return "Loading Complete.";
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
            try {
                callbacklistener.onPostTask(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Requestdatareceipt extends AsyncTask<String, Void, String> {
        private CallBackListener<String> callbacklistener;

        protected Requestdatareceipt(CallBackListener<String> callbacklistener){
            this.callbacklistener = callbacklistener;
        }

        protected String doInBackground(String... params) {
            int count = params.length;
            String codedep = "";
            String serveuse = "";
            if(count>0){
                codedep = params[0];
                serveuse = params[1];
            }

            String url = o.getUrl("json/docventes.php");
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if(response.code()==200){
                    String sbody = response.body().string();
                    o.wrtieFileOnInternalStorage(SynchroData.this.getApplicationContext(),"com.nembotmarius.docventes.json", sbody);
                    Log.i("Reponse", sbody);
                    return "Loading Complete.";
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
            try {
                callbacklistener.onPostTask(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
