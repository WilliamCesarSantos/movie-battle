package br.com.santos.william.moviebattle.commons.error.decode;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ErrorDecode implements ErrorDecoder {

    final ErrorDecoder delegate = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        var exception = delegate.decode(methodKey, response);

        if (exception instanceof RetryableException) {
            return exception;
        }

        if (response.status() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            return new RetryableException(
                    HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "504 error",
                    response.request().httpMethod(),
                    exception,
                    null,
                    response.request()
            );
        }
        return exception;
    }

}
