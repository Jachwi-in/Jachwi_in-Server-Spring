package com.capstone.Jachwi_inServerSpring.controller;

import com.capstone.Jachwi_inServerSpring.domain.dto.EmailAuthDto;
//import com.capstone.Jachwi_inServerSpring.domain.dto.TokenDto;
import com.capstone.Jachwi_inServerSpring.domain.dto.UserJoinDto;
import com.capstone.Jachwi_inServerSpring.repository.UserRepository;
import com.capstone.Jachwi_inServerSpring.service.RedisUtil;
import com.capstone.Jachwi_inServerSpring.service.UserService;
import com.capstone.Jachwi_inServerSpring.service.impl.EmailServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:62328")
public class UsersController {
    @Autowired
    EmailServiceImpl EmailServiceImpl;

    @Autowired
    private final UserService userService;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

//    @PostMapping("/login")
//    public ResponseEntity login(@RequestBody TokenDto dto) throws Exception{
//        User user = userService.findByEmail(dto.getEmail());
//        if(!passwordEncoder)
//    }
    
//    @PostMapping("/login/token")
//    public ResponseEntity<String> login(){
//        return ResponseEntity.ok().body("token");
//    }

    //보안 처리 안하고 그냥 로그인만 처리하자.
//    @PostMapping("/login")
//    public ResponseEntity login(@RequestBody LoginDto dto){
//    }

    @PostMapping("/join/save")
    //responseEntity의 응답 본문의 타입이 <string>인 것이다.
    public ResponseEntity<String> save(@RequestBody UserJoinDto dto){
        userService.join(dto.getEmail(), dto.getName(), dto.getNickname(), dto.getPassword(), dto.getSchool());
        return ResponseEntity.ok("회원가입 완료");
    }

    //회원가입 메일 인증 서비스
    //중복확인 절차 이후, 중복되지 않은 경우 메일 전송
    @GetMapping("/join/mailConfirm/{email}")
    public ResponseEntity<String> mailConfirm(@PathVariable String email) throws Exception {
        if (userService.checkEmailDuplicate(email).equals("SUCCESS")) {  //equals는 내용자체를 비교, ==는 주소를 비교
            String code = EmailServiceImpl.sendSimpleMessage(email);
            System.out.println("인증코드 : " + code);
            return ResponseEntity.ok("인증코드를 보냈습니다.");
        } else {
            return ResponseEntity.ok("이미 사용 중인 이메일입니다.");
        }
    }

    //전송한 코드가 입력받은 코드와 같은지 보는 것.
    //인증 완료 후 인증코드 만료 하는 기능 추가해야 한다.
    @PostMapping("/join/mailConfirm")
    public ResponseEntity<String> sendEmailAndCode(@RequestBody EmailAuthDto dto) throws NoSuchAlgorithmException {
        //코드 일치하는 경우
        if (EmailServiceImpl.verifyEmailCode(dto.getEmail(), dto.getePw())) {
            System.out.println("인증 완료");
            return ResponseEntity.ok("Success");
        }
        System.out.println("코드 오류");
        return ResponseEntity.ok().build();
    }
}