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

    //language=sql
    @Query(
      value = "SELECT EXISTS(SELECT * FROM users u INNER JOIN email_data e ON u.id = e.user_id WHERE e.email = :email)",
      nativeQuery = true
    )
    boolean existByEmail(String email);

    @EntityGraph("withPhones")
    Optional<User> findByIdWithPhones(Long id);

    @EntityGraph("withEmails")
    Optional<User> findByIdWithEmails(Long id);
}