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

    //고객이 찜한 리스트 조회 API
    @GetMapping("/like")
    public Result getUserLikeList(@AuthenticationPrincipal AuthUser authUser){
        return new Result<>(postService.findUserLikePostList(authUser.getUid()));
    }

    //중고거래 글 삭제 API(DELETE X, UPDATE O)
    @DeleteMapping("/items/{id}")
    public String deletePost(@PathVariable("id") Long postId,
                           @AuthenticationPrincipal AuthUser authUser) {
        postService.deleteByPostId(postId, authUser);
        return "삭제됨";
        //인증 정보가 올바르지 않아도 삭제됨으로 표시되나 조회 쿼리만 나가고 삭제 플래그 업데이트 쿼리가 나가진 않음 에러처리 해야함
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
    @PutMapping("/items/{id}")
    public String updatePost(@AuthenticationPrincipal AuthUser authUser,
                             @PathVariable("id") Long postId,
                             @RequestPart PostUpdateReqDto postUpdateReqDto,
                             @RequestPart(name = "file", required = false) List<MultipartFile> files) {
        Long updatePostId = postService.updatePost(postId, postUpdateReqDto, authUser);
        postService.savePostPhotos(updatePostId,files);
        return "수정됨";
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

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
