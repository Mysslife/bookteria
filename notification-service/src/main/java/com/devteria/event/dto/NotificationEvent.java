package com.devteria.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent { // className của phần gửi (trong service identity) và phần nhận phải giống nhau, vì kafka sẽ dựa vào className để serialize dữ liệu giữa đầu gửi và đầu nhận
    String channel;
    String recipient;
    String templateCode; // chưa đề cập trong bài học
    Map<String, Object> params; // for template mail // chưa đề cập trong bài học
    String subject;
    String body;
}

