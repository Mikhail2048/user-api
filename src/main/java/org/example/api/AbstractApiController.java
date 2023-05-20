package org.example.api;

import java.util.Objects;

import org.example.api.request.UserIdRequest;
import org.example.exception.AccessDeniedException;
import org.example.filter.SecurityContextHolder;

public class AbstractApiController {

    protected void checkUserIdBeforeRequest(UserIdRequest request) {
        Long userId = SecurityContextHolder.getUserId();
        if (!Objects.equals(request.getUserId(), userId)) {
            throw new AccessDeniedException(request.getUserId());
        }
    }
}