package com.ganghuan.controller;


import com.ganghuan.pojo.User;
import com.ganghuan.service.UserService;
import com.ganghuan.util.*;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/register")
    public String getRegisterPage(){
        return "register";
    }

    @PostMapping("/sendCode")
    @ResponseBody
    public String sendCode(String email, HttpSession session){
        System.out.println(email);
        if(StringUtils.isBlank(email) || userService.findUserByEmail(email) != null){
            return JsonUtil.getJSONString(0,"邮箱不能为空或者邮箱已被注册，请重新输入");
        }else {
            String code = RandomUtil.generateUUID().substring(0, 6);
            try{
                mailClient.sendMail(email,"欢迎注册猿说账号！","你的邮箱验证码为："+code+"。 5分钟内有效");
                session.setAttribute("code",code);
                return JsonUtil.getJSONString(1,"验证码已发送至邮箱，请查收");
            }catch (Exception e){
                return JsonUtil.getJSONString(0,"邮箱地址无效，请重新输入");
            }
        }
    }


    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user,String code, HttpSession session) {
        String trueCode = (String) session.getAttribute("code");
        Map<String, Object> map = userService.register(user,code,trueCode);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功, 请登陆");
            model.addAttribute("target", "/login");
            return "common/jump";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("codeMsg", map.get("codeMsg"));
            return "register";
        }
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, /**HttpSession session,*/ HttpServletResponse response,
                        @CookieValue("owner") String owner){
        // 检查验证码
        //String kaptcha = (String) session.getAttribute("kaptcha");
        //
        String kaptcha = null;
        if (StringUtils.isNotBlank(owner)){
            String kaptchaKey = RedisUtil.getKaptchaKey(owner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "login";
        }

        // 检查账号,密码
        int expiredSeconds = rememberme ? ConstantUtil.REMEMBER_EXPIRED_SECONDS : ConstantUtil.DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            //cookie.setPath("/");
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response) {
        // Session
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
        //session.setAttribute("kaptcha", text);
        //redis
        String owner = RandomUtil.generateUUID();
        Cookie cookie = new Cookie("owner", owner);
        cookie.setMaxAge(60);
        cookie.setPath("/");
        response.addCookie(cookie);
        String kaptchaKey = RedisUtil.getKaptchaKey(owner);
        redisTemplate.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);
        // 将突图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
