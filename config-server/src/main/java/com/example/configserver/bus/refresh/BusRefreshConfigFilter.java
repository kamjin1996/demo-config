package com.example.configserver.bus.refresh;

import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author kam
 *
 * <p>
 * 消息总线刷新config，webhook的过滤器
 * 作用是将gitlab绑定的webhook请求的请求体置空
 * </p>
 */
@WebFilter(filterName = "bodyFilter", urlPatterns = "/*")
@Order(1)
public class BusRefreshConfigFilter implements Filter {

    private static final String BUS_REFRESH_SUFFIX = "/bus-refresh";

    /**
     * 检查是否是BusRefresh请求
     *
     * @param url
     * @return
     */
    private static boolean checkIsBusRefresh(String url) {
        return url.endsWith(BUS_REFRESH_SUFFIX);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String url = httpServletRequest.getRequestURI();

        //只过滤/actuator/bus-refresh请求
        if (!checkIsBusRefresh(url)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //使用HttpServletRequest包装原始请求达到修改post请求中body内容的目的
        filterChain.doFilter(new CustomRequestWrapper(httpServletRequest), servletResponse);
    }

    @Override
    public void destroy() {

    }

}