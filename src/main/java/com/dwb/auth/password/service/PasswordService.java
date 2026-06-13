package com.dwb.auth.password.service;

import com.dwb.auth.password.dto.ForgotPasswordRequest;
import com.dwb.auth.password.dto.ResetPasswordRequest;
import com.dwb.auth.password.dto.SetPasswordRequest;

public interface PasswordService {

    void setPassword(SetPasswordRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
