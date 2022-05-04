package com.newgreen.passport.service;

import com.newgreen.api.UserServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("passport-service")
public interface UserService extends UserServiceApi {
}
