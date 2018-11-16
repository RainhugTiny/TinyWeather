package cn.edu.pku.wangtianrun.tinyweather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import cn.edu.pku.wangtianrun.viewpager.MypagerAdapter;
/*
* 设置引导页
* */

public class guidePage extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private ViewPager viewPager;
    private ArrayList<View> aList;
    private MypagerAdapter mypagerAdapter;
    private Button mbutton;
    private LinearLayout mLinearLayout;
    int now=0;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //显示界面
        setContentView(R.layout.guide_main);
        mLinearLayout=(LinearLayout)findViewById(R.id.guide_linear);
        //初始化页面
        init();
        //初始化小圆点
        mLinearLayout.getChildAt(0).setEnabled(true);
        viewPager.addOnPageChangeListener(this);
        mbutton=(Button)aList.get(1).findViewById(R.id.button_guide);
        //为启动按钮设置监听点击事件
        mbutton.setOnClickListener(this);
    }
    /*
    * 初始化引导页
    * */
    private void init(){
        viewPager=(ViewPager)findViewById(R.id.guide);
        aList=new ArrayList<View>();
        LayoutInflater Li=getLayoutInflater();
        //储存引导页的两个布局
        aList.add(Li.inflate(R.layout.guidepage1,null,false));
        aList.add(Li.inflate(R.layout.guidepage2,null,false));
        //为适配器赋值
        mypagerAdapter=new MypagerAdapter(aList);
        viewPager.setAdapter(mypagerAdapter);
        View view;
        //为页面设置小圆点
        for(int i=0;i<2;i++){
            view=new View(guidePage.this);
            //background为自定义的小圆点
            view.setBackgroundResource(R.drawable.background);
            view.setEnabled(false);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(30,30);
            //为不同页面的小圆点设置间距
            if(i!=0){
                layoutParams.leftMargin=10;
            }
            mLinearLayout.addView(view,layoutParams);
        }
    }
    /*
    * 在引导页设置按钮，点击后进入城市天气界面
    * */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button_guide){
            //点击按钮后，按钮颜色改变，给用户一个点击的反馈
            mbutton.setBackgroundColor(Color.parseColor("#F5F5DC"));
            //通过Intent启动MainActivity活动
            Intent intent=new Intent(guidePage.this,MainActivity.class);
            startActivity(intent);
            //此活动结束
            finish();
        }
    }
    /*
     * 为页面滑动设置响应事件
     * */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    /*
    * 页面滑动时，改变小圆点的格式
    * */
    @Override
    public void onPageSelected(int position) {
        //刚才选中页面的小圆点设置为未选中样式
        mLinearLayout.getChildAt(now).setEnabled(false);
        //当前选中页面的小圆点设置为选中样式
        mLinearLayout.getChildAt(position).setEnabled(true);
        now=position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
