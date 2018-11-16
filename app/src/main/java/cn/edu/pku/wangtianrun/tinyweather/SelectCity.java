package cn.edu.pku.wangtianrun.tinyweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.edu.pku.wangtianrun.app.MyApplication;
import cn.edu.pku.wangtianrun.bean.City;
import cn.edu.pku.wangtianrun.mlayout.LettersView;
import cn.edu.pku.wangtianrun.util.SortUtil;


public class SelectCity extends Activity implements View.OnClickListener,LettersView.OnLettersListViewListener {
    private ImageView mBackBtn;
    private ListView listView;
    private List<City> cityList;
    private ArrayList<City> sortedCityList;
    private ArrayList<String> city_String;
    private City selected_city;//声明在城市列表中选择的城市
    private EditText mEditText;
    private ArrayAdapter<String> adapter;
    private ProgressBar progressBar;
    private TextView mtextView;
    private LettersView mLettersView;
    private myAdapter mAdapter;
    private ArrayList<City> filterdCityList;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //在选择城市界面中加载布局
        setContentView(R.layout.select_city);
        mtextView=(TextView)findViewById(R.id.title_name);
        MyApplication myApplication=(MyApplication) getApplication();
        //获得城市列表
        cityList=myApplication.getCityList();
        sortedCityList=new ArrayList<>();
        //给selected_city赋初值（即当前城市），避免返回时selected_city为空。
        Intent intent=getIntent();
        String data=intent.getStringExtra("initCityCode");
        Log.d("myWeather","初试城市号码为"+data);
        for(City city:cityList){
            if(data.equals(city.getNumber())){
                selected_city=city;
            }
            sortedCityList.add(city);
        }
        mtextView.setText("当前城市："+selected_city.getCity());
        //初始化城市列表
        initListViews();
        mBackBtn=(ImageView) findViewById(R.id.title_back);
        //为返回按钮设置监听事件
        mBackBtn.setOnClickListener(this);
        mEditText=(EditText)findViewById(R.id.search_edit);
        //为输入栏设置监听事件
        mEditText.addTextChangedListener(mTextwacher);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        //刚进入界面时，进度条不可见，选择完城市并点击返回按钮后进度条可见。
        progressBar.setVisibility(View.INVISIBLE);
    }
    @Override

    /*
     * 为选择城市界面的返回图标设置点击事件
     * */
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                //将进度条设置为可见
                progressBar.setVisibility(View.VISIBLE);
                //活动销毁后返回信息给MainActivity
                Intent i=new Intent();
                //设置返回的信息为选择的城市的代码
                i.putExtra("cityCode",selected_city.getNumber());
                setResult(RESULT_OK,i);
                //销毁当前活动
                finish();
                break;
            default:
                break;
        }
    }
    /*
    * 为EditText控件设置TextWacher，在搜索栏输入文字时，根据输入的文字更新ListView。
    * */
    private TextWatcher mTextwacher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mLettersView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //清空适配器的内容
            adapter.clear();
            //过滤城市列表，将与输入字符有关的城市项保存到城市列表中
            filterCityList(s.toString());
            //在适配器中加入过滤后的城市列表
            adapter.addAll(city_String);
            //重新为listView设置适配器
            listView.setAdapter(adapter);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    /*
    * 构建根据输入信息过滤城市列表的方法
    * */
    private void filterCityList(String string){
        city_String=new ArrayList<String>();
        filterdCityList=new ArrayList<City>();
        //若输入信息为空，城市列表仍包含数据库中的全部城市
        if(TextUtils.isEmpty(string)){
            for(City city:cityList){
                city_String.add(city.getProvince()+"  "+city.getCity()+"               ");
                filterdCityList.add(city);
            }
        }
        //若输入信息不为空，过滤城市列表，留下输入信息（转化为字符串）是城市名称子串的城市
        else {
            for(City city:cityList){
                if(city.getCity().indexOf(string.toString())!=-1){
                    city_String.add(city.getProvince()+"  "+city.getCity()+"               ");
                    filterdCityList.add(city);
                }else if(city.getAllPY().indexOf(string.toUpperCase().toString())!=-1){
                    city_String.add(city.getProvince()+"  "+city.getCity()+"               ");
                    filterdCityList.add(city);
                }else if(city.getAllFristPY().indexOf(string.toUpperCase().toString())!=-1){
                    city_String.add(city.getProvince()+"  "+city.getCity()+"               ");
                    filterdCityList.add(city);
                }
            }
        }
        mAdapter.set_cityList(filterdCityList);
    }
    /*
    * 构造初始化ListViews方法
    * */
    private void initListViews(){
        mLettersView=(LettersView)findViewById(R.id.title_letters_view);
        mLettersView.setOnLettersListViewListener(this);
        listView=(ListView) findViewById(R.id.title_list);
        //将cityList中的城市按照名字首字母的顺序进行排序
        Collections.sort(sortedCityList,new SortUtil());
        //计算城市列表中的城市数目
        city_String=new ArrayList<>();
        filterdCityList=new ArrayList<>();
        //将城市名称储存到city_String列表中
        for(City city:sortedCityList){
            city_String.add(city.getProvince()+"  "+city.getCity());
            filterdCityList.add(city);
        }
        //定义适配器，在ListView中展示city_String列表中的城市名称
        mAdapter=new myAdapter(SelectCity.this,sortedCityList);
        adapter=new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,city_String);
        listView.setAdapter(mAdapter);
        //为城市列表界面的ListViews设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获得选中的城市
                City city=filterdCityList.get(position);
                Log.d("myWeather",city.getCity());
                selected_city=city;
                //将点击城市的城市编码在城市列表界面显示
                Toast.makeText(SelectCity.this,"您选择的城市编码为"+selected_city.getNumber(),Toast.LENGTH_LONG).show();
            }
        });
    }
    /*
    * 为导航栏设置监听事件
    * */
    @Override
    public void onLettersListener(String s) {
        //获得单击字母的位置
        int position=mAdapter.getPositionForName(s.charAt(0));
        //将城市列表切换到所选字母位置处
        listView.setSelection(position);
    }
}
