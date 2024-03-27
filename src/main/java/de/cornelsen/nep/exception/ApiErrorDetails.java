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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@JsonPropertyOrder({"title", "status", "appVersion", "timestamp", "path", "message", "errors", "type", "apiVersion", "buildNr", "tier"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiErrorDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private HttpStatus status;
	private String message;
	private String description;
	private String apiVersion;

	private String buildNr;
	private String path;
	private String type;
	private String tier;

	private Map<String, ErrorFiled> errors;
	private LocalDateTime timestamp = LocalDateTime.now();

	public String getTitle() {
		return status.getReasonPhrase();
	}

	@JsonProperty("status")
	public int getStatusCode() {
		return status.value();
	}

	public String getMessage() {
		return message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getPath() {
		return path;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String getBuildNr() {
		return buildNr;
	}

	public String getTier() {
		return tier;
	}

	public Map<String, ErrorFiled> getErrors() {
		return errors;
	}

	@JsonIgnore
	public HttpStatus getStatus() {
		return status;
	}

	public ApiErrorDetails withStatus(HttpStatus status) {
		this.status = status;
		return this;
	}

	public ApiErrorDetails withMessage(String message) {
		this.message = message;
		return this;
	}

	public ApiErrorDetails withTier(String tier) {
		this.tier = tier;
		return this;
	}

	public ApiErrorDetails withErrors(Map<String, ErrorFiled> errors) {
		this.errors = errors;
		return this;
	}

	public ApiErrorDetails withDescription(String description) {
		this.description = description;
		return this;
	}

	public ApiErrorDetails withVersion(String appVersion) {
		this.apiVersion = appVersion;
		return this;
	}

	public ApiErrorDetails withPath(String path) {
		this.path = path;
		return this;
	}

	public ApiErrorDetails withType(String type) {
		this.type = type;
		return this;
	}

	public ApiErrorDetails withBuildNr(String buildNr) {
		this.buildNr = buildNr;
		return this;
	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ErrorFiled implements Serializable {
		String error;

		String description;

		public ErrorFiled(FieldError fieldError) {
			if (fieldError.isBindingFailure()) {
				this.error = "Rejected value: " + fieldError.getRejectedValue();
				return;
			}
			this.error = fieldError.getDefaultMessage();
			if (this.error != null && fieldError.getRejectedValue() != null && this.error.contains("#value")) {
				this.error = this.error.replace("#value", ":" + fieldError.getRejectedValue().toString());
			}
		}

	}

	@Override
	public String toString() {
		return "Exception: %s [status: %s, message: %s]".formatted(type, status, message);
	}
}

