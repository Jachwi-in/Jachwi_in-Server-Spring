//package com.capstone.Jachwi_inServerSpring.domain;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import lombok.Builder;
//
//@Builder // 빌더를 통해 객체를 생성할 수 있다. (lombok 제공 어노테이션)
//
////@NoArgsConstructor  이거 두개는 어떤건지 알아보고 없을 경우 어떻게 하는건지 파악하고 사용하던지 하자.
////@AllArgsConstructor
//
//@Entity
//public class User {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private String email;
//    private String password;
//    private String name;
//
//
//    public String getName() { return name; }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//}
//
