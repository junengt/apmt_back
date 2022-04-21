package click.applemt.apmt.controller.post;

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
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;
    private final FirebaseAuth firebaseAuth;

    //등록된 중고거래 글 조회 API
    @GetMapping("/items")
    public Result getPostList(@RequestParam(defaultValue = "", required = false) PostSearchCondition searchCond, Pageable pageable) {
        return new Result(postService.findAllPostAndSearchKeyword(searchCond, pageable));
    }

    @DeleteMapping("/items/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deleteByPostId(id);
    }

//    @PostMapping("/posts")
//    public void savePost(@AuthenticationPrincipal AuthUser authUser,
//                         @RequestPart PostDto postDto,
//                         @RequestPart PostTagDto postTagDto,
//                         @RequestPart(name = "file", required = false) List<MultipartFile> files,) {
//        Long postId = postService.savePost(postDto,authUser);
//        postService.savePostPhotos(postId, files);
//        postService.savePostTags(postId,);
//
//    }

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
