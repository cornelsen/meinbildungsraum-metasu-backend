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
import de.cornelsen.nep.model.dto.request.LRTEnum;
import de.cornelsen.nep.model.dto.search.MediaType;
import de.cornelsen.nep.model.dto.search.SchoolType;
import de.cornelsen.nep.model.dto.search.Subject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The container for the information about a resource that is supported by a LOR.
 */
@Data
public class ResourceType {
	/**
	 * The name/title of resource. Model Primitive Datatype = NormalizedString.
	 */
	@NotBlank
	@JsonProperty("name")
	private String name;

	/**
	 * A human readable description of the contents of the resource. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("description")
	private String description;

	/**
	 * The subject(s) of the resource. May have multiple subjects tagged. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("subject")
	private List<Subject> subject;

	/**
	 * How to access resource over Internet e.g. HTTP, FTP, etc. A resource must have either a URL or or a LTI Link. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("url")
	private String url;

	@JsonProperty("ltiLink")
	private CCLTILinkType ltiLink;

	@NotEmpty
	@JsonProperty("learningResourceType")
	private List<LRTEnum> learningResourceType = new ArrayList<>();

	/**
	 * The languages used in the resource. International two digit code for language e.g. 'en' for English. Use the [RFC 3066] annotation. Model Primitive Datatype = Language.
	 */
	@JsonProperty("language")
	private List<String> language;

	/**
	 * Link to a thumbnail representing resource. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("thumbnailUrl")
	private String thumbnailUrl;

	/**
	 * Age of the typical intended user. This is described as EITHER the minimum-maximum age range (the format is '11-12', '5-7', etc. with ONLY integers permitted) OR the age as a single integer e.g. '9'. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("typicalAgeRange")
	private String typicalAgeRange;

	/**
	 * A number indicating text complexity based on number of established measures.
	 */
	@JsonProperty("textComplexity")
	private List<TextComplexityType> textComplexity;

	/**
	 * The set of learning objectives addressed by the resource.
	 */
	@JsonProperty("learningObjectives")
	private List<LearningObjectivesType> learningObjectives;

	/**
	 * Author or creator of the resource. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("author")
	private List<String> author;

	/**
	 * Owner of the rights to the resource or who made it available (company or person). Model Primitive Datatype = NormalizedString.
	 */
	@NotBlank
	@JsonProperty("publisher")
	private String publisher;

	/**
	 * URL describing how resource can be licensed. Could be Creative Commons license link or link to other specific open or proprietary license. Model Primitive Datatype = AnyURI.
	 */
	@JsonProperty("useRightsURL")
	private String useRightsURL;

	/**
	 * Time that the resource takes to consume. Use the [ISO 8601] format for a duration. Model Primitive Datatype = Duration.
	 */
	@JsonProperty("timeRequired")
	private String timeRequired;

	/**
	 * A valid MIME type format for the resource e.g. text, HTML, PDF, MPEG, MP3, etc. See https://www.iana.org/assignments/media-types/media-types.xhtml. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("technicalFormat")
	private MediaType technicalFormat;

	/**
	 * For whom the resource is intended.
	 */
	@JsonProperty("educationalAudience")
	private List<Object> educationalAudience;

	/**
	 * Which (if any) accessibility API is supported by the resource.
	 */
	@JsonProperty("accessibilityAPI")
	private List<Object> accessibilityAPI;

	/**
	 * How the resource can be controlled by the user, which includes full keyboard controllability, mouse controllability, and voice controllability.
	 */
	@JsonProperty("accessibilityInputMethods")
	private List<Object> accessibilityInputMethods;

	/**
	 * These include alternatives and listed enhancements for the resource. These can be transformation features, navigation features, control features or augmentation features. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("accessibilityFeatures")
	private List<String> accessibilityFeatures;

	/**
	 * The set of accessibility hazards which are encountered when using this resource.
	 */
	@JsonProperty("accessibilityHazards")
	private List<Object> accessibilityHazards;

	/**
	 * The human sensory perceptual system or cognitive faculty through which a person may process or perceive information.
	 */
	@JsonProperty("accessMode")
	private List<Object> accessMode;

	/**
	 * Date the resource was published by the publisher. The 'date' using the [ISO 8601] format. Model Primitive Datatype = Date.
	 */
	@JsonProperty("publishDate")
	private LocalDate publishDate;

	/**
	 * A rating of the quality of the resource determined by the Search Provider. Often derived from crowdsource ratings.
	 */
	@JsonProperty("rating")
	private RatingEnum rating;

	/**
	 * This is a floating point value based on relevance to the specific search. Higher relevance has a higher number. Model Primitive Datatype = Float.
	 */
	@JsonProperty("relevance")
	private Float relevance;

	@JsonProperty("schoolType")
	private List<SchoolType> schoolType;

	//Extended standard properties
	/**
	 * The id of resource. Model Primitive Datatype = NormalizedString.
	 */
	@JsonProperty("id")
	private String id;
	/**
	 * Table of content - page from. Model Primitive Datatype = NormalizedString.
	 */
	private String pageFrom;
	/**
	 * Table of content - page to. Model Primitive Datatype = NormalizedString.
	 */
	private String pageTo;

	/**
	 *
	 */
	@RequiredArgsConstructor
	public enum RatingEnum {
		_1("1"),

		_2("2"),

		_3("3"),

		_4("4"),

		_5("5");

		private final String value;

		@Override
		@JsonValue
		public String toString() {
			return value;
		}

		@JsonCreator
		public static RatingEnum fromValue(String text) {
			for (RatingEnum b : RatingEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}
}

