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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The name/pair value container for a text complexity annotation.
 */
@Data
public class TextComplexityType {
	/**
	 * The name of the complexity measure. This is taken from an enumerated vocabulary.
	 */
	@NotNull
	@JsonProperty("name")
	private NameEnum name;

	/**
	 * The text complexity measure in terms of the named measuring scale. Model Primitive Datatype = NormalizedString.
	 */
	@NotBlank
	@JsonProperty("value")
	private String value;

	/**
	 * The name of the complexity measure. This is taken from an enumerated vocabulary.
	 */
	public enum NameEnum {
		LEXILE("Lexile"),

		FLESCH_KINCAID("Flesch-Kincaid"),

		DALE_SCHALL("Dale-Schall"),

		DRA("DRA"),

		FOUNTAS_PINNELL("Fountas-Pinnell");

		private String value;

		NameEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static NameEnum fromValue(String text) {
			for (NameEnum b : NameEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}
}

