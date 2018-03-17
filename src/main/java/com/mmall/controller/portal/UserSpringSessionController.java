package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户登录controller
 *
 * @author chencong
 */
@Controller
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {


    @Autowired
    private IUserService iUserService;


    /**
     * 用户登录
     *
     * @param username            username
     * @param password            password
     * @param session             session
     * @param httpServletResponse httpServletResponse
     * @return 返回json
     */
    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username,
                                      String password,
                                      HttpSession session,
                                      HttpServletResponse httpServletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            /*此时为单节点tomcat，在这里已经将session存储在redis 当中了*/
            session.setAttribute(Const.CURRENT_USER, response.getData());
            /*删除cookie*/
            //CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
            /*sessionId 存储在cookie当中*/
            /*CookieUtil.writeLoginToken(httpServletResponse, session.getId())*/;
            /*从cookie当中独处cookie*/
            // CookieUtil.readLoginToken(httpServletRequest);

            /*RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME)*/;
        }
        return response;
    }


    /**
     * 登出操作<br>
     * 首先从cookie当中获取到loginToken<br>
     * 然后再根据loginToken到cookie当中删除(设置maxAge(0))<br>
     * 最后从Redis当中删除loginToken对应的User序列化Json
     *
     * @param session  session
     * @param request  request
     * @param response response
     * @return 返回服务器响应
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session,HttpServletRequest request, HttpServletResponse response) {
        session.removeAttribute(Const.CURRENT_USER);
        /*String loginToken = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request, response);
        if (StringUtils.isNotEmpty(loginToken)) {
            RedisShardedPoolUtil.del(loginToken);
        }*/
        return ServerResponse.createBySuccess();
    }


    /**
     * get_user_info获取用户信息<br>
     * 首先判断是否存在loginToken，如果不存在则直接返回"用户未登录，无法获取当前用户的信息"<br>
     * 否则，将loginToken作为key 从Redis当中获取到用户User的Json数据 <br>
     * 然后将获取到的JSON字符串转成User对象<br>
     * 判断User对象是否为空，为null返回未登录，否则返回相应用户信息
     *
     * @param session  session
     * @param httpServletRequest request
     * @return 返回请求
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session,HttpServletRequest httpServletRequest) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        /*不再从session当中获取用户，而是从cookie token当中*/
        /*String loginToken = CookieUtil.readLoginToken(httpServletRequest);*/
        /*无法从cookie当中获取到loginToken，直接返回用户未登录*/
       /* if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }*/
        /*拿到loginToken*/
        /*String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.String2Obj(userJsonStr, User.class);*/
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }




}
