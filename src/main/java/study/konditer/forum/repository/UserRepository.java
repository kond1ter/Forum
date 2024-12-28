package study.konditer.forum.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import study.konditer.forum.model.User;

@Repository
public interface UserRepository extends SafeBaseRepository<User, Long> {

    Optional<User> findByName(String name);
}
