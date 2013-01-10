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

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

    private static final Logger LOGGER = LoggerFactory.getLogger("ExceptionAspect");
    private static final Marker ERROR_MARKER = MarkerFactory.getMarker("ERROR");

//    @Pointcut("handler(Exception+) && args(ex)")
//    void handleException(Exception ex) {
//    }
//
////    @Pointcut("execution(* *(String, ..)) && args(th)")
////    void throwingException(Throwable th) {
////    }
//
//    @Before("handleException(ex)")
//    public void logHandledException(Exception ex, JoinPoint.StaticPart thisJoinPointStaticPart) {
//        Signature sig = thisJoinPointStaticPart.getSignature();
//
//        System.out.println("Exception thrown !!!" + sig.getDeclaringType().getName() + ":" + sig.getName() + ":"
//            + ex.toString());
//        LOGGER.error(ERROR_MARKER, "Exception thrown !!!" + sig.getDeclaringType().getName(), sig.getName(),
//            ex.toString());
//    }
//
//
//    //    @AfterThrowing("throwingException(th)")
////    public void logThrowingException(Throwable th, JoinPoint joinPoint) {
//    @AfterThrowing(pointcut = "execution(* *(String, ..))", throwing = "th")
//    public void myAfterThrowing(JoinPoint joinPoint, Throwable th) {
//        Signature signature = joinPoint.getSignature();
//
//        System.out.println(
//            "Exception thrown !!!" + signature.getDeclaringType().getName() + ":" + signature.getName() + ":"
//                + th.toString());
//        LOGGER.error(ERROR_MARKER, "Exception thrown !!!" + signature.getDeclaringType().getName(), signature.getName(),
//            th.toString());
//    }

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
