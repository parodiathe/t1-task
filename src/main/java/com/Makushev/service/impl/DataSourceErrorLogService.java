package com.Makushev.service.impl;

import com.Makushev.dto.ErrorLogDto;
import com.Makushev.model.DataSourceErrorLog;
import com.Makushev.repository.DataSourceErrorLogRepository;
import com.Makushev.service.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Qualifier("dataSourceErrorService")
public class DataSourceErrorLogService implements ErrorLogService {

    private final DataSourceErrorLogRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(ErrorLogDto logDto) {
        DataSourceErrorLog log = DataSourceErrorLog.builder()
                .message(logDto.getException().getMessage())
                .exceptionText(getStackTraceAsString(logDto.getException()))
                .signature(logDto.getMethodName())
                .build();
        repository.save(log);
    }
}
