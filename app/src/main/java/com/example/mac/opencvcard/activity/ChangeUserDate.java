package com.example.mac.opencvcard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.global.AbConstant;
import com.ab.view.wheel.AbNumericWheelAdapter;
import com.ab.view.wheel.AbStringWheelAdapter;
import com.ab.view.wheel.AbWheelUtil;
import com.ab.view.wheel.AbWheelView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mac.opencvcard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeUserDate extends AbActivity {

    RadioGroup Zrg;
    TextView Zhome;
    EditText Zqq;
    EditText Zlike;
    com.ab.view.wheel.AbWheelView Zgrade;
    com.ab.view.wheel.AbWheelView Zage;
    String xueli="本科";
    String age;
    String grade;
    String home;
    String qq;
    String like;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private View mDataView1 = null;
    private TextView ZdateTextView = null;
    private View mTimeView1=null;
    private TextView ZtimeTextView=null;
    private View mHomeView1=null;

    private String[] pro={"北京","天津","上海","重庆","河北省","山西省","辽宁省","吉林省","黑龙江",
            "江苏省","浙江省","安徽省","福建省","江西省","山东省","河南省","湖北省","湖南省","广东省",
            "海南省","四川省","贵州省","云南省","陕西省","甘肃省","青海省","台湾省","内蒙古","广西",
            "西藏","宁夏","新疆","香港","澳门"};
    private String[][] city={
            {"北京市"},
            {"天津市"},
            {"上海市"},
            {"重庆市"},
            {"石家庄","唐山","秦皇岛","邯郸","邢台","保定","张家口","承德","沧州","廊坊","衡水"},
            {"太原","大同","阳泉","长治","晋城","朔州","晋中","运城","忻州","临汾","吕梁"},
            {"沈阳","大连","鞍山","抚顺","本溪","丹东","锦州","营口","阜新","辽阳","盘锦","铁岭","朝阳","葫芦岛"},
            {"长春","吉林","四平","辽源","通化","白山","松原","白城","延边"},
            {"哈尔滨","齐齐哈尔","鸡西","鹤岗","双鸭山","大庆","伊春","佳木斯","七台河","牡丹江","黑河","绥化","大兴安岭"},
            {"南京","无锡","徐州","常州","苏州","南通","连云港","淮安","盐城","扬州","镇江","泰州","宿迁"},
            {"杭州","宁波","温州","嘉兴","湖州","绍兴","金华","衢州","舟山","台州","丽水"},
            {"合肥","芜湖","蚌埠","淮南","马鞍山","淮北","铜陵","安庆","黄山","滁州","阜阳" ,"宿州","巢湖","六安","亳州","池州","宣城"},
            {"福州","厦门","莆田","三明","泉州","漳州","南平","龙岩","宁德"},
            {"南昌","景德镇","萍乡","九江","新余","鹰潭","赣州","吉安","宜春","抚州","上饶"},
            {"济南","青岛","淄博","枣庄","东营","烟台","潍坊","威海","济宁","泰安","日照","莱芜","临沂","德州","聊城","滨州","菏泽"},
            {"郑州","开封","洛阳","平顶山","焦作","鹤壁","新乡","安阳","濮阳","许昌","漯河","三门峡","南阳","商丘","信阳","周口","驻马店"},
            {"武汉","黄石","襄樊","十堰","荆州","宜昌","荆门","鄂州","孝感","黄冈","咸宁","随州","恩施"},
            {"长沙","株洲","湘潭","衡阳","邵阳","岳阳","常德","张家界","益阳","郴州","永州","怀化","娄底","湘西"},
            {"广州","深圳","珠海","汕头","韶关","佛山","江门","湛江","茂名","肇庆","惠州","梅州","汕尾","河源","阳江","清远","东莞","中山","潮州","揭阳","云浮"},
            {"海口","三亚"},
            {"成都","自贡","攀枝花","泸州","德阳","绵阳","广元","遂宁","内江","乐山","南充","宜宾","广安","达州","眉山","雅安","巴中","资阳","阿坝","甘孜 ","凉山"},
            {"贵阳","六盘水","遵义","安顺","铜仁","毕节","黔西南","黔东南","黔南"},
            {"昆明","曲靖","玉溪","保山","昭通","丽江","普洱","临沧","文山","红河","西双版纳","楚雄","大理","德宏","怒江","迪庆"},
            {"西安","铜川","宝鸡","咸阳","渭南","延安","汉中","榆林","安康","商洛"},
            {"兰州","嘉峪关","金昌","白银","天水","武威","张掖","平凉","酒泉","庆阳","定西","陇南","临夏","甘南"},
            {"西宁","海东","海北","黄南","海南","果洛","玉树","海西"},
            {"台北","高雄","基隆","台中","台南","新竹","嘉义"},
            {"呼和浩特","包头","乌海","赤峰","通辽","鄂尔多斯","呼伦贝尔","巴彦淖尔","乌兰察布","兴安","锡林郭勒","阿拉善"},
            {"南宁","柳州","桂林","梧州","北海","防城港","钦州","贵港","玉林","百色","贺州","河池","来宾","崇左"},
            {"拉萨","昌都","山南","日喀则","那曲","阿里","林芝"},
            {"银川","石嘴山","吴忠","固原","中卫"},
            {"乌鲁木齐","克拉玛依","吐鲁番","哈密","和田","阿克苏","喀什","克孜勒苏柯尔克孜","巴音郭楞蒙古","昌吉","博尔塔拉蒙古","伊犁哈萨克","塔城","阿勒泰"},
            {"香港"},
            {"澳门"}
    };
    private String[] col={"农学系","植物保护学院","园艺学院","动物科技学院","动物医学","林学院","风景园林艺术学院","资源环境学院",
            "水利与建筑工程学院","机械与电子工程学院","信息工程学院","食品科学与工程学院","葡萄酒学院","生命科学学院",
            "理学院","经济管理学院","人文社会发展学院","外语系","创新学院"};
    private String[][] cla={
            {"农学系","植物科学","种子科学","未知"},
            {"园艺","设施农业科学","未知"},
            {"植物保护","制药工程","未知"},
            {"动物科学","草业科学","水产养殖学","未知"},
            {"动物医学"},
            {"林学","森林保护","林产化工","木材科学与工程","未知"},
            {"风景园林","园林","环境设计","地理信息科学","人文地理与城乡规划","水土保持与荒漠化防治","未知"},
            {"资源环境科","环境科学","环境工程","地理信息科学","人文地理与城乡规划","水土保持与荒漠化防治","未知"},
            {"农业水利工程","水文与水资源工程","水利水电工程","土木工程","能源与动力工程","电气工程及其自动化","未知"},
            {"机械设计制造及其自动化","农业机械化及其自动化","机械电子工程","电子信息工程","车辆工程","未知"},
            {"计算机科学与技术","软件工程","信息管理","电子商务","未知"},
            {"食品科学与工程","食品质量与安全","未知"},
            {"葡萄酒"},
            {"生物科学","生物技术","生物工程","未知"},
            {"应用化学","信息与计算科学","未知"},
            {"会计学","金融学","保险学","旅游管理","市场营销","国际经济与贸易","工商管理","土地资源","农林经济","经济学","未知"},
            {"法学","社会学","公共事业管理","社会工作","未知"},
            {"外语系"},
            {"生物技术基地班", "生物工程基地班", "创新实验班","未知"},
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_change_user_date);
        //android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);
        this.setTitleText("修改资料");
        this.setTitleTextMargin(10, 0, 0, 0);
        logoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Zrg=(RadioGroup)findViewById(R.id.zi_xw);
        Zrg.check(R.id.bt_bk);
        Zrg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) ChangeUserDate.this.findViewById(group.getCheckedRadioButtonId());
                xueli = rb.getText().toString();
            }
        });
        Zhome=(TextView)findViewById(R.id.zi_home);
        Zhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(AbConstant.DIALOGBOTTOM,mHomeView1,40);
            }
        });
        ZtimeTextView=(TextView)findViewById(R.id.TimeText);
        ZtimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(AbConstant.DIALOGBOTTOM,mTimeView1,40);
            }
        });
        ZdateTextView=(TextView)findViewById(R.id.DateTimeText);
        ZdateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(AbConstant.DIALOGBOTTOM,mDataView1,40);
            }
        });
        //Zage=(com.ab.view.wheel.AbWheelView)findViewById(R.id.zi_age);
        //Zgrade=(com.ab.view.wheel.AbWheelView)findViewById(R.id.zi_grade);
        Zqq=(EditText)findViewById(R.id.zi_qq);
        Zlike=(EditText)findViewById(R.id.zi_like);
        sp=getSharedPreferences("person",MODE_WORLD_READABLE);
        ed=sp.edit();
        chushi();
        mDataView1 = mInflater.inflate(R.layout.choose_three, null);
        initWheelDate(mDataView1,ZdateTextView);
        mTimeView1=mInflater.inflate(R.layout.choose_one,null);
        initWheelData1(mTimeView1);
        mHomeView1=mInflater.inflate(R.layout.choose_two,null);
        initWheelData2(mHomeView1);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }
    public void chushi(){

        Zqq.setText(sp.getString("pqq", ""));
        Zlike.setText(sp.getString("plike", ""));
        Zhome.setText(sp.getString("pprovice", ""));
        ZtimeTextView.setText(sp.getString("pgrade",""));
        ZdateTextView.setText(sp.getString("page",""));
    }

    public void wancheng(View view) {
        ed.putString("pxueligrade",xueli);
        ed.putString("pqq",Zqq.getText().toString());
        ed.putString("plike",Zlike.getText().toString());
        ed.putString("pprovice",Zhome.getText().toString());
        ed.putString("page", ZdateTextView.getText().toString());
        Log.e("ff",ZdateTextView.getText().toString());
        ed.putString("pgrade", ZtimeTextView.getText().toString());
        ed.commit();
        Intent intent=new Intent();
        setResult(4, intent);
        finish();
    }
    public void initWheelData2(View mDataView1){
        final AbWheelView mWheelView1 = (AbWheelView)mDataView1.findViewById(R.id.wheelView1);
        final AbWheelView mWheelView2 = (AbWheelView)mDataView1.findViewById(R.id.wheelView2);
        //mWheelView1.setAdapter(new AbNumericWheelAdapter(40, 190));

        List<String> list= Arrays.asList(pro);
        mWheelView1.setAdapter(new AbStringWheelAdapter(list));
        mWheelView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int index = mWheelView1.getCurrentItem();
                List<String> list1= Arrays.asList(city[index]);
                mWheelView2.setAdapter(new AbStringWheelAdapter(list1));
                return false;
            }
        });
        // ��ѭ������
        mWheelView1.setCyclic(true);
        // �������

        // ��ʼ��ʱ��ʾ������
        mWheelView1.setCurrentItem(40);
        mWheelView1.setValueTextSize(35);
        mWheelView1.setLabelTextSize(35);
        mWheelView1.setLabelTextColor(0x80000000);
        mWheelView1.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelView2.setCyclic(true);
        // �������

        // ��ʼ��ʱ��ʾ������
        mWheelView2.setCurrentItem(40);
        mWheelView2.setValueTextSize(35);
        mWheelView2.setLabelTextSize(35);
        mWheelView2.setLabelTextColor(0x80000000);
        mWheelView2.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        Button okBtn = (Button)mDataView1.findViewById(R.id.okBtn);
        Button cancelBtn = (Button)mDataView1.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                removeDialog(1);
                int index = mWheelView1.getCurrentItem();
                String val = mWheelView1.getAdapter().getItem(index);
                int d2=mWheelView2.getCurrentItem();
                val=val+"-"+mWheelView2.getAdapter().getItem(d2);
                Zhome.setText(val);
            }

        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                removeDialog(1);
            }

        });
    }
    public void initWheelData1(View mDataView1){
        final AbWheelView mWheelView1 = (AbWheelView)mDataView1.findViewById(R.id.wheelView1);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        mWheelView1.setAdapter(new AbNumericWheelAdapter(year-8, year));
        // ��ѭ������
        mWheelView1.setCyclic(true);

        // �������
        mWheelView1.setLabel("级");
        // ��ʼ��ʱ��ʾ������
        mWheelView1.setCurrentItem(40);
        mWheelView1.setValueTextSize(35);
        mWheelView1.setLabelTextSize(35);
        mWheelView1.setLabelTextColor(0x80000000);
        mWheelView1.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));

        Button okBtn = (Button)mDataView1.findViewById(R.id.okBtn);
        Button cancelBtn = (Button)mDataView1.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                removeDialog(1);
                int index = mWheelView1.getCurrentItem();
                String val = mWheelView1.getAdapter().getItem(index);
                ZtimeTextView.setText(val);
            }

        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                removeDialog(1);
            }

        });
    }
    public void initWheelDate(View mDateView,TextView mText){
        //������ʱ��ѡ����
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        int kk=year;
        final AbWheelView mWheelViewY = (AbWheelView)mDateView.findViewById(R.id.wheelView1);
        final AbWheelView mWheelViewM = (AbWheelView)mDateView.findViewById(R.id.wheelView2);
        final AbWheelView mWheelViewD = (AbWheelView)mDateView.findViewById(R.id.wheelView3);
        Button okBtn = (Button)mDateView.findViewById(R.id.okBtn);
        Button cancelBtn = (Button)mDateView.findViewById(R.id.cancelBtn);


        mWheelViewY.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewM.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewD.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        if(!sp.getString("page","").equals(""))
        {
            String ss=sp.getString("page","");
            Log.e("fd",ss);
            year=Integer.parseInt(ss.substring(0, 4));
            Log.e("y",year+"");
            month=Integer.parseInt(ss.substring(5,7));
            Log.e("m",month+"");
            day=Integer.parseInt(ss.substring(8,10));
        }
        AbWheelUtil.initWheelDatePicker(this, //AbActivity����
                mText,//��ʾѡ������TextVIew
                mWheelViewY,//ѡ���������
                mWheelViewM, //ѡ���µ�����
                mWheelViewD,//ѡ���յ�����
                okBtn,//ȷ����ť
                cancelBtn, //ȡ����ť
                year,//��ʾ�����
                month,//��ʾ�����
                day, //��ʾ�����
                1980,//Ĭ����ʾ�����
                kk-1980, //��ʼ���ڽ������ƫ����
                false);//�����Ƿ��ʼ��Ĭ��ʱ��Ϊ��ǰʱ��
    }
}
