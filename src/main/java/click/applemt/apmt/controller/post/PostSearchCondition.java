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
    private String tags = "";
    public PostSearchCondition(String search) {
        this.search = search;
    }

}
