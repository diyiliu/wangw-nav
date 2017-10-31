package com.diyiliu.nav.model;

/**
 * Description: Website
 * Author: DIYILIU
 * Update: 2017-10-19 11:01
 */
public class Website {
    private Integer id;
    private String name;
    private String url;
    private int typeId;
    private int top;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        String regex = "[hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://";
        this.url = url.replaceAll(regex, "");
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }
}
