package account.persistance;

import account.business.Entity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupDataLoader {
    private final GroupRepository groupRepository;

    @Autowired
    public GroupDataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            if (groupRepository.findByName("ROLE_ADMINISTRATOR").isEmpty())
                groupRepository.save(new Group("ROLE_ADMINISTRATOR"));
            if (groupRepository.findByName("ROLE_USER").isEmpty())
                groupRepository.save(new Group("ROLE_USER"));
            if (groupRepository.findByName("ROLE_ACCOUNTANT").isEmpty())
                groupRepository.save(new Group("ROLE_ACCOUNTANT"));
            if (groupRepository.findByName("ROLE_AUDITOR").isEmpty())
                groupRepository.save(new Group("ROLE_AUDITOR"));
        } catch (Exception e) {

        }
    }
}
