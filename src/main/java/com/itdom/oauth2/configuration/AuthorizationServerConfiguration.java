package com.itdom.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

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
