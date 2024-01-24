package si.fri.rso.aichat.models.converters;

import si.fri.rso.aichat.lib.ChatMetadata;
import si.fri.rso.aichat.models.entities.ChatMetadataEntity;

public class ChatMetadataConverter {

    public static ChatMetadata toDto(ChatMetadataEntity entity) {

        ChatMetadata dto = new ChatMetadata();
        dto.setChatId(entity.getChatId());
        dto.setCreated(entity.getCreated());
        dto.setUserCreated(entity.getUserCreated());
        dto.setText(entity.getText());
        dto.setUserText(entity.getUserText());
        return dto;

    }

    public static ChatMetadataEntity toEntity(ChatMetadata dto) {

        ChatMetadataEntity entity = new ChatMetadataEntity();
        entity.setCreated(dto.getCreated());
        entity.setUserCreated(dto.getUserCreated());
        entity.setUserText(dto.getUserText());
        entity.setText(dto.getText());
        return entity;

    }

}
