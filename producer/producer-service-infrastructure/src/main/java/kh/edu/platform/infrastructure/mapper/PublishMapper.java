package kh.edu.platform.infrastructure.mapper;

import kh.edu.platform.application.entity.PublishResult;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.kafka.support.SendResult;

@Mapper(componentModel = "spring")
public interface PublishMapper {
    @Mapping(target = "topic", expression = "java(metadata.topic())")
    @Mapping(target = "partition", expression = "java(metadata.partition())")
    @Mapping(target = "offset", expression = "java(metadata.offset())")
    PublishResult toPublishResult(RecordMetadata metadata);

    default PublishResult toPublishResult(
            SendResult<String, String> sendResult
    ) {
        return toPublishResult(sendResult.getRecordMetadata());
    }
}
