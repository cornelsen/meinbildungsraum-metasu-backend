/*
 * Copyright (c) 2023 Cornelsen Verlag GmbH
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

package de.cornelsen.nep.model.dto.lti;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;


/**
 * This is the container for the status code and associated information returned within the HTTP messages received from the Service Provider.
 */
@Data
public class ImsxStatusInfoType {
	/**
	 * The code major value (from the corresponding enumerated vocabulary).
	 */
	@NotNull
	@JsonProperty("imsx_codeMajor")
	private ImsxCodeMajorEnum imsxCodeMajor;

	/**
	 * The severity value (from the corresponding enumerated vocabulary).
	 */
	@NotNull
	@JsonProperty("imsx_severity")
	private ImsxSeverityEnum imsxSeverity;

	/**
	 * A human readable description supplied by the entity creating the status code information. Model Primitive Datatype = String.
	 */
	@JsonProperty("imsx_description")
	private String imsxDescription;

	/**
	 * The code major value (from the corresponding enumerated vocabulary).
	 */
	@JsonProperty("imsx_codeMinor")
	private ImsxCodeMinorType imsxCodeMinor;

	@RequiredArgsConstructor
	public enum ImsxCodeMajorEnum {
		SUCCESS("success"),

		PROCESSING("processing"),

		FAILURE("failure"),

		UNSUPPORTED("unsupported");

		private final String value;

		@Override
		@JsonValue
		public String toString() {
			return value;
		}

		@JsonCreator
		public static ImsxCodeMajorEnum fromValue(String text) {
			for (ImsxCodeMajorEnum b : ImsxCodeMajorEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

	/**
	 * The severity value (from the corresponding enumerated vocabulary).
	 */
	public enum ImsxSeverityEnum {
		STATUS("status"),

		WARNING("warning"),

		ERROR("error");

		private String value;

		ImsxSeverityEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static ImsxSeverityEnum fromValue(String text) {
			for (ImsxSeverityEnum b : ImsxSeverityEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

}

