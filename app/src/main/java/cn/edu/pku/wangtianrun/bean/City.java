package cn.edu.pku.wangtianrun.bean;
/*
* 为城市对象定义City类
* */
public class City {
    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFristPY;
    /*
    * 构造创建对象的方法
    * */
    public City(String province,String city,String number,String firstPY,String allPY,String allFristPY){
        this.province=province;
        this.city=city;
        this.number=number;
        this.firstPY=firstPY;
        this.allPY=allPY;
        this.allFristPY=allFristPY;
    }
    /*
    * 构造从对象中获取数据的方法
    * */
    public String getProvince(){
        return province;
    }
    public String getCity(){
        return city;
    }
    public String getNumber(){
        return number;
    }
    public String getFirstPY(){
        return firstPY;
    }
    public String getAllPY(){
        return allPY;
    }
    public String getAllFristPY(){
        return allFristPY;
    }
}
