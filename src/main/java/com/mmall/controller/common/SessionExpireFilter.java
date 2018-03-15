package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 解决cookie当中loginToken过期时间重置问题 <br>
 * 详情请看README.md当中#单点登录Session共享-存在问题#这一点
 *
 * @author chencong
 * @createDate 2018/3/13 23:16
 * @Description
 * @updateDate 2018/3/13 23:16
 * @Description
 */
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * 执行过滤器,重置loginToken对应的value过期时间
     *
     * @param request  ServletRequest
     * @param response ServletResponse
     * @param chain    FilterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        /*获取loginToken*/
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        /*判断loginToken是否为null 或者 ""*/
        if (StringUtils.isNotEmpty(loginToken)) {
            /*如果loginToken不空，符合条件，则继续从Redis当中查找loginToken对应的value*/
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            /*将从redis当中拿到的value进行反序列化*/
            User user = JsonUtil.string2Obj(userJsonStr, User.class);
            if (user != null) {
                /*反序列化得到的user不为空则将其重新放入redis，并更新过期时间*/
                RedisShardedPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }
        /*过滤器请求链放行*/
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
