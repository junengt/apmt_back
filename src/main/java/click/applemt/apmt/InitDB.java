package click.applemt.apmt;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.*;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("local")
public class InitDB {

    private final InitService initService;


    @PostConstruct
    public void init() throws IOException, ParseException {
        initService.doInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        private final UserRepository userRepository;
        private final PostRepository postRepository;
        private final ResourceLoader resourceLoader;

        public void doInit() throws IOException, ParseException {
            JSONParser parser = new JSONParser();
            InputStream file = resourceLoader.getResource("classpath:/static/carrot.json").getInputStream();
            ArrayList<JSONObject> jsonObjects = (ArrayList<JSONObject>) parser.parse(new InputStreamReader(file));
            // 통일성 있는 데이터는 한번만 persist 한다


            List<Tag> tagList = new ArrayList<>();
            String[] tags  = {"AirPods Series","AirPods Pro", "AirPods Max", "AirPods",
                        "iPad Pro", "iPad Air", "iPad mini", "Apple Watch Series",
                        "Apple Watch SE", "iPhone Series", "iPhone SE Series", "MacBook Air",
                        "MacBook Pro", "iMac 24\"", "Display", "AirTag", "Mac mini", "Mac Studio", "Accessories",
                        "Etc.", "Display", "AirTag", "Mac mini", "Mac Studio", "Mac Pro", "Mac", "iPad", "Watch", "iPhone"};
                for (String tagName : tags) {
                    Tag tag = new Tag();
                    tag.setName(tagName);
                    em.persist(tag);
                    tagList.add(tag);
                }

            Tag tag = tagList.get(12);
            Tag tag2 = tagList.get(25);
            List <Tag> tagList2 = new ArrayList<>();
            tagList2.add(tag);
            tagList2.add(tag2);

            int idx = 0;

            User user1 = new User();
            user1.setUid("DtTKbg4JRdQtCgyuCLu9sSafo702");
            em.persist(user1);

            User user2 = new User();
            user2.setUid("kSuKt7fM0ufWRuzVUii8HyAG4by2");
            em.persist(user2);

            User user3 = new User();
            user3.setUid("QkWS3G5rC5dRG59yTWSuRjWBm0n2");
            em.persist(user3);

            for (JSONObject jsonObject : jsonObjects) {

                User user = null;
                if (idx % 2 == 0) {
                    // 짝수번째일때 강팀장님 ID에 데이터 추가
                    user = userRepository.findByUid(user1.getUid()).get();
                } else {
                    // 홀수번째일때 이상무 ID에 데이터 추가
                    user = userRepository.findByUid(user2.getUid()).get();
                }



                String tagsJson = (String) jsonObject.get("tags");
                String[] tagsSplit = tagsJson.split(",");

                // db설정이 create-drop인 경우에만 실행

                // tag 로직이 들어가야할 자리


                // Post
                Post post = new Post();
                String title = (String) jsonObject.get("title");
                String content = (String) jsonObject.get("content");
                String region_name = (String) jsonObject.get("region_name");
                // "n원" 형태의 문자열이기 때문에 Long 형으로 변환하였다.
                String priceString = (String) jsonObject.get("price");
                String priceReplace = priceString.replace(",", "").replace("원", "");
                Long price;
                try {
                    price = Long.parseLong(priceReplace);
                } catch (NumberFormatException e) {
                    price = 0L;
                }
                post.setTags(tagList2);
                post.setUser(user);
                post.setTitle(title);
                post.setContent(content);
                post.setStatus(TradeStatus.ING);
                post.setTown(region_name);
                post.setPrice(price);
                em.persist(post);

//                PostsPhoto photo = new PostsPhoto();
//                String img_src = (String) jsonObject.get("img_src");
//                photo.setPost(post);
//                photo.setPhotoPath(img_src);
//                em.persist(photo);

                // 판매자 user1 또는 user2의 판매 상품을
                // 구매자 user3이 모든 상품을 구매한다는 가정

                TradeHistory history = new TradeHistory();
                history.setPost(post);
                history.setUser(user3);     // 구매자
                history.setPrice(post.getPrice());
                em.persist(history);
                // 구매자 User3이 후기내역을 작성했다는 가정
                Review review = new Review();
                review.setTradeHistory(history);
                review.setContent("판매자님이 친절해요. "+idx);
                em.persist(review);

                idx++;
            }
        }

    }

}
