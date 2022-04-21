package click.applemt.apmt.domain.post;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostsPhoto is a Querydsl query type for PostsPhoto
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostsPhoto extends EntityPathBase<PostsPhoto> {

    private static final long serialVersionUID = 1961085608L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostsPhoto postsPhoto = new QPostsPhoto("postsPhoto");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath photoPath = createString("photoPath");

    public final QPost post;

    public QPostsPhoto(String variable) {
        this(PostsPhoto.class, forVariable(variable), INITS);
    }

    public QPostsPhoto(Path<? extends PostsPhoto> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostsPhoto(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostsPhoto(PathMetadata metadata, PathInits inits) {
        this(PostsPhoto.class, metadata, inits);
    }

    public QPostsPhoto(Class<? extends PostsPhoto> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

