package click.applemt.apmt;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.domain.post.PostsPhoto;
import click.applemt.apmt.domain.post.Tag;
import click.applemt.apmt.domain.post.TradeStatus;
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
            Tag tag = new Tag();
            tag.setName("Mac");
            em.persist(tag);

            Tag tag1 = new Tag();
            tag1.setName("MacBook Pro");
            em.persist(tag1);

            List<Tag> tags = new ArrayList<>();
            tags.add(tag);
            tags.add(tag1);

            int idx = 0;

            User user1 = new User();
            user1.setUid("DtTKbg4JRdQtCgyuCLu9sSafo702");
            em.persist(user1);

            User user2 = new User();
            user2.setUid("kSuKt7fM0ufWRuzVUii8HyAG4by2");
            em.persist(user2);

            for (JSONObject jsonObject : jsonObjects) {
                User user = null;
                if (idx % 2 == 0) {
                    // 짝수번째일때 강팀장님 ID에 데이터 추가
                    user = userRepository.findByUid(user1.getUid()).get();
                } else {
                    // 홀수번째일때 이상무 ID에 데이터 추가
                    user = userRepository.findByUid(user2.getUid()).get();
                }
                idx++;


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
                post.setTags(tags);
                post.setUser(user);
                post.setTitle(title);
                post.setContent(content);
                post.setStatus(TradeStatus.ING);
                post.setTown(region_name);
                post.setPrice(price);
                em.persist(post);

                PostsPhoto photo = new PostsPhoto();
                String img_src = (String) jsonObject.get("img_src");
                photo.setPost(post);
                photo.setPhotoPath(img_src);
                em.persist(photo);

            }
        }

    }

}
