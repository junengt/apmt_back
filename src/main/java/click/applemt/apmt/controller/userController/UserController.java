package click.applemt.apmt.controller.userController;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public User save(@RequestBody UidDataDTO uidDataDTO){
        return userService.join(uidDataDTO);
    }

    @GetMapping("/user")
    public User findOne(String uid){
        return userService.findOne(uid);
    }

    @PostMapping("/point")
    public User updatePoint( @RequestBody PointDTO data){
        return userService.pointUpdate(data);
    }


}
