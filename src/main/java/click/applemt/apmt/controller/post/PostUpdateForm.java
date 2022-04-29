package click.applemt.apmt.controller.post;

import click.applemt.apmt.domain.post.PostsPhoto;
import lombok.Data;

import java.util.List;

@Data
public class PostUpdateForm {
    private String title;
    private List<PostsPhoto> postsPhotos;
    private List<String> tags;
    private Long price;
    private String content;
    private String town;
}
