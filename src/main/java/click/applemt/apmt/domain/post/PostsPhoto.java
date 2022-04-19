package click.applemt.apmt.domain.post;

import click.applemt.apmt.domain.post.Post;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@Table(name = "posts_photo")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostsPhoto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "posts_photo_id")
    private Long id;

    @Column(name = "posts_photo_path")
    private String photoPath;
    @JsonIgnore
    @ManyToOne(fetch = LAZY) //단방향 연관관계로 설계
    @JoinColumn(name = "posts_id")
    private Post post;
}
