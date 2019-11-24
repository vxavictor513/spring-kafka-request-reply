# Requester

## Sample Commands
    # First requester
    mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8090,--kafka.request.message=foo,--kafka.reply.group=repliesGroup-0,--spring.application.name=requester-0
    
    # Second requester
    mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8091,--kafka.request.message=bar,--kafka.reply.group=repliesGroup-1,--spring.application.name=requester-1