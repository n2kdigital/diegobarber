package net.mobil.nembotmarius.snackpos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NEMBOT02 on 02/07/2017.
 */

public class Outils {
    public String Entityname;
    public String Entitytax;
    public String Driver;
    public String Printer;
    public String Internet;
    public String Lastcheckedtime;
    public int LockSales;

    public String Domain;
    public String Depot;
    public String DriverName;
    public String SalesCode;
    public String Port;
    public String Dossier;
    public String Droits;
    public String Url;

    public Outils(){
        Domain = "";
        Port = "";
        Dossier = "";
        Entityname = "";
        Droits = "";
    }

    public String getJson(ArrayList<HashMap<String, String>> listitems, String keylist){
        String strjson = "[";
        String vir1 = "";
        for (int j = 0; j < listitems.size(); j++) {
            HashMap<String,String> details = listitems.get(j);
            strjson += vir1 + "{";
            String vir2 = "";
            for (HashMap.Entry<String,String> d : details.entrySet()) {
                strjson += vir2 + "\"_" + d.getKey() + "\":\"" + d.getValue() + "\"";
                vir2 = ",";
            }
            strjson += "}";
            vir1 = ",";
        }
        strjson += "]";

        ArrayList aList= new ArrayList(Arrays.asList(keylist.split(";")));
        int j = 0;
        for(int i=0;i<aList.size();i++)
        {
            j  = i + 1;
            String keyv = "_P" + String.valueOf(j);
            strjson = strjson.replace(keyv, aList.get(i).toString());
        }
        return strjson;
    }

    public void wrtieFileOnInternalStorage(final Context c, String filename, String sBody){
        FileOutputStream outputStream;
        try {
            outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(sBody.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletefile(final Context c, String filename) {
        c.deleteFile(filename);
    }

    public String readFromFile(final Context c, String fileName) {
        String ret = "";
        try {
            InputStream inputStream = c.openFileInput(fileName);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int size = inputStream.available();
                char[] buffer = new char[size];

                inputStreamReader.read(buffer);

                inputStream.close();
                ret = new String(buffer);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public boolean fileExists(final Context c, String filename) {
        File file = c.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public String getUrl(String route) {
        return this.Domain + "/" + this.Dossier + route + "?Serv=" + this.DriverName + "&Pwd=" + this.SalesCode + "&Depot=" + this.Depot;
    }

    public String getUrl(String route, String address) {
        return this.Domain + "/" + this.Dossier + route + "?Serv=" + this.DriverName + "&Pwd=" + this.SalesCode + "&Depot=" + this.Depot + address;
    }

    /*
    public String getUrl(String route) {
        return "http://" + this.Domain + ":" + this.Port + "/" + this.Dossier + "/" + route + "?Serv=" + this.DriverName + "&Pwd=" + this.SalesCode + "&Depot=" + this.Depot;
    }

    public String getUrl(String route, String address) {
        return "http://" + this.Domain + ":" + this.Port + "/" + this.Dossier + "/" + route + "?Serv=" + this.DriverName + "&Pwd=" + this.SalesCode + "&Depot=" + this.Depot + address;
    }
    */

    public void readConfig(final Context c) {
        try {
            if(fileExists(c,"com.nembotmarius.config.json")) {
                JSONObject jo_inside = new JSONObject(readFromFile(c,"com.nembotmarius.config.json"));
                this.Domain = (jo_inside.isNull("Domain"))?"":jo_inside.getString("Domain");
                this.Depot = (jo_inside.isNull("Depot"))?"":jo_inside.getString("Depot");
                this.DriverName = (jo_inside.isNull("DriverName"))?"":jo_inside.getString("DriverName");
                this.SalesCode = (jo_inside.isNull("SalesCode"))?"":jo_inside.getString("SalesCode");
                this.Port = (jo_inside.isNull("Port"))?"":jo_inside.getString("Port");
                this.Dossier = (jo_inside.isNull("Dossier"))?"":jo_inside.getString("Dossier");
                this.Droits = (jo_inside.isNull("Droits"))?"":jo_inside.getString("Droits");
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public void setConfig(final Context c) {
        String jsonconfig = "{\"Domain\":\"" + this.Domain;
        jsonconfig += "\",\"Depot\":\"" + this.Depot;
        jsonconfig += "\",\"DriverName\":\"" + this.DriverName;
        jsonconfig += "\",\"Port\":\"" + this.Port;
        jsonconfig += "\",\"Dossier\":\"" + this.Dossier;
        jsonconfig += "\",\"Droits\":\"" + this.Droits;
        jsonconfig += "\",\"SalesCode\":\"" + String.valueOf(this.SalesCode) + "\"}";
        wrtieFileOnInternalStorage(c, "com.nembotmarius.config.json", jsonconfig);
    }

    public String datejour() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyMMdd HH:mm");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public String getLigne(String[] nCols, int[] nLens, int[] nAligns){
        char[] lignetoprint = new char[100];
        ArrayList<char[]> nListcol = new ArrayList<char[]>();
        int totallen = 0;
        for(int i=0; i<nLens.length;i++){
            totallen += nLens[i];
        }

        for(int i=0; i<totallen;i++){
            lignetoprint[i] = ' ';
        }

        for(int i=0; i<nLens.length;i++){
            nListcol.add(i,nCols[i].toCharArray());
        }

        int posbegin = 0;
        int posend = 0;
        for(int i=0; i<nListcol.size();i++){
            posend += nLens[i];
            int align = nAligns[i];
            char[] ccol = nListcol.get(i);
            if(align>0){
                for(int k=0;k<ccol.length;k++)
                {
                    if(k<posend){
                        lignetoprint[k] = ccol[k];
                    }
                }
            }else if(align<0){
                int n = posend-1;
                for(int k=ccol.length-1;k>=0;k--)
                {
                    if(n>=posbegin){
                        lignetoprint[n--] = ccol[k];
                    }
                }
            }else{

            }
            posbegin = posend;
        }

        String totaltext = String.copyValueOf(lignetoprint);
        return totaltext.substring(0,totallen);
    }


    public String getLigne(String col1, String col2, String col3, String col4, String col5, String col6){
        char[] lignetoprint= {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
        char[] ccol1 = col1.toCharArray();
        char[] ccol2 = col2.toCharArray();
        char[] ccol3 = col3.toCharArray();
        char[] ccol4 = col4.toCharArray();
        char[] ccol5 = col5.toCharArray();
        char[] ccol6 = col6.toCharArray();
        for(int i=0;i<ccol1.length;i++)
        {
            if(i<9){
                lignetoprint[i] = ccol1[i];
            }
        }
        int j = 13;
        for(int i=(ccol2.length-1);i>=0;i--)
        {
            if(j>10){
                lignetoprint[j--] = ccol2[i];
            }
        }
        j = 22;
        for(int i=(ccol3.length-1);i>=0;i--)
        {
            if(j>15){
                lignetoprint[j--] = ccol3[i];
            }
        }
        j = 29;
        for(int i=(ccol4.length-1);i>=0;i--)
        {
            if(j>22){
                lignetoprint[j--] = ccol4[i];
            }
        }


        j = 36;
        for(int i=(ccol5.length-1);i>=0;i--)
        {
            if(j>29){
                lignetoprint[j--] = ccol5[i];
            }
        }

        j = 42;
        for(int i=(ccol6.length-1);i>=0;i--)
        {
            if(j>36){
                lignetoprint[j--] = ccol6[i];
            }
        }

        return String.copyValueOf(lignetoprint);
    }

    public String getLigne(String col1){
        char[] lignetoprint= {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
        char[] ccol1 = col1.toCharArray();
        for(int i=0;i<ccol1.length;i++)
        {
            if(i<43){
                lignetoprint[i] = ccol1[i];
            }
        }

        return String.copyValueOf(lignetoprint);
    }

    public String getLigne(String col1, String col2, String col3, String col4){
        char[] lignetoprint= {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
        char[] ccol1 = col1.toCharArray();
        char[] ccol2 = col2.toCharArray();
        char[] ccol3 = col3.toCharArray();
        char[] ccol4 = col4.toCharArray();
        for(int i=0;i<ccol1.length;i++)
        {
            if(i<9){
                lignetoprint[i] = ccol1[i];
            }
        }
        int j = 13;
        for(int i=(ccol2.length-1);i>=0;i--)
        {
            if(j>10){
                lignetoprint[j--] = ccol2[i];
            }
        }
        j = 22;
        for(int i=(ccol3.length-1);i>=0;i--)
        {
            if(j>15){
                lignetoprint[j--] = ccol3[i];
            }
        }
        j = 29;
        for(int i=(ccol4.length-1);i>=0;i--)
        {
            if(j>22){
                lignetoprint[j--] = ccol4[i];
            }
        }

        return String.copyValueOf(lignetoprint);
    }

    public String getLigne(String col1, int sens){
        char[] lignetoprint= {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
        char[] ccol1 = col1.toCharArray();
        if(sens>0){
            for(int i=0;i<ccol1.length;i++)
            {
                if(i<43){
                    lignetoprint[i] = ccol1[i];
                }
            }
        }else{
            int j=42;
            for(int i=(ccol1.length - 1);i>=0;i--)
            {
                if(j>=0){
                    lignetoprint[j--] = ccol1[i];
                }
            }
        }
        return String.copyValueOf(lignetoprint);
    }
}
