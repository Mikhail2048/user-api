package org.example.repository;

import java.util.List;
import java.util.Optional;

import org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    //language=sql
    @Query(
      value = "SELECT u.*, a.* FROM users u INNER JOIN accounts a ON a.user_id = u.id",
      nativeQuery = true
    )
    List<User> findAllWithAccounts();

    //language=sql
    @Query(
      value = "SELECT u.*, a.* FROM users u INNER JOIN accounts a ON a.user_id = u.id WHERE u.id = :id",
      nativeQuery = true
    )
    Optional<User> findByIdWithAccount(Long id);

    Optional<User> findByName(String name);

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
    @Query(
      value = "SELECT u.*, pd.* FROM users u INNER JOIN phone_data pd ON pd.user_id = u.id WHERE u.id = :id",
      nativeQuery = true
    )
    Optional<User> findWithPhonesById(Long id);

    //language=sql
    @Query(
      value = "SELECT u.*, ed.* FROM users u INNER JOIN email_data ed ON ed.user_id = u.id WHERE u.id = :id",
      nativeQuery = true
    )
    Optional<User> findByIdWithEmails(Long id);
}