package click.applemt.apmt.controller.postController;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostListDto {
    private Long id;
    private String title;
    private int price;
    private String content;
}
