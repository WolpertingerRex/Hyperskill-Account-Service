package account.persistance;

import account.business.Entity.LogEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogsRepository extends CrudRepository<LogEntry, Long> {
    List<LogEntry> findAll();
}
