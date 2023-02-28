package com.modq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.modq.reggie.common.R;
import com.modq.reggie.entity.User;
import com.modq.reggie.service.UserService;
import com.modq.reggie.utils.SMSUtils;
import com.modq.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取手机号
        String phone = user.getPhone();
        if(StringUtils.hasText(phone)){
            // 生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            // 阿里云提供的短信服务api
            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909" ,phone,code);
            session.setAttribute(phone,code);
            return R.success("手机验证码发送成功");

            // 验证码存入session

        }

        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        // 获取手机号
        String phone= map.get("phone").toString();
        // 获取验证码

        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = userService.getOne(queryWrapper);
        if(user==null){
            // 判断当前手机号是否新用户，新用户则自动注册
            user=new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        session.setAttribute("user",user.getId());
        return R.success(user);
    }


    // 开启短信验证时使用
    // @PostMapping("/login")
    // public R<User> login(@RequestBody Map map, HttpSession session){
    //     log.info(map.toString());
    //     // 获取手机号
    //     String phone= map.get("phone").toString();
    //     // 获取验证码
    //     String code= map.get("code").toString();
    //     // 从Session中获取保存的验证码
    //     Object codeInSession = session.getAttribute(phone);
    //     // 进行验证码的比对
    //     if(codeInSession!=null&&codeInSession.equals(code)){
    //         // 比对成功则登录成功
    //         LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
    //         queryWrapper.eq(User::getPhone,phone);
    //         User user = userService.getOne(queryWrapper);
    //         if(user==null){
    //             // 判断当前手机号是否新用户，新用户则自动注册
    //             user=new User();
    //             user.setPhone(phone);
    //             user.setStatus(1);
    //             userService.save(user);
    //         }
    //         session.setAttribute("user",user.getId());
    //         return R.success(user);
    //     }
    //     return R.error("登录失败");
    // }
}
