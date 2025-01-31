package com.homework.rest_security_final.rest_security_final.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PageableResolver implements HandlerMethodArgumentResolver {

    private static final int DEFAULT_PAGE=0; // 페이지 번호의 기본값. 즉, 첫번째 페이지가 0번 부터 시작
    private static final int DEFAULT_SIZE=5; // 한 페이지에 표시할 기본 데이터 수.
    private static final int MAX_SIZE=10; // 한 페이지에 표시할 데이터 수를 최대 10으로 제한.

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String pageParam = webRequest.getParameter("page");
        String sizeParam = webRequest.getParameter("size");

        int page = (pageParam != null) ? Integer.parseInt(pageParam) : DEFAULT_PAGE;
        int size = (sizeParam != null) ? Integer.parseInt(sizeParam) : DEFAULT_SIZE;

        if(size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        return PageRequest.of(page, size);
    }
}
