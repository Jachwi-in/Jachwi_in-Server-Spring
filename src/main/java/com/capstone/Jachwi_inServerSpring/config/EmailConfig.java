package com.capstone.Jachwi_inServerSpring.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class EmailConfig {

/*
    @Value는 Bean 간의 주입을 모두 마친 후에 작업을 시작한다고 한다.
    따라서, Service가 주입되는 과정에서는 @Value가 작업을 하지 않아 null이 출력되는 것이다.
    @Value("{mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
    private int connectionTimeout;

    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private int timeout;

    @Value("${spring.mail.properties.mail.smtp.writetimeout}")
    private int writeTimeout;
*/

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl emailSender = new JavaMailSenderImpl(); //JavaMailSenderImpl는 기존에 있던 class

        emailSender.setHost("smtp.naver.com");
        emailSender.setPort(587);
        emailSender.setUsername("전송 이메일 입력");
        emailSender.setPassword("비밀번호 입력");
        emailSender.setDefaultEncoding("UTF-8");
        return emailSender;
    }
}
