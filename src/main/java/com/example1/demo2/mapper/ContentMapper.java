package com.example1.demo2.mapper;

import com.example1.demo2.pojo.PlanetContent;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ContentMapper {
    //根据内容标题查找
    @Select("select * from tab_planet_content where title=#{title}")
    PlanetContent findByTitle(String title);

    //创建内容
    @Insert("insert into tab_planet_content(planet_id,title,comment,create_time,update_time,content_type)"
            +" values(#{planetId},#{title},#{comment},now(),now(),#{contentType})")
    void add(PlanetContent c);

    //更新知识信息
    @Update("update tab_planet_content set title=#{title}" +
            ",file_url=#{fileUrl}" +
            ",comment=#{comment}" +
            ",update_time=now()" +
            ",content=#{content}"+
            ",version=vertion+1"+
            " where content_id=#{contentId}")
    void update(PlanetContent c);

    //删除知识
    @Delete("delete from tab_planet_content where content_id=#{contentId}")
    void delete(Integer contentId);
}
