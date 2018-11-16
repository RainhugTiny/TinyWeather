package cn.edu.pku.wangtianrun.tinyweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangtianrun.bean.City;

public class myAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<City> cityList;
    //声明控件
    private class ViewHolder{
        TextView lettersTv;
        TextView cityTv;
    }
    /*
    * 构造方法
    * */
    public myAdapter(Context mContext,ArrayList<City> mCityList){
        this.context=mContext;
        this.cityList=mCityList;
    }
    public void set_cityList(ArrayList<City> newcityList){
        this.cityList=newcityList;
    }
    /*
    * 根据字母得到下标
    * */
    public int getPositionForName(int position){
        for(int i=0;i<getCount();i++){
            String letter=cityList.get(i).getAllFristPY();
            //显示首字母
            char firstChar=letter.toUpperCase().charAt(0);
            if(firstChar==position){
                return i;
            }
        }
        return -1;
    }
    /*
    * 根据下标得到字母
    * */
    public int getNameForPositon(int position){
        return cityList.get(position).getAllFristPY().charAt(0);
    }
    /*
    * 寻址函数
    * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.item_listview,parent,false);
            holder=new ViewHolder();
            holder.cityTv=(TextView)convertView.findViewById(R.id.cityTv);
            holder.lettersTv=(TextView)convertView.findViewById(R.id.lettersTv);
            //为控件设置标签
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        City city=cityList.get(position);
        //得到该城市的首字母
        int firstPosition=getNameForPositon(position);
        //得到以该字母开头的的第一个城市的位置
        int Index=getPositionForName(firstPosition);
        //设置以某字母开头的第一个城市的控件
        if(Index==position){
            holder.lettersTv.setVisibility(View.VISIBLE);
            holder.lettersTv.setText(city.getFirstPY());
        }else {
            holder.lettersTv.setVisibility(View.GONE);
        }
        holder.cityTv.setText(city.getCity());
        return convertView;
    }
    //获得控件中的城市数目
    @Override
    public int getCount() {
        return this.cityList.size();
    }
    //获得城市
    @Override
    public Object getItem(int position) {
        return this.cityList.get(position);
    }
    //获得城市位置
    @Override
    public long getItemId(int position) {
        return position;
    }
}
