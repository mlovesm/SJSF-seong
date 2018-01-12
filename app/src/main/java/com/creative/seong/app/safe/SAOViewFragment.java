package com.creative.seong.app.safe;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.creative.seong.app.R;
import com.creative.seong.app.menu.MainActivity;
import com.creative.seong.app.util.UtilClass;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SAOViewFragment extends Fragment {
    private static final String TAG = "SAOViewFragment";

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2 ;

    private String userId="";
    private String info_url = MainActivity.ipAddress+MainActivity.contextPath+"/rest/Equipment/equipmentList/equipNo/";

    @Bind(R.id.top_title) TextView textTitle;

    private HashMap<String,Object> viewDataMap= new HashMap<String,Object>();

    private AQuery aq = new AQuery(getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout,null);
        ButterKnife.bind(this, view);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                UtilClass.logD(TAG, tab+"");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        textTitle.setText(getArguments().getString("title"));

        viewDataMap.put("sao_no" ,getArguments().getString("sao_no"));
        viewDataMap.put("sao_date" ,getArguments().getString("sao_date"));
        viewDataMap.put("check_work" ,getArguments().getString("check_work"));
        viewDataMap.put("mode" ,getArguments().getString("mode"));

        return view;
    }//onCreateView

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 : return new SAOWriteFragment(viewDataMap);
                case 1 : return new SAOWriteFragment2(viewDataMap);
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return UtilClass.iconText(UtilClass.drawable(getActivity(), R.drawable.ic_top_push), "현황");
                case 1 :
                    return "결과서";
                case 2 :
                    return "";
            }
            return null;
        }
    }


    @OnClick(R.id.top_home)
    public void goHome() {
        UtilClass.goHome(getActivity());
    }
}
