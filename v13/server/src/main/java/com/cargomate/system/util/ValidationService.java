package com.cargomate.system.util;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ValidationService
{
    public boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && email.contains("@");
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return StringUtils.hasText(phoneNumber) && phoneNumber.matches("\\+?[0-9]+");
    }
}
