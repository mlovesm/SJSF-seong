package com.creative.seong.app.safe;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.creative.seong.app.R;
import com.creative.seong.app.adaptor.SAOAdapter;
import com.creative.seong.app.menu.MainActivity;
import com.creative.seong.app.util.UtilClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SAOFragment extends Fragment {
    private static final String TAG = "SAOFragment";
    private String url = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoInfoList/";

    private ArrayList<HashMap<String,Object>> arrayList;
    private SAOAdapter mAdapter;
    @Bind(R.id.listView1) ListView listView;
    @Bind(R.id.top_title) TextView textTitle;

    @Bind(R.id.search_top) LinearLayout layout;
    @Bind(R.id.textButton1) TextView tv_button1;
    @Bind(R.id.textButton2) TextView tv_button2;
    private String gubun;
    private boolean flag= false;

    private AQuery aq = new AQuery(getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.safe_list, container, false);
        ButterKnife.bind(this, view);

        textTitle.setText(getArguments().getString("title"));
        view.findViewById(R.id.top_write).setVisibility(View.VISIBLE);
        view.findViewById(R.id.top_search).setVisibility(View.VISIBLE);

        tv_button1.setText(UtilClass.getCurrentDate(2));
        tv_button2.setText(UtilClass.getCurrentDate(1));

//        layout.setVisibility(View.GONE);

        async_progress_dialog("getBoardInfo");

        listView.setOnItemClickListener(new ListViewItemClickListener());

        return view;
    }//onCreateView

    public void async_progress_dialog(String callback){
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Loading...", true, false);
        dialog.setInverseBackgroundForced(false);

        aq.progress(dialog).ajax(url+tv_button1.getText()+"/"+tv_button2.getText(), JSONObject.class, this, callback);
    }

    public void getBoardInfo(String url, JSONObject object, AjaxStatus status) throws JSONException {
        arrayList = new ArrayList<>();
        if(!object.get("count").equals(0)) {
            try {
                arrayList.clear();
                for(int i=0; i<object.getJSONArray("datas").length();i++){
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("key",object.getJSONArray("datas").getJSONObject(i).get("SAO_NO").toString());
                    hashMap.put("data1",object.getJSONArray("datas").getJSONObject(i).get("SAO_DATE").toString());
                    hashMap.put("data2",object.getJSONArray("datas").getJSONObject(i).get("FAC_NM").toString().trim());
                    hashMap.put("data3",object.getJSONArray("datas").getJSONObject(i).get("CHECK_START").toString()
                            +" ~ "+object.getJSONArray("datas").getJSONObject(i).get("CHECK_END").toString());
                    hashMap.put("data4",object.getJSONArray("datas").getJSONObject(i).get("USER_NM").toString());
                    hashMap.put("data5",object.getJSONArray("datas").getJSONObject(i).get("CHECK_WORK").toString());
                    arrayList.add(hashMap);
                }

            } catch ( Exception e ) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "에러코드 SAO 1", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d(TAG,"Data is Null");
            Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }
        mAdapter = new SAOAdapter(getActivity(), arrayList);
        listView.setAdapter(mAdapter);
    }

    //날짜설정
    @OnClick(R.id.textButton1)
    public void getDateDialog() {
        getDialog();
        gubun="S";
    }
    @OnClick(R.id.textButton2)
    public void getDateDialog2() {
        getDialog();
        gubun="E";
    }

    public void getDialog() {
        int year, month, day, hour, minute;

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), date_listener, year, month, day);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month= UtilClass.addZero(monthOfYear+1);
            String day= UtilClass.addZero(dayOfMonth);
            String date= year+"."+month+"."+day;
            if(gubun.equals("S")){
                tv_button1.setText(date);
            }else{
                tv_button2.setText(date);
            }

        }
    };


    @OnClick(R.id.top_search)
    public void getSearch() {
        if(layout.getVisibility()==View.GONE){
            layout.setVisibility(View.VISIBLE);
            layout.setFocusable(true);
        }else{
            layout.setVisibility(View.GONE);
        }
    }

    //해당 검색값 데이터 조회
    @OnClick(R.id.imageView1)
    public void onSearchColumn() {
        //검색하면 키보드 내리기
//        InputMethodManager imm= (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
//
//        if(et_search.getText().toString().length()==0){
//            Toast.makeText(getActivity(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
//        }else{
//            async_progress_dialog("getPersonnelList");
//        }
        async_progress_dialog("getBoardInfo");

    }

    @OnClick(R.id.top_home)
    public void goHome() {
        UtilClass.goHome(getActivity());
    }

    @OnClick(R.id.top_write)
    public void getWriteBoard() {
        Fragment frag = new SAOViewFragment();
        Bundle bundle = new Bundle();

        bundle.putString("title","안전활동관찰작성");
        bundle.putString("mode","insert");
        frag.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentReplace, frag);
        fragmentTransaction.addToBackStack("안전활동관찰작성");
        fragmentTransaction.commit();
    }

    //ListView의 item (상세)
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Fragment frag = null;
            Bundle bundle = new Bundle();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentReplace, frag = new SAOViewFragment());
            bundle.putString("title","안전활동관찰상세");
            String key= arrayList.get(position).get("key").toString();
            String sao_date= arrayList.get(position).get("data1").toString();
            bundle.putString("sao_no", key);
            bundle.putString("sao_date", sao_date);
            bundle.putString("check_work", arrayList.get(position).get("data5").toString());
            bundle.putString("mode", "update");

            frag.setArguments(bundle);
            fragmentTransaction.addToBackStack("안전활동관찰상세");
            fragmentTransaction.commit();
        }
    }

}
