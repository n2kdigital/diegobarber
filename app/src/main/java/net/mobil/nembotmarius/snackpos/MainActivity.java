package net.mobil.nembotmarius.snackpos;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_RESULT_CODE = 0;
    Outils o;

    Button btnvalider;
    EditText txtbar;
    EditText txtserv;
    EditText txtpwd;
    EditText txturl;

    public interface CallBackListener<String> {
        void onPostTask(String result);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intentsynchrodata = new Intent(MainActivity.this, SynchroData.class);


        final CallBackListener<String> callbacklistener = new CallBackListener<String>() {
            @Override
            public void onPostTask(String result) {
                try {
                    btnvalider.setEnabled(true);
                    String jsonid = result.replace("\"{","{").replace("}\"","}").replace("\\","");
                    JSONObject jo_inside = new JSONObject(jsonid);

                    if(!jo_inside.isNull("msg")){
                        String msg = jo_inside.getString("msg");
                        String level = jo_inside.getString("level");
                        if(msg.equals("Ok")){
                            o.Domain = txturl.getText().toString();
                            o.SalesCode = txtpwd.getText().toString();
                            o.DriverName = txtserv.getText().toString();
                            o.Depot = txtbar.getText().toString();
                            o.Droits = level;
                            o.LockSales = 1;
                            o.setConfig(MainActivity.this.getApplicationContext());
                            o.deletefile(MainActivity.this,"com.nembotmarius.product.json");

                            startActivity(intentsynchrodata);
                        }else{
                            Toast.makeText(MainActivity.this,"Information de connection invalide",Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        o = new Outils();
        o.readConfig(this.getApplicationContext());

        btnvalider = (Button) findViewById(R.id.btnvalider);
        txtbar = (EditText)findViewById(R.id.txtbar);
        txturl = (EditText)findViewById(R.id.txturl);
        txtserv = (EditText)findViewById(R.id.txtserv);
        txtpwd = (EditText)findViewById(R.id.txtpwd);

        txtbar.setText(o.Depot);
        txturl.setText(o.Domain);
        txtserv.setText(o.DriverName);

        btnvalider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnvalider.setEnabled(false);
                new Requestdata(callbacklistener).execute(txturl.getText().toString(),txtserv.getText().toString(),txtpwd.getText().toString(),txtbar.getText().toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }).create().show();
    }

    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                String keyop = data.getStringExtra("keyop");
                if (keyop != null) {

                }
            }
        }
    }




    private class Requestdata extends AsyncTask<String, Void, String> {
        private CallBackListener<String> callbacklistener;

        protected Requestdata(CallBackListener<String> callbacklistener){
            this.callbacklistener = callbacklistener;
        }

        protected String doInBackground(String... params) {
            int count = params.length;
            String Serv = "";
            String Pwd = "";
            String Depot = "";
            String Urlstr = "";
            String Port = "";
            String Dossier = "";
            if(count>0){
                Urlstr = params[0];
                Serv = params[1];
                Pwd = params[2];
                Depot = params[3];
            }

            String url = Urlstr + "/json/settingfromandroid.php?Serv=" + Serv + "&Pwd=" + Pwd + "&Depot=" + Depot;
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if(response.code()==200){
                    String sbody = response.body().string().trim();
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
