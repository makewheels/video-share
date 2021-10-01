package com.github.makewheels.videoshare.fileservice;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class FileService {
    @Resource
    private FileRepository fileRepository;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    public Credential getTemporaryCredential() {
        String endpoint = "sts.cn-beijing.aliyuncs.com";
        String AccessKeyId = "LTAI5tJ9scBXxsk4VjYDiupv";
        String accessKeySecret = "THckQsWudA7rNV0hUXn2Hcxu0VHhLC";
        String roleArn = "acs:ram::1618784280874658:role/aliyunosstokengeneratorrole";
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
                "                \"acs:oss:*:*:video-share-bucket/*\" " +
                "            ], " +
                "            \"Effect\": \"Allow\"" +
                "        }" +
                "    ]" +
                "}";
        try {
            String regionId = "cn-beijing";
            // 添加endpoint。
            DefaultProfile.addEndpoint(regionId, "Sts", endpoint);
            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(regionId, AccessKeyId, accessKeySecret);
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy); // 如果policy为空，则用户将获得该角色下所有权限。
            request.setDurationSeconds(3600L); // 设置临时访问凭证的有效时间为3600秒。
            AssumeRoleResponse response = client.getAcsResponse(request);
            Credential credential = new Credential();
            AssumeRoleResponse.Credentials credentials = response.getCredentials();
            credential.setAccessKeyId(credentials.getAccessKeyId());
            credential.setAccessKeySecret(credentials.getAccessKeySecret());
            credential.setBucket(bucketName);
            credential.setRegion(regionId);
            credential.setSecurityToken(credentials.getSecurityToken());
            log.info("生成临时凭证: " + JSON.toJSONString(credential));
            return credential;
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
