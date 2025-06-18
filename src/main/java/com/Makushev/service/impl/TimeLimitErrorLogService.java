package com.Makushev.service.impl;


import com.Makushev.dto.ErrorLogDto;
import com.Makushev.model.TimeLimitExceedLog;
import com.Makushev.repository.TimeLimitExceedLogRepository;
import com.Makushev.service.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimeLimitErrorLogService implements ErrorLogService {

    private final TimeLimitExceedLogRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(ErrorLogDto logDto) {
        TimeLimitExceedLog log = TimeLimitExceedLog.builder()
                .TimeMs(logDto.getTime())
                .signature(logDto.getMethodName())
                .build();
        repository.save(log);
    }
}
