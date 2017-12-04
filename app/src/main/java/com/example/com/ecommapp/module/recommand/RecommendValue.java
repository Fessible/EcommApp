package com.example.com.ecommapp.module.recommand;

import com.example.com.ecommapp.module.BaseModel;

import java.util.List;

/**
 * RecommendModel的实体类
 * Created by rhm on 2017/12/1.
 */

public class RecommendValue extends BaseModel {
    public int type;
    public String logo;
    public String title;
    public int info;
    public String price;
    public String text;
    public String from;
    public String site;
    public String resource;
    public int zan;
    public List<String> url;

    public String clickUrl;
    public List<Monitor> clickMonitor;
    public List<Monitor> startMonitor;
    public List<Monitor> middleMonitor;
    public List<Monitor> endMonitor;



}
