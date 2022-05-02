package click.applemt.apmt.domain.post;

import click.applemt.apmt.domain.common.BaseEntity;
import click.applemt.apmt.domain.point.TradeHistory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@Table(name = "review")
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY) //단방향 연관관계 설계
    @JoinColumn(name = "trade_history_id")
    private TradeHistory tradeHistory;

    @Column(name = "review_seller_uid")
    private String sellerUid;   // 리뷰를 받은 판매자의 UID

    @Column(name = "review_buyer_uid")
    private String buyerUid;    // 리뷰를 작성한 구매자의 UID

    @Column(name = "review_content")
    private String content;
}
