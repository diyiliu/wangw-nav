package com.diyiliu.nav.model;

import java.util.List;

/**
 * Description: GroupSite
 * Author: DIYILIU
 * Update: 2017-10-20 11:33
 */
public class GroupSite {

    private String type;
    private List<Website> websiteList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Website> getWebsiteList() {
        return websiteList;
    }

    public void setWebsiteList(List<Website> websiteList) {
        this.websiteList = websiteList;
    }
}
