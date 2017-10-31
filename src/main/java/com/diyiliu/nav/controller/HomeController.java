package com.diyiliu.nav.controller;

import com.diyiliu.common.cache.ICache;
import com.diyiliu.nav.dao.NavDao;
import com.diyiliu.nav.model.Website;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description: HomeController
 * Author: DIYILIU
 * Update: 2017-10-31 11:25
 */

@Controller
public class HomeController {

    @Resource
    private NavDao navDao;

    @Resource(name = "siteTypeCacheProvider")
    private ICache typeCache;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) throws Exception{
        List types = navDao.querySiteTypeList();
        List groups = navDao.queryGroupSiteList();

        model.addAttribute("typeList", types);
        model.addAttribute("groupList", groups);
        model.addAttribute(typeCache);

        return "index";
    }


    @RequestMapping(value = "/addSite", method = RequestMethod.POST)
    public String addSite(Website site) throws Exception{
        navDao.insertWebSite(site);

        return "redirect:/";
    }
}
