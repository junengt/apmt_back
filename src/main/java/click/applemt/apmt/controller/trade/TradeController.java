package click.applemt.apmt.controller.trade;

import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.service.TradeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class TradeController {

    private final TradeService tradeService;

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }


    @PutMapping("/trade")
    public Result trade(@AuthenticationPrincipal AuthUser authUser,@RequestBody PostIdRequest postIdRequest){
      return new Result(tradeService.trade(authUser,Long.parseLong(postIdRequest.getPostId())));
    }

    @Data
    static class PostIdRequest{
        private String postId;
    }
}
