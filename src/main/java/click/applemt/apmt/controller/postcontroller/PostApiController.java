package click.applemt.apmt.controller.postcontroller;

import click.applemt.apmt.service.postservice.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    @GetMapping("/posts") //등록된 모든 중고거래 글 조회 API
    public Result getPost(String searchKeyword) {
        return new Result(postService.findAllPostAndSearchKeyword(searchKeyword));
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
