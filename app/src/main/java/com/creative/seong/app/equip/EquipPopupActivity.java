package com.creative.seong.app.equip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.creative.seong.app.R;
import com.creative.seong.app.fragment.FragMenuActivity;
import com.creative.seong.app.menu.MainActivity;
import com.creative.seong.app.util.KeyValueArrayAdapter;
import com.creative.seong.app.util.UtilClass;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EquipPopupActivity extends Activity {
    private static final String TAG = "EquipPopupActivity";
    private String url = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Equip/equipGubunList/gubun=";
    private String info_url = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Equip/equipInfoList";

    private String[] gubunKeyList;
    private String[] gubunValueList;
    private String[] equipKeyList;
    private String[] equipValueList;
    String selectGubunKey="";
    String selectEquipKey="";
    String selectWorkerKey="";
    String gubun;

    @Bind(R.id.spinner1) Spinner spn_gubun;
    @Bind(R.id.spinner2) Spinner spn_equip;
    @Bind(R.id.spinner3) Spinner spn_worker;
    @Bind(R.id.textView1) TextView _user_name;

    private AQuery aq = new AQuery( this );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.equip_popup);
        ButterKnife.bind(this);
        this.setFinishOnTouchOutside(false);

        _user_name.setText(MainActivity.loginName);

        String gubunCheck = getIntent().getStringExtra("gubun");
        if(gubunCheck.equals("PSM대상설비점검")){
            gubun="P";
        }else{
            gubun="1";
        }
        getGubunData();

        spn_gubun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KeyValueArrayAdapter adapter = (KeyValueArrayAdapter) parent.getAdapter();
                selectGubunKey= adapter.getEntryValue(position);
//                UtilClass.logD("LOG", "KEY : " + adapter.getEntryValue(position));
//                UtilClass.logD("LOG", "VALUE : " + adapter.getEntry(position));
                async_progress_dialog("getEquipData");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spn_equip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KeyValueArrayAdapter adapter = (KeyValueArrayAdapter) parent.getAdapter();
                selectEquipKey= adapter.getEntryValue(position);
                UtilClass.logD("LOG", "KEY : " + adapter.getEntryValue(position));
                UtilClass.logD("LOG", "VALUE : " + adapter.getEntry(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Spinner 생성
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.worker_list, android.R.layout.simple_spinner_dropdown_item);
//		search_spi.setPrompt("구분을 선택하세요.");
        spn_worker.setAdapter(adapter);

        spn_worker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    selectWorkerKey="1";
                }else if(position==1){
                    selectWorkerKey="2";
                }else if(position==2){
                    selectWorkerKey="3";
                }else if(position==3){
                    selectWorkerKey="S";
                }else{
                    selectWorkerKey=null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }//onCreate

    public void async_progress_dialog(String callback){
        ProgressDialog dialog = ProgressDialog.show(EquipPopupActivity.this, "", "Loading...", true, false);
        dialog.setInverseBackgroundForced(false);

        if(callback.equals("getEquipData")){
            aq.ajax( info_url+"/equip_cd="+selectGubunKey, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status ) {
                    if( object != null) {
                        try {
                            equipKeyList= new String[object.getJSONArray("datas").length()];
                            equipValueList= new String[object.getJSONArray("datas").length()];
                            for(int i=0; i<object.getJSONArray("datas").length();i++){
                                equipKeyList[i]= object.getJSONArray("datas").getJSONObject(i).get("equip_cd").toString().trim();
                                equipValueList[i]= object.getJSONArray("datas").getJSONObject(i).get("equip_cd").toString().trim()
                                    +"  "+object.getJSONArray("datas").getJSONObject(i).get("equip_nm").toString().trim();
                            }

                            KeyValueArrayAdapter adapter = new KeyValueArrayAdapter(EquipPopupActivity.this, android.R.layout.simple_spinner_dropdown_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            adapter.setEntries(equipValueList);
                            adapter.setEntryValues(equipKeyList);

                            spn_equip.setPrompt("설비");
                            spn_equip.setAdapter(adapter);
                        } catch ( Exception e ) {
                            Toast.makeText(getApplicationContext(), "에러코드 Equip 2", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        UtilClass.logD(TAG,"Data is Null");
                        Toast.makeText(getApplicationContext(),"데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            } );
        }
        aq.progress(dialog).ajax(info_url, JSONObject.class, this, callback);
    }

    public void getGubunData() {
        aq.ajax( url+gubun, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status ) {
                if( object != null) {
                    try {
                        gubunKeyList= new String[object.getJSONArray("datas").length()];
                        gubunValueList= new String[object.getJSONArray("datas").length()];
                        for(int i=0; i<object.getJSONArray("datas").length();i++){
                            gubunKeyList[i]= object.getJSONArray("datas").getJSONObject(i).get("equip_cd").toString();
                            gubunValueList[i]= object.getJSONArray("datas").getJSONObject(i).get("equip_nm").toString();
                        }

                        KeyValueArrayAdapter adapter = new KeyValueArrayAdapter(EquipPopupActivity.this, android.R.layout.simple_spinner_dropdown_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter.setEntries(gubunValueList);
                        adapter.setEntryValues(gubunKeyList);

                        spn_gubun.setPrompt("구분");
                        spn_gubun.setAdapter(adapter);
                    } catch ( Exception e ) {
                        Toast.makeText(getApplicationContext(), "에러코드 Equip 1", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    UtilClass.logD(TAG,"Data is Null");
                    Toast.makeText(getApplicationContext(),"데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } );

    }

    @OnClick(R.id.textView3)
    public void closePopup() {
        finish();
    }

    @OnClick(R.id.textView2)
    public void nextDataInfo() {
        Intent intent = new Intent(getBaseContext(),FragMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", "설비점검리스트");
        intent.putExtra("selectEquipKey", selectEquipKey);
        intent.putExtra("selectWorkerKey", selectWorkerKey);
        intent.putExtra("gubun", gubun);
        startActivity(intent);
    }
}
