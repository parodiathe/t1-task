package com.Makushev.service;

import com.Makushev.dto.ErrorLogDto;

import java.io.PrintWriter;
import java.io.StringWriter;

public interface ErrorLogService {
    void saveErrorLog(ErrorLogDto logDto);

    default String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
