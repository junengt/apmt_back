package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.post.QTag;
import click.applemt.apmt.domain.post.Tag;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

import static click.applemt.apmt.domain.post.QTag.*;

public class TagRepositoryImpl implements TagRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public TagRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<Tag> findByName(List<String> names) {

        return queryFactory
                .selectFrom(tag)
                .where(tag.name.in(names))
                .fetch();
    }
}
