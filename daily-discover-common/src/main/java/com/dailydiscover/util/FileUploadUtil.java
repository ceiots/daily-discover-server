package com.dailydiscover.util;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Slf4j
@Component
public class FileUploadUtil {
    
    @Value("${file.upload.local-path:uploads}")
    private String localUploadPath;
    
    @Value("${file.upload.base-url:http://localhost:8080/uploads/}")
    private String baseUrl;
    
    @Autowired(required = false)
    private SshUtil sshUtil;
    
    /**
     * 上传文件
     *
     * @param file 文件
     * @param directory 目录
     * @return 文件访问路径
     */
    public String uploadFile(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        
        try {
            // 生成唯一文件名
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
            
            // 目录路径
            //String relativePath = directory + "/" + filename;
            
            // 优先使用SSH上传到远程服务器
            /* if (sshUtil != null) {
                try {
                    // 尝试通过SSH上传
                    return sshUtil.uploadFile(file, relativePath);
                } catch (JSchException | SftpException e) {
                    log.warn("SSH远程上传失败，将尝试本地上传: {}", e.getMessage());
                }
            } */
            
            // 本地上传
            return uploadLocal(file, directory, filename);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 本地上传文件
     */
    private String uploadLocal(MultipartFile file, String directory, String filename) throws IOException {
        // 创建上传目录
        Path uploadDir = Paths.get(localUploadPath, directory);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 保存文件
        Path destination = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        // 打印文件上传路径
        log.info("文件上传路径: {}", destination.toString());   
        
        // 返回文件访问路径
        String relativePath = directory + "/" + filename;
        log.info("文件上传成功，访问路径: {}", baseUrl + relativePath);
        return baseUrl + relativePath;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}