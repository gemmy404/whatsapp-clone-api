package com.whatsappclone.mapper;

import com.whatsappclone.dto.MessageResponse;
import com.whatsappclone.entity.MessageEntity;
import com.whatsappclone.service.FileService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = FileService.class)
public interface MessageMapper {

    @Mapping(target = "createdAt", source = "createdDate")
    @Mapping(target = "media", expression = "java(FileService.readFileFromLocation(messageEntity.getMediaFilePath()))")
    MessageResponse toMessageResponse(MessageEntity messageEntity);

}
