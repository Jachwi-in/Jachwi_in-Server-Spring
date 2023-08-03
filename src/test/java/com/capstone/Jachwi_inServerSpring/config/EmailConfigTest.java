package com.capstone.Jachwi_inServerSpring.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class EmailConfigTest {
    @Value("{spring.mail.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Test
    public void test(){
        System.out.println(username);
    }
    //application.yml에서 가지고 온 value값이 계속 null로 나온다. 해당 값 빼오는 법 찾기.
}