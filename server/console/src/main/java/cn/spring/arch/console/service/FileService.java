package cn.spring.arch.console.service;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.DeleteFileReqParam;
import cn.spring.arch.console.pojo.resp.FileUploadRespData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface FileService {

    RespInfo<FileUploadRespData> upload(MultipartFile multipartFile, String fileCategory, String publicUrlPrefix);

    RespInfo<FileUploadRespData> uploadBytes(String fileName, byte[] fileBytes, String fileCategory, String publicUrlPrefix);

    RespInfo<FileUploadRespData> uploadDataUrl(String fileName, String dataUrl, String fileCategory, String publicUrlPrefix);

    void download(String fileId, HttpServletResponse response);

    RespInfo<Void> deleteFile(DeleteFileReqParam reqParam);
}

