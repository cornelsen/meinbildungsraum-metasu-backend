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

package de.cornelsen.nep.mapper;

import de.cornelsen.nep.model.dto.lti.ResourceType;
import de.cornelsen.nep.model.dto.search.ResourceDetailsDto;
import de.cornelsen.nep.model.dto.search.ResourceTypeDto;
import de.cornelsen.nep.model.dto.search.SearchResultWrapper;
import org.mapstruct.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceTypeMapper extends CommonMapper {

	Pattern DIGIT_PATTERN = Pattern.compile("(\\d+)");

	List<ResourceTypeDto> toDto(List<ResourceType> src);

	@Mapping(target = "publisher", ignore = true)
	ResourceTypeDto toDto(ResourceType src);

	@Mapping(target = "publisher", source = "wrapper.publisher.name")
	@Mapping(target = "publisherId", source = "wrapper.publisher.id")
	ResourceTypeDto toDto(SearchResultWrapper wrapper, ResourceType src);

	default List<ResourceTypeDto> toDto(SearchResultWrapper src) {
		return src.getResourceSetType().getResources().stream().map(i -> this.toDto(src, i)).toList();
	}

	@AfterMapping
	default void mapTypicalAgeRange(@MappingTarget ResourceTypeDto dst) {

		if (dst.getTypicalAgeRange() == null) {
			return;
		}
		Matcher matcher = DIGIT_PATTERN.matcher(dst.getTypicalAgeRange());
		String typicalAgeRange = dst.getTypicalAgeRange();
		while (matcher.find()) {
			String group = matcher.group();
			String newValue = String.valueOf(Integer.parseInt(group) - 5);
			typicalAgeRange = typicalAgeRange.replaceAll(group, newValue);
		}
		dst.setTypicalAgeRange(typicalAgeRange);
	}

	ResourceTypeDto toDto(ResourceDetailsDto resourceDetailsDto);
}
