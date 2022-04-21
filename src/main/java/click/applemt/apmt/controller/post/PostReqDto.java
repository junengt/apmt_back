package click.applemt.apmt.controller.post;

import click.applemt.apmt.domain.post.PostsPhoto;
import click.applemt.apmt.domain.post.Tag;
import click.applemt.apmt.domain.post.TradeStatus;
import lombok.Data;

import java.util.List;

@Data
public class PostReqDto {
    private final String title;
    private final List<String> tags;
    private Long price;
    private String content;
    private String town;
}
