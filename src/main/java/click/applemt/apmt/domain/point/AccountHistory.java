package click.applemt.apmt.domain.point;

import click.applemt.apmt.controller.profile.PointDto;
import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountHistory extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_history_id")
    private Long id;

    @ManyToOne(fetch = LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;

    private String content;

    @Enumerated(EnumType.STRING)
    private AccountDivision division;

    private Long price;

    public AccountHistory pointDtoToAccountHistory(PointDto data, User user){
        this.user = user;
        this.price = Long.valueOf(data.getPoint());
        this.content = "계좌거래";
        this.division = data.isChargeOrRefund() ? AccountDivision.DEPOSIT : AccountDivision.WITHDRAW;
        return this;
    }

}
