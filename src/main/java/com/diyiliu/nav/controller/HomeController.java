package com.diyiliu.nav.controller;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.nav.dao.NavDao;
import com.diyiliu.nav.model.Website;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Description: HomeController
 * Author: DIYILIU
 * Update: 2017-10-31 11:25
 */

@Controller
public class HomeController {
    private final static String ICON_PATH = "upload/";
    private final static String ICON_TOOL_URL = "http://statics.dnspod.cn/proxy_favicon/_/favicon?domain=";
    @Resource
    private NavDao navDao;

    @Resource(name = "siteTypeCacheProvider")
    private ICache typeCache;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) throws Exception {
        List types = navDao.querySiteTypeList();
        List groups = navDao.queryGroupSiteList();

        model.addAttribute("typeList", types);
        model.addAttribute("groupList", groups);
        model.addAttribute(typeCache);

        return "index";
    }

    @RequestMapping(value = "/addSite", method = RequestMethod.POST)
    public String addSite(Website site, HttpServletRequest request) throws Exception {
        String dest = request.getSession().getServletContext().getRealPath(ICON_PATH);
        URL url = new URL("http://" + site.getUrl() + "/favicon.ico");
        if (!validUrl(url)){
            url = new URL(ICON_TOOL_URL + site.getUrl());
        }
        String iconPath = copyFileToIcon(url, new File(dest));

        site.setIcon(ICON_PATH + iconPath);
        navDao.insertWebSite(site);

        return "redirect:/";
    }

    private String copyFileToIcon(URL url, File file) throws IOException {
        // 创建临时文件，自动生成随机数在文件名中
        File tempFile = File.createTempFile("icon", ".ico", file);
        OutputStream os = new FileOutputStream(tempFile);
        FileCopyUtils.copy(url.openStream(), os);

        return tempFile.getName();
    }

    public boolean validUrl(URL url){
        HttpURLConnection connection = null;
        try {
           connection = (HttpURLConnection) url.openConnection();
           int state = connection.getResponseCode();
           if (state == 200){
               return true;
           }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }

        return false;
    }

}
