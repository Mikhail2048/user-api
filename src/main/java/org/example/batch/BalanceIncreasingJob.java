package org.example.batch;

import java.math.BigDecimal;
import java.util.List;

import org.example.domain.Account;
import org.example.domain.User;
import org.example.service.LockAcquiringService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceIncreasingJob {

    private final UserService userService;
    private final LockAcquiringService lockAcquiringService;

    @Value("${calculation.edge.ratio:2.07}")
    private BigDecimal edgingRation;

    @Value("${multiplication.factor:1.1}")
    private BigDecimal multiplyFactor;

    /**
     * Here, we can of course process users in batches, but this can significantly delay
     * the actual balance change time for last users, which can be unfair, I guess. Plus,
     * since we add users ourselves, we can assume that we understand the size of loaded list,
     * and we do not get oom
     */
    @Scheduled(cron = "0/30 * * * * *")
    public void jobIncreasing() {
        boolean thisPodIsResponsibleForProcessing = lockAcquiringService.isCurrentPodResponsibleForProcessing();

        if (thisPodIsResponsibleForProcessing) {
            log.info("Current pod is selected as responsible for balance processing");
            List<User> users = userService.findAllWithBalances();
            users.stream().filter(it -> {
                Account account = it.getAccount();

                BigDecimal toBeRatio = account.getBalance()
                    .divide(ensureNotZero(account.getBalanceInitial()))
                    .multiply(multiplyFactor);

                return toBeRatio.max(edgingRation).equals(edgingRation);
            }).forEach(user -> {
                user.getAccount().setBalance(user.getAccount().getBalance().multiply(multiplyFactor));
                userService.updateUserInDb(user);
            });
        } else {
            log.debug("This pod is not responsible for balance increasing");
        }
    }

    private BigDecimal ensureNotZero(BigDecimal decimal) {
        if (decimal.equals(BigDecimal.ZERO)) {
            return BigDecimal.ONE;
        }
        return decimal;
    }
}