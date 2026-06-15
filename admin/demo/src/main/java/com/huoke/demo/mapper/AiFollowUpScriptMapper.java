package com.huoke.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huoke.demo.entity.AiFollowUpScript;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AiFollowUpScriptMapper extends BaseMapper<AiFollowUpScript> {

    @Select("select * from ai_follow_up_scripts where lead_id = #{leadId} limit 1")
    AiFollowUpScript selectByLeadId(@Param("leadId") Long leadId);

    @Select("select * from ai_follow_up_scripts order by updated_at desc, id desc")
    List<AiFollowUpScript> selectLatest();
}
