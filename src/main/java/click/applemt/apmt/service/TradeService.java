package click.applemt.apmt.service;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.point.AccountDivision;
import click.applemt.apmt.domain.point.AccountHistory;
import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.domain.post.TradeStatus;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.tradeHistroyRepository.TradeHistoryRepository;
import click.applemt.apmt.repository.userRepository.AccountHistoryRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import click.applemt.apmt.security.AuthUser;
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

    @Transactional
    public TradeDto trade(AuthUser user, Long postId){

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
        // 판매자의 포인트에 넣어준다.
        TradeDto tradeDto = new TradeDto();
        // 반환할 데이터를 만들어 넣어준다.
        tradeDto.setTrade_id(tradeHistory.getId());
        tradeDto.setStatus(TradeStatus.END);
        tradeDto.setPost_id(postId);
        tradeDto.setUser_id(user.getUid());

        return tradeDto;

    }

    @Data
    static class TradeDto{

        private Long post_id;
        private String user_id;
        private Long trade_id;
        private TradeStatus status;
    }

}
