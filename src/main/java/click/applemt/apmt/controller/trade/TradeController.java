package click.applemt.apmt.controller.trade;

import click.applemt.apmt.controller.post.PostApiController;
import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.security.util.RequestUtil;
import click.applemt.apmt.service.TradeService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
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
      return new Result(tradeService.trade(authUser,Long.parseLong(postIdRequest.getPostId()),postIdRequest.getPointPay()));
    }

    @Data
    static class PostIdRequest{
        private String postId;
        private Boolean pointPay;
    }
    @GetMapping("/trade/{id}")
    public Result getPost(@PathVariable Long id, @AuthenticationPrincipal AuthUser authUser) throws Exception {
       return new Result(tradeService.findTradeOne(authUser,id));
    }



}
