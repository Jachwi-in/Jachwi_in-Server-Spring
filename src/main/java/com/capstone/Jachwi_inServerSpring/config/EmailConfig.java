package com.capstone.Jachwi_inServerSpring.config;


import com.capstone.Jachwi_inServerSpring.resource.EmailResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Configuration
public class EmailConfig {

//    @Value는 Bean 간의 주입을 모두 마친 후에 작업을 시작한다고 한다.
//    따라서, Service가 주입되는 과정에서는 @Value가 작업을 하지 않아 null이 출력되는 것이다.
//    value로 값 빼오기 실패해서 그냥 resource파일 따로 만들어서 gitignore에 등록함.

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;


    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl emailSender = new JavaMailSenderImpl();

        emailSender.setHost("smtp.naver.com");
        emailSender.setPort(EmailResource.getPort());
        emailSender.setUsername(EmailResource.getUsername());
        emailSender.setPassword(EmailResource.getUserpwd());
        emailSender.setDefaultEncoding("UTF-8");

        return emailSender;
    }
}
