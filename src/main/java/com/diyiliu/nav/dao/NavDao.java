package com.diyiliu.nav.dao;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.nav.model.GroupSite;
import com.diyiliu.nav.model.SiteType;
import com.diyiliu.nav.model.Website;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: NavDao
 * Author: DIYILIU
 * Update: 2017-10-18 15:37
 */

@Component
public class NavDao {

    @Resource
    private ICache siteTypeCacheProvider;

    @Resource
    private QueryRunner queryRunner;

    public void insertWebSite(Website website) throws SQLException {
        String sql = "INSERT INTO nav_site(name,url,type)VALUES (?,?,?)";
        Object[] params = new Object[]{website.getName(), website.getUrl(), website.getTypeId()};

        queryRunner.execute(sql, params);
    }

    public List<SiteType> querySiteTypeList() throws SQLException {

        String sql = "SELECT t.id, t.name, t.top FROM nav_type t order by t.top";

        List<SiteType> list = new ArrayList();
        queryRunner.query(sql, (ResultSet rs) -> {
            while (rs.next()) {
                SiteType type = new SiteType();
                type.setId(rs.getInt("id"));
                type.setName(rs.getString("name"));
                siteTypeCacheProvider.put(type.getId(), type);

                list.add(type);
            }
            return null;
        });

        return list;
    }

    public List<GroupSite> queryGroupSiteList() throws SQLException {

        String sql = "SELECT s.ID, s.`NAME`, s.URL, t.ID TYPEID,t.`NAME` TYPE " +
                "FROM nav_site s INNER JOIN nav_type t on s.TYPE=t.ID " +
                "ORDER BY t.TOP DESC, s.TOP DESC";

        List<GroupSite> list = new ArrayList();
        queryRunner.query(sql, (ResultSet rs) -> {
            while (rs.next()) {
                Website site = new Website();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setUrl(rs.getString("url"));
                site.setTypeId(rs.getInt("typeid"));

                String type = rs.getString("TYPE");

                boolean flag = true;
                for (GroupSite group: list){
                    if (type.equals(group.getType())){
                        List l = group.getWebsiteList();
                        l.add(site);
                        flag = false;
                        break;
                    }
                }

                if (flag){
                    List l = new ArrayList();
                    l.add(site);
                    GroupSite group = new GroupSite();
                    group.setType(type);
                    group.setWebsiteList(l);

                    list.add(group);
                }
            }
            return null;
        });

        return list;
    }
}
