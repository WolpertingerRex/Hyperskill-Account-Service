package account.persistance;

import account.business.Entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentsRepository extends CrudRepository<Payment, Long> {


    List<Payment> findAllByEmployeeIgnoreCaseOrderByPeriodDesc(String employee);

    Optional<Payment> findByEmployeeAndPeriod(String employee, String period);

}
