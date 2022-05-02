package click.applemt.apmt.service;

import click.applemt.apmt.config.FirebaseInit;
import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.controller.post.PostReqDto;
import click.applemt.apmt.controller.post.PostSearchCondition;
import click.applemt.apmt.controller.post.PostUpdateReqDto;
import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.post.*;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.postRepository.PostRepositoryCustom;
import click.applemt.apmt.repository.postRepository.PostsPhotoRepository;
import click.applemt.apmt.repository.postRepository.TagRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import click.applemt.apmt.security.AuthUser;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final PostRepositoryCustom postRepositoryCustom;
    private final UserRepository userRepository;
    private final PostsPhotoRepository postsPhotoRepository;
    private final TradeHistoryRepository tradeHistoryRepository;

    private final FirebaseInit firebaseInit;

    //검색어가 없다면 모든 목록 or 검색어가 있다면 검색어에 맞는 목록 노출
    public List<PostListDto> findAllPostAndSearchKeyword(PostSearchCondition searchCond) {
        return postRepositoryCustom.findPostsBySearch(searchCond).stream()
                .map(p -> new PostListDto(p.getId(), Time.calculateTime(Timestamp.valueOf(p.getCreatedTime())), isEmpty(p.getPhotoList()) ? null : p.getPhotoList().get(0).getPhotoPath(), p.getTitle(), p.getPrice(), p.getContent(), p.getTown(), p.getStatus()))
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


    public List<PostListReviewDto> findUserBuyingList(String uid){
        List<TradeHistory> postsByUser = postRepository.findPostsByBuying(uid);

        List<PostListReviewDto> buyingList = new ArrayList<>();
        for (TradeHistory tradeHistory : postsByUser) {
            Post post = tradeHistory.getPost(); //
            PostListReviewDto postListReviewDto = new PostListReviewDto();
            postListReviewDto.setAfterDate(Time.calculateTime(Timestamp.valueOf(post.getCreatedTime())));
            postListReviewDto.setContent(post.getContent());
            postListReviewDto.setId(post.getId());
            if(tradeHistory.getReviews().size() != 0){
            postListReviewDto.setReviewId(tradeHistory.getReviews().get(0).getId());
            }
            postListReviewDto.setTradeHistoryId(tradeHistory.getId());
            postListReviewDto.setPrice(post.getPrice());
            postListReviewDto.setRegion(post.getTown());
            postListReviewDto.setTitle(post.getTitle());
            postListReviewDto.setImg(post.getPhotoList().get(0).getPhotoPath());
            postListReviewDto.setStatus(post.getStatus());
            buyingList.add(postListReviewDto);
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

    public List<PostListDto> findUserLikePostList(String uid){
        List<LikePost> likePosts = postRepository.findPostsByLike(uid);
        List<PostListDto> likeList = new ArrayList<>();
        for (LikePost likePost : likePosts) {
            Post post = likePost.getPost();
            PostListDto postListDto = new PostListDto();
            postListDto.setAfterDate(Time.calculateTime(Timestamp.valueOf(post.getCreatedTime())));
            postListDto.setContent(post.getContent());
            postListDto.setId(post.getId());
            postListDto.setPrice(post.getPrice());
            postListDto.setRegion(post.getTown());
            postListDto.setTitle(post.getTitle());
            postListDto.setImg(post.getPhotoList().get(0).getPhotoPath());
            postListDto.setStatus(post.getStatus());
            likeList.add(postListDto);
            }
        return likeList;
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
        if (decodedToken != null)
            postDto.setOwner(decodedToken.getUid().equals(uid));
        postDto.setStatus(findPost.getStatus());
        postDto.setId(findPost.getId());
        postDto.setRegion(findPost.getTown());
        postDto.setPrice(findPost.getPrice());
        postDto.setView(findPost.getView());

        return postDto;

    }

    //Post 조회수 증가 로직
    @Transactional
    public Long updateView(Long postId) {
        return postRepositoryCustom.updateView(postId);
    }

    //Post삭제 (실제로는 delete가 아니라 update(삭제 플래그 값을 Y로 업데이트함))
    @Transactional
    public Long deleteByPostId(Long postId, AuthUser authUser) {
        Post findPost = postRepository.findById(postId).get();
        if (findPost.getUser().getUid().equals(authUser.getUid())) {
            postRepository.updatePostDelete(postId);
        }
        return postId;
    }

    //Post를 등록하는 로직
    @Transactional
    public Long savePost(PostReqDto postReqDto, AuthUser authUser) {
        User findUser = userRepository.findById(authUser.getUid()).orElseThrow();
        Post post = new Post();
        post.setUser(findUser);
        post.setTown(postReqDto.getTown());
        post.setTitle(postReqDto.getTitle());
        post.setPrice(postReqDto.getPrice());
        post.setContent(postReqDto.getContent());
        List<Tag> tags = tagRepository.findByName(postReqDto.getTags());
        post.setTags(tags);
        post.setStatus(TradeStatus.ING);
        postRepository.save(post);
        return post.getId();
    }

    //Post를 등록할 때 중간에 PostsPhoto 저장하는 로직
    @Transactional
    public void savePostPhotos(Long postId, AuthUser authUser, List<MultipartFile> files) {
        //입력된 이미지가 없으면 이 메서드는 실행하지 않음
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        //입력된 이미지가 있다면 savePost메서드에서 저장된 게시물을 찾음
        Post findPost = postRepository.findById(postId).orElseThrow();
        //사용자 인증 정보가 일치하지 않으면 이 메서드는 실행하지 않음
        if (!findPost.getUser().getUid().equals(authUser.getUid())) {
            return;
        }
        //Post를 등록 및 수정할 때 이미지가 이미 존재한다면 레파지토리에서 삭제함
        if (!findPost.getPhotoList().isEmpty()) {
            postsPhotoRepository.deleteByPostId(postId);
        }
        for (MultipartFile file : files) {
            //하나의 게시물을 참조하는 이미지 하나 생성 (루프 돌면서 복수의 이미지 넣기)
            String filePath = "C:\\Users\\kaas1\\Downloads\\" + file.getOriginalFilename();
            //filePath 수정해야함
            PostsPhoto postsPhoto = PostsPhoto.builder().photoPath(filePath).post(findPost).build();
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

    //Post 수정하는 로직
    @Transactional
    public Long updatePost(Long postId, PostUpdateReqDto postUpdateReqDto, AuthUser authUser) {
        Post findPost = postRepository.findById(postId).get();
        if (findPost.getUser().getUid().equals(authUser.getUid())) {
            findPost.setTown(postUpdateReqDto.getTown());
            findPost.setTitle(postUpdateReqDto.getTitle());
            findPost.setPrice(postUpdateReqDto.getPrice());
            findPost.setContent(postUpdateReqDto.getContent());
            List<Tag> tags = tagRepository.findByName(postUpdateReqDto.getTags());
            findPost.setTags(tags);
            findPost.setStatus(TradeStatus.ING);
            postRepository.save(findPost);
        }
        return findPost.getId();
    }

    @Transactional
    public Long changeEndStatus(Long postId, AuthUser authUser) {
        Post findPost = postRepository.findById(postId).get();
        if (findPost.getUser().getUid().equals(authUser.getUid())) {
            findPost.setStatus(TradeStatus.END);
        }
        return findPost.getId();
    }

    /**
     * postId에 해당하는 판매글을 가져온다
     * 판매글의 판매자 user ID를 가져온다
     * 판매자 user Id에 해당하는 판매중인 판매글 리스트를 가져온다
     *
     * @param postId    판매글의 post ID
     * @return  판매글의 판매자의 판매글 리스트
     */
    public List<Post> getSellerPostsByPostId(Long postId) {
        // postId에 해당하는 판매글을 가져온다 -> 판매글의 판매자 user ID를 가져온다
        String sellerUserId = postRepository.getUserIdByPostId(postId);
        // 판매자 user ID에 해당하는 판매글 목록을 가져온다
        return postRepository.findPostsByUserSelling(sellerUserId);
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class PostListReviewDto {      // 판매글 리뷰포함 내역
        private Long id;
        private Long reviewId;
        private Long tradeHistoryId;
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
        private Integer view;
    }
}
