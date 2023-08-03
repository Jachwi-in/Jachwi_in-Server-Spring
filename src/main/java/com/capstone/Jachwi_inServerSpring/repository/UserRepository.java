//package com.capstone.Jachwi_inServerSpring.repository;
//
//import com.capstone.Jachwi_inServerSpring.domain.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface UserRepository{
//
//    //저장, 이메일로 찾기, 비밀번호 찾기, 이름으로 찾기
//
//    User save(User user);
//    Optional<User> findByUserEmail(String userEmail);
//
//    Optional<User> findByUserName(String userName); //UserName이 있으면 optional안에 값이 들어오고 아니면 안들어온다.
//
//    List<User> findAll();
//}
//
//
