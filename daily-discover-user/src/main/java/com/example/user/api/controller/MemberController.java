package com.example.user.api.controller;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.api.vo.MemberLevelVO;
import com.example.user.api.vo.MemberVO;
import com.example.user.application.dto.MemberDTO;
import com.example.user.application.dto.MemberLevelDTO;
import com.example.user.application.service.MemberService;
import com.example.user.domain.repository.MemberQueryCondition;
import com.example.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员控制器
 */
@Api(tags = "会员管理")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ApiOperation("获取会员信息")
    @GetMapping("/{userId}")
    public Result<MemberVO> getMemberInfo(@PathVariable Long userId) {
        MemberDTO memberDTO = memberService.getMemberInfo(userId);
        MemberVO memberVO = convertToMemberVO(memberDTO);
        return Result.success(memberVO);
    }

    @ApiOperation("开通会员")
    @PostMapping("/{userId}")
    public Result<MemberVO> openMember(
            @PathVariable Long userId,
            @ApiParam("会员等级") @RequestParam Integer level,
            @ApiParam("是否永久") @RequestParam(defaultValue = "false") Boolean isForever,
            @ApiParam("开通月数") @RequestParam(defaultValue = "12") Integer months) {
        MemberDTO memberDTO = memberService.openMember(userId, level, isForever, months);
        MemberVO memberVO = convertToMemberVO(memberDTO);
        return Result.success(memberVO);
    }

    @ApiOperation("续费会员")
    @PostMapping("/{userId}/renew")
    public Result<MemberVO> renewMember(
            @PathVariable Long userId,
            @ApiParam("续费月数") @RequestParam Integer months) {
        MemberDTO memberDTO = memberService.renewMember(userId, months);
        MemberVO memberVO = convertToMemberVO(memberDTO);
        return Result.success(memberVO);
    }

    @ApiOperation("升级会员")
    @PostMapping("/{userId}/upgrade")
    public Result<MemberVO> upgradeMember(
            @PathVariable Long userId,
            @ApiParam("会员等级") @RequestParam Integer level) {
        MemberDTO memberDTO = memberService.upgradeMember(userId, level);
        MemberVO memberVO = convertToMemberVO(memberDTO);
        return Result.success(memberVO);
    }

    @ApiOperation("取消会员")
    @DeleteMapping("/{userId}")
    public Result<Boolean> cancelMember(@PathVariable Long userId) {
        boolean result = memberService.cancelMember(userId);
        return Result.success(result);
    }

    @ApiOperation("冻结会员")
    @PutMapping("/{userId}/freeze")
    public Result<Boolean> freezeMember(@PathVariable Long userId) {
        boolean result = memberService.freezeMember(userId);
        return Result.success(result);
    }

    @ApiOperation("解冻会员")
    @PutMapping("/{userId}/unfreeze")
    public Result<Boolean> unfreezeMember(@PathVariable Long userId) {
        boolean result = memberService.unfreezeMember(userId);
        return Result.success(result);
    }

    @ApiOperation("增加成长值")
    @PostMapping("/{userId}/growth")
    public Result<MemberVO> addGrowth(
            @PathVariable Long userId,
            @ApiParam("成长值") @RequestParam Integer growthValue,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId) {
        MemberDTO memberDTO = memberService.addGrowth(userId, growthValue, source, sourceId);
        MemberVO memberVO = convertToMemberVO(memberDTO);
        return Result.success(memberVO);
    }

    @ApiOperation("增加积分")
    @PostMapping("/{userId}/points/add")
    public Result<MemberVO> addPoints(
            @PathVariable Long userId,
            @ApiParam("积分") @RequestParam Integer points,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId) {
        MemberDTO memberDTO = memberService.addPoints(userId, points, source, sourceId);
        MemberVO memberVO = convertToMemberVO(memberDTO);
        return Result.success(memberVO);
    }

    @ApiOperation("使用积分")
    @PostMapping("/{userId}/points/use")
    public Result<MemberVO> usePoints(
            @PathVariable Long userId,
            @ApiParam("积分") @RequestParam Integer points,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId) {
        MemberDTO memberDTO = memberService.usePoints(userId, points, source, sourceId);
        MemberVO memberVO = convertToMemberVO(memberDTO);
        return Result.success(memberVO);
    }

    @ApiOperation("分页查询会员")
    @GetMapping("/page")
    public Result<PageResult<MemberVO>> getMemberPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("会员等级") @RequestParam(required = false) Integer level,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        MemberQueryCondition condition = new MemberQueryCondition();
        condition.setUserId(userId);
        condition.setLevel(level);
        condition.setStatus(status);
        PageResult<MemberDTO> pageResult = memberService.getMemberPage(pageRequest, condition);
        
        List<MemberVO> memberVOList = pageResult.getList().stream()
                .map(this::convertToMemberVO)
                .collect(Collectors.toList());
        
        return Result.success(new PageResult<>(memberVOList, pageResult.getTotal(), pageResult.getPages(), pageResult.getPageNum(), pageResult.getPageSize()));
    }

    @ApiOperation("获取会员等级列表")
    @GetMapping("/levels")
    public Result<List<MemberLevelVO>> getMemberLevels() {
        List<MemberLevelDTO> memberLevelDTOList = memberService.getMemberLevels();
        List<MemberLevelVO> memberLevelVOList = memberLevelDTOList.stream()
                .map(this::convertToMemberLevelVO)
                .collect(Collectors.toList());
        return Result.success(memberLevelVOList);
    }

    @ApiOperation("获取会员等级")
    @GetMapping("/levels/{id}")
    public Result<MemberLevelVO> getMemberLevel(@PathVariable Long id) {
        MemberLevelDTO memberLevelDTO = memberService.getMemberLevelById(id);
        MemberLevelVO memberLevelVO = convertToMemberLevelVO(memberLevelDTO);
        return Result.success(memberLevelVO);
    }

    @ApiOperation("创建会员等级")
    @PostMapping("/levels")
    public Result<MemberLevelVO> createMemberLevel(@Valid @RequestBody MemberLevelVO memberLevelVO) {
        MemberLevelDTO memberLevelDTO = new MemberLevelDTO();
        BeanUtils.copyProperties(memberLevelVO, memberLevelDTO);
        MemberLevelDTO createdMemberLevelDTO = memberService.createMemberLevel(memberLevelDTO);
        MemberLevelVO createdMemberLevelVO = convertToMemberLevelVO(createdMemberLevelDTO);
        return Result.success(createdMemberLevelVO);
    }

    @ApiOperation("更新会员等级")
    @PutMapping("/levels/{id}")
    public Result<MemberLevelVO> updateMemberLevel(@PathVariable Long id, @Valid @RequestBody MemberLevelVO memberLevelVO) {
        MemberLevelDTO memberLevelDTO = new MemberLevelDTO();
        BeanUtils.copyProperties(memberLevelVO, memberLevelDTO);
        memberLevelDTO.setId(id);
        MemberLevelDTO updatedMemberLevelDTO = memberService.updateMemberLevel(memberLevelDTO);
        MemberLevelVO updatedMemberLevelVO = convertToMemberLevelVO(updatedMemberLevelDTO);
        return Result.success(updatedMemberLevelVO);
    }

    @ApiOperation("删除会员等级")
    @DeleteMapping("/levels/{id}")
    public Result<Boolean> deleteMemberLevel(@PathVariable Long id) {
        boolean result = memberService.deleteMemberLevel(id);
        return Result.success(result);
    }

    /**
     * 将MemberDTO转换为MemberVO
     */
    private MemberVO convertToMemberVO(MemberDTO memberDTO) {
        if (memberDTO == null) {
            return null;
        }
        MemberVO memberVO = new MemberVO();
        BeanUtils.copyProperties(memberDTO, memberVO);
        return memberVO;
    }

    /**
     * 将MemberLevelDTO转换为MemberLevelVO
     */
    private MemberLevelVO convertToMemberLevelVO(MemberLevelDTO memberLevelDTO) {
        if (memberLevelDTO == null) {
            return null;
        }
        MemberLevelVO memberLevelVO = new MemberLevelVO();
        BeanUtils.copyProperties(memberLevelDTO, memberLevelVO);
        return memberLevelVO;
    }
} 