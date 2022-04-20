package account.persistance;

import account.business.Entity.Group;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {
    Optional<Group> findByName(String name);
}
