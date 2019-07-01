package com.navid.test.creditcard.config.async

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.task.AsyncTaskExecutor
import java.util.concurrent.Callable
import java.util.concurrent.Future

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class ExceptionHandlingAsyncTaskExecutor(private val executor: AsyncTaskExecutor) :
    AsyncTaskExecutor, InitializingBean, DisposableBean {

    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        if (executor is InitializingBean) {
            val bean = executor as InitializingBean
            bean.afterPropertiesSet()
        }
    }

    @Throws(Exception::class)
    override fun destroy() {
        if (executor is DisposableBean) {
            val bean = executor as DisposableBean
            bean.destroy()
        }
    }

    override fun execute(task: Runnable) {
        executor.execute(createWrappedRunnable(task))
    }

    override fun execute(task: Runnable, startTimeout: Long) {
        executor.execute(createWrappedRunnable(task), startTimeout)
    }

    override fun submit(task: Runnable): Future<*> {
        return executor.submit(createWrappedRunnable(task))
    }

    override fun <T> submit(task: Callable<T>): Future<T> {
        return executor.submit(createCallable(task))
    }

    private fun <T> createCallable(task: Callable<T>): Callable<T> {
        return Callable {
            try {
                return@Callable task.call()
            } catch (ignore: Exception) {
                log.error(EXCEPTION_MESSAGE, ignore)
                throw ignore
            }
        }
    }

    private fun createWrappedRunnable(task: Runnable): Runnable {
        return Runnable {
            try {
                task.run()
            } catch (ignore: Exception) {
                log.error(EXCEPTION_MESSAGE, ignore)
            }
        }
    }

    companion object {
        const val EXCEPTION_MESSAGE = "Caught async exception"
    }
}
