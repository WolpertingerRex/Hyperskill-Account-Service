package account.presentation;

import account.business.Entity.LogEntry;
import account.business.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class AuditorController {
    @Autowired
    private LoggingService loggingService;

    @GetMapping("api/security/events")
    public ResponseEntity<List<LogEntry>> getLogs(){
        List<LogEntry> logs = loggingService.getAllEntries();
        if(logs.isEmpty()) return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        else return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}
