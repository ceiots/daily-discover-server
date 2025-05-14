package com.example.util;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * SSH工具类，用于处理远程文件上传
 */
@Slf4j
@Component
public class SshUtil {

    @Value("${ssh.remote.host}")
    private String remoteHost;
    
    @Value("${ssh.remote.user}")
    private String remoteUser;
    
    @Value("${ssh.remote.port:22}")
    private int remotePort;
    
    @Value("${ssh.remote.base-path}")
    private String remoteBasePath;
    
    @Value("${ssh.remote.content-path}")
    private String remoteContentPath;
    
    @Value("${ssh.remote.private-key-path:#{null}}")
    private String privateKeyPath;
    
    @Value("${ssh.remote.password:#{null}}")
    private String remotePassword;

    /**
     * 上传文件到远程服务器
     * @param file 要上传的文件
     * @param remoteFileName 远程文件名
     * @return 远程文件URL
     */
    public String uploadFile(MultipartFile file, String remoteFileName) throws IOException, JSchException, SftpException {
        // 创建临时文件
        Path tempFile = createTempFile(file);
        
        try {
            // 上传文件到远程服务器
            uploadFileToRemoteServer(tempFile.toFile(), remoteFileName);
            
            // 返回可访问的URL
            return "/images/content/" + remoteFileName;
        } finally {
            // 删除临时文件
            Files.deleteIfExists(tempFile);
        }
    }
    
    /**
     * 创建临时文件
     */
    private Path createTempFile(MultipartFile file) throws IOException {
        // 创建临时目录
        Path tempDir = Files.createTempDirectory("upload_");
        
        // 创建临时文件
        Path tempFile = tempDir.resolve(file.getOriginalFilename());
        Files.write(tempFile, file.getBytes());
        
        return tempFile;
    }
    
    /**
     * 上传文件到远程服务器
     */
    private void uploadFileToRemoteServer(File file, String remoteFileName) throws JSchException, SftpException {
        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        FileInputStream fis = null;
        
        try {
            // 如果使用私钥认证
            if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
                // 添加私钥
                jsch.addIdentity(privateKeyPath);
                log.info("使用私钥认证: {}", privateKeyPath);
            }
            
            // 创建SSH会话
            session = jsch.getSession(remoteUser, remoteHost, remotePort);
            
            // 如果使用密码认证
            if (remotePassword != null && !remotePassword.isEmpty()) {
                session.setPassword(remotePassword);
                log.info("使用密码认证");
            }
            
            // 设置SSH连接属性
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // 连接到远程服务器
            session.connect(30000);
            log.info("SSH会话已建立，连接到: {}", remoteHost);
            
            // 打开SFTP通道
            channel = session.openChannel("sftp");
            channel.connect(30000);
            log.info("SFTP通道已打开");
            
            channelSftp = (ChannelSftp) channel;
            
            // 确保远程目录存在
            try {
                channelSftp.stat(remoteBasePath);
                log.info("远程基础目录已存在: {}", remoteBasePath);
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                    channelSftp.mkdir(remoteBasePath);
                    log.info("创建远程基础目录: {}", remoteBasePath);
                }
            }
            
            try {
                channelSftp.stat(remoteContentPath);
                log.info("远程内容目录已存在: {}", remoteContentPath);
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                    channelSftp.mkdir(remoteContentPath);
                    log.info("创建远程内容目录: {}", remoteContentPath);
                }
            }
            
            // 上传文件
            fis = new FileInputStream(file);
            channelSftp.cd(remoteContentPath);
            channelSftp.put(fis, remoteFileName);
            log.info("文件上传成功: {}", remoteFileName);
        } catch (FileNotFoundException e) {
            log.error("文件不存在", e);
            throw new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE, "文件不存在: " + e.getMessage());
        } finally {
            // 关闭文件流
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("关闭文件流失败", e);
                }
            }
            
            // 关闭连接
            if (channelSftp != null) {
                channelSftp.exit();
            }
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
    
    /**
     * 检查远程目录是否存在，不存在则创建
     */
    public void ensureRemoteDirectoryExists() {
        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        
        try {
            // 如果使用私钥认证
            if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
                // 添加私钥
                jsch.addIdentity(privateKeyPath);
                log.info("使用私钥认证: {}", privateKeyPath);
            }
            
            // 创建SSH会话
            session = jsch.getSession(remoteUser, remoteHost, remotePort);
            
            // 如果使用密码认证
            if (remotePassword != null && !remotePassword.isEmpty()) {
                session.setPassword(remotePassword);
                log.info("使用密码认证");
            }
            
            // 设置SSH连接属性
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // 连接到远程服务器
            session.connect(30000);
            log.info("SSH会话已建立，连接到: {}", remoteHost);
            
            // 打开SFTP通道
            channel = session.openChannel("sftp");
            channel.connect(30000);
            log.info("SFTP通道已打开");
            
            channelSftp = (ChannelSftp) channel;
            
            // 确保远程目录存在
            try {
                channelSftp.stat(remoteBasePath);
                log.info("远程基础目录已存在: {}", remoteBasePath);
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                    channelSftp.mkdir(remoteBasePath);
                    log.info("创建远程基础目录: {}", remoteBasePath);
                }
            }
            
            try {
                channelSftp.stat(remoteContentPath);
                log.info("远程内容目录已存在: {}", remoteContentPath);
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                    channelSftp.mkdir(remoteContentPath);
                    log.info("创建远程内容目录: {}", remoteContentPath);
                }
            }
            
            log.info("远程目录检查完成");
        } catch (JSchException | SftpException e) {
            log.error("检查远程目录时发生异常", e);
        } finally {
            // 关闭连接
            if (channelSftp != null) {
                channelSftp.exit();
            }
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
} 