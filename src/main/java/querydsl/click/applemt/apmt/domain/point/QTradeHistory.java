package click.applemt.apmt.domain.point;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTradeHistory is a Querydsl query type for TradeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTradeHistory extends EntityPathBase<TradeHistory> {

    private static final long serialVersionUID = 1796634131L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTradeHistory tradeHistory = new QTradeHistory("tradeHistory");

    public final click.applemt.apmt.domain.common.QBaseEntity _super = new click.applemt.apmt.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final click.applemt.apmt.domain.post.QPost post;

    public final NumberPath<Long> price = createNumber("price", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public final click.applemt.apmt.domain.QUser user;

    public QTradeHistory(String variable) {
        this(TradeHistory.class, forVariable(variable), INITS);
    }

    public QTradeHistory(Path<? extends TradeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTradeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTradeHistory(PathMetadata metadata, PathInits inits) {
        this(TradeHistory.class, metadata, inits);
    }

    public QTradeHistory(Class<? extends TradeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new click.applemt.apmt.domain.post.QPost(forProperty("post"), inits.get("post")) : null;
        this.user = inits.isInitialized("user") ? new click.applemt.apmt.domain.QUser(forProperty("user")) : null;
    }

}

