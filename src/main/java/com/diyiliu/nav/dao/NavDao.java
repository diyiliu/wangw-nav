package com.diyiliu.nav.dao;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.nav.model.GroupSite;
import com.diyiliu.nav.model.SiteType;
import com.diyiliu.nav.model.Website;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String typeName = website.getTypeName();

        long typeId;
        if (!siteTypeCacheProvider.containsKey(typeName)){
            SiteType type = new SiteType();
            type.setName(typeName);

           typeId = insertSiteType(type);
        }else {
            SiteType type = (SiteType) siteTypeCacheProvider.get(typeName);
            typeId = type.getId();
        }
        String sql = "INSERT INTO nav_site(name,url,icon,type)VALUES (?,?,?,?)";
        Object[] params = new Object[]{website.getName(), website.getUrl(), website.getIcon(), typeId};

        queryRunner.execute(sql, params);
    }

    public long insertSiteType(SiteType type) throws SQLException {
        String sql = "INSERT INTO nav_type(name,top)VALUES (?,?)";
        Object[] params = new Object[]{type.getName(), type.getTop()};
        Map rs =  queryRunner.insert(sql, new MapHandler(),params);
        return (long) rs.get("GENERATED_KEY");
    }

    public List<SiteType> querySiteTypeList() throws SQLException {
        String sql = "SELECT t.id, t.name, t.top FROM nav_type t order by t.top";

        List<SiteType> list = new ArrayList();
        queryRunner.query(sql, (ResultSet rs) -> {
            while (rs.next()) {
                SiteType type = new SiteType();
                type.setId(rs.getInt("id"));
                type.setName(rs.getString("name"));
                siteTypeCacheProvider.put(type.getName(), type);

                list.add(type);
            }
            return null;
        });

        return list;
    }

    public List<GroupSite> queryGroupSiteList() throws SQLException {

        String sql = "SELECT s.ID, s.`NAME`, s.URL, s.ICON, t.`NAME` TYPE " +
                "FROM nav_site s INNER JOIN nav_type t on s.TYPE=t.ID " +
                "ORDER BY t.TOP DESC, s.TOP DESC";

        List<GroupSite> list = new ArrayList();
        queryRunner.query(sql, (ResultSet rs) -> {
            while (rs.next()) {
                Website site = new Website();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setUrl(rs.getString("url"));
                site.setIcon(rs.getString("icon"));

                String type = rs.getString("TYPE");

                boolean flag = true;
                for (GroupSite group : list) {
                    if (type.equals(group.getType())) {
                        List l = group.getWebsiteList();
                        l.add(site);
                        flag = false;
                        break;
                    }
                }

                if (flag) {
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
