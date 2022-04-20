package account.presentation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPaymentsResponse {
    @NotBlank
    private String name;
    @NotBlank
    private String lastname;
    private String period;
    private String salary;

    public void setFormattedSalary(long salary) {
        long dollars = salary / 100;
        long cents = salary % 100;
        this.salary = String.format("%d dollar(s) %d cent(s)", dollars, cents);
    }

    public void setFormattedPeriod(String period) {
        String[] date = period.split("-");
        Month month = Month.of(Integer.parseInt(date[0]));
        this.period = month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH) + "-" + date[1];
    }
}
