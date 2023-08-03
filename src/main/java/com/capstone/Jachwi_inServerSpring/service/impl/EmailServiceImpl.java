package com.capstone.Jachwi_inServerSpring.service.impl;

import com.capstone.Jachwi_inServerSpring.service.EmailService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import java.io.UnsupportedEncodingException;
import java.util.concurrent.ThreadLocalRandom;


//@Service bean을 만들고, 객체를 생성
@Service
public class EmailServiceImpl implements EmailService {//extends는 클래스 확장, implements는 인터페이스를 구현

    @Autowired
    JavaMailSender emailSender;//의존성 주입

    private String ePw; //인증번호

    @Override
    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);  //보내는 대상
        message.setSubject("자취인 회원가입 이메일 인증");

        String msg = "";

        msg += "<xlink href=\"https://fonts.googleapis.com/css?family=Roboto&amp;display=swap\" rel=\"stylesheet\">\n" +
                "    <div style=\"margin:auto;background-color:#13b3af;width:600px;height:100%\">\n" +
                "        <div style=\"margin:auto;background-color:#13b3af;width:600px;height:200px\">\n" +
                "            <div style=\"display:inline-block;margin:auto;width:540px;height:222px;margin-left:30px;margin-top:40px;padding-top:50px;padding-bottom:50px;background-color:#fff\">\n" +
                "                <div style=\"height:100%;margin:auto;padding-left:30px;padding-right:30px\">\n" +
                "                    <table cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%\">\n" +
                "                        <tbody>\n" +
                "                            <tr>\n" +
                "                                <td><h1 style=\"font-size:26px;margin-bottom:0;margin-top:0\">안녕하세요, 회원님.</h1>\n" +
                "                                    <div style=\"margin-bottom:20px;color:#3c3c3c;font-size:18px\">Jachwi-in에 등록하신 이메일의 검증을 위한 인증번호입니다.</div>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                            <tr>\n" +
                "                                <td align=\"center\">\n" +
                "                                    <div style=\"height:50px;padding-left:20px;padding-right:20px;border:2px solid #13b3af;display:flex;box-sizing:border-box;width:100%\">\n" +
                "                                        <div style=\"font-size:18px;text-align:center;margin-top:auto;margin-bottom:auto\">"+ePw+"</div>\n" +
                "                                        <div style=\"font-size:14px;text-align:right;margin-left:auto;margin-top:auto;margin-bottom:auto\">발송된 인증번호는 24시간 유효합니다</div>\n" +
                "                                    </div>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                            <tr>\n" +
                "                                <td>\n" +
                "                                    <div style=\"height:50px;width:480px;display:flex;margin:auto;margin-top:20px;font-size:14px\">\n" +
                "                                        <div style=\"color:#a42b2b;float:left\">※</div>\n" +
                "                                        <div style=\"color:#a42b2b;float:left\">인증번호를 요청하지 않았다면 다른 사람이 귀하의 계정을 사용하여 Jachwi-in에 엑세스하려고 시도하는 것일 수 있습니다. 인증번호를 절대 타인과 공유하지 마시길 바랍니다.</div>\n" +
                "                                    </div>\n" +
                "                                </td>\n" +
                "                            </tr>\n" +
                "                        </tbody>\n" +
                "                    </table>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div style=\"margin:auto;width:600px;height:100%;padding-top:192px;background-color:#f3f3f3\">\n" +
                "            <div style=\"display:inline;margin:auto;margin:auto;color:#777\">\n" +
                "                <div style=\"width:100%;margin:auto\">\n" +
                "                    <div style=\"font-size:14px;text-align:center\">본 메일은 발신전용이며, 문의에 대한 회신은 처리되지 않습니다.<br>궁금하신 점이나 불편한 사항은 Jachwi-in앱의 [문의하기]를 이용해주세요.</div>\n" +
                "                    <div style=\"text-align:center;padding-top:20px;font-size:12px\">ⓒ Jachwi-in Co.,Ltd. All rights reserved.<br><br><br><br><br></div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <img alt=\"\" src=\"http://j2xi8c6r.r.ap-northeast-2.awstrack.me/I0/010c017c24d26ba3-7f1a5f20-5dc7-46a8-9247-cef5deff43da-000000/s4WRSDANr-8HHkazjB2U7g18V9w=21\" style=\"display: none; width: 1px; height: 1px;\" loading=\"lazy\">\n" +
                "</xlink>";

        // 내용, charset 타입, subtype / StringBuffer 타입 인자로 못들어간다.
        message.setText(msg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("yongwoo1207@naver.com", "Jachwi-in admin"));// 보내는 사람

        return message;
    }

    //인증번호 생성
    public static String createKey() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }


    // 메일 발송
    // sendSimpleMessage 의 매개변수로 들어온 to 는 곧 이메일 주소가 되고,
    // MimeMessage 객체 안에 내가 전송할 메일의 내용을 담는다.
    // 그리고 bean 으로 등록해둔 javaMail 객체를 사용해서 이메일 send!!
    @Override
    public String sendSimpleMessage(String to) throws Exception {
        ePw = createKey();
        MimeMessage message = createMessage(to); //메일 발송
        try { //여긴 예외`처리 그냥 복사해옴.
            emailSender.send(message);
        } catch (MailException es){
            es.printStackTrace();
            throw new IllegalAccessException();
        }
        return ePw; // 메일로 보냈던 인증 코드를 서버로 반환
    }
}