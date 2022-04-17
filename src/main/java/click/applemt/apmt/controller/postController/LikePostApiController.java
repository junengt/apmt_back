package click.applemt.apmt.controller.postController;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.service.LikePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikePostApiController {

    private LikePostService likePostService;

    @PostMapping("/likePost/{id}")
    public ResponseEntity<String> addLike(@AuthenticationPrincipal AuthUser authUser,
                                          @PathVariable Long postId) {
        boolean result = false;

        if(authUser != null) {
            result = likePostService.addLike(authUser,postId);
        }
        return result ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
