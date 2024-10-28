package com.devteria.post.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class DateTimeFormatter {
    Map<Long, Function<Instant, String>> strategyMap = new LinkedHashMap<>(); // dùng linkedHashMap để đảm bảo thứ tự khi put vào map được giữ nguyên

    public String format(Instant instant) {
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());
//        System.out.println("strategyMap: " + Arrays.toString(strategyMap.entrySet().toArray()));

        var strategy = strategyMap.entrySet()
                .stream()
                .filter(el -> elapseSeconds < el.getKey())
                .findFirst().get(); // .orElseThrow();

//        System.out.println("strategy: " + strategy);
        return strategy.getValue().apply(instant); // vì value là kiểu function -> nên có thể dùng apply để thực thi function luôn
    }

    private DateTimeFormatter() {
        strategyMap.put(60L, this::formatInSeconds); // nếu nhỏ hơn 60 seconds thì hàm này
        strategyMap.put(3600L, this::formatInMinutes); // nếu nhỏ hơn 3600 seconds thì hàm này
        strategyMap.put(8640L, this::formatInHours); // nếu nhỏ hơn 8640 seconds thì hàm này
        strategyMap.put(Long.MAX_VALUE, this::formatInDays); // còn lại vào hàm này
    }

    private String formatInSeconds(Instant instant) {
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());
        return elapseSeconds + " seconds";
    }

    private String formatInMinutes(Instant instant) {
        long elapseMinutes = ChronoUnit.MINUTES.between(instant, Instant.now());
        return elapseMinutes + " minutes";
    }

    private String formatInHours(Instant instant) {
        long elapseHours = ChronoUnit.HOURS.between(instant, Instant.now());
        return elapseHours + " hours";
    }

    private String formatInDays(Instant instant) {
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
//        System.out.println("ZoneId.systemDefault(): " + ZoneId.systemDefault());
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ISO_DATE;
//        System.out.println("dateTimeFormatter: " + dateTimeFormatter);
        return localDateTime.format(dateTimeFormatter);
    }
}
