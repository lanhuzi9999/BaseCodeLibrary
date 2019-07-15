package com.example.basecodelibrary.util;

public class ApkSignatureException extends Exception {

    public int m_errorCode;
    public String m_errorString;

    public ApkSignatureException(int errorCode, String errorString) {
        super();
        // TODO Auto-generated constructor stub

        m_errorCode = errorCode;
        m_errorString = errorString;
    }

}
