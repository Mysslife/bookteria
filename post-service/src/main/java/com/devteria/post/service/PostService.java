package com.devteria.post.service;

import com.devteria.post.dto.PageResponse;
import com.devteria.post.dto.request.PostRequest;
import com.devteria.post.dto.response.PostResponse;
import com.devteria.post.dto.response.UserProfileResponse;
import com.devteria.post.entity.Post;
import com.devteria.post.exception.AppException;
import com.devteria.post.exception.ErrorCode;
import com.devteria.post.mapper.PostMapper;
import com.devteria.post.repository.PostRepository;
import com.devteria.post.repository.httpClient.ProfileClient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    PostMapper postMapper;
    DateTimeFormatter dateTimeFormatter;
    ProfileClient profileClient;

    public PostResponse createPost(PostRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Post post = Post.builder()
                .content(request.getContent())
                .userId(authentication.getName())
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        post = postRepository.save(post);
        return postMapper.toPostResponse(post);
    }

    public PageResponse<PostResponse> getMyPosts(int page, int size){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

//        JwtAuthenticationToken oauthToken = (JwtAuthenticationToken) authentication;
//        String token = oauthToken.getToken().getTokenValue();
//        System.out.println("token: " + token);

        UserProfileResponse userProfileResponse = null;

        try {
            // Call to profile service to get username:
            userProfileResponse = profileClient.getProfileByUserId(userId).getResult();
        } catch (Exception e) {
            log.error("Error while getting user profile");
        }

        System.out.println("userProfileResponse: " + userProfileResponse);

        Sort sort = Sort.by("createdDate").descending(); // sort by bởi field trong entity
        Pageable pageable = PageRequest.of(page - 1, size, sort ); // page bắt đầu = 0. Ở đây -1 vì trong controller xác định từ đầu là 1
        var pageData = postRepository.findAllByUserId(userId, pageable);

        String username = userProfileResponse != null ? userProfileResponse.getUsername() : null;
        var postList = pageData.getContent().stream().map(post -> {
            PostResponse postRepose = postMapper.toPostResponse(post);
            postRepose.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
            postRepose.setUsername(username);
            return postRepose;
        }).collect(Collectors.toList());

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }
}
