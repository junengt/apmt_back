package click.applemt.apmt.controller.postController;

import click.applemt.apmt.domain.post.Tag;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostTagDto {
    private List<Tag> tags = new ArrayList<>();
}
