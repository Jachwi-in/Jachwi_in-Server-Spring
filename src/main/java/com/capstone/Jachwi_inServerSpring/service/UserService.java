//package com.capstone.Jachwi_inServerSpring.service;
//
//import com.capstone.Jachwi_inServerSpring.domain.User;
//import com.capstone.Jachwi_inServerSpring.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@RequiredArgsConstructor  //이게 뭐하는건지 모르겠다.
//public class UserService {
//
//    private final UserRepository userRepository; //중복체크를 위해서 DB 다녀와야 하기에 주입.
//
//    public String join(String userEmail, String password){
//
//        //userName 중복체크
//        userRepository.findByUserEmail(userEmail)
//                .ifPresent(user -> {  //ifPresent함수는 optional 클래스에서 제공하는 메소드. Optional 객체가 비어있지 않은 경우에만 동작을 실행한다.
//                    throw new RuntimeException(userEmail + "는 이미 있습니다.");
//                });
//
//        //저장
//        User user = User.builder()
//            .email(userEmail)
//            .password(password) //추후에 암호화 해야한다.
//            .build();
//        userRepository.save(user);
//
//        return "SUCCESS";
//    }
//}
