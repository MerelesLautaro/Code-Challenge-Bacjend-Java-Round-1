package com.hackathon.finservice.Controllers.dashboard;

import com.hackathon.finservice.DTO.response.dashboard.AccountDetailResponse;
import com.hackathon.finservice.DTO.response.dashboard.UserDetailsResponse;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Service.customer.AccountService;
import com.hackathon.finservice.Service.customer.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final AccountService accountService;

    @GetMapping("/user")
    public ResponseEntity<UserDetailsResponse> getUserInfo() {
        return ResponseEntity.ok(userService.getLoggedInUserDetails());
    }


    @GetMapping("/account")
    public ResponseEntity<AccountDetailResponse> getAccountDetail() {
        return ResponseEntity.ok(accountService.getLoggedInUserAccount());
    }

    @GetMapping("/account/{index}")
    public ResponseEntity<AccountDetailResponse> getAccountByIndex(@PathVariable String index) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        AccountDetailResponse response = accountService.getAccountByIndex(user, index);

        return ResponseEntity.ok(response);
    }

}
