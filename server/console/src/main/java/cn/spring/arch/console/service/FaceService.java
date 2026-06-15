package cn.spring.arch.console.service;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.CompareFaceFeatureReqParam;
import cn.spring.arch.console.pojo.req.ExtractFaceFeatureReqParam;
import cn.spring.arch.console.pojo.req.OpenFaceCompareReqParam;
import cn.spring.arch.console.pojo.req.OpenFaceSearchReqParam;
import cn.spring.arch.console.pojo.resp.FaceCompareDTO;
import cn.spring.arch.console.pojo.resp.FaceFeatureDTO;
import cn.spring.arch.console.pojo.resp.OpenFaceCompareDTO;
import cn.spring.arch.console.pojo.resp.OpenFaceSearchDTO;

public interface FaceService {

    RespInfo<FaceFeatureDTO> extractFaceFeature(ExtractFaceFeatureReqParam reqParam);

    RespInfo<FaceCompareDTO> compareFaceFeature(CompareFaceFeatureReqParam reqParam);

    RespInfo<OpenFaceCompareDTO> openCompareFace(OpenFaceCompareReqParam reqParam);

    RespInfo<OpenFaceSearchDTO> openSearchFace(OpenFaceSearchReqParam reqParam);
}

