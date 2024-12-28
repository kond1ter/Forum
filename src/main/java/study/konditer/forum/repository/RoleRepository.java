package study.konditer.forum.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import study.konditer.forum.model.Role;
import study.konditer.forum.model.emun.UserRoles;

@Repository
public interface RoleRepository extends SafeBaseRepository<Role, Long> {

    Optional<Role> findRoleByName(UserRoles role);
}
