package click.applemt.apmt.controller.review;

import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.service.ReviewService;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;


    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }


    @PutMapping("/saveReview")
    public Result saveReview(@RequestBody ReviewForm reviewForm) {
        return new Result(reviewService.saveReview(reviewForm));
    }

    @GetMapping("/review/{id}")
    public Result findReview(@PathVariable("id") Long id) throws FirebaseAuthException {
        return new Result(reviewService.findOne(id));
    }

}
