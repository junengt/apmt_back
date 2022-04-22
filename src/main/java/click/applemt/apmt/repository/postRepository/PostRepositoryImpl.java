package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.controller.post.PostSearchCondition;
import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.domain.post.QTag;
import click.applemt.apmt.domain.post.TradeStatus;
import click.applemt.apmt.security.AuthUser;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static click.applemt.apmt.domain.post.QPost.*;
import static click.applemt.apmt.domain.post.QTag.*;
import static org.springframework.util.StringUtils.*;

public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Post> findPostsBySearch(PostSearchCondition searchCond) {
        return queryFactory
                .selectFrom(post)
                .join(post.tags, tag)
                .where(post.title.contains(searchCond.getSearch())
                        .and(post.deleted.isFalse())
                        .and(post.status.in(TradeStatus.ING,TradeStatus.END))
                        .and(tag.name.in(searchCond.getTags())))
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
