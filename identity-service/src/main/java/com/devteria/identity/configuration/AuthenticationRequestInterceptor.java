package com.devteria.identity.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
//@Component
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    // RequestInterceptor thộc thư viện Feign, nên khi đánh dấu là Bean, thì được init khi app started
    // Sau đó, Feign sẽ tự động apply hàm apply được override lại ở dưới để thực hiện interceptor cho các request
    // nhưng init global như này thì sẽ ảnh hưởng nếu call service bên ngoài (3rd party), vì có thể request tới
    // 3rd party sẽ yêu cầu 1 loại token theo phương thức khác (k phải jwt), thì sẽ bị ảnh hưởng.
    // Nên có cách khác là khai báo tường minh trong các Feign Client (class ProfileClient)

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");
        log.info("authHeader: {}", authHeader);

        if(StringUtils.hasText(authHeader)) {
            requestTemplate.header("Authorization", authHeader);
        }
    }
}
