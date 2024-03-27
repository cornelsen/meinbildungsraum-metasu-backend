/*
 * Copyright (c) 2023-2024 Cornelsen Verlag GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cornelsen.nep.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class UIExceptionHandler {

	private static final String EXCEPTION_MESSAGE = "Exception: {}, message: {}";
	private final Environment environment;

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<Object> handleException(WebExchangeBindException ex) {

		log.error(EXCEPTION_MESSAGE, ex.getClass().getName(), ex.getMessage(), ex);

		Map<String, ApiErrorDetails.ErrorFiled> errors = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, ApiErrorDetails.ErrorFiled::new));
		ApiErrorDetails apiErrorDetails = of(HttpStatus.BAD_REQUEST)
			.withType(ex.getClass().getSimpleName())
			.withMessage("errors.validation")
			.withErrors(errors);
		return ResponseEntity.status(apiErrorDetails.getStatus()).body(apiErrorDetails);
	}

	@ExceptionHandler(NoSuchElementException.class)
	protected ResponseEntity<Object> handleNoSuchElementException(final NoSuchElementException ex, final WebRequest request) {
		log.error(EXCEPTION_MESSAGE, ex.getClass().getName(), ex.getMessage());

		ApiErrorDetails apiErrorDetails = of(HttpStatus.NOT_FOUND)
			.withPath(((ServletWebRequest) request).getRequest().getRequestURI())
			.withType(ex.getClass().getSimpleName())
			.withMessage("errors.notFound");
		return ResponseEntity.status(apiErrorDetails.getStatus()).body(apiErrorDetails);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<Object> handleMissingServletRequestParameterException(final MissingServletRequestParameterException ex, final WebRequest request) {
		log.error(EXCEPTION_MESSAGE, ex.getClass().getName(), ex.getMessage());

		ApiErrorDetails apiErrorDetails = of(HttpStatus.BAD_REQUEST)
			.withPath(((ServletWebRequest) request).getRequest().getRequestURI())
			.withType(ex.getClass().getSimpleName())
			.withMessage(ex.getMessage());
		return ResponseEntity.status(apiErrorDetails.getStatus()).body(apiErrorDetails);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final WebRequest request) {
		log.error(EXCEPTION_MESSAGE, ex.getClass().getName(), ex.getMessage());

		Map<String, ApiErrorDetails.ErrorFiled> errors = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, ApiErrorDetails.ErrorFiled::new));
		ApiErrorDetails apiErrorDetails = of(HttpStatus.BAD_REQUEST)
			.withPath(((ServletWebRequest) request).getRequest().getRequestURI())
			.withErrors(errors)
			.withType(ex.getClass().getSimpleName())
			.withMessage("errors.validation");
		return ResponseEntity.status(apiErrorDetails.getStatus()).body(apiErrorDetails);
	}

	@ExceptionHandler({IllegalArgumentException.class})
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.error(EXCEPTION_MESSAGE, ex.getClass().getName(), ex.getMessage(), ex);
		ApiErrorDetails apiErrorDetails = of(HttpStatus.BAD_REQUEST)
			.withMessage(ex.getMessage())
			.withType(ex.getClass().getSimpleName());
		return ResponseEntity.status(apiErrorDetails.getStatus()).body(apiErrorDetails);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException ex) {
		log.error(EXCEPTION_MESSAGE, ex.getClass().getName(), ex.getMessage(), ex);
		ApiErrorDetails apiErrorDetails = of(HttpStatus.BAD_REQUEST)
			.withMessage("errors.file.tooLarge")
			.withType(ex.getClass().getSimpleName());
		return ResponseEntity.status(apiErrorDetails.getStatus()).body(apiErrorDetails);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<Object> handleAll(Exception ex) {
		log.error(EXCEPTION_MESSAGE, ex.getClass().getName(), ex.getMessage(), ex);
		ApiErrorDetails apiErrorDetails = of(HttpStatus.INTERNAL_SERVER_ERROR)
			.withMessage("errors.http.500")
			.withType(ex.getClass().getSimpleName());
		return ResponseEntity.status(apiErrorDetails.getStatus()).body(apiErrorDetails);
	}

	private ApiErrorDetails of(HttpStatus status) {
		return new ApiErrorDetails()
			.withStatus(status)
			.withVersion(environment.getProperty("info.application-version"))
			.withBuildNr(environment.getProperty("info.build-nr"))
			.withTier(environment.getProperty("info.tier"));
	}
}
