package click.applemt.apmt.domain;

import click.applemt.apmt.domain.post.Post;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "users")//h2 DB에선 USER가 예약어이므로 users로 설정 나중에 user로 바꿔야함
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "firebase_uid", nullable = false)
    private String uid;

    @OneToMany(mappedBy = "user") //양방향 연관관계로 설계
    private List<Post> posts = new ArrayList<>();

    @Column(name = "pay_account")
    private Long account;
}
