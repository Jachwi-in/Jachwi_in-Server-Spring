package com.capstone.Jachwi_inServerSpring.service;

import com.capstone.Jachwi_inServerSpring.domain.User;
import com.capstone.Jachwi_inServerSpring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor  //이게 뭐하는건지 모르겠다.
public class UserService {

    private final UserRepository userRepository; //중복체크를 위해서 DB 다녀와야 하기에 주입.

    public String checkEmailDuplicate(String email){
        //userName 중복체크
        userRepository.findByEmail(email)
                .ifPresent(user -> {  //ifPresent함수는 optional 클래스에서 제공하는 메소드. Optional 객체가 비어있지 않은 경우에만 동작을 실행한다.
                    System.out.println("이미 가입된 계정입니다.");
                    throw new RuntimeException(email + "는 이미 있습니다.");
                });
        System.out.println("등록할 수 있는 이메일 입니다.");
        return "SUCCESS";
    }

    public String join(String email,String name, String nickname, String password, String school){
        User user = User.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .school(school)
                .build();
        userRepository.save(user);
        return "SUCCESS";
    }


}
