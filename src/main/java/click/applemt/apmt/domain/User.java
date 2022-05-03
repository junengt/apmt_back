package click.applemt.apmt.domain;

import click.applemt.apmt.domain.point.TradeHistory;
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

    @Id
    @Column(name = "user_id")
    private String uid;

    @JsonIgnore
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY) //양방향 연관관계로 설계
    private List<Post> posts = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<TradeHistory> tradeHistories = new ArrayList<>();

    @Column(name = "pay_account")
    private Long account = 0l;


    public User newUser(String uid){
        this.account = 0l;
        this.uid = uid;
        return this;
    }

    public void plusAccount(Long point){
        this.account += point;
    }
    public void minusAccount(Long point){
        this.account -= point;
    }
}
