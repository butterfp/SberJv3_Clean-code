package butterfp.cleancode.web;

import butterfp.cleancode.model.AccountType;
import butterfp.cleancode.service.InterestRateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rate")
public class InterestRateController {
    private final InterestRateService interestRateService;

    public InterestRateController(InterestRateService interestRateService) {
        this.interestRateService = interestRateService;
    }

    @PostMapping
    public int calculateRate(@RequestBody RateRequest request) {
        if (request.date() == null || request.id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Необходимы дата и id");
        }

        // TODO Внедрить Clock с часовым поясом расчёта ставки,
        // чтобы тестировать смену суток и месяца с фиксированным временем.
        LocalDate today = LocalDate.now();
        if (request.date().isAfter(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата не должна быть в будущем");
        }

        return interestRateService.calc(request.date(), request.id(), today, request.accountType());
    }

    public record RateRequest(LocalDate date, UUID id, AccountType accountType) {
        public RateRequest {
            if (accountType == null) {
                accountType = AccountType.SAVINGS;
            }
        }
    }
}
