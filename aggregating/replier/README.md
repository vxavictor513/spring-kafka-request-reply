# Replier

## Sample Commands
    # First requester
    mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080,--spring.application.name=replier-0
    
    # Second requester
    mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081,--spring.application.name=replier-1