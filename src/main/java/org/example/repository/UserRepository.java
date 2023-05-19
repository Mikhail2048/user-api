package org.example.repository;

import java.util.Optional;

import org.example.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    //language=sql
    @Query(
      value = "SELECT EXISTS(SELECT * FROM users u INNER JOIN phone_data pd ON u.id = pd.user_id WHERE pd.phone = :phone)",
      nativeQuery = true
    )
    boolean existByPhoneNumber(String phone);

    @EntityGraph("withPhones")
    Optional<User> findByIdWithPhones(Long id) ;
}