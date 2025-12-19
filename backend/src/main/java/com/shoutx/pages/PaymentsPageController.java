package com.shoutx.pages;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.*;

@RestController
@RequestMapping("/api/pages/payments")
public class PaymentsPageController {
    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        return ResponseEntity.ok(Arrays.asList(
            new Object() {
                public String getId() { return "basic"; }
                public String getName() { return "Basic"; }
                public Integer getPrice() { return 0; }
                public Integer getDailyRequests() { return 10; }
            },
            new Object() {
                public String getId() { return "pro"; }
                public String getName() { return "Pro"; }
                public Integer getPrice() { return 99; }
                public Integer getDailyRequests() { return 50; }
            }
        ));
    }
    @PostMapping("/upgrade-to-pro")
    public ResponseEntity<?> upgradeToPro(@RequestBody Object payment) {
        return ResponseEntity.ok("Upgrade initiated");
    }
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestParam String paymentId) {
        return ResponseEntity.ok("Payment confirmed");
    }
    @GetMapping("/payment-history")
    public ResponseEntity<?> getPaymentHistory() {
        return ResponseEntity.ok("Payment history");
    }
}