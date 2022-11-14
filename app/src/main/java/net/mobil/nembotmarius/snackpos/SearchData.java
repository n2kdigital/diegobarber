package net.mobil.nembotmarius.snackpos;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchData extends AppCompatActivity {
    String _url;
    String _keyop;
    ArrayList<HashMap<String, String>> listitems;
    ArrayList<HashMap<String, String>> listitems_cloned;

    ListViewAdapter adapter;

    EditText edtdata;
    ListView listvdata;

    Outils o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_data);


        listitems = new ArrayList<HashMap<String,String>>();
        listitems_cloned = new ArrayList<HashMap<String,String>>();

        listvdata = (ListView)findViewById(R.id.listvdata);
        edtdata = (EditText)findViewById(R.id.edtdata);

        o = new Outils();
        o.readConfig(this.getApplicationContext());

        this._url = "";
        this._keyop = "";
        Intent i = getIntent();
        String keyop = i.getStringExtra("keyop");
        if (keyop != null) {
            this._keyop = keyop;
            if(keyop.equals("v100")){
                this._url = "com.nembotmarius.product.json";
                if(o.Droits.equals("2")){
                    this._url = "com.nembotmarius.productinvoice.json";
                }

            }else if(keyop.equals("v200")){
                this._url = "com.nembotmarius.customer.json";
            };
        }

        initListFromJson();

        listvdata.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                HashMap<String,String> p = listitems.get(position);
                // put the String to pass back into an Intent and close this activity
                Intent intent = new Intent();
                intent.putExtra("keyop", _keyop);
                intent.putExtra("item1", p.get("P1"));
                intent.putExtra("item2", p.get("P2"));
                intent.putExtra("item3", p.get("P3"));
                intent.putExtra("item4", p.get("P4"));
                intent.putExtra("item5", p.get("P5"));
                intent.putExtra("item6", p.get("P6"));
                intent.putExtra("item7", p.get("P7"));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        edtdata.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    //reset list view
                    //initList();
                    initListFromJson();
                }else{
                    //perform code change
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void searchItem(String textToSearch){
        ArrayList<HashMap<String,String>> listitems1 = (ArrayList<HashMap<String,String>>)listitems.clone();
        for (int i = 0; i < listitems1.size(); i++) {
            HashMap<String,String> p = listitems1.get(i);
            String item = p.get("P1");
            for (int j = 0; j < listitems.size(); j++) {
                HashMap<String,String> p1 = listitems.get(j);
                String item1 = p1.get("P1");
                if(!item1.startsWith(textToSearch.toUpperCase())){
                    listitems.remove(j);
                }
            }
        }

        if(this._keyop.equals("v100")){
            adapter = new ListViewAdapter(this, listitems, 2);
        }else if(this._keyop.equals("v200")){
            adapter = new ListViewAdapter(this, listitems, 1);
        }
        listvdata.setAdapter(adapter);
    }

    public void initListFromJson() {
        try {
            if(listitems_cloned.size()==0){
                Outils o = new Outils();
                JSONArray m_jArry = new JSONArray(o.readFromFile(SearchData.this.getApplicationContext(),this._url));
                listitems = new ArrayList<HashMap<String,String>>();
                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                    HashMap<String,String> p = new HashMap<String, String>();
                    p.put("P1", jo_inside.getString("item1"));
                    p.put("P2", jo_inside.getString("item2"));
                    p.put("P3", jo_inside.getString("item3"));
                    p.put("P4", jo_inside.getString("item4"));
                    p.put("P5", jo_inside.getString("item5"));
                    p.put("P6", jo_inside.getString("item6"));
                    p.put("P7", jo_inside.getString("item7"));
                    listitems.add(p);
                }
                listitems_cloned = (ArrayList<HashMap<String,String>>)listitems.clone();
            }else{
                listitems = (ArrayList<HashMap<String,String>>)listitems_cloned.clone();
            }

            if(this._keyop.equals("v100")){
                adapter = new ListViewAdapter(this, listitems, 2);
            }else if(this._keyop.equals("v200")){
                adapter = new ListViewAdapter(this, listitems, 1);
            }
            listvdata.setAdapter(adapter);
        }catch(JSONException e) {
            e.printStackTrace();
        }
    }


}
