package click.applemt.apmt.controller.postController;

import click.applemt.apmt.service.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    //등록된 중고거래 글 조회 API
    @GetMapping("/posts")
    public Result getPost(String searchKeyword) {
        return new Result(postService.findAllPostAndSearchKeyword(searchKeyword));
    }

    @DeleteMapping("/posts/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deleteByPostId(id);
    }

//    @PostMapping("/posts")
//    public void savePost(@RequestPart
//                             @RequestPart(name = "file", required = false) List<MultipartFile> files) {
//        Long postId = postService.savePost();
//        postService.savePostPhotos(postId, files);
//        postService.savePostTags(postId,);
//
//    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
