package com.crypt.cryptguard.resolver;

import com.crypt.cryptguard.annotation.DecryptRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.print.attribute.standard.JobKOctets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.crypt.cryptguard.constant.CryptGuardConstant.requestParams;

/**
 * @FileName DecryptArgumentResolver
 * @Description
 * @Author yaoHui
 * @date 2024-12-19
 **/
public class DecryptArgumentResolver implements HandlerMethodArgumentResolver {

//    public final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasDecryptRequestAnnotation = parameter.hasMethodAnnotation(DecryptRequest.class);
        boolean hasRequestBodyAnnotation = parameter.hasParameterAnnotation(RequestBody.class);

        return hasDecryptRequestAnnotation && hasRequestBodyAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

        if (!ObjectUtils.isEmpty(httpServletRequest)) {
            // 获取请求体
            String originalBody = httpServletRequest.getReader().lines().collect(Collectors.joining());

//            // 将请求体转换为 Map
//            Map<String, Object> originalMap = objectMapper.readValue(originalBody, Map.class);
//
//            // 构建新的 Map（你可以根据需要做进一步修改）
//            Map<String, Object> handledMap = new HashMap<>();
//            for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
//                handledMap.put(entry.getKey() + requestParams, entry.getValue());
//            }
//
//            // 你可以返回处理后的 Map，或者将其转换为你需要的目标类型
//            return objectMapper.convertValue(handledMap, parameter.getParameterType());
        }

        // 如果请求体为空，返回 null 或者默认值
        return null;
    }

    @Override
    public String toString() {
        return "DecryptArgumentResolver";
    }
}
