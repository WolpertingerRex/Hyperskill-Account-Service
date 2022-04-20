package account.business.service;

import account.business.Entity.Payment;
import account.persistance.PaymentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentsRepository paymentsRepository;

    @Autowired
    public PaymentService(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    public void addSalary(Payment payment) {
        Optional<Payment> existingPayment = paymentsRepository.findByEmployeeAndPeriod(payment.getEmployee(), payment.getPeriod());
        if (existingPayment.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Payment for %s period already exists", payment.getPeriod()));
        paymentsRepository.save(payment);
    }

    public List<Payment> getAllPayments(String email) {
        return paymentsRepository.findAllByEmployeeIgnoreCaseOrderByPeriodDesc(email);
    }

    public void updatePayment(Payment payment) {
        Optional<Payment> existingPayment = paymentsRepository.findByEmployeeAndPeriod(payment.getEmployee(), payment.getPeriod());
        if (existingPayment.isPresent()) {
            Payment previous = existingPayment.get();
            previous.setSalary(payment.getSalary());
            paymentsRepository.save(previous);
        }
    }
}
