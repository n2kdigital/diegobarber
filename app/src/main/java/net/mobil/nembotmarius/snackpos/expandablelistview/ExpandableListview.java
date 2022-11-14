package net.mobil.nembotmarius.snackpos.expandablelistview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import net.mobil.nembotmarius.snackpos.Outils;
import net.mobil.nembotmarius.snackpos.R;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExpandableListview extends AppCompatActivity {

    EditText edtserveuse;
    Button btsearch;

    public interface CallBackListener<String> {
        void onPostTask(String result);
    }
    Outils o;
    // more efficient than HashMap for mapping integers to objects
    SparseArray<Group> groups = new SparseArray<Group>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_listview);
        o = new Outils();
        o.readConfig(this.getApplicationContext());

        edtserveuse = (EditText)findViewById(R.id.edtserveuse);
        btsearch = (Button)findViewById(R.id.btsearch);

        edtserveuse.setEnabled(false);
        if(o.Droits.equals("2")) edtserveuse.setEnabled(true);

        final CallBackListener<String> callbacklistener = new CallBackListener<String>() {
            @Override
            public void onPostTask(String result) {
                try {
                    String jsondetails = result;
                    JSONArray m_jArry = new JSONArray(jsondetails);
                    String facture="";
                    String isfacture="";
                    String isvalide="";
                    int totqte = 0;
                    int totmont = 0;
                    int key = 0;
                    Group group = new Group();
                    for (int i = 0; i < m_jArry.length(); i++) {
                        JSONObject jo_inside = m_jArry.getJSONObject(i);
                        String Serveuse = jo_inside.getString("Serveuse");
                        String NoFacture = jo_inside.getString("NoFacture");
                        String IsFacture = jo_inside.getString("Facture");
                        String IsValide = jo_inside.getString("Valide");
                        String Quantite = jo_inside.getString("Quantite");
                        String Montant = jo_inside.getString("Montant");
                        String Designation = jo_inside.getString("Designation");
                        String hhmm = jo_inside.getString("HHMM");

                        if(!facture.equals("") && !facture.equals(NoFacture)){
                            group.setMontant(String.valueOf(totmont));
                            group.setNofact(facture);
                            group.setFacture(isfacture);
                            group.setValide(isvalide);
                            group.setTotqte(String.valueOf(totqte));
                            groups.append(key++, group);
                            group = new Group();
                            totqte = 0;
                            totmont = 0;
                        }

                        totmont+=Integer.parseInt(Montant);
                        totqte+=Integer.parseInt(Quantite);

                        Product p = new Product(Quantite,Designation,Montant,hhmm);
                        group.details.add(p);
                        facture=NoFacture;
                        isfacture=IsFacture;
                        isvalide=IsValide;
                    }

                    if(totqte>0){
                        group.setMontant(String.valueOf(totmont));
                        group.setNofact(facture);
                        group.setFacture(isfacture);
                        group.setValide(isvalide);
                        group.setTotqte(String.valueOf(totqte));
                        groups.append(key++, group);
                    }

                    ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
                    ExpandableListAdapter adapter = new ExpandableListAdapter(ExpandableListview.this,groups);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        btsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Requestdatareceipt(callbacklistener).execute(edtserveuse.getText().toString());
            }
        });

        edtserveuse.setText(o.DriverName);
        new Requestdatareceipt(callbacklistener).execute(o.DriverName);
    }



    private class Requestdatareceipt extends AsyncTask<String, Void, String> {
        private ExpandableListview.CallBackListener<String> callbacklistener;

        protected Requestdatareceipt(ExpandableListview.CallBackListener<String> callbacklistener){
            this.callbacklistener = callbacklistener;
        }

        protected String doInBackground(String... params) {
            int count = params.length;
            String Serv2 = "";
            if(count>0){
                Serv2 = params[0];
            }

            String address = "&Serv2=" + Serv2;

            String url = o.getUrl("json/docventes.php", address);
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