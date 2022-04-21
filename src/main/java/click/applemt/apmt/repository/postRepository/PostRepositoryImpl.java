package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.controller.post.PostSearchCondition;
import click.applemt.apmt.domain.post.Post;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static click.applemt.apmt.domain.post.QPost.*;

public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> findPostsBySearch(PostSearchCondition searchCond, Pageable pageable) {
        QueryResults<Post> results = queryFactory
                .selectFrom(post)
                .where(post.title.contains(searchCond.getSearch())
                        .and(post.deleted.isFalse()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Post> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
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
