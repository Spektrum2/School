package ru.hogwarts.school.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.service.StudentService;

@Aspect
    @Component
    @ConditionalOnExpression("${aspect.enabled:true}")
    public class ExecutionTimeAdvice {
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

        @Around("@annotation(TrackExecutionTime)")
        public Object executionTime(ProceedingJoinPoint point) throws Throwable {
            long startTime = System.currentTimeMillis();
            Object object = point.proceed();
            long endTime = System.currentTimeMillis();
            logger.info("Class Name: "+ point.getSignature().getDeclaringTypeName() +". Method Name: "+ point.getSignature().getName() + ". Time taken for Execution is : " + (endTime-startTime) +"ms");
            return object;
        }
    }

