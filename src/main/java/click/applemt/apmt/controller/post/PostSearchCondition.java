package click.applemt.apmt.controller.post;

import click.applemt.apmt.domain.post.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchCondition {
    private String search;
    private TradeStatus status;
    private List<String> tags;

    public PostSearchCondition(String search) {
        this.search = search;
    }

    public PostSearchCondition(String search, TradeStatus status) {
        this.search = search;
        this.status = status;
    }
}
