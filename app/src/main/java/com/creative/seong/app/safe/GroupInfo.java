package com.creative.seong.app.safe;

import java.util.ArrayList;

/**
 * Created by GS on 2017-02-01.
 */
public class GroupInfo {
    private String code;
    private String name;
    private ArrayList<ChildInfo> list = new ArrayList<ChildInfo>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ChildInfo> getChildList() {
        return list;
    }

    public void setChildList(ArrayList<ChildInfo> childList) {
        this.list = childList;
    }
}
