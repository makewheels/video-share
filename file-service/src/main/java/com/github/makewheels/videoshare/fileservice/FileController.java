package com.github.makewheels.videoshare.fileservice;

import com.github.makewheels.videoshare.common.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("file")
public class FileController {
    @Resource
    FileService fileService;

    @GetMapping("getTemporaryCredential")
    public Result<Credential> getTemporaryCredential(String uploadToken) {
        return fileService.getTemporaryCredential(uploadToken);
    }

}
