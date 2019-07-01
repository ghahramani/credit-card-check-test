package com.navid.test.creditcard.config.aop.logging

import com.navid.test.creditcard.config.AppConstants
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.stereotype.Component
import java.util.Arrays

/**
 * Aspect for logging execution of service and repository Spring components.
 *
 *
 * By default, it only runs with the "dev" profile.
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Aspect
@Component
@Profile(AppConstants.General.PROFILE_DEV)
class LoggingAspect(private val env: Environment) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    fun logAfterThrowing(joinPoint: JoinPoint, e: Throwable) =
        if (env.acceptsProfiles(Profiles.of(AppConstants.General.PROFILE_DEV))) {
            logger.error(
                "Exception in {}.{}() with cause = \'{}\' and exception = \'{}\'",
                joinPoint.signature.declaringTypeName,
                joinPoint.signature.name,
                if (e.cause != null) e.cause else "NULL",
                e.message,
                e
            )
        } else {
            logger.error(
                "Exception in {}.{}() with cause = {}",
                joinPoint.signature.declaringTypeName,
                joinPoint.signature.name,
                if (e.cause != null) e.cause else "NULL"
            )
        }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    @Throws(Throwable::class)
    fun logAround(joinPoint: ProceedingJoinPoint): Any {
            logger.debug(
                "Enter: {}.{}() with argument[s] = {}", joinPoint.signature.declaringTypeName,
                joinPoint.signature.name, Arrays.toString(joinPoint.args)
            )
        try {
            val result = joinPoint.proceed()
                logger.debug(
                    "Exit: {}.{}() with result = {}",
                    joinPoint.signature.declaringTypeName,
                    joinPoint.signature.name,
                    result
                )
            return result
        } catch (e: IllegalArgumentException) {
            logger.error(
                "Illegal argument: {} in {}.{}()",
                Arrays.toString(joinPoint.args),
                joinPoint.signature.declaringTypeName,
                joinPoint.signature.name
            )
            throw e
        }

    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.navid.test.creditcard..*)")
    fun applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut(
        "within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)"
    )
    fun springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }
}
