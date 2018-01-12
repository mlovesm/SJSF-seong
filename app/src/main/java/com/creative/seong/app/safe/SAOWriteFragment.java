package com.creative.seong.app.safe;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.creative.seong.app.R;
import com.creative.seong.app.adaptor.AnyExpandableAdapter;
import com.creative.seong.app.menu.MainActivity;
import com.creative.seong.app.util.AnimatedExpandableListView;
import com.creative.seong.app.util.KeyValueArrayAdapter;
import com.creative.seong.app.util.UtilClass;

import net.jcip.annotations.NotThreadSafe;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SAOWriteFragment extends Fragment {
    private final String TAG = "SAOWriteFragment";
    private String userId="";

    private String locationUrl = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/factoryList";
    private String saoLCategoryUrl = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoLCategoryList";
    private String saoMCategoryUrl = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoMCategoryList/large_cd/";
    private String saoItemUrl = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoItemList/large_cd/";

    private String saoMInfoUrl = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoMasterInfoList/saoInfo=";
    private String saoDInfoUrl = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoDInfoList/saoInfo=";

    private final String INSERT_URL = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoInfoInsert";
    private final String UPDATE_URL = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoInfoUpdate";
    private final String DELETE_URL = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoInfoDelete/saoInfo=";

    private final String PUSH_DATA_URL = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Board/sendPushData";

    private AnyExpandableAdapter anyAdapter;

    @Bind(R.id.date_button) TextView tvData1;
    @Bind(R.id.stime_button) TextView tvData2;
    @Bind(R.id.etime_button) TextView tvData3;
    @Bind(R.id.spinner1) Spinner spn_location;
    @Bind(R.id.spinner2) Spinner spn_daesang;
    @Bind(R.id.radioGroup) RadioGroup radioGroup;
    @Bind(R.id.textView1) TextView tv_writerName;

//    @Bind(R.id.top_title) TextView textTitle;
    @Bind(R.id.listView1) AnimatedExpandableListView expandableList;

    private String[] locationKeyList;
    private String[] locationValueList;
    String selectLocationKey ="";

    String selectDaesangKey ="";

    private String selectedPostionKey;  //스피너 선택된 키값
    private int selectedPostion=0;    //스피너 선택된 Row 값
    private int selectedPostion2=0;

    private String selectedRadio="1";    //라디오 선택된 라디오 값

    private String mode="";
    private String key_sao_date ="";
    private String cu_sao_date ="";
    private String sao_no ="";

    private String large_cd="10";
    private String mid_cd="";
    private String chk_cd="";
    private boolean isTime=false;

    //수정 데이터
    private String modifyDate;
    private String modifysTime="";
    private String modifyeTime="";

    private LinkedHashMap<Object, GroupInfo> groupInfoMap = new LinkedHashMap<Object, GroupInfo>();
    private ArrayList<GroupInfo> groupInfoList = new ArrayList<GroupInfo>();
    private ArrayList<ChildInfo> childInfoList;
    private ArrayList<Boolean> flagList= new ArrayList<Boolean>();

    private HashMap<String, Object> viewDataMap= new HashMap<String, Object>();

    //클릭위치저장
    private int groupClickPos=0;
    private int childClickPos=0;

    private AQuery aq = new AQuery(getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.safe_write01, container, false);
        ButterKnife.bind(this, view);

        mode= viewDataMap.get("mode").toString();
//        textTitle.setText(viewDataMap.get("title").toString());
//        view.findViewById(R.id.top_write).setVisibility(View.VISIBLE);

        async_progress_dialog("getSaoMCategoryData");

        if(mode.equals("insert")){
            view.findViewById(R.id.linear2).setVisibility(View.GONE);
            userId= MainActivity.loginSabun;
//            textTitle.setText("안전활동관찰작성");
            tv_writerName.setText(MainActivity.loginName);

            getSAOLocationData();
            tvData1.setText(UtilClass.getCurrentDate(1));
            tvData2.setText(UtilClass.getCurrentDate(3));
            tvData3.setText(UtilClass.getCurrentDate(4));
            radioGroup.check(R.id.radio1);

        }else if(mode.equals("update")){
//            textTitle.setText("안전활동관찰수정");
            key_sao_date = viewDataMap.get("sao_date").toString();
            sao_no = viewDataMap.get("sao_no").toString();
            async_progress_dialog("getModifySaoMInfo");

            String check_work= viewDataMap.get("check_work").toString();
            if(check_work.equals("1")) {
                selectedRadio="1";
                radioGroup.check(R.id.radio1);
            }else if(check_work.equals("2")) {
                selectedRadio="2";
                radioGroup.check(R.id.radio2);
            }else{
                selectedRadio="3";
                radioGroup.check(R.id.radio3);
            }
        }else{

        }

        spn_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KeyValueArrayAdapter adapter = (KeyValueArrayAdapter) parent.getAdapter();
                selectLocationKey = adapter.getEntryValue(position);
                UtilClass.logD("LOG", "KEY : " + adapter.getEntryValue(position));
                UtilClass.logD("LOG", "VALUE : " + adapter.getEntry(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        final ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.daesang_list, android.R.layout.simple_spinner_dropdown_item);
        spn_daesang.setAdapter(adapter);
        spn_daesang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    selectDaesangKey="1";
                }else if(position==1){
                    selectDaesangKey="2";
                }else if(position==2){
                    selectDaesangKey="3";
                }else{
                    selectDaesangKey=null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio1:
                        selectedRadio="1";
                        break;
                    case R.id.radio2:
                        selectedRadio="2";
                        break;
                    default:
                        break;
                }
            }
        });

        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            int lastClickedPosition = 0;
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                Log.d("DEBUG", "heading clicked="+groupPosition+","+l);
//                UtilClass.logD(TAG, "group cd="+groupInfoMap.get(groupPosition).getLarge_cd());
                mid_cd= groupInfoList.get(groupPosition).getCode();
                groupClickPos= groupPosition;

                // 선택 한 groupPosition 의 펼침/닫힘 상태 체크
//                Boolean isExpand = (!expandableList.isGroupExpanded(groupPosition));
//
//                // 이 전에 열려있던 group 닫기
//                expandableList.collapseGroupWithAnimation(lastClickedPosition);
//
//                if(isExpand){
//
//                    expandableList.expandGroupWithAnimation(groupPosition);
//                }
//                lastClickedPosition = groupPosition+groupInfoList.get(groupPosition).getChildList().size();

                //열기 체크안함
                Boolean isExpand = (expandableList.isGroupExpanded(groupPosition));
                UtilClass.logD(TAG, "isExpand="+isExpand);
                if (expandableList.isGroupExpanded(groupPosition)) {
                    expandableList.collapseGroupWithAnimation(groupPosition);
                } else {
                    expandableList.expandGroupWithAnimation(groupPosition);
                    if(!flagList.get(groupPosition)){
                        if(mode.equals("insert")){
                            async_progress_dialog("getSaoItemData");
                        }else{
                            async_progress_dialog("getModifySaoDInfo");

                        }
                    }
                    flagList.set(groupPosition, true);
                }

                return true;

            }
        });
        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                GroupInfo headerInfo = groupInfoList.get(groupPosition);
                //get the child info
                ChildInfo detailInfo =  headerInfo.getChildList().get(childPosition);
                childClickPos= childPosition;
                //display it or do something with it
                Toast.makeText(getActivity(), " Clicked on :: " + headerInfo.getName() + "/" + detailInfo.getName(), Toast.LENGTH_LONG).show();

                return false;
            }
        });

        return view;
    }//onCreateView

    public SAOWriteFragment() {
    }

    public SAOWriteFragment(HashMap<String, Object> viewDataMap) {
        this.viewDataMap = viewDataMap;
    }

    public void async_progress_dialog(String callback){
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Loading...", true, false);
        dialog.setInverseBackgroundForced(false);

        String asyncUrl = null;
        if(callback.equals("getModifySaoMInfo")){
            asyncUrl= saoMInfoUrl + key_sao_date +"/"+ sao_no;

        }else if(callback.equals("getModifySaoDInfo")){
            asyncUrl= saoDInfoUrl + key_sao_date +"/"+ sao_no +"/"+ mid_cd;

        }else if(callback.equals("getSaoMCategoryData")){
            asyncUrl= saoMCategoryUrl+large_cd;

        }else if(callback.equals("getSaoItemData")){
            asyncUrl= saoItemUrl+large_cd+"/mid_cd/"+mid_cd;

        }else{

        }

        aq.progress(dialog).ajax(asyncUrl, JSONObject.class, this, callback);
    }

    public void getSAOLocationData() {
        aq.ajax(locationUrl, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status ) {
                if( object != null) {
                    try {
                        locationKeyList = new String[object.getJSONArray("datas").length()];
                        locationValueList = new String[object.getJSONArray("datas").length()];
                        for(int i=0; i<object.getJSONArray("datas").length();i++){
                            locationKeyList[i]= object.getJSONArray("datas").getJSONObject(i).get("C001").toString();
                            if(locationKeyList[i].equals(selectedPostionKey))  selectedPostion= i;
                            locationValueList[i]= object.getJSONArray("datas").getJSONObject(i).get("C002").toString();
                        }

                        KeyValueArrayAdapter adapter = new KeyValueArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter.setEntries(locationValueList);
                        adapter.setEntryValues(locationKeyList);

                        spn_location.setPrompt("장소");
                        spn_location.setAdapter(adapter);
                        spn_location.setSelection(selectedPostion);
                    } catch ( Exception e ) {
                        Toast.makeText(getActivity(),"에러코드 SAO Write 1", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    UtilClass.logD(TAG,"Data is Null");
                    Toast.makeText(getActivity(),"데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } );
    }

    public void getSaoMCategoryData(String url, JSONObject object, AjaxStatus status) throws JSONException {
        if(!object.get("count").equals(0)) {
            try {
                GroupInfo headerInfo = null;
                groupInfoList= new ArrayList<GroupInfo>();
                for(int i=0; i<object.getJSONArray("datas").length();i++){
                    flagList.add(false);

                    headerInfo = new GroupInfo();
                    headerInfo.setCode(object.getJSONArray("datas").getJSONObject(i).get("mid_cd").toString());
                    headerInfo.setName(object.getJSONArray("datas").getJSONObject(i).get("mid_nm").toString());
                    groupInfoList.add(headerInfo);

                    groupInfoMap.put(i, headerInfo);

                    anyAdapter = new AnyExpandableAdapter(getActivity(), groupInfoList);
                    expandableList.setAdapter(anyAdapter);

                }

            } catch ( Exception e ) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "에러코드 SAO Write 2", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d(TAG,"Data is Null");
            Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getSaoItemData(String url, JSONObject object, AjaxStatus status) throws JSONException {
        if(!object.get("count").equals(0)) {
            try {
                childInfoList= new ArrayList<ChildInfo>();
                for(int i=0; i<object.getJSONArray("datas").length();i++){
                    ChildInfo childInfo = new ChildInfo();
                    childInfo.setMid_cd(object.getJSONArray("datas").getJSONObject(i).get("mid_cd").toString());
                    childInfo.setChk_cd(object.getJSONArray("datas").getJSONObject(i).get("chk_cd").toString());
                    childInfo.setName(object.getJSONArray("datas").getJSONObject(i).get("chk_nm").toString());
                    childInfo.setSao_value("N");

                    childInfoList.add(childInfo);
                    groupInfoList.get(groupClickPos).setChildList(childInfoList);
                }

            } catch ( Exception e ) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "에러코드 SAO Write 3", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d(TAG,"Data is Null");
            Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getModifySaoMInfo(String url, JSONObject object, AjaxStatus status) throws JSONException {
        if(!object.get("count").equals(0)) {
            try {
                userId= object.getJSONArray("datas").getJSONObject(0).get("USER_ID").toString();
                tv_writerName.setText(object.getJSONArray("datas").getJSONObject(0).get("USER_NM").toString());
                if(MainActivity.loginSabun.equals(userId)){
                }else{
//                       getActivity().findViewById(R.id.layout_bottom).setVisibility(View.GONE);
                }
                selectedPostionKey = object.getJSONArray("datas").getJSONObject(0).get("FACTORY_CD").toString();
                selectedPostion2 = Integer.parseInt(object.getJSONArray("datas").getJSONObject(0).get("CHECK_VIEW").toString());
                tvData1.setText(object.getJSONArray("datas").getJSONObject(0).get("SAO_DATE").toString().trim());
                tvData2.setText(object.getJSONArray("datas").getJSONObject(0).get("CHECK_START").toString().trim());
                tvData3.setText(object.getJSONArray("datas").getJSONObject(0).get("CHECK_END").toString().trim());

                modifyDate= tvData1.getText().toString();
                modifysTime= tvData2.getText().toString();
                modifyeTime= tvData3.getText().toString();
            } catch ( Exception e ) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "에러코드 SAO Write 4", Toast.LENGTH_SHORT).show();
            }
            getSAOLocationData();
            spn_daesang.setSelection(selectedPostion2-1);
        }else{
            Log.d(TAG,"Data is Null");
            Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getModifySaoDInfo(String url, JSONObject object, AjaxStatus status) throws JSONException {
        if(!object.get("count").equals(0)) {
            try {
                childInfoList= new ArrayList<ChildInfo>();
                for(int i=0; i<object.getJSONArray("datas").length();i++){
                    ChildInfo childInfo = new ChildInfo();
                    childInfo.setMid_cd(object.getJSONArray("datas").getJSONObject(i).get("MID_CD").toString());
                    childInfo.setChk_cd(object.getJSONArray("datas").getJSONObject(i).get("CHK_CD").toString());
                    childInfo.setName(object.getJSONArray("datas").getJSONObject(i).get("CHK_NM").toString());
                    childInfo.setSao_value(object.getJSONArray("datas").getJSONObject(i).get("SAO_VALUE").toString());

                    childInfoList.add(childInfo);
                    groupInfoList.get(groupClickPos).setChildList(childInfoList);
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "에러코드 CheckWrite 5", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d(TAG,"Data is Null");
            Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //날짜설정
    @OnClick(R.id.date_button)
    public void getDateDialog() {
        getDialog("D");
    }

    //시간설정
    @OnClick(R.id.stime_button)
    public void getTimeDialog() {
        isTime=true;
        getDialog("ST");
    }

    @OnClick(R.id.etime_button)
    public void getTimeDialog2() {
        isTime=false;
        getDialog("ET");
    }

    public void getDialog(String gubun) {
        int year, month, day, hour, minute, plus_hour, plus_minute;

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.MINUTE, 30);
        plus_minute = calendar.get(Calendar.MINUTE);
        UtilClass.logD(TAG, "min"+minute);
        if(minute > 29){
            if(hour==23){
                plus_hour= 0;
            }else{
                plus_hour= hour+1;
            }
        }else{
            plus_hour= hour;
        }
        UtilClass.logD(TAG, "hour"+hour);
        UtilClass.logD(TAG, "plus_hour"+plus_hour);

        if(mode.equals("insert")){
            if(gubun.equals("D")){
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), date_listener, year, month, day);
                dialog.show();
            }else if(gubun.equals("ST")){
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), time_listener, hour, minute, false);
                dialog.show();
            }else{
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), time_listener, plus_hour, plus_minute, false);
                dialog.show();
            }
        }else{
            String[] dateArr = modifyDate.split("[.]");
            String[] sTimeArr = modifysTime.split(":");
            String[] eTimeArr = modifyeTime.split(":");

            if(gubun.equals("D")){
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), date_listener, Integer.parseInt(dateArr[0]),
                        Integer.parseInt(dateArr[1])-1, Integer.parseInt(dateArr[2]));
                dialog.show();
            }else if(gubun.equals("ST")){
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), time_listener, Integer.parseInt(sTimeArr[0]), Integer.parseInt(sTimeArr[1]), false);
                dialog.show();
            }else{
                TimePickerDialog dialog = new TimePickerDialog(getActivity(), time_listener, Integer.parseInt(eTimeArr[0]), Integer.parseInt(eTimeArr[1]), false);
                dialog.show();
            }
        }

    }

    private DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//            Toast.makeText(getActivity(), year + "년" + (monthOfYear+1) + "월" + dayOfMonth +"일", Toast.LENGTH_SHORT).show();
            String month= UtilClass.addZero(monthOfYear+1);
            String day= UtilClass.addZero(dayOfMonth);

            tvData1.setText(year+"."+month+"."+day);
        }
    };

    private TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 설정버튼 눌렀을 때
//            Toast.makeText(getActivity(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
            String hour= UtilClass.addZero(hourOfDay);
            String min= UtilClass.addZero(minute);
            if(isTime){
                tvData2.setText(hour+":"+min);
            }else{
                tvData3.setText(hour+":"+min);
            }
        }
    };

    @OnClick({R.id.textButton1})
    public void alertDialogSave(){
        if(MainActivity.loginSabun.equals(userId)){
            alertDialog("S");
        }else{
            Toast.makeText(getActivity(),"작성자만 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.textButton2})
    public void alertDialogDelete(){
        if(MainActivity.loginSabun.equals(userId)){
            alertDialog("D");
        }else{
            Toast.makeText(getActivity(),"작성자만 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void alertDialog(final String gubun){
        final AlertDialog.Builder alertDlg = new AlertDialog.Builder(getActivity());
        alertDlg.setTitle("알림");
        if(gubun.equals("S")){
            alertDlg.setMessage("작성하시겠습니까?");
        }else if(gubun.equals("D")){
            alertDlg.setMessage("삭제하시겠습니까?");
        }else{
            alertDlg.setMessage("전송하시겠습니까?");
        }
        // '예' 버튼이 클릭되면
        alertDlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(gubun.equals("S")){
                    postData();
                }else if(gubun.equals("D")){
                    deleteData();
                }else{
                    onPushData();
                }
            }
        });
        // '아니오' 버튼이 클릭되면
        alertDlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();  // AlertDialog를 닫는다.
            }
        });
        alertDlg.show();
    }

    //삭제
    public void deleteData() {
        WebServiceTask wst = new WebServiceTask(WebServiceTask.DELETE_TASK, getActivity(), "Loading...");
        wst.addNameValuePair("key_sao_date", key_sao_date);
        wst.addNameValuePair("sao_no",sao_no);

        wst.execute(new String[] { DELETE_URL + key_sao_date +"/"+sao_no  });
    }

    //작성,수정
    public void postData() {
        String sao_date = tvData1.getText().toString();
        String chk_stime = tvData2.getText().toString();
        String chk_etime = tvData3.getText().toString();
        String location_cd = selectLocationKey;
        String daesang_cd = selectDaesangKey;

        WebServiceTask wst=null;
        if(mode.equals("insert")){
            wst = new WebServiceTask(WebServiceTask.POST_TASK, getActivity(), "Loading...");
        }else{
            wst = new WebServiceTask(WebServiceTask.PUT_TASK, getActivity(), "Loading...");
        }

        wst.addNameValuePair("loginSabun",MainActivity.loginSabun);
        wst.addNameValuePair("sao_date",sao_date);
        wst.addNameValuePair("location_cd",location_cd);
        wst.addNameValuePair("daesang_cd",daesang_cd);
        wst.addNameValuePair("chk_stime",chk_stime);
        wst.addNameValuePair("chk_etime",chk_etime);
        wst.addNameValuePair("check_work",selectedRadio);
        wst.addNameValuePair("large_cd",large_cd);
        wst.addNameValuePair("check_txt",SAOWriteFragment2.etData1.getText().toString());
        wst.addNameValuePair("uncheck_txt",SAOWriteFragment2.etData2.getText().toString());

        for ( Object key : groupInfoMap.keySet() ) {
//            UtilClass.logD(TAG ,"방법1) key : " + key +" / getName : " + groupInfoMap.get(key).getName()
//                    +",getChildList="+groupInfoMap.get(key).getChildList().size());

            wst.addNameValuePair("mid_cd", groupInfoMap.get(key).getCode());
            wst.addNameValuePair("childSizeList", groupInfoMap.get(key).getChildList().size()+"");
            if(groupInfoMap.get(key).getChildList().size()>0){
                for (int i=0; i<groupInfoMap.get(key).getChildList().size(); i++){
                    wst.addNameValuePair("chk_cd"+key, groupInfoMap.get(key).getChildList().get(i).getChk_cd());
                    wst.addNameValuePair("sao_value"+key, groupInfoMap.get(key).getChildList().get(i).getSao_value());
                }
            }
        }

        // the passed String is the URL we will POST to
        if(mode.equals("insert")){
            wst.execute(new String[] { INSERT_URL });
        }else{
            wst.addNameValuePair("key_sao_date",key_sao_date);
            wst.addNameValuePair("key_sao_no",sao_no);
            wst.execute(new String[] { UPDATE_URL });
        }

    }

    //푸시 전송
    public void onPushData() {
        WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, getActivity(), "Loading...");

        wst.addNameValuePair("writer_sabun",MainActivity.loginSabun);
        wst.addNameValuePair("writer_name",MainActivity.loginName);

        wst.execute(new String[] { PUSH_DATA_URL });

    }

    //작성 완료
    public void handleResponse(String response) {
        UtilClass.logD(TAG,"response="+response);

        try {
            JSONObject jso = new JSONObject(response);
            String status= jso.get("status").toString();

            if(status.equals("success")){
                getActivity().onBackPressed();
//                Intent intent = new Intent(getActivity(),FragMenuActivity.class);
//                intent.putExtra("title", "점검관리");
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
            }else if(status.equals("successOnPush")){

            }else{
                Toast.makeText(getActivity(), "작업에 실패하였습니다.",Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            Toast.makeText(getActivity(), "handleResponse SafeWrite",Toast.LENGTH_LONG).show();
        }

    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private class WebServiceTask extends AsyncTask<String, Integer, String> {

        public static final int POST_TASK = 1;
        public static final int GET_TASK = 2;
        public static final int PUT_TASK = 3;
        public static final int DELETE_TASK = 4;

        private static final String TAG = "WebServiceTask";

        // connection timeout, in milliseconds (waiting to connect)
        private static final int CONN_TIMEOUT = 5000;

        // socket timeout, in milliseconds (waiting for data)
        private static final int SOCKET_TIMEOUT = 10000;

        private int taskType = GET_TASK;
        private Context mContext = null;
        private String processMessage = "Processing...";

        private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        private ProgressDialog pDlg = null;

        public WebServiceTask(int taskType, Context mContext, String processMessage) {

            this.taskType = taskType;
            this.mContext = mContext;
            this.processMessage = processMessage;
        }

        public void addNameValuePair(String name, String value) {
            params.add(new BasicNameValuePair(name, value));
        }

        private void showProgressDialog() {
            pDlg = new ProgressDialog(mContext);
            pDlg.setMessage(processMessage);
            pDlg.setProgressDrawable(mContext.getWallpaper());
            pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDlg.setCancelable(false);
            pDlg.show();
        }

        @Override
        protected void onPreExecute() {
            hideKeyboard();
            showProgressDialog();
        }

        protected String doInBackground(String... urls) {
            String url = urls[0];
            String result = "";
            HttpResponse response = doResponse(url);

            if (response == null) {
                return result;
            } else {
                try {
                    result = inputStreamToString(response.getEntity().getContent());

                } catch (IllegalStateException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            handleResponse(response);
            pDlg.dismiss();
        }

        // Establish connection and socket (data retrieval) timeouts
        private HttpParams getHttpParams() {
            HttpParams htpp = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

            return htpp;
        }

        @NotThreadSafe
        class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
            public static final String METHOD_NAME = "DELETE";

            public String getMethod() {
                return METHOD_NAME;
            }
            public HttpDeleteWithBody(final String uri) {
                super();
                setURI(URI.create(uri));
            }
            public HttpDeleteWithBody(final URI uri) {
                super();
                setURI(uri);
            }
            public HttpDeleteWithBody() {
                super();
            }
        }

        private HttpResponse doResponse(String url) {
            HttpClient httpclient = new DefaultHttpClient(getHttpParams());
            HttpResponse response = null;

            try {
                switch (taskType) {

                    case POST_TASK:
                        HttpPost httppost = new HttpPost(url);
                        // Add parameters
                        httppost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
                        response= httpclient.execute(httppost);
                        break;
                    case GET_TASK:
                        HttpGet httpget = new HttpGet(url);
                        response= httpclient.execute(httpget);
                        break;
                    case PUT_TASK:
                        HttpPut httpput = new HttpPut(url);
                        // Add parameters
                        httpput.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
                        response= httpclient.execute(httpput);
                        break;
                    case DELETE_TASK:
                        HttpDeleteWithBody httpdel = new HttpDeleteWithBody(url);
                        // Add parameters
                        httpdel.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
                        response= httpclient.execute(httpdel);
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

            return response;
        }

        private String inputStreamToString(InputStream is) {

            String line = "";
            StringBuilder total = new StringBuilder();

            // Wrap a BufferedReader around the InputStream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                // Read response until the end
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

            // Return full string
            return total.toString();
        }

    }//WebServiceTask

//    @OnClick(R.id.top_home)
//    public void goHome() {
//        UtilClass.goHome(getActivity());
//    }

}
