package click.applemt.apmt.controller.post;

import click.applemt.apmt.domain.post.TradeStatus;
import lombok.Data;

import java.util.List;


@Data
public class PostSearchCondition {
    private String search;
    private TradeStatus status;
    private List<String> tags;

    public PostSearchCondition(String search) {
        this.search = search;
    }
}
