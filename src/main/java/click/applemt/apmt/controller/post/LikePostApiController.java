package click.applemt.apmt.controller.post;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.repository.postRepository.LikePostRepository;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.service.LikePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikePostApiController {

    private final LikePostService likePostService;

    @PostMapping("/likePost/{id}")
    public ResponseEntity<String> addLike(@AuthenticationPrincipal AuthUser authUser,
                                          @PathVariable("id") Long postId) {

        boolean result;

        result = likePostService.addLike(authUser, postId);

        return result ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
