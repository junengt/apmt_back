package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.post.Tag;

import java.util.List;

public interface TagRepositoryCustom {
    List<Tag> findByName(List<String> names);
}
