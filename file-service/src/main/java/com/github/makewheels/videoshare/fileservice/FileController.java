package com.github.makewheels.videoshare.fileservice;

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
    public Credential getTemporaryCredential() {
        return fileService.getTemporaryCredential();
    }

}
