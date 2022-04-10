package click.applemt.apmt.domain.point;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="user_id")
    private User userId;

    @Enumerated(EnumType.STRING)
    private AccountDivision division;

    private Long price;


}
