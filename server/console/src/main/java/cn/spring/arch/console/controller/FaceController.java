package cn.spring.arch.console.controller;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.CompareFaceFeatureReqParam;
import cn.spring.arch.console.pojo.req.ExtractFaceFeatureReqParam;
import cn.spring.arch.console.pojo.req.OpenFaceCompareReqParam;
import cn.spring.arch.console.pojo.req.OpenFaceSearchReqParam;
import cn.spring.arch.console.pojo.resp.FaceCompareDTO;
import cn.spring.arch.console.pojo.resp.FaceFeatureDTO;
import cn.spring.arch.console.pojo.resp.OpenFaceCompareDTO;
import cn.spring.arch.console.pojo.resp.OpenFaceSearchDTO;
import cn.spring.arch.console.service.FaceService;
import cn.spring.arch.framework.operationlog.annotation.OperateLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "人脸识别")
@RestController
@RequestMapping("/face")
public class FaceController {

    @Resource
    private FaceService faceService;

    @Operation(summary = "提取人脸特征值", description = "权限：system:user:query")
    @PostMapping("/extract-face-feature")
    public RespInfo<FaceFeatureDTO> extractFaceFeature(@Valid @RequestBody ExtractFaceFeatureReqParam reqParam) {
        return faceService.extractFaceFeature(reqParam);
    }

    @Operation(summary = "人脸特征值比对", description = "权限：system:user:query")
    @PostMapping("/compare-face-feature")
    @OperateLog(module = "人脸识别", action = "人脸特征值比对")
    public RespInfo<FaceCompareDTO> compareFaceFeature(@Valid @RequestBody CompareFaceFeatureReqParam reqParam) {
        return faceService.compareFaceFeature(reqParam);
    }

    @Operation(summary = "开放 1 比 1 人脸比对")
    @PostMapping("/compare")
    public RespInfo<OpenFaceCompareDTO> compare(@Valid @RequestBody OpenFaceCompareReqParam reqParam) {
        return faceService.openCompareFace(reqParam);
    }

    @Operation(summary = "开放 1 比 N 人脸检索")
    @PostMapping("/search")
    public RespInfo<OpenFaceSearchDTO> search(@Valid @RequestBody OpenFaceSearchReqParam reqParam) {
        return faceService.openSearchFace(reqParam);
    }
}

