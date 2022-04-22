package click.applemt.apmt.controller.post;

import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.security.util.RequestUtil;
import click.applemt.apmt.service.PostService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;
    private final FirebaseAuth firebaseAuth;

    //등록된 중고거래 글 조회 API
    @GetMapping("/items")
    public Result getPostList(@RequestParam(defaultValue = "", required = false) PostSearchCondition searchCond) {
        return new Result(postService.findAllPostAndSearchKeyword(searchCond));
    }

    //중고거래 글 삭제 API(DELETE X, UPDATE O)
    @DeleteMapping("/items/{id}")
    public void deletePost(@PathVariable Long id,
                           @AuthenticationPrincipal AuthUser authUser) {
        postService.deleteByPostId(id, authUser);
    }

    //중고거래 글 등록 API
    @PostMapping("/items")
    public String savePost(@AuthenticationPrincipal AuthUser authUser,
                         @RequestPart PostReqDto postReqDto,
                         @RequestPart(name = "file", required = false) List<MultipartFile> files) {
        Long postId = postService.savePost(postReqDto, authUser);
        postService.savePostPhotos(postId, files);
        return "등록됨";
    }

    //중고거래 글 수정 API
    @PatchMapping("/items")

    @GetMapping("/items/{id}")
    public Result getPost(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false, defaultValue = "") String auth) throws FirebaseAuthException {
        FirebaseToken decodedToken = null;
        if (!auth.isEmpty()) {
            try {
                String header = RequestUtil.getAuthorizationToken(auth);
                decodedToken = firebaseAuth.verifyIdToken(header);
            } catch (FirebaseAuthException | IllegalArgumentException e) {
                log.info("token verify exception: " + e.getMessage());

            }
        }
        return new Result(postService.findOne(id,decodedToken));
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
