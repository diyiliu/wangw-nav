package com.diyiliu.nav.controller;

import com.diyiliu.common.util.JacksonUtil;
import com.diyiliu.nav.dao.NavDao;
import com.diyiliu.nav.model.SiteType;
import com.diyiliu.nav.model.Website;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
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

            OutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();
        }
    }


    public String base64ICO(String location) {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36";
        String scheme = "http";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, userAgent);

        try {
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                    scheme + "://" + location,
                    HttpMethod.GET,
                    new HttpEntity<byte[]>(headers),
                    byte[].class);

            int statusCode = responseEntity.getStatusCode().value();
            if (301 == statusCode || 302 == statusCode) {
                URI uri = responseEntity.getHeaders().getLocation();
                scheme = uri.getScheme();
                location = uri.getHost();

                responseEntity = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        new HttpEntity<byte[]>(headers),
                        byte[].class);
                statusCode = responseEntity.getStatusCode().value();
            }

            String icoPath = null;
            if (200 == statusCode) {
                InputStream inputStream = new ByteArrayInputStream(responseEntity.getBody());
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains(".ico")) {
                        // 取出有用的范围
                        Pattern p = Pattern.compile(".*<link[^>]*href=\"(?<href>[^\"]*)\"[^>]*>");
                        Matcher m = p.matcher(line.trim());
                        if (m.matches()) {
                            String path = m.group(1);
                            if (!path.contains("//")) {
                                path = scheme + "://" + location + path;
                            }
                            icoPath = path;
                            break;
                        }
                    }
                }
            }

            // 未匹配成功，用网络ICO工具获取
            if (icoPath == null) {
                icoPath = "http://statics.dnspod.cn/proxy_favicon/_/favicon?domain=" + location;
            }

            responseEntity = restTemplate.exchange(
                    icoPath,
                    HttpMethod.GET,
                    new HttpEntity<byte[]>(headers),
                    byte[].class);
            statusCode = responseEntity.getStatusCode().value();
            if (statusCode == 200) {
                byte[] bytes = responseEntity.getBody();
                BASE64Encoder encoder = new BASE64Encoder();
                return encoder.encode(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
