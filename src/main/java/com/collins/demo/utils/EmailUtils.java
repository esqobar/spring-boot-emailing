package com.collins.demo.utils;

public class EmailUtils {
    public static String getEmailMessage(String name, String host, String token){
        return "hello " + name + ",\n\nYour account has been created. Please click the link below to verify your account. \n\n"
                + getVerificationUrl(host, token) + "\n\nThe Support Team";
    }

    public static String getVerificationUrl(String host, String token) {
        return host + "/api/users?token=" + token;
    }
}
