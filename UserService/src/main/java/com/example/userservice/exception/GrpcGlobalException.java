package com.example.userservice.exception;

import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.advice.GrpcAdvice;
import org.springframework.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GrpcAdvice
public class GrpcGlobalException {

    @GrpcExceptionHandler(BusinessException.class)
    public Status handleBusinessException(BusinessException e) {
        log.error("Bắt được exception này nè");
        return Status.NOT_FOUND.withDescription(e.getErrorResponse().getMessage());
    }

    @GrpcExceptionHandler(NullPointerException.class)
    public Status handleNullPointerException(NullPointerException e){
        log.error("Bat duoc loi Null Pointer: " + e.getMessage());
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }
}
