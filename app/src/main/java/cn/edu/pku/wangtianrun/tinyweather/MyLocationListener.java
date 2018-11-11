package cn.edu.pku.wangtianrun.tinyweather;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import java.util.List;

import cn.edu.pku.wangtianrun.app.MyApplication;
import cn.edu.pku.wangtianrun.bean.City;

public class MyLocationListener extends BDAbstractLocationListener {
    public String recity;
    public String cityCode;

    @Override
    public void onReceiveLocation(BDLocation location) {
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取地址相关的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        String addr = location.getAddrStr();    //获取详细地址信息
        String country = location.getCountry();    //获取国家
        String province = location.getProvince();    //获取省份
        String city = location.getCity();    //获取城市
        String district = location.getDistrict();    //获取区县
        String street = location.getStreet();    //获取街道信息
        Log.d("Location_code",city);
        recity=city.replace("市","");
        List<City> mCityList;
        MyApplication myApplication;
        myApplication=MyApplication.getInstance();
        mCityList=myApplication.getCityList();
        for(City temp_city:mCityList){
            if (temp_city.getCity().equals(recity)){
                cityCode=temp_city.getNumber();
                Log.d("Location_code",cityCode);
            }
        }
    }
}
