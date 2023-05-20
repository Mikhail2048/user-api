package org.example.repository;

import java.util.Optional;

import org.example.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

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

    //language=sql
    @EntityGraph("withPhones")
    @Query(
      value = "SELECT u.*, pd.* FROM users u INNER JOIN phone_data pd ON pd.user_id = u.id WHERE u.id = :id",
      nativeQuery = true
    )
    Optional<User> findByIdWithPhones(Long id);

    //language=sql
    @EntityGraph("withEmails")
    @Query(
      value = "SELECT u.*, ed.* FROM users u INNER JOIN email_data ed ON ed.user_id = u.id WHERE u.id = :id",
      nativeQuery = true
    )
    Optional<User> findByIdWithEmails(Long id);
}