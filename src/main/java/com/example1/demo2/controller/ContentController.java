package com.example1.demo2.controller;

import com.example1.demo2.pojo.dto.KnowledgePlanetDto;
import com.example1.demo2.pojo.dto.ResponseMessage;
import com.example1.demo2.util.ConvertUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController //接口返回对象，转化为json文本
@RequestMapping("/content")  //localhost:8081/content/**
@Validated
public class ContentController {
    @Autowired
    private IContentService contentService;

    //知识创建（必要参数：所属星球ID，知识标题，知识类型，知识简介,返回值：知识ID）
    @PostMapping("/create")         //localhost:8081/planet/create
    public ResponseMessage<String> create(@Valid @RequestBody PlanetContentDto content) {
        //查询知识标题
        PlanetContent c = contentService.findByTitle(content.getTitle());
        if(c!=null) {
            //知识标题已经占用
            return ResponseMessage.error("该知识标题已被占用，请重新输入");
        }
        else{
            //没有占用
            contentService.create(content);
            c=contentService.findByTitle(content.getTitle());
            return ResponseMessage.success(c.getContentId());
        }
    }

    //查看知识详情(必要参数：知识标题，返回值：知识信息列表）
    @GetMapping("/contentinfo")
    public ResponseMessage<KnowledgePlanetDto> contentinfo(@Valid @RequestBody PlanetContentDto content) {
        //根据知识标题查询
        PlanetContent c = contentService.findByTitle(content.getTitle());
        if(c==null) {
            return ResponseMessage.error("知识不存在");
        }
        //转化为dto
        PlanetContentDto dto=ConvertUtil.convertPlanetContentToDto(c);
        return ResponseMessage.success(dto);
    }

    //更新知识信息(title,file_url,comment,content等,必要参数：知识标题，返回值：成功或失败）
    @PutMapping("/update")
    public ResponseMessage update(@Valid @RequestBody PlanetContentDto content) {
        PlanetContent c = contentService.findByTitle(content.getTitle());
        // 判断信息是否有变化
        boolean isChanged = false;
        if (!Objects.equals(content.getTitle(),c.getTitle())) isChanged = true;
        if (!Objects.equals(content.getFileUrl(),c.getFileUrl())) isChanged = true;
        if (!Objects.equals(content.getComment(),c.getComment())) isChanged = true;
        if (!Objects.equals(content.getContent(),c.getContent())) isChanged = true;
        if (!isChanged) {
            return ResponseMessage.success("没有需要修改的信息！");
        }

        //更新信息
        contentService.update(content);
        return ResponseMessage.success("已更新知识信息！");
    }

    //删除知识（必要参数：知识标题,返回值：成功或失败）
    @DeleteMapping("/delete")       //localhost:8081/planet/delete
    public ResponseMessage<String> delete(@Valid @RequestBody PlanetContentDto content) {
        //通过id删除知识,通过知识标题查找
        PlanetContent c = contentService.findByTitle(content.getTitle());
        if(c==null) {
            //知识不存在
            return ResponseMessage.error("知识不存在或已被删除");
        }
        else{
            //删除知识
            contentService.delete(c.getContentId());
            return ResponseMessage.success("知识"+content.getTitle()+"已成功删除！");
        }
    }

}
