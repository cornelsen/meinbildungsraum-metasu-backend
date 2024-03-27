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
 * This is the container for a single code minor status code.
 */
@Data
public class ImsxCodeMinorFieldType {
	/**
	 * This should contain the identity of the system that has produced the code minor status code report. Model Primitive Datatype = NormalizedString.
	 */
	@NotNull
	@JsonProperty("imsx_codeMinorFieldName")
	private String imsxCodeMinorFieldName;

	/**
	 * The code minor status code (this is a value from the corresponding enumerated vocabulary).
	 */
	@NotNull
	@JsonProperty("imsx_codeMinorFieldValue")
	private ImsxCodeMinorFieldValueEnum imsxCodeMinorFieldValue;

	/**
	 * The code minor status code (this is a value from the corresponding enumerated vocabulary).
	 */
	@RequiredArgsConstructor
	public enum ImsxCodeMinorFieldValueEnum {
		FULLSUCCESS("fullsuccess"),

		FORBIDDEN("forbidden"),

		INVALID_QUERY_PARAMETER("invalid_query_parameter"),

		UNAUTHORISEDREQUEST("unauthorisedrequest"),

		INTERNAL_SERVER_ERROR("internal_server_error"),

		SERVER_BUSY("server_busy"),

		INVALID_DATA("invalid_data");

		private final String value;

		@Override
		@JsonValue
		public String toString() {
			return value;
		}

		@JsonCreator
		public static ImsxCodeMinorFieldValueEnum fromValue(String text) {
			for (ImsxCodeMinorFieldValueEnum b : ImsxCodeMinorFieldValueEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}

	}
}

