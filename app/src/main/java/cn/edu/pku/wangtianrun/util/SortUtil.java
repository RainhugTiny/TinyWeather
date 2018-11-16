package cn.edu.pku.wangtianrun.util;

import java.util.Comparator;

import cn.edu.pku.wangtianrun.bean.City;
/*
* 构造排序方法，按照字母顺序排序
* */
public class SortUtil implements Comparator<City> {
    @Override
    public int compare(City o1, City o2) {
        return o1.getAllFristPY().compareTo(o2.getAllFristPY());
    }
}
