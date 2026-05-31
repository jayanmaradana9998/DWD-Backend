package com.dwb.otp.service;

import com.dwb.common.dto.BaseResponse;
import com.dwb.otp.dto.SendPhoneOtpRequest;
import com.dwb.otp.dto.VerifyPhoneOtpRequest;

public interface OtpService {

    BaseResponse<Object> sendPhoneOtp(SendPhoneOtpRequest request);

    BaseResponse<Object> verifyPhoneOtp(VerifyPhoneOtpRequest request);
}
