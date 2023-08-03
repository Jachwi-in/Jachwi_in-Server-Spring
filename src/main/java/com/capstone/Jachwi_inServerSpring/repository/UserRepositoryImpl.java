//package com.capstone.Jachwi_inServerSpring.repository;
//
//import com.capstone.Jachwi_inServerSpring.domain.User;
//import jakarta.persistence.EntityManager;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Transactional
//@Repository
//public class UserRepositoryImpl implements UserRepository{
//    private final EntityManager em;
//    public UserRepositoryImpl(EntityManager em){
//        this.em = em;
//    }
//
//    @Override
//    public User save(User user) {
//        em.persist(user);
//        return user;
//    }
//
//    @Override  //PK같은 경우는 이런 식으로 할 수 있다.
//    public Optional<User> findByUserEmail(String userEmail) {
//        User user = em.find(User.class, userEmail);
//        return Optional.ofNullable(user);
//    }
//
//    @Override
//    public Optional<User> findByUserName(String userName) {
//        List<User> result = em.createQuery("select m from User m where m.name=:name", User.class)
//                .setParameter("name", userName)
//                .getResultList();
//        return result.stream().findAny();
//    }
//
//    @Override
//    public List<User> findAll() {
//        return em.createQuery("select u from User u", User.class)
//                .getResultList();
//    }
//
//
//
//    public void clearStore(){
//        em.clear();
//    }
//}
