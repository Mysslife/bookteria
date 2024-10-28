package com.devteria.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {
    String channel;
    String recipient;
    String templateCode; // chưa đề cập trong bài học
    Map<String, Object> params; // for template mail // chưa đề cập trong bài học
    String subject;
    String body;
}

