package account.persistance;

import account.business.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByEmailIgnoreCase(String email);
    void deleteByEmailIgnoreCase(String email);

}
