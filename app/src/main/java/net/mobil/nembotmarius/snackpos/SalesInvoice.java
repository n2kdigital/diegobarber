package net.mobil.nembotmarius.snackpos;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SalesInvoice extends AppCompatActivity implements View.OnClickListener {
    private static final int SECOND_ACTIVITY_RESULT_CODE = 0;
    Outils o;

    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
        //items.remove(index.intValue());
        listitems.remove(index.intValue());
        adapter = new ListViewAdapter(this, listitems, 4);
        listvdet.setAdapter(adapter);
    }

    public interface CallBackListener<String> {
        void onPostTask(String result);
    }

    CallBackListener<String> callbacklistener;

    EditText edtsalescust;
    EditText edtsalescust2;
    EditText edtsalescust3;
    EditText edtnofact;
    EditText edtsalesprod;
    EditText edtsalesqte;
    EditText edtsalesprice;
    ListView listvdet;
    //CheckBox chkglace;
    //CheckBox chkannulation;

    String vartauxrist;
    String varcodetour;
    String varprixbase;
    String varsens;
    String varQtemax;
    String strjsonStock;

    ArrayList<HashMap<String, String>> listitems;
    ListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_invoice);

        final Intent intentsearchcustomer = new Intent(this, SearchData.class);
        final Intent intentsearchScannedBarcodeActivity= new Intent(this, ScannedBarcodeActivity.class);
        final Intent intentsearchproduct = new Intent(this, SearchData.class);
        final Intent intentprintinvoice = new Intent(this, PrintInvoice.class);
        final Intent intentsynchrodata = new Intent(this, SynchroData.class);

        final ImageButton imgbtncust = (ImageButton) findViewById(R.id.imgbtncust);
        final ImageButton imgbtnprod = (ImageButton) findViewById(R.id.imgbtnprod);
        final Button btnregister = (Button) findViewById(R.id.btnregister);
        final Button btnvalidate = (Button) findViewById(R.id.btnvalidate);

        o = new Outils();
        o.readConfig(SalesInvoice.this);

        edtsalescust = (EditText)findViewById(R.id.edtsalescust);
        edtsalescust2 = (EditText)findViewById(R.id.edtsalescust2);
        edtsalescust3 = (EditText)findViewById(R.id.edtsalescust3);
        edtnofact = (EditText)findViewById(R.id.edtnofact);
        edtsalesprod = (EditText)findViewById(R.id.edtsalesprod);
        edtsalesqte = (EditText)findViewById(R.id.edtsalesqte);
        edtsalesprice = (EditText)findViewById(R.id.edtsalesprice);
        listvdet = (ListView) findViewById(R.id.listvdet);
        //chkglace = (CheckBox) findViewById(R.id.chkglace);
        //chkannulation = (CheckBox) findViewById(R.id.chkannulation);

        //chkannulation.setVisibility(View.INVISIBLE);
        //chkannulation.setChecked(false);
        if(o.Droits.equals("2")){
            //chkannulation.setVisibility(View.VISIBLE);
            //chkannulation.setChecked(true);
            //chkannulation.setEnabled(false);
        }

        edtsalesprice.setEnabled(false);
        edtsalescust.setEnabled(true);
        edtsalescust2.setEnabled(true);
        edtsalescust3.setEnabled(true);
        edtsalesprod.setEnabled(false);
        edtnofact.setEnabled(false);

        imgbtncust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intentsearchcustomer.putExtra("keyop", "v200");
                //startActivityForResult(intentsearchcustomer, SECOND_ACTIVITY_RESULT_CODE);

                //startActivity(new Intent(SalesInvoice.this, ScannedBarcodeActivity.class));

                intentsearchScannedBarcodeActivity.putExtra("keyop", "v200");
                startActivityForResult(intentsearchScannedBarcodeActivity, SECOND_ACTIVITY_RESULT_CODE);
            }
        });

        imgbtnprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentsearchproduct.putExtra("keyop", "v100");
                startActivityForResult(intentsearchproduct, SECOND_ACTIVITY_RESULT_CODE);
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDetail();
            }
        });

        btnvalidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strjson = o.getJson(listitems,"codeprod;quantite;prixtotal;prixemb;ristourne");
                if(strjson.length()>15){
                    int inttransp = 1;
                    String strCustomer = edtsalescust.getText().toString() + "_" + edtsalescust2.getText().toString() + "_" + edtsalescust3.getText().toString();
                    intentprintinvoice.putExtra("customer", strCustomer);
                    intentprintinvoice.putExtra("tournee", varcodetour);
                    intentprintinvoice.putExtra("transport", String.valueOf(inttransp));
                    intentprintinvoice.putExtra("jsondetails", strjson);
                    startActivityForResult(intentprintinvoice, SECOND_ACTIVITY_RESULT_CODE);
                }else{
                    Toast.makeText(SalesInvoice.this.getApplicationContext(),"The document is empty",Toast.LENGTH_SHORT).show();
                }
            }
        });

        listitems = new ArrayList<HashMap<String,String>>();
        edtsalescust.requestFocus();

        Intent intent = getIntent();
        String nofact = intent.getStringExtra("nofact");
        if(nofact == null){
            nofact="999999";
        }
        edtnofact.setText(nofact);

        vartauxrist = "0";
        varprixbase = "0";
        varsens = "0";
        varcodetour = "";
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SynchroData.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.closeapp:
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                String keyop = data.getStringExtra("keyop");
                String value;
                if (keyop != null) {
                    if (keyop.equals("v100")) {
                        value = data.getStringExtra("item1");
                        if (value != null) {
                            if (!value.equals("")) {
                                edtsalesprod.setText(value);
                            }
                        }
                        //varQtemax
                        value = data.getStringExtra("item3");
                        if (value != null) {
                            if (!value.equals("")) {
                                try{
                                    varQtemax=value;
                                    edtsalesqte.setText(varQtemax);
                                }catch (Exception e){
                                    varQtemax="1";
                                    edtsalesqte.setText(varQtemax);
                                }

                            }
                        }
                        value = data.getStringExtra("item4");
                        if (value != null) {
                            if (!value.equals("")) {
                                try{
                                    edtsalesprice.setText(value);
                                }catch (Exception e){
                                    edtsalesprice.setText("0");
                                }

                            }
                        }
                        value = data.getStringExtra("item5");
                        if (value != null) {
                            vartauxrist = value;
                        }
                        value = data.getStringExtra("item6");
                        if (value != null) {
                            varprixbase = value;
                        }
                        value = data.getStringExtra("item7");
                        if (value != null) {
                            varsens = value;
                        }
                    }else if (keyop.equals("v200")) {
                        value = data.getStringExtra("item1");
                        if (value != null) {
                            if (!value.equals("")) {
                                String[] tblvalues = value.split("_");
                                edtsalescust.setText(tblvalues[0]);
                                if(tblvalues.length>1) edtsalescust2.setText(tblvalues[1]);
                                if(tblvalues.length>2) edtsalescust3.setText(tblvalues[2]);
                            }
                        }
                        value = data.getStringExtra("item2");
                        if (value != null) {
                            if (!value.equals("")) {
                                varcodetour = value;
                            }
                        }
                    }else if (keyop.equals("v300")) {
                        edtsalescust.setText("CLIENTS DIVERS - DIVERS01");
                        edtsalesprod.setText("");
                        edtsalesqte.setText("1");
                        edtsalesprice.setText("0");
                        varcodetour = "";
                        listitems = new ArrayList<HashMap<String,String>>();
                        adapter = new ListViewAdapter(this, listitems, 1);
                        listvdet.setAdapter(adapter);
                        if(o.fileExists(SalesInvoice.this,"com.nembotmarius.stock.json")) {
                            strjsonStock = o.readFromFile(SalesInvoice.this, "com.nembotmarius.stock.json");
                        }

                        startActivity(new Intent(this, SynchroData.class));
                    }
                }
            }
        }
    }

    public void addDetail(){
        if(edtsalescust.getText().toString().equals("")){
            String msg = "Tel Client attendu";
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }
        if(edtsalescust2.getText().toString().equals("")){
            String msg = "Nom client attendu";
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }
        if(edtsalescust3.getText().toString().equals("")){
            String msg = "Quartier attendu";
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }
        if(edtsalesprod.getText().toString().equals("")){
            String msg = "Service rendu attendu";
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        if(edtsalesqte.getText().toString().equals("")){
            String msg = "Qte attendu";
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }else if(Integer.parseInt(edtsalesqte.getText().toString())<=0){
            for (int v = 0; v < listitems.size(); v++) {
                HashMap<String,String> p1 = new HashMap<String, String>();
                p1 = listitems.get(v);
                String codeprod1 = p1.get("P1");
                String codeprod = edtsalesprod.getText().toString();
                if(codeprod.equals(codeprod1)){
                    listitems.remove(v);
                    break;
                }
            }

            String msg = "Invalid quantity";
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }else if(Integer.parseInt(varQtemax)<Integer.parseInt(edtsalesqte.getText().toString())){
            if(o.Droits.equals("2")){
                String msg = "La quantité doit etre inferieur ou egal a la quantité commandée";
                Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        HashMap<String,String> p = new HashMap<String, String>();
        int intsens = Integer.parseInt(varsens);
        int stk = 1000;
        int intqte = 0;
        try{intqte = Integer.parseInt(edtsalesqte.getText().toString());}catch (Exception e){}

        int intprice = 0;
        try{intprice = Integer.parseInt(edtsalesprice.getText().toString());}catch (Exception e){}
        int intpriceemb = 0;
        int inttauxrist = 0;
        int montant = 0;
        int montantemb = 0;
        int ristourne = -1;

        montant = intqte * intprice;
        montantemb = intqte * intpriceemb;

        p.put("P1", edtsalesprod.getText().toString());
        p.put("P2", String.valueOf(intqte));
        p.put("P3", String.valueOf(montant));
        //p.put("P4", String.valueOf(montantemb));
        p.put("P4", (ristourne==1)?"":"N");
        p.put("P5", String.valueOf(ristourne));
        /*
        int ik = -1;
        for (int k = 0; k < listitems.size(); k++) {
            HashMap<String,String> p1 = new HashMap<String, String>();
            p1 = listitems.get(k);
            String codeprod1 = p1.get("P1");
            String codeprod = p.get("P1");
            if(codeprod.equals(codeprod1)){
                listitems.remove(k);
                ik = k;
                break;
            }
        }
        if(ik>=0){
            listitems.add(ik, p);
        }else{
            listitems.add(p);
        }
         */

        listitems.add(p);
        adapter = new ListViewAdapter(this, listitems, 4);
        listvdet.setAdapter(adapter);

        edtsalesprod.setText("");
        edtsalesqte.setText("1");
        edtsalesprice.setText("0");
        varsens = "1";
        varprixbase = "0";
        vartauxrist = "0";

        edtsalesprod.requestFocus();
    }

}
