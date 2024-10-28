package com.devteria.identity.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import com.devteria.event.dto.NotificationEvent;
import com.devteria.identity.dto.request.ProfileCreationRequest;
import com.devteria.identity.dto.response.ProfileCreationResponse;
import com.devteria.identity.mapper.ProfileCreationMapper;
import com.devteria.identity.repository.httpClient.ProfileClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devteria.identity.constant.PredefinedRole;
import com.devteria.identity.dto.request.UserCreationRequest;
import com.devteria.identity.dto.request.UserUpdateRequest;
import com.devteria.identity.dto.response.UserResponse;
import com.devteria.identity.entity.Role;
import com.devteria.identity.entity.User;
import com.devteria.identity.exception.AppException;
import com.devteria.identity.exception.ErrorCode;
import com.devteria.identity.mapper.UserMapper;
import com.devteria.identity.repository.RoleRepository;
import com.devteria.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    ProfileClient profileClient;
    ProfileCreationMapper profileCreationMapper;
    KafkaTemplate<String, Object> kafkaTemplate;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // ------- Create user profile:
        // (1) get userId:
        ProfileCreationRequest profileRequest = profileCreationMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getId());

        System.out.println("profileRequest: " + profileRequest);

        // (2) get authorization token from header: đã được triển khai trong AuthenticationRequestInterceptor Configuration
        // (3) call to profile service:
        profileClient.createProfile(profileRequest);

        // ------- Publish message to kafka:
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .subject("Welcome to Bookteria")
                .body("<p>Hello, " + request.getUsername() + "</p>")
                .build();

        kafkaTemplate.send("notification-delivery", notificationEvent);

        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        // name này chính là lấy từ field jwt.subject() trong đoạn "new
        // JWTClaimsSet.Builder().subject(user.getUsername())" khi generate token
        // khi Spring security enable, thì dùng oauth2 (trong pom.xml), thì mặc định SpringContextHolder sẽ có thuộc
        // tính name = jwt.subject() này => hay !!

        log.info("name: {}", name);

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize(
            "hasRole('ADMIN')") // dùng hasRole thì không cần Prefix / còn dùng hasAuthority thì cần prefix "ROLE_ADMIN"
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    // @PostAuthorize("returnObject.username == authentication.name") // đảm bảo chỉ trả kết quả khi username trong
    // return object == với name của user đang đăng nhập. returnObject chính là đối tượng mà api này trả về
    // (UserResponse)
    // authentication tự động có được bởi SpringSecurityContextHolder khi user đăng nhập thành công. Tham khảo thêm tại:
    // https://www.baeldung.com/get-user-in-spring-security
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
}
