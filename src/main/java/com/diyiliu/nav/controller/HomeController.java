package com.diyiliu.nav.controller;

import com.diyiliu.common.util.JacksonUtil;
import com.diyiliu.nav.dao.NavDao;
import com.diyiliu.nav.model.SiteType;
import com.diyiliu.nav.model.Website;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: HomeController
 * Author: DIYILIU
 * Update: 2017-10-31 11:25
 */

@Controller
public class HomeController {

    @Resource
    private NavDao navDao;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) throws Exception {
        List types = navDao.querySiteTypeList();
        List groups = navDao.queryGroupSiteList();

        model.addAttribute("typeList", types);
        model.addAttribute("groupList", groups);

        return "index";
    }

    @RequestMapping(value = "/addSite", method = RequestMethod.POST)
    public String addSite(Website site) throws Exception {
        String icon = base64ICO(site.getUrl());
        site.setIcon(icon);
        navDao.insertWebsite(site);

        return "redirect:/";
    }

    @RequestMapping(value = "/saveSort", method = RequestMethod.POST)
    public @ResponseBody
    HashMap saveSort(String updateList, String delList) throws Exception {
        List<Website> list1 = JacksonUtil.toList(updateList, Website.class);
        navDao.updateWebsite(list1);

        List<Integer> list2 = JacksonUtil.toList(delList, Integer.class);
        navDao.deleteWebsite(list2);

        HashMap result = new HashMap();
        result.put("result", "success");

        return result;
    }

    @RequestMapping(value = "/saveType", method = RequestMethod.POST)
    public @ResponseBody
    HashMap saveType(String typesJson) throws Exception {
        List<SiteType> list = JacksonUtil.toList(typesJson, SiteType.class);
        navDao.batchSiteType(list);
        HashMap result = new HashMap();
        result.put("result", "success");

        return result;
    }

    @RequestMapping(value = "/icon/{id}", method = RequestMethod.GET)
    public void showIcon(@PathVariable String id, HttpServletResponse response) throws Exception {
        String iconStr = navDao.querySiteIcon(Long.parseLong(id));
        if (!StringUtils.isEmpty(iconStr)) {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(iconStr);

           /*
           for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            */

            OutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();
        }
    }


    public String base64ICO(String location) {
        String method = "GET";
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36";

        HttpURLConnection connection = null;
        InputStreamReader streamReader = null;
        try {
            URL url = new URL("http://" +location);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.addRequestProperty("User-Agent", userAgent);

            int state = connection.getResponseCode();

            String dirLocation = location;
            if (state == 302 || state == 301) {
                dirLocation = connection.getHeaderField("Location");
                url = new URL(dirLocation);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.addRequestProperty("User-Agent", userAgent);
                state = connection.getResponseCode();
            }

            if (state == 200) {
                streamReader = new InputStreamReader(connection.getInputStream(), "utf-8");
                BufferedReader br = new BufferedReader(streamReader);

                String icoPath = null;
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains(".ico")) {
                        // 取出有用的范围
                        Pattern p = Pattern.compile("<link[^>]*href=\"(?<href>[^\"]*)\"[^>]*>");
                        Matcher m = p.matcher(line);
                        if (m.matches()) {
                            String path = m.group(1);
                            if (path.contains("//")) {
                                icoPath = path;
                            } else {
                                icoPath = dirLocation + path;
                            }
                            break;
                        }
                    }
                }

                // 未匹配成功，用网络ICO工具获取
                if (icoPath == null) {
                    String iconTool = "http://statics.dnspod.cn/proxy_favicon/_/favicon?domain=";
                    url = new URL(iconTool + location);
                }else {
                    url = new URL(icoPath);
                }

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.addRequestProperty("User-Agent", userAgent);
                state = connection.getResponseCode();
                if (state == 200) {
                    byte[] bytes = FileCopyUtils.copyToByteArray(url.openStream());

                    BASE64Encoder encoder = new BASE64Encoder();
                    return encoder.encode(bytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (streamReader != null) {
                    streamReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
