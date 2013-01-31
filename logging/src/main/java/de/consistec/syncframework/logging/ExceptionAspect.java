package de.consistec.syncframework.logging;

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
