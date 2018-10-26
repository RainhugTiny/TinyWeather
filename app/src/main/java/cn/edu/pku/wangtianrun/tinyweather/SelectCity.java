package cn.edu.pku.wangtianrun.tinyweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cn.edu.pku.wangtianrun.app.MyApplication;
import cn.edu.pku.wangtianrun.bean.City;

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView listView;
    private List<City> cityList;
    private String[] city_String;
    private City selected_city;//声明在城市列表中选择的城市
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //在选择城市界面中加载布局
        setContentView(R.layout.select_city);
        initListViews();
        mBackBtn=(ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
    }
    @Override

    /*
    * 为选择城市界面的返回图标设置点击事件
    * */
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
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
    * 构造初始化ListViews方法
    * */
    private void initListViews(){

        listView=(ListView) findViewById(R.id.title_list);
        MyApplication myApplication=(MyApplication) getApplication();
        //获得城市列表
        cityList=myApplication.getCityList();
        //计算城市列表中的城市数目
        city_String=new String[cityList.size()];
        //将城市名称储存到city_String列表中
        int i=0;
        for(City city:cityList){
            String temp=city.getCity();
            city_String[i]=temp;
            i++;
        }
        //定义适配器，在ListView中展示city_String列表中的城市名称
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,city_String);
        listView.setAdapter(adapter);
        //为城市列表界面的ListViews设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_city=cityList.get(position);
                //将点击城市的城市编码在城市列表界面显示
                Toast.makeText(SelectCity.this,"您选择的城市编码为"+selected_city.getNumber(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
