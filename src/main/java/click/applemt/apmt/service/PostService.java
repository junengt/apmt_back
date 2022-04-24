package click.applemt.apmt.service;

import click.applemt.apmt.config.FirebaseInit;
import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.*;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.postRepository.PostsPhotoRepository;
import click.applemt.apmt.repository.tradeHistroyRepository.TradeHistoryRepository;
import click.applemt.apmt.util.Time;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final PostsPhotoRepository postsPhotoRepository;
    private final TradeHistoryRepository tradeHistoryRepository;

    private final FirebaseInit firebaseInit;
    //검색어가 없다면 모든 목록 or 검색어가 있다면 검색어에 맞는 목록 노출
    public List<PostListDto> findAllPostAndSearchKeyword(String searchKeyword) {
            return postRepository.findPostsBySearch(searchKeyword).stream()
                    .map(p -> new PostListDto(p.getId(), Time.calculateTime(Timestamp.valueOf(p.getCreatedTime())), p.getPhotoList().get(0).getPhotoPath() ,p.getTitle(), p.getPrice(), p.getContent(),p.getTown(),p.getStatus()))
                    .collect(Collectors.toList());
    }

    /**
     * uid에 해당하는 User의 판매 목록을 가져온다
     * @param uid User의 userID
     * @return PostListDto 판매목록 Dto
     */
    public  List<PostListDto> findUserPostSellingList(String uid){

        List<Post> postsByUser = postRepository.findPostsByUserSelling(uid);
        List<PostListDto> sellingList = new ArrayList<>();
        for (Post post : postsByUser) {
            PostListDto postListDto = new PostListDto();
            postListDto.setAfterDate(Time.calculateTime(Timestamp.valueOf(post.getCreatedTime())));
            postListDto.setContent(post.getContent());
            postListDto.setId(post.getId());
            postListDto.setPrice(post.getPrice());
            postListDto.setRegion(post.getTown());
            postListDto.setTitle(post.getTitle());
            postListDto.setImg(post.getPhotoList().get(0).getPhotoPath());
            postListDto.setStatus(post.getStatus());
            sellingList.add(postListDto);
        }
        return sellingList;
    }

    public List<PostListDto> findUserBuyingList(String uid){
        List<TradeHistory> postsByUser = postRepository.findPostsByBuying(uid);

        List<PostListDto> buyingList = new ArrayList<>();
        for (TradeHistory tradeHistory : postsByUser) {
            Post post = tradeHistory.getPost(); //
            PostListDto postListDto = new PostListDto();
            postListDto.setAfterDate(Time.calculateTime(Timestamp.valueOf(post.getCreatedTime())));
            postListDto.setContent(post.getContent());
            postListDto.setId(post.getId());
            postListDto.setPrice(post.getPrice());
            postListDto.setRegion(post.getTown());
            postListDto.setTitle(post.getTitle());
            postListDto.setImg(post.getPhotoList().get(0).getPhotoPath());
            postListDto.setStatus(post.getStatus());
            buyingList.add(postListDto);
        }
        return buyingList;

    }

    /**
     * uid에 해당하는 User의 Review 리스트를 가져온다
     * @param uid review 목록을 가져올 user의 id
     * @return  user의 후기 내역
     */
    public List<ReviewListDto> findReviewsByUid(String uid) {
        List<ReviewListDto> reviewList = new ArrayList<>();

        List<TradeHistory> tradeHistories = tradeHistoryRepository.findTradeHistoriesByUid(uid);
        for (TradeHistory tradeHistory : tradeHistories) {
            System.out.println("tradeHistory = " + tradeHistory);
            List<Review> reviews = tradeHistoryRepository.findReviewsByTradeHistoryId(tradeHistory.getId());
            for (Review review : reviews) {
                ReviewListDto reviewListDto = new ReviewListDto();
                reviewListDto.setId(review.getId());
                reviewListDto.setBuyer(tradeHistory.getUser());
                reviewListDto.setContent(review.getContent());
                reviewListDto.setAfterDate(Time.calculateTime(Timestamp.valueOf(review.getCreatedTime())));
                reviewList.add(reviewListDto);
            }
        }
        return reviewList;
    }

    /**
     * uid에 해당하는 판매자의 uid, 전체 판매글 목록, 전체 후기 내역을 가져온다
     * @param uid 판매자의 uid
     * @return 판매자 정보(uid, 전체 판매글 목록, 전체 후기 내역)
     */
    public SellerInfoDto getSellerInfoByUserId(String uid) {
        SellerInfoDto sellerInfo = new SellerInfoDto();
        sellerInfo.setSellerUid(uid);
        sellerInfo.setPosts(findUserPostSellingList(uid));
        sellerInfo.setReviews(findReviewsByUid(uid));
        return sellerInfo;
    }

    public PostDto findOne(Long postId, FirebaseToken decodedToken) throws FirebaseAuthException {
        Post findPost = postRepository.findById(postId).get();
        String uid = findPost.getUser().getUid();
        UserRecord user = FirebaseAuth.getInstance().getUser(uid);

        PostDto postDto = new PostDto();
        postDto.setContent(findPost.getContent());
        postDto.setAfterDate(Time.calculateTime(Timestamp.valueOf(findPost.getCreatedTime())));
        postDto.setCreatorId(uid);
        postDto.setCreatorName(user.getDisplayName());
        postDto.setProfileImg(user.getPhotoUrl());
        postDto.setPhotoList(findPost.getPhotoList());
        postDto.setTags(findPost.getTags().stream().map(e -> e.getName()).toList());
        postDto.setTitle(findPost.getTitle());
        if(decodedToken != null)
        postDto.setOwner(decodedToken.getUid().equals(uid));
        postDto.setStatus(findPost.getStatus());
        postDto.setId(findPost.getId());
        postDto.setRegion(findPost.getTown());
        postDto.setPrice(findPost.getPrice());

        return postDto;

    }

    public void deleteByPostId(Long postId) {
        postRepository.updatePostDelete(postId);
    }

    //Post를 등록할 때 중간에 PostsPhoto 저장하는 로직
    @Transactional
    public void savePostPhotos(Long postId, List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        Post post = postRepository.findById(postId).get();
        //.get 말고 orElseThrow로 에러 처리
        for (MultipartFile file : files) {
            //하나의 게시물을 참조하는 이미지 하나 생성 (루프 돌면서 복수의 이미지 넣기)
            String filePath = "C:\\Users\\kaas1\\Downloads\\" + file.getOriginalFilename();
            //filePath 수정해야함
            PostsPhoto postsPhoto = PostsPhoto.builder().photoPath(filePath).post(post).build();
            //파일을 서버 저장소에 저장
            try {
                Files.copy(file.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println(e);
                //콘솔 출력 말고 log출력으로..
            }
            //파일 저장 끝
            postsPhotoRepository.save(postsPhoto);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class SellerInfoDto {    // 판매자 정보
        private String sellerUid;
        private List<ReviewListDto> reviews;
        private List<PostListDto> posts;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ReviewListDto {    // 후기 내역
        private Long id;
        private User buyer;
        private String content;
        private String afterDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class PostListDto {      // 판매글 내역
        private Long id;
        private String afterDate;
        private String img;
        private String title;
        private Long price;
        private String content;
        private String Region;
        private TradeStatus status;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class PostDto {
        private Long id;
        private String creatorId;
        private String profileImg;
        private String creatorName;
        private String afterDate;
        private List<PostsPhoto> photoList;
        private String title;
        private Long price;
        private String content;
        private String Region;
        private TradeStatus status;
        private boolean isOwner;
        private List<String> tags;

    }
}
