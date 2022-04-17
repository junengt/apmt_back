package click.applemt.apmt.domain.post;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "posts")
@Getter @Setter
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "posts_id")
    private Long id;

    @ManyToOne(fetch = LAZY) //양방향 연관관계로 설계함
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "posts_title")
    private String title;

    @Column(name = "posts_price")
    private int price;

    @Column(name = "posts_content")
    private String content;

    @Column(name = "posts_delete_yn")
    private boolean deleted = false;

    @Column(name = "posts_status")
    @Enumerated(EnumType.STRING)
    private TradeStatus status ; //ing : 판매 중 / end : 판매 완료 / RESERVATION 예약 / HIDE 숨기기

    @Column(name = "posts_town")
    private String postTownCode;

    private Integer view;

}
