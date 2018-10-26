package cn.edu.pku.wangtianrun.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangtianrun.bean.City;
import cn.edu.pku.wangtianrun.db.CityDB;

public class MyApplication extends Application {
    private static final String TAG="MyAPP";
    private static MyApplication myApplication;
    private CityDB mCityDB;
    private List<City> mCityList;
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->Oncreate");
        myApplication=this;
        //打开数据库
        mCityDB=openCityDB();
        //初始化城市列表
        initCityList();
    }
    /*
    * 构造初始化城市列表方法
    * */
    private void initCityList(){
        mCityList = new ArrayList<City>();
        //新建子线程准备城市列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                prepareCityList();
            }
        }).start();
    }
    /*
    * 构建准备城市列表的方法
    * */
    private boolean prepareCityList() {
        //获取mCityDB对象中的全部城市信息
        mCityList = mCityDB.getAllCity();
        int i=0;
        for (City city : mCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return true;
    }
    /*
    * 构建获取城市列表的方法
    * */
    public List<City> getCityList() {
        return mCityList;
    }
    /*
    * 构建getInstance方法
    * */
    public static MyApplication getInstance(){
        return myApplication;
    }
    /*
    *构造打开数据库的方法
    * */
    private CityDB openCityDB() {
        //数据库文件的路径
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath ()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        //如果数据库文件不存在，寻找文件夹是否存在
        if (!db.exists()) {
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            //如果文件夹不存在，则新建文件夹
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");
            //异常处理
            try {
                //从city.db中读取数据
                InputStream is = getAssets().open("city.db");
                //将数据写入db中
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }
            //捕获异常
            catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

}
