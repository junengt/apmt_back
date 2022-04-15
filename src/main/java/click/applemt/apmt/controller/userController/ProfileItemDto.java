package click.applemt.apmt.controller.userController;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileItemDto {

    private String title;
    private String division;
    private Long price;
    private String date;
}
