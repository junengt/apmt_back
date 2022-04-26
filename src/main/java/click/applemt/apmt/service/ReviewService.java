package click.applemt.apmt.service;

import click.applemt.apmt.controller.review.ReviewForm;
import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.Review;
import click.applemt.apmt.repository.reviewRepository.ReviewRepository;
import click.applemt.apmt.repository.tradeHistroyRepository.TradeHistoryRepository;
import click.applemt.apmt.security.AuthUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TradeHistoryRepository historyRepository;

    public ReviewDto saveReview( ReviewForm reviewForm){
        TradeHistory trade = historyRepository.findById(reviewForm.getTradeId()).get();

        Review review = new Review();
        review.setTradeHistory(trade);
        review.setContent(reviewForm.getContent());

        Review save = reviewRepository.save(review);

        ReviewDto reviewDto = new ReviewDto();

        reviewDto.setReviewId(save.getId());

        return reviewDto;

    }

    public ReviewDataDto findOne(Long id) throws FirebaseAuthException {
        Review review = reviewRepository.findById(id).get();
        ReviewDataDto reviewDataDto = new ReviewDataDto();
        UserRecord seller = FirebaseAuth.getInstance().getUser(review.getTradeHistory().getPost().getUser().getUid());

        reviewDataDto.setContent(review.getContent());
        reviewDataDto.setItemTitle(review.getTradeHistory().getPost().getTitle());
        reviewDataDto.setSellerName(seller.getDisplayName());

        return reviewDataDto;
    }

    @Data
    static class ReviewDto{
        private Long reviewId;
    }

    @Data
    static class ReviewDataDto{
        private String content;
        private String itemTitle;
        private String sellerName;
    }

}
