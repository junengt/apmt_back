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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public Result getPostList(@RequestParam(defaultValue = "", required = false) String search) {
        return new Result(postService.findAllPostAndSearchKeyword(search));
    }
    //고객이 등록한 판매중 조회 API
    @GetMapping("/sale")
    public Result getUserPostList(@AuthenticationPrincipal AuthUser authUser){
        return new Result(postService.findUserPostSellingList(authUser.getUid()));
    }

    //고객이 구매한 리스트 조회 API
    @GetMapping("/buy")
    public Result getUserBuyList(@AuthenticationPrincipal AuthUser authUser){
        return new Result(postService.findUserBuyingList(authUser.getUid()));
    }

    @PutMapping("/posts/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deleteByPostId(id);
    }


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



    // 판매글에서 판매자의 판매 목록 조회 API
    /**
     * 판매자의 정보 + 전체 판매 목록 + 리뷰 목록을 가져온다
     * @param uid 판매자의 ID
     * @return 판매자의 정보 + 전체 판매 목록 + 리뷰 목록
     */
    @GetMapping("/seller_profile/{uid}")
    public Result getPostsSellerProfile(@PathVariable String uid) {
        return new Result(postService.getSellerInfoByUserId(uid));
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
