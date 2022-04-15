package click.applemt.apmt.controller.userController;

import lombok.Data;

@Data
public class PointDTO {

    private String uid;
    private int point;
    private boolean chargeOrRefund;

}
