package de.consistec.doubleganger.logging;

/*
 * #%L
 * doppelganger
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
        JoinPoint.StaticPart thisJoinPointStaticPart) {
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
        JoinPoint.StaticPart thisJoinPointStaticPart) {
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
