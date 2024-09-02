package com.App.fullStack.utility;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.App.fullStack.responseHandler.ApiResponse;

@Service
public class APIResponseForFoundOrNot {

    public static <T> ResponseEntity<ApiResponse<T>> generateResponse(T payload, String Success, String Error) {

        ApiResponse<T> response;

        if (payload != null && !((payload instanceof List) && ((List<?>) payload).isEmpty())) {
            response = new ApiResponse<>(true, Success + ".", payload);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response = new ApiResponse<>(false, Error + ".", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }

}