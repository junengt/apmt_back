package click.applemt.apmt.domain.point;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.common.BaseEntity;
import click.applemt.apmt.domain.post.Post;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@Table(name = "trade_history")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class TradeHistory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_history_id")
    private Long id;

    @ManyToOne(fetch = LAZY,cascade = CascadeType.ALL) //단방향 연관관계 설계
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY,cascade = CascadeType.ALL) //단방향 연관관계 설계
    @JoinColumn(name = "posts_id")
    private Post post;

    private Long price;




}
