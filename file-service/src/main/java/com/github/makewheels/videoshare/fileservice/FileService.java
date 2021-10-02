package com.github.makewheels.videoshare.fileservice;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.github.makewheels.videoshare.common.bean.OssFile;
import com.github.makewheels.videoshare.common.bean.Video;
import com.github.makewheels.videoshare.common.response.ErrorCode;
import com.github.makewheels.videoshare.common.response.Result;
import com.github.makewheels.videoshare.fileservice.oss.OssService;
import com.github.makewheels.videoshare.fileservice.redis.FileRedisService;
import com.github.makewheels.videoshare.fileservice.upload.Credential;
import com.github.makewheels.videoshare.fileservice.util.FileSnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class FileService {
    @Resource
    private FileRepository fileRepository;

    @Resource
    private FileRedisService fileRedisService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private OssService ossService;

    public Result<Credential> getTemporaryCredential(String uploadToken, String originalFilename) {
        Video video = fileRedisService.getVideoByUploadToken(uploadToken);
        //删掉Redis uploadToken
        fileRedisService.deleteUploadToken(uploadToken);
        if (video == null) {
            return Result.error(ErrorCode.UPLOAD_TOKEN_EXPIRED);
        }
        String uploadPath = video.getUploadPath();
        //创建我本地文件
        OssFile ossFile = new OssFile();
        ossFile.setStatus("create");
        ossFile.setUserMongoId(video.getUserMongoId());
        ossFile.setVideoMongoId(video.getMongoId());
        ossFile.setOriginalFilename(originalFilename);
        ossFile.setCreateTime(new Date());
        ossFile.setUploadToken(uploadToken);
        ossFile.setProvider("aliyun-oss");

        long fileSnowflakeId = FileSnowflakeUtil.get();

        ossFile.setSnowflakeId(fileSnowflakeId);
        ossFile.setBucket(ossService.getBucket());
        ossFile.setRegion(ossService.getRegion());
        ossFile.setKey(uploadPath);
        ossFile.setBaseUrl(ossService.getBaseUrl());
        ossFile.setAccessUrl(ossService.getAccessUrl(uploadPath));
        mongoTemplate.save(ossFile);
        //生成阿里云上传凭证
        Credential credential = ossService.getAliyunOssUploadCredential(uploadPath);
        credential.setFileSnowflakeId(fileSnowflakeId + "");
        return Result.ok(credential);
    }

    public Result<Void> uploadFinish(long fileSnowflakeId) {
        //查数据库拿到文件
        OssFile ossFile = fileRepository.findBySnowflakeId(fileSnowflakeId);
        //向阿里云查询文件信息
        String key = ossFile.getKey();
        OSSObject ossObject = ossService.getFileByKey(key);
        ObjectMetadata objectMetadata = ossObject.getObjectMetadata();
        String md5 = objectMetadata.getETag().toLowerCase();
        Date lastModified = objectMetadata.getLastModified();
        //修改文件信息
        ossFile.setMd5(md5);
        ossFile.setSize(objectMetadata.getContentLength());
        ossFile.setUploadFinishTime(lastModified);
        ossFile.setStatus("upload-finish");
        mongoTemplate.save(ossFile);
        //发送消息队列，上传完成

        //返回前端
        return Result.ok();
    }
}
