package com.devteria.identity.repository.httpClient;

import com.devteria.identity.configuration.AuthenticationRequestInterceptor;
import com.devteria.identity.dto.request.ProfileCreationRequest;
import com.devteria.identity.dto.response.ApiResponse;
import com.devteria.identity.dto.response.ProfileCreationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

// url property là root url để call đến service khác
@FeignClient(name = "profile-service-client", url = "${app.services.profile}",
    configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<ProfileCreationResponse> createProfile(
            @RequestBody ProfileCreationRequest request
    ); // chỉ khai báo hàm
}
