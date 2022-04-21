package click.applemt.apmt.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 903937136L;

    public static final QUser user = new QUser("user");

    public final NumberPath<Long> account = createNumber("account", Long.class);

    public final ListPath<click.applemt.apmt.domain.post.Post, click.applemt.apmt.domain.post.QPost> posts = this.<click.applemt.apmt.domain.post.Post, click.applemt.apmt.domain.post.QPost>createList("posts", click.applemt.apmt.domain.post.Post.class, click.applemt.apmt.domain.post.QPost.class, PathInits.DIRECT2);

    public final ListPath<click.applemt.apmt.domain.point.TradeHistory, click.applemt.apmt.domain.point.QTradeHistory> tradeHistories = this.<click.applemt.apmt.domain.point.TradeHistory, click.applemt.apmt.domain.point.QTradeHistory>createList("tradeHistories", click.applemt.apmt.domain.point.TradeHistory.class, click.applemt.apmt.domain.point.QTradeHistory.class, PathInits.DIRECT2);

    public final StringPath uid = createString("uid");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

