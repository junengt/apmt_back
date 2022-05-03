package click.applemt.apmt.repository.postRepository;

import java.util.List;

public interface PostsPhotoRepositoryCustom {
    void deleteByPostId(Long postId);

    void deleteByPaths(List<String> paths,Long postId);
}
