package net.mobil.nembotmarius.snackpos;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PrintInvoice extends AppCompatActivity {

    public interface CallBackListener<String> {
        void onPostTask(String result);
    }

    ArrayList<HashMap<String, String>> listitems;
    ArrayList<HashMap<String, String>> listitems2;
    ListViewAdapter adapter;
    String sbody;
    Button btsave;
    Outils o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_invoice);

        final CallBackListener<String> callbacklistener = new CallBackListener<String>() {
            @Override
            public void onPostTask(String result) {
                try {
                    String jsonid = result.replace("\"{","{").replace("}\"","}").replace("\\","");
                    JSONObject jo_inside = new JSONObject(jsonid);

                    if(!jo_inside.isNull("msg")){
                        String msg = jo_inside.getString("msg");
                        if(msg.equals("Ok")){
                            Intent intent = new Intent();
                            intent.putExtra("keyop", "v300");
                            setResult(RESULT_OK, intent);
                            finish();
                        }else{
                            btsave.setEnabled(true);
                            Toast.makeText(PrintInvoice.this,"non ok",Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    btsave.setEnabled(true);
                    e.printStackTrace();
                }

            }
        };


        o = new Outils();
        o.readConfig(this.getApplicationContext());

        listitems = new ArrayList<HashMap<String,String>>();
        listitems2 = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> p = new HashMap<String, String>();

        ListView listvdetails = (ListView) findViewById(R.id.listvdetailsdel);
        btsave = (Button) findViewById(R.id.btsave);
        Button btcancel = (Button) findViewById(R.id.btcanceldel);
        Button btreturn = (Button) findViewById(R.id.btreturndel);

        btsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btsave.setEnabled(false);
                new Requestdatainvoice(callbacklistener).execute(sbody);
            }
        });

        btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("keyop", "v300");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("keyop", "v000");
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        sbody = "";

        Intent intent = getIntent();
        String customer = intent.getStringExtra("customer");
        ArrayList aList= new ArrayList(Arrays.asList(customer.split("-")));
        if(aList.size()>1){
            customer = aList.get(1).toString().trim() + " - " + aList.get(0).toString().trim();
        }
        String transport = intent.getStringExtra("transport");
        String tournee = intent.getStringExtra("tournee");
        String jsondetails = intent.getStringExtra("jsondetails");
        String Datedoc = o.datejour();
        String nligne = "";

        // ---
        String[] nCols = {o.Entityname}; int[] nLens = {42}; int[] nAligns = {1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p = new HashMap<String, String>();
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems2.add(p);

        nCols = new String[]{o.Entityname}; nLens = new int[]{32}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p = new HashMap<String, String>();
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems.add(p);
        //---

        // ---
        nCols = new String[]{customer + " - " + tournee}; nLens = new int[]{42}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p = new HashMap<String, String>();
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems2.add(p);

        nCols = new String[]{customer + " - " + tournee}; nLens = new int[]{32}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p = new HashMap<String, String>();
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems.add(p);
        // ---

        // ---
        nCols = new String[]{Datedoc}; nLens = new int[]{42}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p = new HashMap<String, String>();
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems2.add(p);

        nCols = new String[]{Datedoc}; nLens = new int[]{32}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p = new HashMap<String, String>();
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems.add(p);
        // ---

        // ---
        p = new HashMap<String, String>();
        String nlig = "";
        for(int nk=0; nk<45; nk++){ nlig += "-";}
        nCols = new String[]{nlig}; nLens = new int[]{42}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems2.add(p);

        p = new HashMap<String, String>();
        nlig = "";
        for(int nk=0; nk<45; nk++){ nlig += "-";}
        nCols = new String[]{nlig}; nLens = new int[]{32}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems.add(p);
        // ---

        // ---
        p = new HashMap<String, String>();
        nCols = new String[]{"PROD","QTY","GLA","LIQ","DIS","NET"}; nLens = new int[]{10,5,7,7,7,7}; nAligns = new int[]{1,-1,-1,-1,-1,-1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems2.add(p);

        p = new HashMap<String, String>();
        nCols = new String[]{"PROD","QTY","GLA","LIQ"}; nLens = new int[]{10,5,8,8}; nAligns = new int[]{1,-1,-1,-1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems.add(p);
        // ---

        int montant = 0;
        int montantemb = 0;
        int ristourne = 0;
        int intqte = 0;
        String sbodydet = "";
        String vir = "";
        try {
            JSONArray m_jArry = new JSONArray(jsondetails);
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                p = new HashMap<String, String>();
                int totlignedet = 0;
                String col1 = jo_inside.getString("codeprod");
                String col2 = jo_inside.getString("quantite");
                String col3 = jo_inside.getString("prixtotal");
                String col4 = jo_inside.getString("prixemb");
                col4=(col4.equals("N"))?"0":col4;
                col4=(col4.equals(""))?"0":col4;
                String col5 = jo_inside.getString("ristourne");
                String col21 = String.valueOf(Integer.parseInt(col2)*Integer.parseInt(transport));
                String col31 = String.valueOf(Integer.parseInt(col3)*Integer.parseInt(transport));
                sbodydet += vir + "{\"codeprod\":\"" + col1 + "\",\"quantite\":\"" + col21 + "\",\"prixtotal\":\"" + col31 + "\",\"prixemb\":\"" + col4 + "\",\"ristourne\":\"" + col5 + "\"}";
                vir = ",";
               if(Integer.parseInt(col2)>0){
                   intqte += Integer.parseInt(col21);
               }else{
                   col2 = "";
               }
                totlignedet = Integer.parseInt(col31);
                montant += Integer.parseInt(col31);
                montantemb = Integer.parseInt(col4);
                ristourne = Integer.parseInt(col5);

                // --
                nCols = new String[]{col1, col21, col4, col31, col5, String.valueOf(totlignedet)}; nLens = new int[]{10,5,7,7,7,7}; nAligns = new int[]{1,-1,-1,-1,-1,-1};
                nligne = o.getLigne(nCols, nLens, nAligns);
                p = new HashMap<String, String>();
                p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
                listitems2.add(p);

                nCols = new String[]{col1, col21, (ristourne==1)?"":"N", col31}; nLens = new int[]{10,5,8,8}; nAligns = new int[]{1,-1,-1,-1};
                nligne = o.getLigne(nCols, nLens, nAligns);
                p = new HashMap<String, String>();
                p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
                listitems.add(p);
                // --
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }

        // ---
        p = new HashMap<String, String>();
        for(int nk=0; nk<45; nk++){ nlig += "-";}
        nCols = new String[]{nlig}; nLens = new int[]{42}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems2.add(p);

        p = new HashMap<String, String>();
        for(int nk=0; nk<32; nk++){ nlig += "-";}
        nCols = new String[]{nlig}; nLens = new int[]{32}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems.add(p);
        // ---

        int montantmoinsris = montant - ristourne;

        // ---
        p = new HashMap<String, String>();
        nCols = new String[]{"Total",String.valueOf(intqte), String.valueOf(montantemb), String.valueOf(montant), String.valueOf(ristourne), String.valueOf(montantmoinsris)}; nLens = new int[]{10,5,7,7,7,7}; nAligns = new int[]{1,-1,-1,-1,-1,-1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems2.add(p);

        p = new HashMap<String, String>();
        nCols = new String[]{"Total",String.valueOf(intqte), String.valueOf(montantemb), String.valueOf(montant)}; nLens = new int[]{10,5,8,8}; nAligns = new int[]{1,-1,-1,-1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems.add(p);

        int inttransport = 0;
        int nettopay = montant + montantemb + inttransport;


        // ---

        nCols = new String[]{o.DriverName}; nLens = new int[]{42}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p = new HashMap<String, String>();
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems2.add(p);


        nCols = new String[]{o.DriverName}; nLens = new int[]{32}; nAligns = new int[]{1};
        nligne = o.getLigne(nCols, nLens, nAligns);
        p = new HashMap<String, String>();
        p.put("P1", nligne); p.put("P2", ""); p.put("P3", ""); p.put("P4", "");
        listitems.add(p);

        // ---

        adapter = new ListViewAdapter(this, listitems, 1);
        listvdetails.setAdapter(adapter);
        sbody = "{\"Customer\":\"" + customer + "\",\"Transport\":\"" + transport + "\",\"Tournee\":\"" + tournee + "\",\"Datedoc\":\"" + Datedoc.replace(":","h") + "\",\"Details\":[" + sbodydet + "]}";
    }

    private class Requestdatainvoice extends AsyncTask<String, Void, String> {
        private CallBackListener<String> callbacklistener;

        protected Requestdatainvoice(CallBackListener<String> callbacklistener){
            this.callbacklistener = callbacklistener;
        }

        protected String doInBackground(String... params) {
            int count = params.length;
            String jsondata = "";
            if(count>0){
                jsondata = params[0];
            }

            String url = o.getUrl("json/docfromandroid.php") + "&jsondata=" + jsondata;
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            String jsonresult = "";
            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if(response.code()==200){
                    String sbody = response.body().string().trim();
                    return sbody;
                }else{
                    return "Facture Error.";
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
