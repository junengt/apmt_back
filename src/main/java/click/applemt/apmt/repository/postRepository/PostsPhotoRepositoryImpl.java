package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.post.QPostsPhoto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;

import static click.applemt.apmt.domain.post.QPostsPhoto.*;

public class PostsPhotoRepositoryImpl implements PostsPhotoRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PostsPhotoRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public void deleteByPostId(Long postId) {
        queryFactory
                .delete(postsPhoto)
                .where(postsPhoto.post.id.eq(postId))
                .execute();
    }

    @Override
    public void deleteByPaths(List<String> paths,Long postId) {

        queryFactory
                .delete(postsPhoto)
                .where(postsPhoto.photoPath.notIn(paths).and(postsPhoto.post.id.eq(postId)))
                .execute();

    }

}
