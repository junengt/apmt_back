package click.applemt.apmt.service;

import click.applemt.apmt.domain.post.Review;
import click.applemt.apmt.repository.reviewRepository.ReviewRepository;
import click.applemt.apmt.util.Time;
import com.google.firebase.auth.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;

    /**
     * uid에 해당하는 판매자의 uid, displayName을 가져온다
     * @param uid 판매자의 uid
     * @return 판매자 정보(uid, displayName)
     */
    public SellerInfoDto getSellerInfoByUserId(String uid) throws FirebaseAuthException {
        // 반환 값 초기 생성
        SellerInfoDto sellerInfo = new SellerInfoDto();
        UserRecord seller = FirebaseAuth.getInstance().getUser(uid);
        // 판매자 정보 DTO에 값 설정
        sellerInfo.setSellerUid(uid);
        sellerInfo.setSellerDisplayName(seller.getDisplayName());
        sellerInfo.setSellerPhoto(seller.getPhotoUrl());

        return sellerInfo;
    }

    public List<ReviewListDto> getSellerReviewsBySellerId(String uid) throws FirebaseAuthException, ExecutionException, InterruptedException {

        // 후기 내역 DTO List 생성
        List<ReviewListDto> reviewListDtos = new ArrayList<>();

        // 판매자 uid로 판매자의 전체 후기 내a역을 가져온다
        List<Review> reviews = reviewRepository.getReviewsBySellerUid(uid);

        Collection<UserIdentifier> uidList = new ArrayList<>();
        for (Review review : reviews) {
            uidList.add(new UidIdentifier(review.getBuyerUid()));
        }
        GetUsersResult result = FirebaseAuth.getInstance().getUsersAsync(uidList).get();
        // 전체 후기 내역을 순회한다
        for (Review review : reviews) {
            // 후기 내역 DTO 생성
            ReviewListDto reviewListDto = new ReviewListDto();

            // 후기 내역 DTO 값 설정
            String buyerUid = review.getBuyerUid();
            Optional<UserRecord> buyer = result.getUsers()
                    .stream()
                    .filter(u -> u.getUid().equals(buyerUid)).findFirst();

            reviewListDto.setId(review.getId());
            reviewListDto.setBuyerUid(buyerUid);
            if (buyer.isPresent()) {
                reviewListDto.setBuyerDisplayName(buyer.get().getDisplayName());
                reviewListDto.setBuyerPhoto(buyer.get().getPhotoUrl());
            }
            reviewListDto.setContent(review.getContent());
            reviewListDto.setAfterDate(Time.calculateTime(Timestamp.valueOf(review.getCreatedTime())));
            // 후기 내역 DTO List에 후기 내역 DTO List 추가
            reviewListDtos.add(reviewListDto);
        }
        return reviewListDtos;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class SellerInfoDto {    // 판매자 정보
        private String sellerUid;
        private String sellerDisplayName;
        private String sellerPhoto;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ReviewListDto {    // 후기 내역
        private Long id;
        private String buyerUid;
        private String buyerDisplayName;
        private String buyerPhoto;
        private String content;
        private String afterDate;
    }
}
