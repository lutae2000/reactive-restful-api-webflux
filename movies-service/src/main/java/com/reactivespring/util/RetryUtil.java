package com.reactivespring.util;

import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsServerException;
import java.time.Duration;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;
import reactor.util.retry.RetrySpec;

public class RetryUtil {

    //retry 할때 딜레이를 준 후 실패시에만 재시도
    public static Retry retrySpec() {
        return RetrySpec.fixedDelay(3, Duration.ofSeconds(1))
            .filter((ex) -> ex instanceof MoviesInfoServerException || ex instanceof ReviewsServerException)
            .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));

    }

}
