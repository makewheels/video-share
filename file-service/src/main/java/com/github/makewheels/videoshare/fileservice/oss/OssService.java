package com.github.makewheels.videoshare.fileservice.oss;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.makewheels.videoshare.fileservice.upload.Credential;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class OssService {
    @Getter
    @Value("${aliyun.oss.region}")
    private String region;

    @Getter
    @Value("${aliyun.oss.bucket}")
    private String bucket;
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    private final String accessKeyId = "LTAI5tJ9scBXxsk4VjYDiupv";
    private final String accessKeySecret = "THckQsWudA7rNV0hUXn2Hcxu0VHhLC";

    private OSS ossClient;

    public OSS getOssClient() {
        if (ossClient == null) {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        }
        return ossClient;
    }

    /**
     * 生成临时凭证
     *
     * @param uploadPath
     * @return
     */
    public Credential getAliyunOssUploadCredential(String uploadPath) {
        String endpoint = "sts.cn-beijing.aliyuncs.com";
        String roleArn = "acs:ram::1618784280874658:role/oss-video-share";
        // 自定义角色会话名称，用来区分不同的令牌，例如可填写为SessionTest。
        String roleSessionName = "RoleSessionName";
        // 以下Policy用于限制仅允许使用临时访问凭证向目标存储空间examplebucket上传文件。
        // 临时访问凭证最后获得的权限是步骤4设置的角色权限和该Policy设置权限的交集，
        // 即仅允许将文件上传至目标存储空间examplebucket下的exampledir目录。
        String policy = "{" +
                "    \"Version\": \"1\", " +
                "    \"Statement\": [" +
                "        {" +
                "            \"Action\": [" +
                "                \"oss:PutObject\"" +
                "            ], " +
                "            \"Resource\": [" +
                "                \"acs:oss:*:*:video-share-bucket/" + uploadPath + "\" " +
                "            ], " +
                "            \"Effect\": \"Allow\"" +
                "        }" +
                "    ]" +
                "}";
        Credential credential = new Credential();
        try {
            // 添加endpoint。
            DefaultProfile.addEndpoint(region, "Sts", endpoint);
            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(region, accessKeyId, accessKeySecret);
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy); // 如果policy为空，则用户将获得该角色下所有权限。
            request.setDurationSeconds(3600L); // 设置临时访问凭证的有效时间为3600秒。
            AssumeRoleResponse response = client.getAcsResponse(request);

            AssumeRoleResponse.Credentials credentials = response.getCredentials();
            credential.setAccessKeyId(credentials.getAccessKeyId());
            credential.setAccessKeySecret(credentials.getAccessKeySecret());
            credential.setBucket(bucket);
            credential.setRegion(region);
            credential.setSecurityToken(credentials.getSecurityToken());
            log.info("生成临时凭证: " + JSON.toJSONString(credential));
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return credential;
    }

    public String getBaseUrl() {
        return "https://" + bucket + "." + region + ".aliyuncs.com/";
    }

    /**
     * 获取访问地址
     *
     * @param key
     * @return
     */
    public String getAccessUrl(String key) {
        return getBaseUrl() + key;
    }

    /**
     * 获取对象
     *
     * @param key
     * @return
     */
    public OSSObject getFileByKey(String key) {
        return getOssClient().getObject(bucket, key);
    }

    /**
     * 签名对象
     *
     * @param key
     * @param time
     * @return
     */
    public String getSignedUrl(String key, long time) {
        String url = getOssClient().generatePresignedUrl(
                bucket, key, new Date(System.currentTimeMillis() + time)).toString();
        if (url.startsWith("http://")) {
            return url.replaceFirst("http://", "https://");
        }
        return url;
    }
}
