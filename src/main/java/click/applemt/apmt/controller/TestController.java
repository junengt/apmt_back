package click.applemt.apmt.controller;

import click.applemt.apmt.security.AuthUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/app")
public class TestController {

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal AuthUser authUser){
        return authUser.getUid();
    }
}
