package click.applemt.apmt.controller.postController;

import click.applemt.apmt.domain.post.PostTag;
import lombok.Data;

import java.util.List;

@Data
public class PostDto {
    private String title;
    private int price;
    private String content;
}
