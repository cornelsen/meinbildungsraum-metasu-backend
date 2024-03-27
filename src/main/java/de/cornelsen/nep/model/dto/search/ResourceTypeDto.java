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

package de.cornelsen.nep.model.dto.search;

import de.cornelsen.nep.model.dto.lti.CCLTILinkType;
import de.cornelsen.nep.model.dto.lti.LearningObjectivesType;
import de.cornelsen.nep.model.dto.lti.ResourceType;
import de.cornelsen.nep.model.dto.lti.TextComplexityType;
import de.cornelsen.nep.model.dto.request.LRTEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class ResourceTypeDto {
	@Schema(description = "The name/title of resource. Model Primitive Datatype = NormalizedString.")
	private String name;

	@Schema(description = "A human readable description of the contents of the resource. Model Primitive Datatype = NormalizedString.")
	private String description;

	@Schema(description = "The subject(s) of the resource. May have multiple subjects tagged. Model Primitive Datatype = NormalizedString.")
	private List<Subject> subject;

	@Schema(description = "How to access resource over Internet e.g. HTTP, FTP, etc. A resource must have either a URL or or a LTI Link. Model Primitive Datatype = AnyURI.")
	private String url;

	private CCLTILinkType ltiLink;

	private List<LRTEnum> learningResourceType = new ArrayList<>();

	@Schema(description = "The languages used in the resource. International two digit code for language e.g. 'en' for English. Use the [RFC 3066] annotation. Model Primitive Datatype = Language.")
	private List<String> language;

	@Schema(description = "Link to a thumbnail representing resource. Model Primitive Datatype = AnyURI.")
	private String thumbnailUrl;

	@Schema(description = "Age of the typical intended user. This is described as EITHER the minimum-maximum age range (the format is '11-12', '5-7', etc. with ONLY integers permitted) OR the age as a single integer e.g. '9'. Model Primitive Datatype = NormalizedString.")
	private String typicalAgeRange;

	@Schema(description = "A number indicating text complexity based on number of established measures.")
	private List<TextComplexityType> textComplexity;

	@Schema(description = "The set of learning objectives addressed by the resource.")
	private List<LearningObjectivesType> learningObjectives;

	@Schema(description = "Author or creator of the resource. Model Primitive Datatype = NormalizedString.")
	private List<String> author;

	@Schema(description = "Owner of the rights to the resource or who made it available (company or person). Model Primitive Datatype = NormalizedString.")
	private String publisher;

	@Schema(description = "URL describing how resource can be licensed. Could be Creative Commons license link or link to other specific open or proprietary license. Model Primitive Datatype = AnyURI.")
	private String useRightsURL;

	@Schema(description = "Time that the resource takes to consume. Use the [ISO 8601] format for a duration. Model Primitive Datatype = Duration.")
	private String timeRequired;

	@Schema(description = "A valid MIME type format for the resource e.g. text, HTML, PDF, MPEG, MP3, etc. See https://www.iana.org/assignments/media-types/media-types.xhtml. Model Primitive Datatype = NormalizedString.")
	private MediaType technicalFormat;

	@Schema(description = "For whom the resource is intended.")
	private List<Object> educationalAudience;

	@Schema(description = "Which (if any) accessibility API is supported by the resource.")
	private List<Object> accessibilityAPI;

	@Schema(description = "How the resource can be controlled by the user, which includes full keyboard controllability, mouse controllability, and voice controllability.")
	private List<Object> accessibilityInputMethods;

	@Schema(description = "These include alternatives and listed enhancements for the resource. These can be transformation features, navigation features, control features or augmentation features. Model Primitive Datatype = NormalizedString.")
	private List<String> accessibilityFeatures;

	@Schema(description = "The set of accessibility hazards which are encountered when using this resource.")
	private List<Object> accessibilityHazards;

	@Schema(description = "The human sensory perceptual system or cognitive faculty through which a person may process or perceive information.")
	private List<Object> accessMode;

	@Schema(description = "Date the resource was published by the publisher. The 'date' using the [ISO 8601] format. Model Primitive Datatype = Date.")
	private LocalDate publishDate;

	@Schema(description = "A rating of the quality of the resource determined by the Search Provider. Often derived from crowdsource ratings.")
	private ResourceType.RatingEnum rating;

	@Schema(description = "This is a floating point value based on relevance to the specific search. Higher relevance has a higher number. Model Primitive Datatype = Float.")
	private Float relevance;

	//Extended properties
	@Schema(description = "Resource ID. Model Primitive Datatype = NormalizedString.")
	private String id;

	@Schema(description = "Table of Content - page from. Model Primitive Datatype = NormalizedString.")
	private String pageFrom;

	@Schema(description = "Table of Content - page to. Model Primitive Datatype = NormalizedString.")
	private String pageTo;

	@Schema(description = "Owner of the rights to the resource or who made it available (company or person). Model Primitive Datatype = NormalizedString.")
	private String publisherId;

	public List<Subject> getSubject() {
		return subject.stream().filter(Objects::nonNull).toList();
	}

	public List<LRTEnum> getLearningResourceType() {
		return learningResourceType.stream().filter(Objects::nonNull).toList();
	}
}
