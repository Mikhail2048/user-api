package org.example.service;

import java.math.BigDecimal;
import java.net.URI;

import org.assertj.core.api.Assertions;
import org.example.api.request.MoneyTransferRequest;
import org.example.exception.InsufficientFundsException;
import org.example.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@TestPropertySource(properties = "scheduling.enabled=false")
class UserServiceTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenFromAccountDoesNotExists_whenTransferMoney_thenGotException() {
        var moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.setToUserId(1L);
        moneyTransferRequest.setFromUserId(22L);
        moneyTransferRequest.setAmount(BigDecimal.valueOf(10));

        Assertions.assertThatThrownBy(() -> userService.performMoneyTransfer(moneyTransferRequest)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void givenToAccountDoesNotExists_whenTransferMoney_thenGotException() {
        var moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.setToUserId(11L);
        moneyTransferRequest.setFromUserId(2L);
        moneyTransferRequest.setAmount(BigDecimal.valueOf(10));

        Assertions.assertThatThrownBy(() -> userService.performMoneyTransfer(moneyTransferRequest)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void givenFromAccountDoesNotHaveSufficientFunds_whenTransferMoney_thenGotException() {
        var moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.setToUserId(1L);
        moneyTransferRequest.setFromUserId(2L);
        moneyTransferRequest.setAmount(BigDecimal.valueOf(550));

        Assertions.assertThatThrownBy(() -> userService.performMoneyTransfer(moneyTransferRequest)).isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void givenFromAccountHaveSufficientFunds_whenTransferMoney_thenMoneySuccessfullyTransferred() {
        var moneyTransferRequest = new MoneyTransferRequest();
        moneyTransferRequest.setToUserId(1L);
        moneyTransferRequest.setFromUserId(2L);
        moneyTransferRequest.setAmount(BigDecimal.valueOf(100));

        userService.performMoneyTransfer(moneyTransferRequest);

        userService.findAllWithBalances().forEach(user -> {
            if (user.getId().equals(1L)) {
                Assertions.assertThat(user.getAccount().getBalance()).isEqualTo(BigDecimal.valueOf(200));
            }
            if (user.getId().equals(2L)) {
                Assertions.assertThat(user.getAccount().getBalance()).isEqualTo(BigDecimal.valueOf(400));
            }
        });
    }

    @Test
    void givenThreeUsersInDb_whenFindUsersByExactNameWithoutToken_then401Returned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URI.create("http://localhost:8080/users/v1")).queryParam("name", "Alex"))
          .andExpect(MockMvcResultMatchers.status().is4xxClientError())
          .andReturn();
    }
}