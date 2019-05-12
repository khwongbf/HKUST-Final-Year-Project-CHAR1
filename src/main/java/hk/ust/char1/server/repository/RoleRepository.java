package hk.ust.char1.server.repository;

import hk.ust.char1.server.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    boolean existsRoleByRole(String role);

    Role findRoleByRole(String role);
}
