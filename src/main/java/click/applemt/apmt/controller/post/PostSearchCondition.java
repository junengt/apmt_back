package click.applemt.apmt.controller.post;

import click.applemt.apmt.domain.post.TradeStatus;
import lombok.Data;


@Data
public class PostSearchCondition {
    private String search;
    private TradeStatus status;

    public PostSearchCondition(String search) {
        this.search = search;
    }
}
