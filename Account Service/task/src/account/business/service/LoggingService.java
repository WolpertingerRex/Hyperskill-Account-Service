package account.business.service;

import account.business.Entity.LogEntry;
import account.persistance.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoggingService {
    @Autowired
    private LogsRepository logsRepository;

    public void saveEntry(LogEntry entry) {
        logsRepository.save(entry);
    }

    public List<LogEntry> getAllEntries() {
        return logsRepository.findAll();
    }
}
