package com.creative.seong.app.safe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.creative.seong.app.R;
import com.creative.seong.app.adaptor.AnyExpandableAdapter;
import com.creative.seong.app.menu.MainActivity;
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
import java.util.HashMap;

import butterknife.ButterKnife;

public class SAOWriteFragment2 extends Fragment {
    private final String TAG = "SAOWriteFragment2";
    private String userId="";

    private String saoMInfoUrl = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoMasterInfoList/saoInfo=";

    private final String INSERT_URL = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoInfoInsert";
    private final String UPDATE_URL = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoInfoUpdate";
    private final String DELETE_URL = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Safe/saoInfoDelete/saoInfo=";


    private AnyExpandableAdapter anyAdapter;

    static EditText etData1;
    static EditText etData2;

    private String mode="";
    private String key_sao_date ="";
    private String cu_sao_date ="";
    private String sao_no ="";

    private String large_cd="10";
    private String mid_cd="";
    private String chk_cd="";

    private HashMap<String, Object> viewDataMap= new HashMap<String, Object>();

    //탭 저장용
    public static HashMap<String, Object> tabDataMap= new HashMap<String, Object>();

    //클릭위치저장
    private int groupClickPos=0;
    private int childClickPos=0;

    private AQuery aq = new AQuery(getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.safe_write02, container, false);
        ButterKnife.bind(this, view);

        etData1= (EditText) view.findViewById(R.id.editText1);
        etData2= (EditText) view.findViewById(R.id.editText2);

        mode= viewDataMap.get("mode").toString();
//        textTitle.setText(viewDataMap.get("title").toString());
//        view.findViewById(R.id.top_write).setVisibility(View.VISIBLE);

        if(mode.equals("insert")){
            userId= MainActivity.loginSabun;
//            textTitle.setText("안전활동관찰작성");

        }else if(mode.equals("update")){
//            textTitle.setText("안전활동관찰수정");
            key_sao_date = viewDataMap.get("sao_date").toString();
            sao_no = viewDataMap.get("sao_no").toString();
            async_progress_dialog("getModifySaoMInfo");

        }else{

        }
        return view;
    }//onCreateView

    public SAOWriteFragment2() {
    }

    public SAOWriteFragment2(HashMap<String, Object> viewDataMap) {
        this.viewDataMap = viewDataMap;
    }

    public void async_progress_dialog(String callback){
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Loading...", true, false);
        dialog.setInverseBackgroundForced(false);

        String asyncUrl = null;
        if(callback.equals("getModifySaoMInfo")){
            asyncUrl= saoMInfoUrl + key_sao_date +"/"+ sao_no;

        }else{

        }

        aq.progress(dialog).ajax(asyncUrl, JSONObject.class, this, callback);
    }

    public void getModifySaoMInfo(String url, JSONObject object, AjaxStatus status) throws JSONException {
        if(!object.get("count").equals(0)) {
            try {
                userId= object.getJSONArray("datas").getJSONObject(0).get("USER_ID").toString();

                etData1.setText(object.getJSONArray("datas").getJSONObject(0).get("CHECK_TXT").toString().trim());
                etData2.setText(object.getJSONArray("datas").getJSONObject(0).get("UNCHECK_TXT").toString().trim());

            } catch ( Exception e ) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "에러코드 SAO Write 6", Toast.LENGTH_SHORT).show();
            }

        }else{
            Log.d(TAG,"Data is Null");
            Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

//    @OnClick({R.id.textButton1})
//    public void alertDialogSave(){
//        if(MainActivity.loginSabun.equals(userId)){
//            alertDialog("S");
//
//        }else{
//            Toast.makeText(getActivity(),"작성자만 가능합니다.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @OnClick({R.id.textButton2})
//    public void alertDialogDelete(){
//        if(MainActivity.loginSabun.equals(userId)){
//            alertDialog("D");
//        }else{
//            Toast.makeText(getActivity(),"작성자만 가능합니다.", Toast.LENGTH_SHORT).show();
//        }
//    }

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


        WebServiceTask wst=null;
        if(mode.equals("insert")){
            wst = new WebServiceTask(WebServiceTask.POST_TASK, getActivity(), "Loading...");
        }else{
            wst = new WebServiceTask(WebServiceTask.PUT_TASK, getActivity(), "Loading...");
        }

        wst.addNameValuePair("loginSabun",MainActivity.loginSabun);

        // the passed String is the URL we will POST to
        if(mode.equals("insert")){
            wst.execute(new String[] { INSERT_URL });
        }else{
            wst.addNameValuePair("key_sao_date",key_sao_date);
            wst.addNameValuePair("key_sao_no",sao_no);
            wst.execute(new String[] { UPDATE_URL });
        }

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
