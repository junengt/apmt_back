package click.applemt.apmt.service;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.post.LikePost;
import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.repository.postRepository.LikePostRepository;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import click.applemt.apmt.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikePostService {

    private final PostRepository postRepository;
    private final LikePostRepository likePostRepository;
    private final UserRepository userRepository;

    //좋아요 기능 로직
    @Transactional
    public boolean addLike(AuthUser authUser, Long postId) {
        User findUser = userRepository.findById(authUser.getUid()).orElseThrow();
        Post findPost = postRepository.findById(postId).orElseThrow();
        if(isNotLike(findUser, findPost)) {
            likePostRepository.save(new LikePost(findUser, findPost));
            return true;
        }
        return false;
    }

    private boolean isNotLike(User user, Post findPost) {
        return likePostRepository.findByUserAndPost(user,findPost).isPresent();
    }
}
