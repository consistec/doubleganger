package de.consistec.syncframework.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * <ul style="list-style-type: none;">
 * <li><b>Company:</b> consistec Engineering and Consulting GmbH</li>
 * <li><b>Date:</b> 26.11.12 15:23</li>
 * </ul>
 *
 * @author marcel
 */
@Aspect
public class ExceptionAspect {


    private static final Logger LOGGER = LoggerFactory.getLogger("ExceptionAspect");
    private static final Marker ERROR_MARKER = MarkerFactory.getMarker("ERROR");

    @Pointcut("execution(* *(..)) && !cflow(within(TracingAspectUtil))")
    void throwableMethod() {
    }

    @Around("throwableMethod()")
    public Object logRuntimeException(ProceedingJoinPoint thisJoinPoint) throws Throwable { //NOSONAR
        try {
            return thisJoinPoint.proceed();
        } catch (Throwable th) {
            if (th instanceof RuntimeException) {
                Signature signature = thisJoinPoint.getSignature();
                LOGGER.error("Runtime Exception thrown in {}:{} Message: {}", signature.getDeclaringType().getName(),
                    signature.getName(),
                    th.toString());
            }
            throw th;
        }
    }
}
