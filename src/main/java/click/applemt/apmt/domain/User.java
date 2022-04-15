package click.applemt.apmt.domain;

import click.applemt.apmt.controller.userController.UidDataDTO;
import click.applemt.apmt.domain.post.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY) //양방향 연관관계로 설계
    private List<Post> posts = new ArrayList<>();

    @Column(name = "pay_account")
    private Long account;

    public User UidDataToUser(UidDataDTO uid){
        this.uid = uid.getUid();
        this.account = 0l;
        return this;
    }
    public User newUser(String uid){
        this.uid = uid;
        this.account = 0l;
        return this;
    }

    public void plusAccount(int point){
        this.account += point;
    }
    public void minusAccount(int point){
        this.account -= point;
    }
}
