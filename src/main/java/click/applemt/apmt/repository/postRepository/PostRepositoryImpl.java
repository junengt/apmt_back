package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.post.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static click.applemt.apmt.domain.post.QPost.*;

public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Post> findAll() {

        return queryFactory
                .selectFrom(post)
                .where(post.deleted.isFalse())
                .fetch();
    }

    @Override
    public List<Post> findPostsBySearch(String searchKeyword) {

        return queryFactory
                .selectFrom(post)
                .where(post.title.contains(searchKeyword)
                        .and(post.deleted.isFalse()))
                .fetch();
    }

    @Override
    public void updatePostDelete(Long postId) {

        queryFactory
                .update(post)
                .set(post.deleted,true)
                .where(post.id.eq(postId))
                .execute();
    }
}
