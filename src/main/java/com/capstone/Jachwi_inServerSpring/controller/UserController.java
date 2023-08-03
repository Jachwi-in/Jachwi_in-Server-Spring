package com.capstone.Jachwi_inServerSpring.controller;

/*import com.capstone.Jachwi_inServerSpring.service.UserService;
import com.capstone.Jachwi_inServerSpring.domain.dto.UserJoinRequest;*/
import com.capstone.Jachwi_inServerSpring.service.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor  //이거 뭐하는지 몰겠다.
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    EmailServiceImpl EmailServiceImpl;
//    private final UserService userService;
//
//    @PostMapping("/join")
//    //responseEntity의 응답 본문의 타입이 <string>인 것이다.
//    public ResponseEntity<String> join(@RequestBody UserJoinRequest dto) {
//        return ResponseEntity.ok().body("회원가입이 성공 했습니다.");
//    }

    //회원가입 메일 인증 서비스
    @PostMapping("/join/mailConfirm/{email}")
    @ResponseBody
    String mailConfirm(@PathVariable("email") String email) throws Exception {
        //값 받아 오는 부분 공부하기
        String code = EmailServiceImpl.sendSimpleMessage(email);
        //String code = EmailServiceImpl.sendSimpleMessage(email);
        System.out.println("인증코드 : " + code);
        return code;
    }
}