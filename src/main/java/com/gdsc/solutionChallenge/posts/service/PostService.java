package com.gdsc.solutionChallenge.posts.service;


import com.gdsc.solutionChallenge.global.utils.SecurityUtil;
import com.gdsc.solutionChallenge.member.entity.Member;
import com.gdsc.solutionChallenge.member.repository.MemberRepository;
import com.gdsc.solutionChallenge.posts.dto.req.PostSaveDto;
import com.gdsc.solutionChallenge.posts.dto.res.FullPost;
import com.gdsc.solutionChallenge.posts.entity.Post;
import com.gdsc.solutionChallenge.posts.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public String createPost(PostSaveDto postSaveDto) {

        Post post = postSaveDto.toEntity();
        post.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자가 DB에 없습니다.")));

        Post savedPost = postRepository.save(post);
        Member member = memberRepository.findByUsername(SecurityUtil.getLoginUsername()).get();
        member.addScore(10);
        memberRepository.save(member);

        return savedPost.getTitle();
    }

    public List<FullPost> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<FullPost> fullPostList = new ArrayList<>();
        for (Post post : posts) {
            fullPostList.add(FullPost.builder()
                            .course_id(post.getId())
                            .grade(post.getGrade())
                            .subject(post.getSubject())
                            .writer(post.getWriter().getNickName())
                            .title(post.getTitle())
                            .description(post.getDescription())
                            .video_id(post.getVideo_id())
                            .drive_link(post.getDrive_link())
                            .likes(post.getLikes())
                            .created_at(post.getCreatedDate().toString())
                            .build());
        }
        return fullPostList;
    }
}
