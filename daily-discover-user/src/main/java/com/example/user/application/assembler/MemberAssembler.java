package com.example.user.application.assembler;

import com.example.user.api.vo.MemberVO;
import com.example.user.api.vo.MemberLevelVO;
import com.example.user.application.dto.MemberDTO;
import com.example.user.application.dto.MemberLevelDTO;
import com.example.user.application.dto.PointsLogDTO;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.domain.model.member.MemberLevel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 会员数据转换器
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberAssembler {

    /**
     * Member实体转MemberDTO
     *
     * @param member 会员实体
     * @return 会员DTO
     */
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "userId", source = "userId.value")
    MemberDTO toDTO(Member member);

    /**
     * MemberDTO转Member实体
     *
     * @param memberDTO 会员DTO
     * @return 会员实体
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "longToMemberId")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "longToUserId")
    Member toEntity(MemberDTO memberDTO);

    /**
     * MemberLevel实体转MemberLevelDTO
     *
     * @param memberLevel 会员等级实体
     * @return 会员等级DTO
     */
    MemberLevelDTO toLevelDTO(MemberLevel memberLevel);

    /**
     * MemberLevelDTO转MemberLevel实体
     *
     * @param memberLevelDTO 会员等级DTO
     * @return 会员等级实体
     */
    MemberLevel toLevelEntity(MemberLevelDTO memberLevelDTO);

    /**
     * MemberLevel实体列表转MemberLevelDTO列表
     *
     * @param memberLevels 会员等级实体列表
     * @return 会员等级DTO列表
     */
    List<MemberLevelDTO> toLevelDTOList(List<MemberLevel> memberLevels);

    /**
     * UserPointsLog实体转PointsLogDTO
     *
     * @param userPointsLog 积分记录实体
     * @return 积分记录DTO
     */
    @Mapping(target = "userId", source = "userId.value")
    PointsLogDTO toPointsLogDTO(UserPointsLog userPointsLog);

    /**
     * UserPointsLog实体列表转PointsLogDTO列表
     *
     * @param userPointsLogs 积分记录实体列表
     * @return 积分记录DTO列表
     */
    List<PointsLogDTO> toPointsLogDTOList(List<UserPointsLog> userPointsLogs);

    /**
     * MemberDTO转MemberVO
     *
     * @param memberDTO 会员DTO
     * @return 会员VO
     */
    MemberVO toVO(MemberDTO memberDTO);

    /**
     * MemberLevelDTO转MemberLevelVO
     *
     * @param memberLevelDTO 会员等级DTO
     * @return 会员等级VO
     */
    MemberLevelVO toLevelVO(MemberLevelDTO memberLevelDTO);

    /**
     * MemberDTO列表转MemberVO列表
     *
     * @param memberDTOs 会员DTO列表
     * @return 会员VO列表
     */
    List<MemberVO> toVOList(List<MemberDTO> memberDTOs);

    /**
     * MemberLevelDTO列表转MemberLevelVO列表
     *
     * @param memberLevelDTOs 会员等级DTO列表
     * @return 会员等级VO列表
     */
    List<MemberLevelVO> toLevelVOList(List<MemberLevelDTO> memberLevelDTOs);
    
    /**
     * 将Long转换为UserId
     */
    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }
    
    /**
     * 将Long转换为MemberId
     */
    @Named("longToMemberId")
    default com.example.user.domain.model.id.MemberId longToMemberId(Long id) {
        return id != null ? new com.example.user.domain.model.id.MemberId(id) : null;
    }
} 