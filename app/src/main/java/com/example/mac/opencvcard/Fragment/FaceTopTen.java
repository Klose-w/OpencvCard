package com.example.mac.opencvcard.Fragment;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mac.opencvcard.R;
import com.example.mac.opencvcard.activity.ui.YLListView;


public class FaceTopTen extends Fragment {

    public FaceTopTen() {
        // Required empty public constructor
    }
    YLListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview=inflater.inflate(R.layout.fragment_face_top_ten, container, false);
        listView = (YLListView) rootview.findViewById(R.id.listView);
        // 不添加也有默认的头和底
        View topView=View.inflate(getActivity(),R.layout.top,null);
        listView.addHeaderView(topView);
        View bottomView=new View(getActivity().getApplicationContext());
        listView.addFooterView(bottomView);

        // 顶部和底部也可以固定最终的高度 不固定就使用布局本身的高度
        listView.setFinalBottomHeight(100);
        listView.setFinalTopHeight(600);

        listView.setAdapter(new DemoAdapter());

        //YLListView默认有头和底  处理点击事件位置注意减去
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position=position-listView.getHeaderViewsCount();
            }
        });
        return rootview;
    }

    class DemoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 20;
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if(convertView!=null&&convertView instanceof TextView){
                tv= (TextView) convertView;
            }else{
                tv=new TextView(getActivity().getApplicationContext());
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            }
            tv.setText(String.format("第一个", position));
            return tv;
        }

    }
}


