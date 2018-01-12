package com.creative.seong.app.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creative.seong.app.R;
import com.creative.seong.app.safe.ChildInfo;
import com.creative.seong.app.safe.GroupInfo;
import com.creative.seong.app.safe.SAOWriteFragment;
import com.creative.seong.app.util.AnimatedExpandableListView;
import com.creative.seong.app.util.UtilClass;

import java.util.ArrayList;
import java.util.HashMap;

public class AnyExpandableAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private static final String TAG = "AnyExpandableAdapter";

    private LayoutInflater inflater;

    private ArrayList<GroupInfo> groupInfoList;
    private ArrayList<ChildInfo> childInfoList;

    public AnyExpandableAdapter(Context context, ArrayList<GroupInfo> groupInfoList) {
        inflater = LayoutInflater.from(context);
        this.groupInfoList = groupInfoList;
    }

    public void setArrayList(ArrayList<GroupInfo> groupInfoList){
        this.groupInfoList = groupInfoList;
    }

    public ArrayList<ChildInfo> setChildInfoList(ArrayList<ChildInfo> childInfoList){
        childInfoList= this.childInfoList;
        return childInfoList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ChildInfo> productList = groupInfoList.get(groupPosition).getChildList();
        return productList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        final ChildHolder childHolder;
        final ChildInfo childInfo = (ChildInfo) getChild(groupPosition, childPosition);

        if (convertView == null) {
            childHolder = new ChildHolder();
            convertView = inflater.inflate(R.layout.safe_write_item, parent, false);
            childHolder.title = (TextView) convertView.findViewById(R.id.textView1);
            childHolder.yes_button = (TextView) convertView.findViewById(R.id.textView2);
            childHolder.no_button = (TextView) convertView.findViewById(R.id.textView3);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        childHolder.title.setText(childInfo.getName());

        childHolder.yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childInfo.setSao_value("Y");
//                childInfoList= groupInfoList.get(groupPosition).getChildList();
//                groupInfoList.get(groupPosition).setChildList(childInfoList);

                childHolder.yes_button.setTextColor(Color.parseColor("#FFFFFF"));
                childHolder.yes_button.setBackgroundResource(R.color.blue2);
                childHolder.no_button.setTextColor(Color.parseColor("#ff333333"));
                childHolder.no_button.setBackgroundResource(R.color.gary_light);
            }
        });
        childHolder.no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childInfo.setSao_value("N");
//                childInfoList= groupInfoList.get(groupPosition).getChildList();

                childHolder.no_button.setTextColor(Color.parseColor("#FFFFFF"));
                childHolder.no_button.setBackgroundResource(R.color.orange_dark);
                childHolder.yes_button.setTextColor(Color.parseColor("#ff333333"));
                childHolder.yes_button.setBackgroundResource(R.color.gary_light);
            }
        });

        if(childInfo.getSao_value().equals("Y")){
//                UtilClass.logD(TAG,"예스 "+groupPosition+","+childPosition);
                childHolder.yes_button.setTextColor(Color.parseColor("#FFFFFF"));
                childHolder.yes_button.setBackgroundResource(R.color.blue2);
                childHolder.no_button.setTextColor(Color.parseColor("#ff333333"));
                childHolder.no_button.setBackgroundResource(R.color.gary_light);
        }else{
//                UtilClass.logD(TAG,"노   "+groupPosition+","+childPosition);
                childHolder.no_button.setTextColor(Color.parseColor("#FFFFFF"));
                childHolder.no_button.setBackgroundResource(R.color.orange_dark);
                childHolder.yes_button.setTextColor(Color.parseColor("#ff333333"));
                childHolder.yes_button.setBackgroundResource(R.color.gary_light);
        }
//        SAOWriteFragment.sChildInfoMap.put(childPosition, childInfo);
//        UtilClass.logD(TAG, "map="+SAOWriteFragment.sChildInfoMap.get(childPosition).getSao_value());

        return convertView;
    }



    @Override
    public int getRealChildrenCount(int groupPosition) {
        ArrayList<ChildInfo> childInfoList = groupInfoList.get(groupPosition).getChildList();
        return childInfoList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupInfoList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groupInfoList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        GroupInfo headerInfo = (GroupInfo) getGroup(groupPosition);

        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.safe_write_itemtitle, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textView1);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.title.setText(headerInfo.getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    private static class ChildHolder {
        TextView title;
        TextView yes_button;
        TextView no_button;
    }

    private static class GroupHolder {
        TextView title;
    }

}


