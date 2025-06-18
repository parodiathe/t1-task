package com.Makushev.aop;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

public record CachedData(
        LocalDateTime time,
        Object data
){}
