package com.jxc.user.config.security;

import com.jxc.user.entity.User;
import com.jxc.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

@SpringBootConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JxcAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private JxcAuthenticationFailureHandler authenticationFailureHandler;
    @Resource
    private JxcLogoutSuccessHandler jxcLogoutSuccessHandler;
    @Resource
    private IUserService userService;
    @Resource
    private DataSource dataSource;

    /**
     * 放行的静态资源
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //web.ignoring().antMatchers("/index");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //禁用跨站攻击csrf
        http.csrf().disable()
                //验证码过滤器
                //.addFilterBefore(cacptcnaController, UsernamePasswordAuthenticationFilter.class)
                //禁用frame
                .headers().frameOptions().disable()
            .and()
                //开启表单登录
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                //登录请求处理地址
                .loginProcessingUrl("/login")
                //自定义登录页面
                //.loginPage("/index")
                //登录成功处理器 继承savedRequestAwareAuthenticationSuccessHandler
                .successHandler(authenticationSuccessHandler)
                //登录失败处理器 继承simpleUrlAuthenticationFailureHandler
                .failureHandler(authenticationFailureHandler)
            .and()
                //开启用户退出
                .logout()
                //退出处理url
                .logoutUrl("/logout")
                //删除用户cookie
                .deleteCookies("JSESSIONID")
                //退出成功处理器 实现LogoutSuccessHandler
                .logoutSuccessHandler(jxcLogoutSuccessHandler)
            .and()
                //开启记住我
                .rememberMe()
                //记住我功能参数
                .rememberMeParameter("rememberMe")
                //往客户端写入的token名 与数据库persistent_logins相关联
                .rememberMeCookieName("remember-me-cookie")
                //有效时间
                .tokenValiditySeconds(7*24*60*60)
                //令牌存放位置
                .tokenRepository(persistentTokenRepository())
            .and()
                //放行的请求
                .authorizeRequests().antMatchers("/login").permitAll()
                .anyRequest().authenticated();
    }

    /**
     * 配置从数据库中获取token
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository=new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    /**
     * 告诉框架当前登录用户信息数据库查询信息
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User userDetails = userService.findByUsername(username);
                return userDetails;
            }
        };
    }

    /**
     * 创建BCrypt密码加密对象
     * @return
     */
    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 传递用户信息和密码加密对象
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(encoder());
    }


}
