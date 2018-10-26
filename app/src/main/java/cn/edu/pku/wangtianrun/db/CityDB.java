package cn.edu.pku.wangtianrun.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangtianrun.bean.City;

public class CityDB {
    public static final String CITY_DB_NAME="city.db";
    private static final String CITY_TABLE_NAME="city";
    private SQLiteDatabase db;
    /*
    * 构造创建数据库的方法
    * */
    public CityDB(Context context,String path){
        db=context.openOrCreateDatabase(path,Context.MODE_PRIVATE,null);
    }
    /*
    * 构造获得数据库中全部城市信息的方法
    * */
    public List<City>getAllCity(){
        //定义一个City类的列表来储存所有城市信息
        List<City> list=new ArrayList<City>();
        //定义一个按行访问数据的光标
        Cursor c=db.rawQuery("SELECT * from " + CITY_TABLE_NAME,null);
        //当下一行不为空时，光标循环读取每一行的数据
        while(c.moveToNext()){
            String province=c.getString(c.getColumnIndex("province"));
            String city=c.getString(c.getColumnIndex("city"));
            String number=c.getString(c.getColumnIndex("number"));
            String allPY=c.getString(c.getColumnIndex("allpy"));
            String allFirstPY=c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY=c.getString(c.getColumnIndex("firstpy"));
            City item=new City(province,city,number,firstPY,allPY,allFirstPY);
            ((ArrayList) list).add(item);
        }
        return list;
    }
}
