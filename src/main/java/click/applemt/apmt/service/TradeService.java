package click.applemt.apmt.service;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.domain.post.TradeStatus;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.tradeHistroyRepository.TradeHistoryRepository;
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

    @Transactional
    public TradeDto trade(AuthUser user, Long postId){

        Post findPost = postRepository.findById(postId).get();
        User postUser = findPost.getUser();
        User buyer = userRepository.findById(user.getUid()).get();

        TradeHistory tradeHistory = new TradeHistory();
        Long price = findPost.getPrice();
        tradeHistory.setPrice(price);
        tradeHistory.setPost(findPost);
        tradeHistory.setUser(buyer);

        historyRepository.save(tradeHistory);

        findPost.setStatus(TradeStatus.END);

        buyer.minusAccount(price);
        postUser.plusAccount(price);

        TradeDto tradeDto = new TradeDto();

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
