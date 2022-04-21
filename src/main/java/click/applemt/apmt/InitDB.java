package click.applemt.apmt;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.*;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("local")
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.doInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;
        private final UserRepository userRepository;
        private final PostRepository postRepository;

        public void doInit(){

            User user = new User();
            user.setAccount(0l);
            user.setUid("6ZQGTNxeyAd7bDWdz6fCEPzwn9v2");
//            db설정이 create-drop인 경우에만 실행

            TradeHistory history = new TradeHistory();

            Tag tag = new Tag();
            tag.setName("Mac");
            em.persist(tag);
            Tag tag1 = new Tag();
            tag1.setName("MacBook Air");
            em.persist(tag1);

            List<Tag> tags = new ArrayList<>();
            tags.add(tag);
            tags.add(tag1);



            Post post = new Post();
            post.setTags(tags);
            post.setUser(user);
            post.setTitle("[맥북프로]Macbook pro 13' 2015 early retina\n");
            post.setContent("[Macbook Pro 13' 2015 early retina 256GB]\n" +
                    "\n" +
                    "뒷판에 눌림 한곳 있고 모서리에 찍힘 여러군데 있습니다.\n" +
                    "충전케이블이 사용감은 있지만 성능상 문제는 없으며 약한 부분은 검정케이블로 보강해뒀습니다. 풀박스 구성에서 연장선은 없고 그자리에 매직마우스2 화이트 채워서 드립니다.\n" +
                    "\n" +
                    "성능상 아무 하자 없고 최대 사이클(1000사이클)의 절반 사용한 상태 이며 베터리 최대 충전량도 4740mAh라 아직 90%의 베터리 효율을 보입니다.\n" +
                    "\n" +
                    "사과의 불들어 오는 마지막 모델이고 현재 포멧한 상태입니다.\n" +
                    "\n" +
                    "제품 특성상 교환이나 환불은 어려우며 너무 민감하신 분들은 거래가 어려울거 같습니다. 감사합니다.");
            post.setStatus(TradeStatus.ING);
            post.setTown("경기도 광명시 소하동");
            post.setPrice(10000L);
            em.persist(post);




            PostsPhoto photo = new PostsPhoto();

            photo.setPost(post);
            photo.setPhotoPath("https://dnvefa72aowie.cloudfront.net/origin/article/202203/825A4C9C5F813255EF061676B0274DA1B7F01BAAFFD51F97259DD9147317D820.jpg?q=82&s=300x300&t=crop");

            em.persist(photo);
        }

    }

}
