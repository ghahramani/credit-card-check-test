package com.navid.test.creditcard.config.serdes.deserializer

import org.springframework.core.MethodParameter
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.Locale

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 **/

@Component
class PageableResolver(private val resolver: PageableHandlerMethodArgumentResolver) : HandlerMethodArgumentResolver {

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return Mono.just(resolver.resolveArgument(parameter, null, MockNative(exchange.request.queryParams), null))
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return this.resolver.supportsParameter(parameter)
    }

    private class MockNative(private val parameters: MultiValueMap<String, String>) : NativeWebRequest {
        override fun getParameter(paramName: String): String? {
            return this.parameters.getFirst(paramName)
        }

        override fun getParameterValues(paramName: String): Array<String>? {
            return this.parameters[paramName]?.toTypedArray()
        }

        override fun isUserInRole(role: String): Boolean {
            return false
        }

        override fun getRemoteUser(): String? {
            return null
        }

        override fun getLocale(): Locale {
            return Locale.getDefault()
        }

        override fun getParameterMap(): MutableMap<String, Array<String>> {
            return mutableMapOf()
        }

        override fun getSessionId(): String {
            return ""
        }

        override fun getAttributeNames(scope: Int): Array<String> {
            return arrayOf()
        }

        override fun registerDestructionCallback(name: String, callback: Runnable, scope: Int) {
        }

        override fun resolveReference(key: String): Any? {
            return null
        }

        override fun getHeaderValues(headerName: String): Array<String>? {
            return null
        }

        override fun getUserPrincipal(): Principal? {
            return null
        }

        override fun getDescription(includeClientInfo: Boolean): String {
            return ""
        }

        override fun getSessionMutex(): Any {
            return ""
        }

        override fun getNativeResponse(): Any? {
            return null
        }

        override fun <T : Any?> getNativeResponse(requiredType: Class<T>?): T? {
            return null
        }

        override fun getParameterNames(): MutableIterator<String> {
            return mutableListOf<String>().iterator()
        }

        override fun getNativeRequest(): Any {
            return ""
        }

        override fun <T : Any?> getNativeRequest(requiredType: Class<T>?): T? {
            return null
        }

        override fun removeAttribute(name: String, scope: Int) {
        }

        override fun getHeader(headerName: String): String? {
            return null
        }

        override fun getContextPath(): String {
            return ""
        }

        override fun checkNotModified(lastModifiedTimestamp: Long): Boolean {
            return false
        }

        override fun checkNotModified(etag: String): Boolean {
            return false
        }

        override fun checkNotModified(etag: String?, lastModifiedTimestamp: Long): Boolean {
            return false
        }

        override fun getHeaderNames(): MutableIterator<String> {
            return mutableListOf<String>().iterator()
        }

        override fun getAttribute(name: String, scope: Int): Any? {
            return null
        }

        override fun setAttribute(name: String, value: Any, scope: Int) {
        }

        override fun isSecure(): Boolean {
            return false
        }
    }

}
