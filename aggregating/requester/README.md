# Requester

## Sample Commands
    # First requester
    mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8090,--kafka.request.message=foo,--kafka.reply.group=repliesGroup-0,--spring.application.name=requester-0
    
    # Second requester
    mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8091,--kafka.request.message=bar,--kafka.reply.group=repliesGroup-1,--spring.application.name=requester-1


2019-11-24 11:21:31.778  INFO 35335 --- [           main] c.e.k.KafkaRequestReplyApplication       : Return value: [
    ConsumerRecord(topic = kReplies, partition = 0, leaderEpoch = 0, offset = 194, CreateTime = 1574565691757, serialized key size = -1, serialized value size = 47, headers = RecordHeaders(headers = [RecordHeader(key = kafka_correlationId, value = [-5, 92, -73, 5, 101, 83, 64, 90, -113, 90, 70, -117, 86, 32, 84, 87])], isReadOnly = false), key = null, value = APP: REQUESTER-0 | MESSAGE: FOO-11:21:31.510211),
    ConsumerRecord(topic = kReplies, partition = 1, leaderEpoch = 0, offset = 196, CreateTime = 1574565691757, serialized key size = -1, serialized value size = 59, headers = RecordHeaders(headers = [RecordHeader(key = kafka_correlationId, value = [-5, 92, -73, 5, 101, 83, 64, 90, -113, 90, 70, -117, 86, 32, 84, 87])], isReadOnly = false), key = null, value = PROCESSED - App: requester-0 | Message: foo-11:21:31.510211)
]
