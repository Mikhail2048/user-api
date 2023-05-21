package org.example.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.api.request.MoneyTransferRequest;
import org.example.api.request.UserSearchRequest;
import org.example.api.response.UserDto;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users/v1")
@RequiredArgsConstructor
public class UserApi extends AbstractApiController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> findUsersBySearchForm(
                                            @RequestParam(name = "page", required = false) Integer page,
                                            @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                            UserSearchRequest request) {
        return userService.findUsersByRequest(
                request,
                PageRequest.of(
                  Optional.ofNullable(page).orElse(0),
                  Optional.ofNullable(pageSize).orElse(10)
                )
          )
          .stream()
          .map(userMapper::toUserDto)
          .collect(Collectors.toList());
    }

    @PutMapping
    public void transferMoney(@RequestBody MoneyTransferRequest request) {
        super.checkUserIdBeforeRequest(request);
        userService.performMoneyTransfer(request);
    }
}