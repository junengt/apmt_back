package click.applemt.apmt.controller.post;

import lombok.Data;

import java.util.List;

@Data
public class PostUpdateReqDto {
    private  String title;
    private  List<String> tags;
    private  Long price;
    private  String content;
    private  String town;
}
