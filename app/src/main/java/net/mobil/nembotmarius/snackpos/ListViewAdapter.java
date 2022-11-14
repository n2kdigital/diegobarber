package net.mobil.nembotmarius.snackpos;

/**
 * Created by NEMBOT02 on 01/07/2017.
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class ListViewAdapter extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    public int item;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;
    TextView txtFour;

    public ListViewAdapter(Activity activity,ArrayList<HashMap<String, String>> list, int item){
        super();
        this.activity=activity;
        this.list=list;
        this.item = item;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub



        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){
            if(this.item == 1){
                convertView = inflater.inflate(R.layout.listview_1item, null);
            }else if(this.item == 2){
                convertView=inflater.inflate(R.layout.listview_2item, null);
            }else if(this.item == 3){
                convertView=inflater.inflate(R.layout.listview_3item, null);
            }else if(this.item == 4){
                convertView=inflater.inflate(R.layout.listview_5item, null);
            }
        }

        try{
            if(this.item == 1){
                HashMap<String, String> map=list.get(position);
                txtFirst=(TextView) convertView.findViewById(R.id.item1);
                txtFirst.setText(map.get("P1"));
            }else if(this.item == 2){
                HashMap<String, String> map=list.get(position);
                txtFirst=(TextView) convertView.findViewById(R.id.item1);
                txtSecond=(TextView) convertView.findViewById(R.id.item2);
                txtFirst.setText(map.get("P1"));
                txtSecond.setText(map.get("P2"));
            }else if(this.item == 3){
                HashMap<String, String> map=list.get(position);
                txtFirst=(TextView) convertView.findViewById(R.id.item1);
                txtSecond=(TextView) convertView.findViewById(R.id.item2);
                txtThird=(TextView) convertView.findViewById(R.id.item3);
                txtFirst.setText(map.get("P1"));
                txtSecond.setText(map.get("P2"));
                txtThird.setText(map.get("P3"));
            }else if(this.item == 4){
                HashMap<String, String> map=list.get(position);
                txtFirst=(TextView) convertView.findViewById(R.id.item1);
                txtSecond=(TextView) convertView.findViewById(R.id.item2);
                txtThird=(TextView) convertView.findViewById(R.id.item3);
                txtFour=(TextView) convertView.findViewById(R.id.item4);
                txtFirst.setText(map.get("P1"));
                txtSecond.setText(map.get("P2"));
                txtThird.setText(map.get("P3"));
                txtFour.setText(map.get("P4"));

                ImageButton btn=(ImageButton)convertView.findViewById(R.id.imageButton3);
                btn.setTag(position);
                btn.setOnClickListener((SalesInvoice) this.activity);
                /*
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer index = (Integer) v.getTag();
                        //items.remove(index.intValue());
                        list.remove(index);
                        notifyDataSetChanged();
                        activity.get
                    }
                });
                */

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

}
