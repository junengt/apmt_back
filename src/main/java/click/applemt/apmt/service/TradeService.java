package click.applemt.apmt.service;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.point.AccountDivision;
import click.applemt.apmt.domain.point.AccountHistory;
import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.domain.post.Review;
import click.applemt.apmt.domain.post.TradeStatus;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.reviewRepository.ReviewRepository;
import click.applemt.apmt.repository.tradeHistroyRepository.TradeHistoryRepository;
import click.applemt.apmt.repository.userRepository.AccountHistoryRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import click.applemt.apmt.security.AuthUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeHistoryRepository historyRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public TradeDto trade(AuthUser user, Long postId,boolean isPointPay) {

        Post findPost = postRepository.findById(postId).get();
        //게시글을 찾는다.
        User postUser = findPost.getUser();
        //게시글 작성자 ( 판매자 ) 를 가져온다.
        User buyer = userRepository.findByUid(user.getUid()).get();
        //구매자를 찾는다.
        TradeHistory tradeHistory = new TradeHistory();
        //구매내역을 인스턴스화 한다.
        Long price = findPost.getPrice();
        tradeHistory.setPrice(price);
        tradeHistory.setPost(findPost);
        tradeHistory.setUser(buyer);
        //찾은 데이터들을 조합한 후
        historyRepository.save(tradeHistory);
        //데이터 베이스에 집어넣는다.

        findPost.setStatus(TradeStatus.END);
        //판매 상태를 변경하고

        //포인트결제가 true인 경우에만 포인트 정보를 변경한다.
        if(isPointPay){
            buyer.minusAccount(price);
            // 구매자의 포인트를 차감 한다. 동시에 포인트 내역을 추가해준다.
            AccountHistory buyHistory = new AccountHistory();
            // 거래 내역 객체를 인스턴스 화한다.
            buyHistory.setPrice(price);
            buyHistory.setDivision(AccountDivision.BUY);
            buyHistory.setContent(findPost.getTitle());
            buyHistory.setUser(buyer);

            accountHistoryRepository.save(buyHistory);
            //구매내역을 넣어준다.


            AccountHistory sellHistory = new AccountHistory();
            // 거래 내역 객체를 인스턴스 화한다.
            sellHistory.setPrice(price);
            sellHistory.setDivision(AccountDivision.SELL);
            sellHistory.setContent(findPost.getTitle());
            sellHistory.setUser(postUser);

            accountHistoryRepository.save(sellHistory);
            //판매내역을 넣어준다.

            postUser.plusAccount(price);
        }

        // 판매자의 포인트에 넣어준다.
        TradeDto tradeDto = new TradeDto();
        // 반환할 데이터를 만들어 넣어준다.
        tradeDto.setTradeId(tradeHistory.getId());

        return tradeDto;

    }

    public ReviewPageTradeDto findTradeOne(AuthUser authUser, Long tradeId) throws Exception {

        TradeHistory trade = historyRepository.findById(tradeId).get();
        // 거래내역을 찾는다.
        long count = trade.getReviews().stream().filter(review -> !review.isDeleted()).count();
        //삭제되지 않은 리뷰 수를 조회한다.
        boolean b = !trade.getUser().getUid().equals(authUser.getUid());
        //내 리뷰인지 판단한다.
        if (b || count > 0) {
            throw new Exception("잘못된 요청");
        } // 내리뷰가 아니거나 이미 리뷰가 작성된 경우 null을 반환한다.

        //거래내역 구매자 ID와 인증유저와 비교해서 틀리면 null을 반환시킨다.
        UserRecord seller = FirebaseAuth.getInstance().getUser(trade.getPost().getUser().getUid());
        // 판매자 닉네임을 얻기위해 uid로 파베데이터를 가져온다.
        ReviewPageTradeDto reviewPageTradeDto = new ReviewPageTradeDto();
        reviewPageTradeDto.setTradeId(trade.getId());
        reviewPageTradeDto.setItemName(trade.getPost().getTitle());
        reviewPageTradeDto.setSellerName(seller.getDisplayName());
        //판매자 닉네임으로 설정한다.

        return reviewPageTradeDto;
    }

    @Data
    static class TradeDto {


        private Long tradeId;

    }

    @Data
    static class ReviewPageTradeDto {
        private String sellerName;
        private String ItemName;
        private Long tradeId;

    }

}
