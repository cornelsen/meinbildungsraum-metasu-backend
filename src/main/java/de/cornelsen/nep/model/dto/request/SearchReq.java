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

package de.cornelsen.nep.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.cornelsen.nep.model.dto.lti.LearningObjectivesType;
import de.cornelsen.nep.model.dto.lti.ResourceType;
import de.cornelsen.nep.model.dto.search.*;
import de.cornelsen.nep.serializer.AsOrSerializer;
import de.cornelsen.nep.serializer.CollectionSerializer;
import de.cornelsen.nep.serializer.SearchFieldSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchReq implements Serializable {

	//This is a special term that requires the associated filter term to be applied to the 'name', 'subject' and 'description' values.
	@JsonSerialize(using = SearchFieldSerializer.class)
	private String search;

	@AsOrSerializer
	@JsonSerialize(using = CollectionSerializer.class)
	private Set<Subject> subject;

	@AsOrSerializer
	@JsonSerialize(using = CollectionSerializer.class)
	private Set<String> id;
	private LRTEnum learningResourceType;
	private String language;
	private SchoolType schoolType;
	private StudyYear typicalAgeRange;
	private String textComplexity;

	@JsonUnwrapped
	private LearningObject learningObjectives;
	private String author;
	private String publisher;
	private String timeRequired;

	@AsOrSerializer
	@JsonSerialize(using = CollectionSerializer.class)
	private Set<MediaType> technicalFormat;
	private String educationalAudience;
	private String accessibilityAPI;
	private String accessibilityInputMethods;
	private String accessMode;
	private LocalDate publishDate;
	private ResourceType.RatingEnum rating;

	@AsOrSerializer
	@JsonSerialize(using = CollectionSerializer.class)
	private Set<FederalState> federalStates;

	private SchoolBook schoolBook;
	private Grade grade;
	private Differentiation differentiation;

	@JsonIgnore
	private ProviderType providerType = ProviderType.DEFAULT;

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class LearningObject implements Serializable {

		@JsonProperty("learningObject.alignmentType")
		private LearningObjectivesType.AlignmentTypeEnum alignmentType;

		@JsonProperty("learningObject.educationalFramework")
		private String educationalFramework;

		@JsonProperty("learningObject.targetDescription")
		private String targetDescription;
		@JsonProperty("learningObject.targetName")
		private String targetName;

		@JsonProperty("learningObject.targetURL")
		private String targetURL;

		@JsonProperty("learningObject.caseItemURI")
		private String caseItemURI;

		@JsonProperty("learningObject.caseItemGUID")
		private String caseItemGUID;
	}

	@Getter
	@RequiredArgsConstructor
	public enum SortBy {
		ALPHABETIC("name", "asc"),
		MOST_VIEWED("relevance", "desc"),
		NEWEST("publishDate", "desc"),
		OLDEST("publishDate", "asc");
		private final String sort;
		private final String orderBy;
	}

	static Function<Sort.Order, SortBy> sortOfPageable = sort -> Stream.of(SortBy.values())
		.filter(sortBy -> sortBy.sort.equalsIgnoreCase(sort.getProperty()) && sortBy.orderBy.equalsIgnoreCase(sort.getDirection().name()))
		.findFirst().orElseThrow(IllegalArgumentException::new);

	public static Optional<SearchReq.SortBy> getSorted(Pageable pageable) {
		return pageable.getSort().isSorted() ? Optional.of(sortOfPageable.apply(pageable.getSort().iterator().next())) : Optional.empty();
	}

	public String getSchoolType() {
		if (schoolType == null) {
			return null;
		}
		return ProviderType.DUF.equals(providerType) ? schoolType.getDufValue() : schoolType.getDefaultValue();
	}

	public String getLearningResourceType() {
		if (learningResourceType == null) {
			return null;
		}
		return ProviderType.DUF.equals(providerType) ? learningResourceType.getDufValue() : learningResourceType.getDefaultValue();
	}
}
