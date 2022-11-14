package net.mobil.nembotmarius.snackpos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListVentesServ extends AppCompatActivity {
    Outils o;
    String option = "";
    ArrayList<HashMap<String, String>> listitems;
    ListViewAdapter adapter;

    public interface CallBackListener<String> {
        void onPostTask(String result) throws JSONException;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_ventes);

        final ListView listvdetails = (ListView) findViewById(R.id.listvdetailsdel);

        o = new Outils();
        o.readConfig(this.getApplicationContext());

        final CallBackListener<String> callbacklistener = new CallBackListener<String>() {
            @Override
            public void onPostTask(String result) {
                String jsonid = result;
                try {
                    JSONObject jo_inside = new JSONObject(jsonid);
                    JSONArray jsArray = jo_inside.getJSONArray("Encaisser");

                    // ---
                    listitems = new ArrayList<HashMap<String,String>>();
                    String[] nCols = {o.DriverName}; int[] nLens = {42}; int[] nAligns = {1};
                    String nligne = o.getLigne(nCols, nLens, nAligns);
                    HashMap p = new HashMap<String, String>();
                    p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
                    listitems.add(p);

                    String Libelle = "";
                    for (int j = 1; j <= 3; j++) {
                        if(j==1) jsArray = jo_inside.getJSONArray("Commandes");
                        if(j==2) jsArray = jo_inside.getJSONArray("Factures");
                        if(j==3) jsArray = jo_inside.getJSONArray("Encaisser");

                        if(j==1) Libelle = "Commandes";
                        if(j==2) Libelle = "Factures";
                        if(j==3) Libelle = "Encaisser";

                        nCols = new String[]{Libelle}; nLens = new int[]{32}; nAligns = new int[]{1};
                        nligne = o.getLigne(nCols, nLens, nAligns);
                        p = new HashMap<String, String>();
                        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
                        listitems.add(p);

                        nCols = new String[]{"No", "Qte", "Montant", "Bonus"}; nLens = new int[]{6,6,12,8}; nAligns = new int[]{1,-1,-1,-1};
                        nligne = o.getLigne(nCols, nLens, nAligns);
                        p = new HashMap<String, String>();
                        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
                        listitems.add(p);

                        Integer QteTot = 0;
                        Integer MontantTot = 0;
                        Integer CommissionTot = 0;

                        for (int i = 0; i < jsArray.length(); i++) {
                            JSONObject jsdet = jsArray.getJSONObject(i);
                            if(!jsdet.isNull("datedoc")) {
                                String _datedoc = jsdet.getString("datedoc");
                                String _numauto = jsdet.getString("numauto");
                                String _codecl = jsdet.getString("codecl");
                                String _nom = jsdet.getString("nom");
                                String _qtedoc = jsdet.getString("qtedoc");
                                String _montantht = jsdet.getString("montantht");
                                String _Commission = jsdet.getString("Commission");

                                QteTot += Integer.parseInt(_qtedoc);
                                MontantTot += Integer.parseInt(_montantht);
                                CommissionTot += Integer.parseInt(_Commission);

                                nCols = new String[]{_numauto, _qtedoc, _montantht, _Commission}; nLens = new int[]{6,6,12,8}; nAligns = new int[]{1,-1,-1,-1};
                                nligne = o.getLigne(nCols, nLens, nAligns);
                                p = new HashMap<String, String>();
                                p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
                                listitems.add(p);
                            }
                        }

                        p = new HashMap<String, String>();
                        p.put("P1", "--------------------------------");
                        p.put("P2", "--------------------------------");
                        p.put("P3", "--------------------------------");
                        listitems.add(p);

                        nCols = new String[]{"", String.valueOf(QteTot) , String.valueOf(MontantTot), String.valueOf(CommissionTot)}; nLens = new int[]{6,6,12,8}; nAligns = new int[]{1,-1,-1,-1};
                        nligne = o.getLigne(nCols, nLens, nAligns);
                        p = new HashMap<String, String>();
                        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
                        listitems.add(p);
                    }

                    adapter = new ListViewAdapter(ListVentesServ.this, listitems, 1);
                    listvdetails.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //m_jArryjsoninvoice = new JSONArray(strInvoices);
            }
        };

        new Requestdata(callbacklistener).execute(o.Depot);
    }

    private class Requestdata extends AsyncTask<String, Void, String> {
        private CallBackListener<String> callbacklistener;

        protected Requestdata(CallBackListener<String> callbacklistener){
            this.callbacklistener = (CallBackListener<String>) callbacklistener;
        }

        protected String doInBackground(String... params) {
            int count = params.length;
            String nDepot = "";
            if(count>0){
                nDepot = params[0];
            }

            String url = o.getUrl("json/ventesjour.php");
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            String result="";
            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if(response.code()==200){
                    String sbody = response.body().string().trim();
                    result=sbody;
                }else{
                    return "Loading Fail.";
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return result;
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