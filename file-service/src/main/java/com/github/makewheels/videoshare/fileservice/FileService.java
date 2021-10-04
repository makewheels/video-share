package com.github.makewheels.videoshare.fileservice;

import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.github.makewheels.videoshare.common.bean.file.FileMongoId;
import com.github.makewheels.videoshare.common.bean.file.FileStatus;
import com.github.makewheels.videoshare.common.bean.file.OssFile;
import com.github.makewheels.videoshare.common.bean.file.OssSignRequest;
import com.github.makewheels.videoshare.common.bean.video.Video;
import com.github.makewheels.videoshare.common.mq.Topic;
import com.github.makewheels.videoshare.common.response.ErrorCode;
import com.github.makewheels.videoshare.common.response.Result;
import com.github.makewheels.videoshare.fileservice.mq.RocketMQService;
import com.github.makewheels.videoshare.fileservice.oss.OssService;
import com.github.makewheels.videoshare.fileservice.redis.FileRedisService;
import com.github.makewheels.videoshare.fileservice.upload.Credential;
import com.github.makewheels.videoshare.fileservice.util.FileSnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Resource
    private RocketMQService rocketMQService;

    public OssFile createOssFile(String key) {
        OssFile ossFile = new OssFile();
        ossFile.setStatus(FileStatus.CREATE);
        ossFile.setCreateTime(new Date());
        ossFile.setSnowflakeId(FileSnowflakeUtil.get());
        ossFile.setBucket(ossService.getBucket());
        ossFile.setRegion(ossService.getRegion());
        ossFile.setKey(key);
        ossFile.setBaseUrl(ossService.getBaseUrl());
        ossFile.setAccessUrl(ossService.getAccessUrl(key));
        ossFile.setExtension(FilenameUtils.getExtension(key));
        ossFile.setProvider("aliyun-oss");
        return ossFile;
    }

    public Result<Credential> getTemporaryCredential(String uploadToken, String originalFilename) {
        Video video = fileRedisService.getVideoByUploadToken(uploadToken);
        //删掉Redis uploadToken
        fileRedisService.deleteUploadToken(uploadToken);
        if (video == null) {
            return Result.error(ErrorCode.UPLOAD_TOKEN_EXPIRED);
        }
        String uploadPath = video.getUploadPath();
        //创建我本地文件
        OssFile ossFile = createOssFile(uploadPath);
        ossFile.setUserMongoId(video.getUserMongoId());
        ossFile.setVideoMongoId(video.getMongoId());
        ossFile.setOriginalFilename(originalFilename);
        ossFile.setOriginalBasename(FilenameUtils.getBaseName(originalFilename));
        ossFile.setUploadToken(uploadToken);
        mongoTemplate.save(ossFile);

        //生成阿里云上传凭证
        Credential credential = ossService.getAliyunOssUploadCredential(uploadPath);
        credential.setFileSnowflakeId(ossFile.getSnowflakeId() + "");
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
        ossFile.setStatus(FileStatus.READY);
        mongoTemplate.save(ossFile);

        //发送消息队列，上传完成
        rocketMQService.send(Topic.TOPIC_ORIGINAL_FILE_READY, new FileMongoId(ossFile.getMongoId()));

        //返回前端
        return Result.ok();
    }

    public OssFile getOssFileByMongoId(String mongoId) {
        return fileRepository.findByMongoId(mongoId);
    }

    public OssFile getOssFileByVideoMongoId(String videoMongoId) {
        return fileRepository.findOne("videoMongoId", videoMongoId);
    }

    public Map<String, String> getSignedUrl(List<OssSignRequest> ossSignRequests) {
        Map<String, String> map = new HashMap<>(ossSignRequests.size());
        for (OssSignRequest ossSignRequest : ossSignRequests) {
            String key = ossSignRequest.getKey();
            String url = ossService.getSignedUrl(key, ossSignRequest.getTime());
            map.put(key, url);
        }
        return map;
    }
}
