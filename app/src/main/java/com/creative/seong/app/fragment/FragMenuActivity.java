package com.creative.seong.app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.creative.seong.app.R;
import com.creative.seong.app.board.NoticeBoardFragment;
import com.creative.seong.app.equip.EquipMainFragment;
import com.creative.seong.app.equip.EquipPopupActivity;
import com.creative.seong.app.gear.GearMainFragment;
import com.creative.seong.app.gear.GearPopupActivity;
import com.creative.seong.app.menu.LoginActivity;
import com.creative.seong.app.menu.MainActivity;
import com.creative.seong.app.safe.SAOFragment;
import com.creative.seong.app.safe.SafeMainFragment;
import com.creative.seong.app.safe.SafePopupActivity;
import com.creative.seong.app.util.BackPressCloseSystem;
import com.creative.seong.app.util.SettingPreference;
import com.creative.seong.app.util.UtilClass;
import com.creative.seong.app.work.WorkStandardFragment;

public class FragMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "FragMenuActivity";
    private SettingPreference pref = new SettingPreference("loginData",this);
    private String title;
    private String user_code;
    private String user_nm;

    private DrawerLayout drawer;

    private FragmentManager fm;

    private BackPressCloseSystem backPressCloseSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        backPressCloseSystem = new BackPressCloseSystem(this);
        loadLoginData();
        title= getIntent().getStringExtra("title");
        UtilClass.logD(TAG,"onCreate title="+title);

        onMenuInfo(title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }//onCreate

    private void loadLoginData() {
        user_code = pref.getValue("sabun_no","");
        user_nm= pref.getValue("user_nm","");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragmentStackCount = fm.getBackStackEntryCount();
            String tag=fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
            UtilClass.logD(TAG, "count="+fragmentStackCount+", tag="+tag);
            if(tag.equals("메인")){
                backPressCloseSystem.onBackPressed();
            }else if(fragmentStackCount!=1&&(tag.equals("공지사항작성")||tag.equals("공지사항상세")||tag.equals("작업기준상세")||tag.equals("작업기준상세")||tag.equals("설비점검작성")||tag.equals("설비점검이력")
                    ||tag.equals("장비점검작성")||tag.equals("장비점검이력")||tag.equals("안전활동관찰작성")||tag.equals("안전활동관찰상세"))){
                super.onBackPressed();
            }else{
                UtilClass.logD(TAG, "피니쉬");
                finish();
            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        title= intent.getStringExtra("title");
        UtilClass.logD(TAG,"onNewIntent title="+title);
        onMenuInfo(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        BusProvider.getInstance().post(ActivityResultEvent.create(requestCode, resultCode, data));
    }

    public void onMenuInfo(String title){
        Fragment frag = null;
        Bundle bundle = new Bundle();

        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(title.equals("공지사항")){
            fragmentTransaction.replace(R.id.fragmentReplace, frag = new NoticeBoardFragment());

        }else if(title.equals("사람찾기")){
            fragmentTransaction.replace(R.id.fragmentReplace, frag = new PersonnelFragment());

        }else if(title.equals("무재해현황판")){
            fragmentTransaction.replace(R.id.fragmentReplace, frag = new WebFragment());
            bundle.putString("url", MainActivity.ipAddress+MainActivity.contextPath+"/Common/accident_view.do");

        }else if(title.equals("작업기준")){
            fragmentTransaction.replace(R.id.fragmentReplace, frag = new WorkStandardFragment());

        }else if(title.equals("설비점검리스트")){
            String gubun= getIntent().getStringExtra("gubun");
            String selectEquipKey= getIntent().getStringExtra("selectEquipKey");
            String selectWorkerKey= getIntent().getStringExtra("selectWorkerKey");

            fragmentTransaction.replace(R.id.fragmentReplace, frag = new EquipMainFragment());
            bundle.putString("selectEquipKey",selectEquipKey);
            bundle.putString("selectWorkerKey",selectWorkerKey);
            bundle.putString("url", MainActivity.ipAddress+MainActivity.contextPath+"/Equip/equipList.do?gubun="+gubun+"&equip_cd="+selectEquipKey);

        }else if(title.equals("안전활동관찰")){
            fragmentTransaction.replace(R.id.fragmentReplace, frag = new SAOFragment());

        }else if(title.equals("안전관리작성")){
            String url= getIntent().getStringExtra("url");
            String selectDaeClassKey= getIntent().getStringExtra("selectDaeClassKey");
            String selectJungClassKey= getIntent().getStringExtra("selectJungClassKey");

            fragmentTransaction.replace(R.id.fragmentReplace, frag = new SafeMainFragment());
            bundle.putString("selectDaeClassKey",selectDaeClassKey);
            bundle.putString("selectJungClassKey",selectJungClassKey);
            bundle.putString("url", MainActivity.ipAddress+ MainActivity.contextPath+"/Safe/safe_write.do?large_cd="+selectDaeClassKey+"&mid_cd="+selectJungClassKey+"&user_code="+user_code);

        }else if(title.equals("안전관리이력팝업")||title.equals("안전관리이력")){
            String selectDaeClassKey= getIntent().getStringExtra("selectDaeClassKey");
            String selectJungClassKey= getIntent().getStringExtra("selectJungClassKey");

            fragmentTransaction.replace(R.id.fragmentReplace, frag = new SafeMainFragment());
            bundle.putString("title","안전관리이력");
            bundle.putString("selectDaeClassKey",selectDaeClassKey);
            bundle.putString("selectJungClassKey",selectJungClassKey);
            bundle.putString("url", MainActivity.ipAddress+ MainActivity.contextPath+"/Safe/safe_check_history.do?large_cd="+selectDaeClassKey+"&mid_cd="+selectJungClassKey+"&user_code="+user_code);

        }else if(title.equals("장비점검")){
            String selectGearKey= getIntent().getStringExtra("selectGearKey");

            fragmentTransaction.replace(R.id.fragmentReplace, frag = new GearMainFragment());
            bundle.putString("selectGearKey",selectGearKey);
            bundle.putString("url", MainActivity.ipAddress+MainActivity.contextPath+"/Gear/gearList.do?gear_cd="+selectGearKey);

        }
        fragmentTransaction.addToBackStack(title);

        bundle.putString("title",title);
        bundle.putString("user_code",user_code);
        bundle.putString("user_nm",user_nm);

        frag.setArguments(bundle);
        fragmentTransaction.commit();
    }

    public void onFragment(Fragment fragment, Bundle bundle, String title){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.fragmentReplace, fragment);
        fragmentTransaction.addToBackStack(title);

        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }



    public void alertDialog(final String gubun){
        final AlertDialog.Builder alertDlg = new AlertDialog.Builder(FragMenuActivity.this);
        alertDlg.setTitle("알림");
        if(gubun.equals("S")){
            alertDlg.setMessage("작성하시겠습니까?");
        }else if(gubun.equals("D")){
            alertDlg.setMessage("삭제하시겠습니까?");
        }else{
            alertDlg.setMessage("로그아웃 하시겠습니까?");
        }
        // '예' 버튼이 클릭되면
        alertDlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(gubun.equals("S")){
                }else if(gubun.equals("D")){
                }else{
                    Intent logIntent = new Intent(getBaseContext(), LoginActivity.class);
                    logIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logIntent);
                }
            }
        });
        // '아니오' 버튼이 클릭되면
        alertDlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDlg.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent=null;

        if (id == R.id.nav_common||id == R.id.nav_safe||id == R.id.nav_mate) {
            return false;
        }else if (id == R.id.nav_common1) {
            intent = new Intent(getApplicationContext(),FragMenuActivity.class);
            intent.putExtra("title", "공지사항");

        } else if (id == R.id.nav_common2) {
            intent = new Intent(getApplicationContext(),FragMenuActivity.class);
            intent.putExtra("title", "사람찾기");

        } else if (id == R.id.nav_common3) {
            intent = new Intent(getApplicationContext(),FragMenuActivity.class);
            intent.putExtra("title", "무재해현황판");

        } else if (id == R.id.nav_safe1) {
            intent = new Intent(getApplicationContext(),FragMenuActivity.class);
            intent.putExtra("title", "안전활동관찰");

        } else if (id == R.id.nav_safe2) {
            intent = new Intent(getApplicationContext(),SafePopupActivity.class);
            intent.putExtra("gubun", "안전관리");
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
            return false;

        } else if (id == R.id.nav_safe3) {
            intent = new Intent(getApplicationContext(),EquipPopupActivity.class);
            intent.putExtra("gubun", "일반설비점검");
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
            return false;

        } else if (id == R.id.nav_safe4) {
            intent = new Intent(getApplicationContext(),FragMenuActivity.class);
            intent.putExtra("title", "작업기준");

        } else if (id == R.id.nav_mate1) {
            intent = new Intent(getApplicationContext(),GearPopupActivity.class);
            intent.putExtra("title", "장비점검");
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
            return false;

        } else if (id == R.id.nav_log_out) {
            alertDialog("L");
            return false;
        }else{

        }
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


}
