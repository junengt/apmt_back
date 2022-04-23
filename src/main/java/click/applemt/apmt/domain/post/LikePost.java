package click.applemt.apmt.domain.post;

import click.applemt.apmt.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@Table(name = "like_posts")
public class LikePost {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_post_id")
    private Long id;

    @ManyToOne(fetch = LAZY) //단방향 연관관계 설계
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY) //단방향 연관관계 설계
    @JoinColumn(name = "posts_id")
    private Post post;

    public LikePost(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
