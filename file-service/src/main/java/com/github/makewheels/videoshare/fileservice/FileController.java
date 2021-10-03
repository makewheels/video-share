package com.github.makewheels.videoshare.fileservice;

import com.github.makewheels.videoshare.common.bean.OssFile;
import com.github.makewheels.videoshare.common.response.Result;
import com.github.makewheels.videoshare.fileservice.bean.GetTemporaryCredentialRequest;
import com.github.makewheels.videoshare.fileservice.bean.UploadFinishRequest;
import com.github.makewheels.videoshare.fileservice.upload.Credential;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("file")
public class FileController {
    @Resource
    FileService fileService;

    @PostMapping("getTemporaryCredential")
    public Result<Credential> getTemporaryCredential(
            @RequestBody GetTemporaryCredentialRequest getTemporaryCredentialRequest) {
        return fileService.getTemporaryCredential(getTemporaryCredentialRequest.getUploadToken(),
                getTemporaryCredentialRequest.getOriginalFilename());
    }

    @PostMapping("uploadFinish")
    public Result<Void> uploadFinish(@RequestBody UploadFinishRequest uploadFinishRequest) {
        return fileService.uploadFinish(uploadFinishRequest.getFileSnowflakeId());
    }

    @PostMapping("getOssFileByMongoId")
    public OssFile getOssFileByMongoId(@RequestParam String mongoId) {
        return fileService.getOssFileByMongoId(mongoId);
    }

    @PostMapping("getOssFileByVideoMongoId")
    public OssFile getOssFileByVideoMongoId(@RequestParam String videoMongoId) {
        return fileService.getOssFileByVideoMongoId(videoMongoId);
    }
}
