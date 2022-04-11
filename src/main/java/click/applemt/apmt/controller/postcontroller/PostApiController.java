package click.applemt.apmt.controller.postcontroller;

import click.applemt.apmt.service.postservice.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    //등록된 중고거래 글 조회 API
    @GetMapping("/posts")
    public Result getPost(String searchKeyword) {
        return new Result(postService.findAllPostAndSearchKeyword(searchKeyword));
    }

    @PutMapping("/posts/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deleteByPostId(id);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
