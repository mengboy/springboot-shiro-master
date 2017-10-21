package com.study.controller;

import com.study.model.User;
import com.study.model.UserLog;
import com.study.service.UserLogService;
import com.study.service.UserService;
import com.study.service.impl.UserServiceImpl;
import com.study.util.DateFormate;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Created by yangqj on 2017/4/21.
 */
@Controller
public class HomeController {

    static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserLogService userLogService;

    @Autowired
    UserService userService;

    @RequestMapping(value="/login",method= RequestMethod.GET)
    public String login(){
        return "login";
    }

    /**
     * 用户登录
     * @param request
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value="/login",method=RequestMethod.POST)
    public String login(HttpServletRequest request, User user, Model model){
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            request.setAttribute("msg", "用户名或密码不能为空！");
            return "login";
        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(user.getUsername(),user.getPassword());
        try {
            subject.login(token);
            User u = (User) subject.getPrincipal();
            UserLog userLog = new UserLog(u.getId());
            Date now = new Date();
            String strNow = DateFormate.date2String(now);
            request.getSession().setAttribute("logintime", strNow);
            userLog.setLoginTime(strNow);
            userLogService.insertLog(userLog);
            logger.info(user.getUsername() + " 登录成功");
            return "redirect:usersPage";
        }catch (LockedAccountException lae) {
            token.clear();
            logger.error("用户已经被锁定不能登录，请与管理员联系！", lae);
            request.setAttribute("msg", "用户已经被锁定不能登录，请与管理员联系！");
            return "login";
        } catch (AuthenticationException e) {
            token.clear();
            logger.error("用户或密码不正确！", e);
            request.setAttribute("msg", "用户或密码不正确！");
            return "login";
        }
    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        UserLog userLog = new UserLog(user.getId());
        userLog.setLogoutTime(DateFormate.date2String(new Date()));
        String loginDate = (String) request.getSession().getAttribute("logintime");
        request.getSession().invalidate();
        userLog.setLoginTime(loginDate);
        userLogService.updateLog(userLog);
        return "login";
    }

    @RequestMapping(value={"/usersPage",""})
    public String usersPage(){
        return "user/users";
    }

    @RequestMapping("/rolesPage")
    public String rolesPage(){
        return "role/roles";
    }

    @RequestMapping("/resourcesPage")
    public String resourcesPage(){
        return "resources/resources";
    }

    @RequestMapping("/403")
    public String forbidden(){
        return "403";
    }
}
