package cn.edu.pku.wangtianrun.tinyweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cn.edu.pku.wangtianrun.bean.TodayWeather;
import cn.edu.pku.wangtianrun.util.NetUtil;
import cn.edu.pku.wangtianrun.viewpager.MypagerAdapter;

public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener {
    private static final int UPDATE_TODAY_WEATHER=1;
    private static final int DB=2;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, wendutv,humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv,weekTv1,temperatureTv1,climateTv1, windTv1,weekTv2,temperatureTv2,climateTv2, windTv2,weekTv3,temperatureTv3,climateTv3, windTv3,weekTv4,temperatureTv4,climateTv4, windTv4;
    private ImageView weatherImg, pmImg,weatherImg1,weatherImg2,weatherImg3,weatherImg4;
    private ProgressBar progressBar;
    public LocationClient mLocationClient=null;
    private MyLocationListener myListener=new MyLocationListener();
    private ImageView mTitleLocation;
    private ProgressBar progressBar_location;
    private ViewPager vpager;
    private ArrayList<View> aList;
    private MypagerAdapter mAdapter;
    private LinearLayout mLinearLayout;
    private int mNub=0;
    /*
    *主线程增加Handler，接收到消息数据后，调用updateTodayWeather方法，更新UI界面的数据
    * */
    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayweather((TodayWeather)msg.obj);
                    break;
                default:
                        break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载主界面布局
        setContentView(R.layout.weather_info);
        //初始化展示未来四天天气的界面
        initViewPager();
        mLinearLayout=(LinearLayout)findViewById(R.id.main_linear);
        //初始化展示未来四天天气界面的小圆点
        initDot();
        //为未来四天天气界面设置页面滑动监听事件
        vpager.addOnPageChangeListener(this);
        //设置当前页面的小圆点可见
        mLinearLayout.getChildAt(0).setEnabled(true);
        mTitleLocation=(ImageView)findViewById(R.id.title_location);
        //为定位按钮设置监听事件
        mTitleLocation.setOnClickListener(this);
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        //为更新按钮设置监听事件
        mUpdateBtn.setOnClickListener(this);
        //为更新按钮设置进度条
        progressBar=(ProgressBar)findViewById(R.id.title_update_progress);
        //为定位按钮设置进度条
        progressBar_location=(ProgressBar)findViewById(R.id.title_location_progress);
        mCitySelect=(ImageView) findViewById(R.id.title_city_manager);
        //为选择城市按钮设置监听事件
        mCitySelect.setOnClickListener(this);
        mLocationClient=new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        //初始化定位配置信息
        initLocation();
        //界面控件初始化
        initView();
        /*进行网络状态检测。
         *通过Toast在界面通知信息。
         */
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了!", Toast.LENGTH_LONG).show();
        }
    }
    /*
    * 构造初始化小圆点方法
    * */
    private void initDot(){
        View view;
        for(int i=0;i<2;i++){
            view=new View(MainActivity.this);
            //加载自定义的布局
            view.setBackgroundResource(R.drawable.background);
            //设置小圆点为未选中样式
            view.setEnabled(false);
            //设置布局参数
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(30,30);
            if(i!=0){
                //设置小圆点的间距
                layoutParams.leftMargin=10;
            }
            //在布局中添加元素
            mLinearLayout.addView(view,layoutParams);
        }
    }
    /*
    * 构造初始化未来四天天气界面方法
    * */
    private void initViewPager(){
        vpager=(ViewPager)findViewById(R.id.vpager);
        aList=new ArrayList<View>();
        //使用LayoutInflater来载入需要动态载入的界面
        LayoutInflater Li=getLayoutInflater();
        //在aList中添加布局
        aList.add(Li.inflate(R.layout.view_one,null,false));
        aList.add(Li.inflate(R.layout.view_two,null,false));
        //获得已经载入界面中的控件元素
        weekTv1 = (TextView)aList.get(0).findViewById(R.id.viewone_week_today1);
        temperatureTv1= (TextView)aList.get(0).findViewById(R.id.viewone_temperature1);
        windTv1 = (TextView) aList.get(0).findViewById(R.id.viewone_wind1);
        climateTv1 = (TextView) aList.get(0).findViewById(R.id.viewone_climate1);
        weekTv2 = (TextView) aList.get(0).findViewById(R.id.viewone_week_today2);
        temperatureTv2= (TextView) aList.get(0).findViewById(R.id.viewone_temperature2);
        windTv2 = (TextView) aList.get(0).findViewById(R.id.viewone_wind2);
        climateTv2 = (TextView) aList.get(0).findViewById(R.id.viewone_climate2);
        weekTv3 = (TextView) aList.get(1).findViewById(R.id.viewtwo_week_today1);
        temperatureTv3= (TextView) aList.get(1).findViewById(R.id.viewtwo_temperature1);
        windTv3 = (TextView) aList.get(1).findViewById(R.id.viewtwo_wind1);
        climateTv3 = (TextView) aList.get(1).findViewById(R.id.viewtwo_climate1);
        weekTv4 = (TextView) aList.get(1).findViewById(R.id.viewtwo_week_today2);
        temperatureTv4= (TextView) aList.get(1).findViewById(R.id.viewtwo_temperature2);
        windTv4 = (TextView) aList.get(1).findViewById(R.id.viewtwo_wind2);
        climateTv4 = (TextView) aList.get(1).findViewById(R.id.viewtwo_climate2);
        weatherImg1=(ImageView)aList.get(0).findViewById(R.id.viewone_weather_img1);
        weatherImg2=(ImageView)aList.get(0).findViewById(R.id.viewone_weather_img2);
        weatherImg3=(ImageView)aList.get(1).findViewById(R.id.viewtwo_weather_img1);
        weatherImg4=(ImageView)aList.get(1).findViewById(R.id.viewtwo_weather_img2);
        mAdapter=new MypagerAdapter(aList);
        vpager.setAdapter(mAdapter);
    }
    /*
    * 构造初始化位置信息方法
    * */
    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setIsNeedAddress(true);
        //可选，设置定位模式，默认高精度
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置返回经纬度坐标类型，默认GCJ02,BD09ll：百度经纬度坐标
        option.setCoorType("bd09ll");
        //可选，设置发起定位请求的间隔，int类型，单位ms
        option.setScanSpan(1000);
        //可选，设置是否使用gps，默认false
        option.setOpenGps(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        mLocationClient.setLocOption(option);
    }

    /*
    * 定义初始化界面控件内容的方法
    * */
    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        wendutv=(TextView) findViewById(R.id.wendu);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        //初始状态下信息为空
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        wendutv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        weekTv1.setText("N/A");
        temperatureTv1.setText("N/A");
        climateTv1.setText("N/A");
        windTv1.setText("N/A");
        weekTv2.setText("N/A");
        temperatureTv2.setText("N/A");
        climateTv2.setText("N/A");
        windTv2.setText("N/A");
        weekTv3.setText("N/A");
        temperatureTv3.setText("N/A");
        climateTv3.setText("N/A");
        windTv3.setText("N/A");
        weekTv4.setText("N/A");
        temperatureTv4.setText("N/A");
        climateTv4.setText("N/A");
        windTv4.setText("N/A");
    }

    /*
     *添加单击事件
     **/
    @Override
    public void onClick(View view) {
        //为选择城市图标增加点击事件
        if(view.getId()==R.id.title_city_manager){
            //创建Intent对象来在活动间传递信息，在MainActivity这个活动的基础上打开SelectCity这个活动活动
            Intent i=new Intent(this,SelectCity.class);
            //将城市信息传递给SelectCity中的活动
            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            String citycode=sharedPreferences.getString("main_city_code","101010100");
            i.putExtra("initCityCode",citycode);
            //SelectCity活动销毁后返回信息给MainActivity活动，请求码设置为1
            startActivityForResult(i,1);
        }
        //为更新按钮增加点击事件
        if (view.getId() == R.id.title_update_btn) {
            progressBar.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.INVISIBLE);

            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            //通过SharedPreferences读取城市id，如果没有定义则缺省为101010100
            String cityCode=sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                //获取网络数据
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了!", Toast.LENGTH_LONG).show();
            }
        }
        //为定位按钮设置单击事件
        if(view.getId()==R.id.title_location){
            //设置进度条可见
            progressBar_location.setVisibility(View.VISIBLE);
            //设置定位图标不可见
            mTitleLocation.setVisibility(View.INVISIBLE);
            //若定位已启动，则停止重新启动定位
            if(mLocationClient.isStarted()){
                mLocationClient.stop();
            }
            mLocationClient.start();
            final Handler BDHandler=new Handler(){
                public void handleMessage(Message msg){
                    switch (msg.what){
                        case DB:
                            if(msg.obj!=null){
                                if(NetUtil.getNetworkState(MainActivity.this)!=NetUtil.NETWORN_NONE){
                                    Log.d("myWeather","网络OK");
                                    //查询城市天气信息并更新
                                    queryWeatherCode((String) msg.obj);
                                }else {
                                    Log.d("myWeather","网络挂了");
                                    Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
                                }
                            }
                            myListener.cityCode=null;
                            break;
                        default:
                                break;
                    }
                }
            };
            //设置线程获得定位城市信息，并传递信息给Handler
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(myListener.cityCode==null){
                            Thread.sleep(2000);
                        }
                        Message msg=new Message();
                        //设置标记
                        msg.what=DB;
                        //设置传递信息的内容
                        msg.obj=myListener.cityCode;
                        BDHandler.sendMessage(msg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    /*
    *定义onActivityResult方法接收返回SelectCity活动撤销时返回的数据
    * */
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        //请求码（requestCode）为startActivityForResult中的请求码，返回码为setResult中的返回码）
        if(requestCode==1&&resultCode==RESULT_OK){
            String newCityCode=data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);
            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(newCityCode);
            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }

        }

    }
    /*
    *定义获取网络数据方法
    * */
    private void queryWeatherCode(String cityCode) {
        SharedPreferences.Editor editor=getSharedPreferences("config",MODE_PRIVATE).edit();
        editor.putString("main_city_code",cityCode);
        editor.apply();
        //定义所选城市天气信息的URL地址
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //HttpURLConnection继承自URLConnection
                HttpURLConnection con = null;
                TodayWeather todayWeather=null;
                try {
                    URL url = new URL(address);
                    //通过URL地址打开链接
                    con = (HttpURLConnection) url.openConnection();
                    //请求方式为获取数据
                    con.setRequestMethod("GET");
                    //设置超时时间8000毫秒
                    con.setConnectTimeout(8000);
                    //设置读取超时8000毫秒
                    con.setReadTimeout(8000);
                    //得到网络返回的输入流
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
                    //将解析的数据保存到todayweather对象中
                    todayWeather=parseXML(responseStr);
                    if(todayWeather!=null){
                        Log.d("myWeather",todayWeather.toString());
                        //通过消息机制，将解析的天气对象，通过消息发送给主线程
                        Message msg=new Message();
                        msg.what=UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    /*
    * 定义解析xml数据方法
    * */
    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang")&&fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli")) {
                                eventType = xmlPullParser.next();
                                if(fengliCount == 0) todayWeather.setFengli(xmlPullParser.getText());
                                if(fengliCount == 3) todayWeather.setFengli1(xmlPullParser.getText());
                                if(fengliCount == 5) todayWeather.setFengli2(xmlPullParser.getText());
                                if(fengliCount == 7) todayWeather.setFengli3(xmlPullParser.getText());
                                if(fengliCount == 9) todayWeather.setFengli4(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date")&&dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high")&&highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low")&&lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type")) {
                                eventType = xmlPullParser.next();
                                if(typeCount == 0) todayWeather.setType(xmlPullParser.getText());
                                if(typeCount == 2) todayWeather.setType1(xmlPullParser.getText());
                                if(typeCount == 4) todayWeather.setType2(xmlPullParser.getText());
                                if(typeCount == 6) todayWeather.setType3(xmlPullParser.getText());
                                if(typeCount == 8) todayWeather.setType4(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("date")&&dateCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate1(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high")&&highCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh1(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low")&&lowCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow1(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("date")&&dateCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate2(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high")&&highCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh2(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low")&&lowCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow2(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("date")&&dateCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate3(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high")&&highCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh3(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low")&&lowCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow3(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("date")&&dateCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate4(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high")&&highCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh4(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low")&&lowCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow4(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }
                        }
                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    /*
    * 定义更新UI控件的方法
    * */
    void updateTodayweather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度:"+todayWeather.getShidu());
        if(todayWeather.getPm25()!=null) pmDataTv.setText(todayWeather.getPm25());
        else pmDataTv.setText("无");
        if(todayWeather.getQuality()!=null) pmQualityTv.setText(todayWeather.getQuality());
        else pmQualityTv.setText("无");
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+'~'+todayWeather.getLow());
        wendutv.setText("当前温度:"+todayWeather.getWendu()+"℃");
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        weekTv1.setText(todayWeather.getDate1());
        temperatureTv1.setText(todayWeather.getHigh1()+'~'+todayWeather.getLow1());
        climateTv1.setText(todayWeather.getType1());
        windTv1.setText(todayWeather.getFengli1());
        weekTv2.setText(todayWeather.getDate2());
        temperatureTv2.setText(todayWeather.getHigh2()+'~'+todayWeather.getLow2());
        climateTv2.setText(todayWeather.getType2());
        windTv2.setText(todayWeather.getFengli2());
        weekTv3.setText(todayWeather.getDate3());
        temperatureTv3.setText(todayWeather.getHigh3()+'~'+todayWeather.getLow3());
        climateTv3.setText(todayWeather.getType3());
        windTv3.setText(todayWeather.getFengli3());
        weekTv4.setText(todayWeather.getDate4());
        temperatureTv4.setText(todayWeather.getHigh4()+'~'+todayWeather.getLow4());
        climateTv4.setText(todayWeather.getType4());
        windTv4.setText(todayWeather.getFengli4());

        //根据解析的PM2.5的值更新PM2.5的图案
        if(todayWeather.getPm25()!=null){
            int pm2_5=Integer.parseInt(todayWeather.getPm25());
            if(pm2_5<=50) pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            if(pm2_5>50&&pm2_5<=100) pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            if(pm2_5>100&&pm2_5<=150) pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            if(pm2_5>150&&pm2_5<=200) pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            if(pm2_5>200&&pm2_5<=300) pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            if(pm2_5>300) pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
        }

        //根据解析的天气类型更新界面的天气图案
        updateImage(weatherImg,todayWeather.getType());
        updateImage(weatherImg1,todayWeather.getType1());
        updateImage(weatherImg2,todayWeather.getType2());
        updateImage(weatherImg3,todayWeather.getType3());
        updateImage(weatherImg4,todayWeather.getType4());
        Toast.makeText(MainActivity.this,"更新成功!",Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.INVISIBLE);
        mUpdateBtn.setVisibility(View.VISIBLE);
        progressBar_location.setVisibility(View.INVISIBLE);
        mTitleLocation.setVisibility(View.VISIBLE);
    }
    /*
    * 构造更新图片方法
    * */
    private void updateImage(ImageView weatherImg,String climate){
        if(climate.equals("暴雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        if(climate.equals("暴雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        if(climate.equals("大暴雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        if(climate.equals("大雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
        if(climate.equals("大雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
        if(climate.equals("多云"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        if(climate.equals("雷阵雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        if(climate.equals("雷阵雨冰雹"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        if(climate.equals("晴"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
        if(climate.equals("沙尘暴"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        if(climate.equals("特大暴雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        if(climate.equals("雾"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
        if(climate.equals("小雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        if(climate.equals("小雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        if(climate.equals("阴"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
        if(climate.equals("雨夹雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        if(climate.equals("阵雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        if(climate.equals("阵雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        if(climate.equals("中雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        if(climate.equals("中雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
    }
    /*
    * 为未来四天天气界面的小圆点设置滑动事件
    * */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //过去选中的页面小圆点不可见
        mLinearLayout.getChildAt(mNub).setEnabled(false);
        //当前选中页面的小圆点可见
        mLinearLayout.getChildAt(position).setEnabled(true);
        //记录当前位置
        mNub=position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

