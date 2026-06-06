package com.huoke.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huoke.demo.entity.LeadConsultation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface LeadConsultationMapper extends BaseMapper<LeadConsultation> {

    @Update("update lead_consultations set follow_status = #{followStatus} where id = #{id}")
    int updateFollowStatusById(@Param("id") Long id, @Param("followStatus") String followStatus);
}
