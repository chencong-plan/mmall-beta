package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cookie封装操作
 *
 * @author chencong
 */
@Slf4j
public class CookieUtil {

    /**
     * 域名读写，eg:3.abc.dianpoint.com 只能够读到abc.dianpoint.com下的内容 .dianpoint.com
     */
    private final static String COOKIE_DOMAIN = ".dianpoint.com";
    private final static String COOKIE_NAME = "mmall_login_token";

    /**
     * 从request当中读取cookie，判断是否存在cookieName = mmall_login_token的cookie
     *
     * @param request request
     * @return 存在则将其返回，不存在返回null
     */
    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("read cookieName:{}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    log.info("return cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 存入cookie，cookie过期时间为60 * 60 * 24 * 365
     *
     * @param response response
     * @param token    token
     */
    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie ck = new Cookie(COOKIE_NAME, token);
        ck.setDomain(COOKIE_DOMAIN);

        /*这是根目录，cookie存在的domain*/
        ck.setPath("/");

        /*如果这个setMaxAge不设置，cookie就不会写入硬盘，而是在内存当中，只是在当前页面有效*/
        /*同理：如果setMaxAge设置成-1 代表为永久*/
        /*单位：秒*/
        ck.setMaxAge(60 * 60 * 24 * 365);

        log.info("write cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
        response.addCookie(ck);
    }

    /**
     * 删除cookie,查看request当中的cookie存在name为mmall_login_token的cookie，则将过期时间maxAge设置为0<br>
     * maxAge=0染回给浏览器，浏览器则会将其删除
     *
     * @param request  request
     * @param response response
     */
    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    /*设置成0，代表着删除此cookie*/
                    cookie.setMaxAge(0);
                    log.info("del cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                    /*将一个有效期为0的cookie返回给浏览器，浏览器就会删除此cookie*/
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
