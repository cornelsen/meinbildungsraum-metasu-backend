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
import lombok.Data;

/**
 * The container for the learning objectives which are addressed by the resource.
 */
@Data
public class LearningObjectivesType {
	/**
	 * A category of alignment between the learning resource and the framework node.
	 */
	@JsonProperty("alignmentType")
	private AlignmentTypeEnum alignmentType;

	/**
	 * The framework to which the resource being described is aligned. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("educationalFramework")
	private String educationalFramework;

	/**
	 * The description of a node in an established educational framework. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("targetDescription")
	private String targetDescription;

	/**
	 * The name of a node in an established educational framework. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("targetName")
	private String targetName;

	/**
	 * The URL of a node in an established educational framework. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("targetURL")
	private String targetURL;

	/**
	 * Reference to a CASE CFItem for a standard or skill [CASE, 17]. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("caseItemUri")
	private String caseItemUri;

	/**
	 * Reference to CASE CFItem as a GUID [CASE, 17]. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("caseItemGUID")
	private String caseItemGUID;

	/**
	 * A category of alignment between the learning resource and the framework node.
	 */
	public enum AlignmentTypeEnum {
		ASSESSES("assesses"),

		TEACHES("teaches"),

		REQUIRES("requires"),

		TEXTCOMPLEXITY("textComplexity"),

		READINGLEVEL("readingLevel"),

		EDUCATIONALSUBJECT("educationalSubject"),

		EDUCATIONLEVEL("educationLevel");

		private String value;

		AlignmentTypeEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static AlignmentTypeEnum fromValue(String text) {
			for (AlignmentTypeEnum b : AlignmentTypeEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

}

