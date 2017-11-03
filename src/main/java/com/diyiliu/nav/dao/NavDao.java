package com.diyiliu.nav.dao;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.nav.model.GroupSite;
import com.diyiliu.nav.model.SiteType;
import com.diyiliu.nav.model.Website;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
        if (siteTypeCacheProvider.containsKey(typeName)) {
            SiteType type = (SiteType) siteTypeCacheProvider.get(typeName);
            String sql = "INSERT INTO nav_site(name,url,icon,type)VALUES (?,?,?,?)";
            Object[] params = new Object[]{website.getName(), website.getUrl(), website.getIcon(), type.getId()};

            queryRunner.execute(sql, params);
        }
    }

    public long insertSiteType(SiteType type) throws SQLException {
        String sql = "INSERT INTO nav_type(name,top)VALUES (?,?)";
        Object[] params = new Object[]{type.getName(), type.getTop()};
        Map rs = queryRunner.insert(sql, new MapHandler(), params);
        return (long) rs.get("GENERATED_KEY");
    }

    public void batchSiteType(List<SiteType> types) throws SQLException {
        List<SiteType> updateList = new ArrayList();
        List<SiteType> insertList = new ArrayList();
        types.forEach(t -> {
            if (siteTypeCacheProvider.containsKey(t.getName())) {
                updateList.add(t);
            } else {
                insertList.add(t);
            }
        });

        // 批量更新
        String updateSql = "UPDATE nav_type SET top=? WHERE name=?";
        Object[][] updateParam = new Object[updateList.size()][];
        for (int i = 0; i < updateList.size(); i++) {
            SiteType type = updateList.get(i);
            updateParam[i] = new Object[]{type.getTop(), type.getName()};
        }
        queryRunner.batch(updateSql, updateParam);

        // 批量插入
        String insertSql = "INSERT INTO nav_type(name,top)VALUES (?,?)";
        Object[][] insertParam = new Object[insertList.size()][];
        for (int i = 0; i < insertList.size(); i++) {
            SiteType type = insertList.get(i);
            insertParam[i] = new Object[]{type.getName(), type.getTop()};
        }
        queryRunner.batch(insertSql, insertParam);


        // 批量删除
        String delSql = "DELETE FROM nav_type WHERE name = ?";
        Set set = types.stream().map(SiteType::getName).collect(Collectors.toSet());
        Set tmp = siteTypeCacheProvider.getKeys();
        Collection subKeys = CollectionUtils.subtract(tmp, set);
        Object[][] delParam = new Object[subKeys.size()][];
        int i = 0;
        for (Iterator iterator = subKeys.iterator(); iterator.hasNext();){
            delParam[i++] = new Object[]{iterator.next()};
        }
        queryRunner.batch(delSql, delParam);
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
                "ORDER BY t.TOP, s.TOP";

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
