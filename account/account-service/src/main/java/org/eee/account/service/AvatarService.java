package org.eee.account.service;

import com.atguigu.param.ResourceParam;
import com.atguigu.service.ResourceRpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import com.atguigu.param.Response;
import org.eee.account.param.UserAvatarParam;
import org.eee.account.param.UserInfoParam;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;

@Slf4j
@Service
public class AvatarService
{
    @DubboReference(mock = "return {\"code\": 500, \"message\": \"服务降级\"}")
    private ResourceRpc resourceRpc;

    private final String AVATAR_BUCKET = "avatar-blog-5";

    public Response<URL> getAvatarUrl(UserAvatarParam userInfoParam)
    {
        if (userInfoParam.getAvatar() == null)
        {
            return Response.error(100, "avatar为空");
        }

        if (userInfoParam.getUserId() == null)
        {
            return Response.error(100, "userId为空");
        }

        log.info("userInfoParam: {}", userInfoParam);
        ResourceParam resourceParam1 = new ResourceParam();
        resourceParam1.setBucketName(AVATAR_BUCKET);
        resourceParam1.setOssId(userInfoParam.getUserId().toString());
        resourceParam1.setContentType(getContentTypeByFileName(userInfoParam.getAvatar()));

        log.info("resourceParam1: {}", resourceParam1);

        Response<URL> uploadData = resourceRpc.getUploadUrl(resourceParam1);
        URL url = transformToURL(uploadData.getData());
        return Response.success(url);
    }

    public String getContentTypeByFileName(String fileName)
    {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        switch (extension)
        {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "bmp":
                return "image/bmp";
            case "svg":
                return "image/svg+xml";
            case "ico":
                return "image/x-icon";
            default:
                return "application/octet-stream";
        }
    }

    public URL transformToURL(Object data)
    {
        if (data instanceof Map)
        {
            // 如果是 Map，手动构造 URL
            Map<String, Object> dataMap = (Map<String, Object>) data;
            String protocol = (String) dataMap.get("protocol");
            String host = (String) dataMap.get("host");
            String file = (String) dataMap.get("file");

            String urlStr = protocol + "://" + host + file;

            try
            {
                URL url = new URL(urlStr);
                log.info("URL: {}", url);
                return url ;
            }
            catch (Exception e)
            {
                log.error("构造URL失败: {}", e.getMessage());
                return null;
            }
        }
        else if (data instanceof URL)
        {
            // 如果是 URL，直接返回
            URL url = (URL) data;
            log.info("URL: {}", url);
            return url;
        }
        else
        {
            log.error("未知的数据类型: {}", data.getClass());
            return null;
        }
    }
}
