package de.consistec.syncframework.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * <ul style="list-style-type: none;">
 * <li><b>Company:</b> consistec Engineering and Consulting GmbH</li>
 * <li><b>Date:</b> 26.11.12 10:02</li>
 * </ul>
 *
 * @author marcel
 */
@Aspect
public class TracingAspect {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

    private static final Logger LOGGER = LoggerFactory.getLogger("TracingAspect");
    private static final Marker TRACE_MARKER = MarkerFactory.getMarker("TRACE");

    @Pointcut("execution(*.new(..)) && !cflow(within(TracingAspect))")
    void executeConstructors() {
    }

    @Pointcut("execution(* *(..)) && !cflow(within(TracingAspect))")
    void executeMethods() {
    }

    @Before("executeConstructors()")
    public void traceConstructors(JoinPoint thisJoinPoint,
                                  JoinPoint.StaticPart thisJoinPointStaticPart
    ) {
        Signature sig = thisJoinPointStaticPart.getSignature();
        String line = "" + thisJoinPointStaticPart.getSourceLocation().getLine();

        Object[] paramValues = thisJoinPoint.getArgs();
        String[] paramNames = ((CodeSignature) thisJoinPointStaticPart
            .getSignature()).getParameterNames();

        String arguments = TracingAspectUtil.argsToString(paramValues, paramNames);

        LOGGER.trace(TRACE_MARKER, "Create {}.{} ({}) line {}", sig.getDeclaringTypeName(),
            sig.getName(), arguments, line);
    }

    @Before("executeMethods()")
    public void traceMethods(JoinPoint thisJoinPoint,
                             JoinPoint.StaticPart thisJoinPointStaticPart
    ) {
        Signature sig = thisJoinPointStaticPart.getSignature();
        String line = "" + thisJoinPointStaticPart.getSourceLocation().getLine();

        Object[] paramValues = thisJoinPoint.getArgs();
        String[] paramNames = ((CodeSignature) thisJoinPointStaticPart
            .getSignature()).getParameterNames();

        String arguments = TracingAspectUtil.argsToString(paramValues, paramNames);

        LOGGER.trace(TRACE_MARKER, "Enter {}.{} ({}) line {}", sig.getDeclaringTypeName(),
            sig.getName(), arguments, line);
    }

    @After("executeMethods()")
    public void traceMethods(JoinPoint.StaticPart thisJoinPointStaticPart) {
        Signature sig = thisJoinPointStaticPart.getSignature();

        LOGGER.trace(TRACE_MARKER, "Leave {}.{}", sig.getDeclaringTypeName(), sig.getName());
    }
}
