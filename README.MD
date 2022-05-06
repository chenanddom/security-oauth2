# SpringSecurity实现Oauth2协议

## Security实现Oauth2需要做的相关的配置
一.配置security，使得登录，等处和授权的请求可以通过，详细的配置如下:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
@Bean
public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/oauth/**","/login/**","/logout/**")
            .permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().permitAll();
    }
}
```

二.配置授权服务器
```java
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                //配置的client-id
                .withClient("root")
                //配置的client-secrect
                .secret(passwordEncoder.encode("test123"))
                //配置访问token的有效时长
                .accessTokenValiditySeconds(3600)
                //跳转的url
                .redirectUris("http://www.baidu.com")
                //配置申请权限的范围
                .scopes("all")
                //指定的授权莫斯可以是"implicit","refresh_token", "password", "authorization_code"
                .authorizedGrantTypes("authorization_code") ;
    }
}
```

三.配置资源服务器
```java
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .requestMatchers()
                .antMatchers("/user/**");
    }
}
```
四.配置需要获取的资源
```java
@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping("currentUser")
    public Object getUser(Authentication authentication) {
        return authentication.getPrincipal();
    }
}
```

## 操作的步骤
做好上述的配置之后，我们就可以正常的启动，此时按照下面的步骤操作可以实现对资源接口的，也就是第四步的资源进行访问
1. 访问如下的URL，注意这个URL 和第二步的配置需要对应的上的。
```html
http://localhost:8089/oauth/authorize?response_type=code&client_id=admin&redirect_uri=http://www.baidu.com&scope=all
```
此时会弹出窗口需要登录，登录完成会进行一个授权的页面，可以点击approve或者deny

![授权登录页面](./files\用户授权的页面.PNG)

点击approve就会跳转到第二部配置的url即http://www.baidu.com,注意，此时url上携带了一个code,https://www.baidu.com/?code=G9TJ0u

2. 通过上一步获取到的code去提取token,请求的地址如下:
```java
http://localhost:8089/oauth/token
```
需要在Authentication选择basic Auth并填写第二步配置client-id和client-secrect,详细参数如下所示：
![client-id和secrect信息](./files\根据授权码去获取token-1.PNG)

![请求的参数](./files\根据授权码去获取token-2.PNG)



3. 根据获取的token请求第四步的资源
![根据token获取资源](./files\根据token请求资源-1.PNG)
