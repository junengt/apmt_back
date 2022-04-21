package click.applemt.apmt.controller.profile;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api")

public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public AuthUser info(@AuthenticationPrincipal AuthUser authUser){
        return authUser;
    }
    @PostMapping("/user")
    public AuthUser save(@AuthenticationPrincipal AuthUser authUser){
        return authUser;
    }

    @PostMapping("/point")
    public User updatePoint(@RequestBody PointDto data, @AuthenticationPrincipal AuthUser authUser){
        return userService.pointUpdate(data,authUser);
    }


}
