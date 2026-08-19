package com.example.userservice.grpc;

import com.example.grpc.user.v1.GetUserRequest;
import com.example.grpc.user.v1.UserResponse;
import com.example.grpc.user.v1.UserServiceGrpc;
import com.example.userservice.config.GrpcServerInterceptor;
import com.example.userservice.entity.User;
import com.example.userservice.exception.BusinessException;
import com.example.userservice.exception.ErrorResponse;
import com.example.userservice.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        Claims claims = GrpcServerInterceptor.CLAIMS_CONTEXT_KEY.get();
        String roles = claims.get("roles").toString();
        Long userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        UserResponse userResponse = UserResponse.newBuilder()
                .setEmail(user.getEmail())
                .setFullName(user.getFullName())
                .setUserId(user.getUserId())
                .build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

}
