package com.Makushev.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorLogDto{
        private String methodName;
        private Exception exception;
        private Long time;
}
