package org.eee.account.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
public class OauthFailureHandler implements AuthenticationFailureHandler
{
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException
    {
        // 打印请求的基本信息
        log.info("OAuth2 认证失败 - 请求信息:");
        log.info("请求方法: {}", request.getMethod());
        log.info("请求URL: {}", request.getRequestURL());
        log.info("请求URI: {}", request.getRequestURI());
        log.info("查询参数: {}", request.getQueryString());
        log.info("客户端IP: {}", getClientIpAddress(request));
        log.info("User-Agent: {}", request.getHeader("User-Agent"));
        log.info("Referer: {}", request.getHeader("Referer"));

        // 打印所有请求头
        log.info("请求头信息:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("  {}: {}", headerName, headerValue);
        }

        // 打印所有请求参数
        log.info("请求参数:");
        request.getParameterMap().forEach((key, values) ->
            log.info("  {}: {}", key, String.join(", ", values))
        );

        // 打印异常信息
        log.error("认证异常: {}", exception.getMessage(), exception);

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(ErrorPage);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    final String ErrorPage =
            """
                    <!DOCTYPE html>
                    <html lang="zh-CN">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>访问超时</title>
                        <style>
                            * {
                                margin: 0;
                                padding: 0;
                                box-sizing: border-box;
                            }
                    
                            body {
                                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                                min-height: 100vh;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                color: #333;
                            }
                    
                            .container {
                                background: white;
                                padding: 3rem 2rem;
                                border-radius: 12px;
                                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
                                text-align: center;
                                max-width: 400px;
                                width: 90%;
                            }
                    
                            .icon {
                                font-size: 4rem;
                                color: #ff6b6b;
                                margin-bottom: 1rem;
                            }
                    
                            h1 {
                                font-size: 1.8rem;
                                margin-bottom: 1rem;
                                color: #2c3e50;
                            }
                    
                            p {
                                color: #7f8c8d;
                                margin-bottom: 2rem;
                                line-height: 1.6;
                            }
                    
                            .btn {
                                background: #667eea;
                                color: white;
                                padding: 12px 24px;
                                border: none;
                                border-radius: 6px;
                                font-size: 1rem;
                                cursor: pointer;
                                transition: background 0.3s ease;
                                text-decoration: none;
                                display: inline-block;
                            }
                    
                            .btn:hover {
                                background: #5a6fd8;
                            }
                    
                            .countdown {
                                margin-top: 1rem;
                                font-size: 0.9rem;
                                color: #95a5a6;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="icon">⏰</div>
                            <h1>访问超时</h1>
                            <p>抱歉，您的请求处理时间过长，连接已超时。请稍后重试或检查网络连接。</p>
                            <a href="javascript:history.back()" class="btn">返回上页</a>
                            <a href="/" class="btn" style="margin-left: 10px;">回到首页</a>
                            <div class="countdown" id="countdown">页面将在 <span id="timer">10</span> 秒后自动刷新</div>
                        </div>
                    
                        <script>
                            let timeLeft = 10;
                            const timer = document.getElementById('timer');
                    
                            const countdown = setInterval(() => {
                                timeLeft--;
                                timer.textContent = timeLeft;
                    
                                if (timeLeft <= 0) {
                                    clearInterval(countdown);
                                    location.reload();
                                }
                            }, 1000);
                        </script>
                    </body>
                    </html>
            """;
}
