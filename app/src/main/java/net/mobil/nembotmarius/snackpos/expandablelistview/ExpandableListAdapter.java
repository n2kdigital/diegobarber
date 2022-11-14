package net.mobil.nembotmarius.snackpos.expandablelistview;

import net.mobil.nembotmarius.snackpos.FactureAddition;
import net.mobil.nembotmarius.snackpos.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final SparseArray<Group> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public ExpandableListAdapter(Activity act, SparseArray<Group> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).details.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Product p = (Product) getChild(groupPosition, childPosition);
        final Group grp = (Group) getGroup(groupPosition);
        TextView text1 = null;
        TextView text2 = null;
        TextView text3 = null;
        TextView text4 = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_expandable_listrow_details, null);
        }
        text1 = (TextView) convertView.findViewById(R.id.textView1);
        text2 = (TextView) convertView.findViewById(R.id.textView2);
        text3 = (TextView) convertView.findViewById(R.id.textView3);
        text4 = (TextView) convertView.findViewById(R.id.textView4);
        text1.setText(p.quantity);
        text2.setText(p.designation);
        text3.setText(p.montant);
        text4.setText(p.hhmm);

        String strcolor = "#B0E0E6,#B0E0E6,#B0E0E6,#B0E0E6,#B0E0E6,#B0E0E6,#B0E0E6,#B0E0E6,#B0E0E6";
        //String strcolor = "#B0C4DE,#B0E0E6,#ADD8E6,#87CEEB,#87CEFA,#00BFFF,#1E90FF,#6495ED,#4682B4";
        String[] arrColor = strcolor.split(",");
        int ind = groupPosition % 9;
        convertView.setBackgroundColor(Color.parseColor(arrColor[ind]));
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(grp.getValide().equals("OUI")){
                    Toast.makeText(v.getContext(), "Facture encaissée", Toast.LENGTH_SHORT).show();
                }else{
                    Intent facturationaddition = new Intent(activity.getApplicationContext(), FactureAddition.class);
                    activity.startActivity(facturationaddition.putExtra("nofact", grp.nofact));
                }
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).details.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_expandable_listrow_group, null);
        }
        Group group = (Group) getGroup(groupPosition);
        String strchr = "F:" + getLeftString("    "+ group.nofact,6) + " | ";
        strchr += "Q:" + getLeftString("        "+ group.totqte,3) + " | ";
        strchr += "M:" + getLeftString("        "+ group.montant,10);

        ((CheckedTextView) convertView).setText(strchr);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public static String getLeftString(String st,int length){
        int stringlength=st.length();

        if(stringlength<=length){
            return st;
        }

        return st.substring((stringlength-length));
    }
}
