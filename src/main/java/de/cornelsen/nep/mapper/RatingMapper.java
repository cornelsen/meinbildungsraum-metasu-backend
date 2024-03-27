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
import de.cornelsen.nep.model.dto.rating.RatingCreateReq;
import de.cornelsen.nep.model.dto.rating.RatingOwnRsp;
import de.cornelsen.nep.model.dto.rating.RatingRsp;
import de.cornelsen.nep.model.dto.search.SearchResultWrapper;
import de.cornelsen.nep.model.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapper extends CommonMapper {

	@Mapping(source = "publisherId", target = "publisherId", qualifiedByName = "decryptedValue")
	Rating toEntity(RatingCreateReq src);

	List<RatingRsp.Comment> toDto(List<Rating> src);

	@Mapping(source = "id", target = "id", qualifiedByName = "encryptedValue")
	RatingRsp.Comment toDto(Rating src);

	@Mapping(target = "publisher", source = "wrapper.publisher.name")
	@Mapping(target = "publisherId", source = "wrapper.publisher.id")
	RatingOwnRsp toDto(SearchResultWrapper wrapper, ResourceType src);

	@Mapping(source = "id", target = "id", qualifiedByName = "encryptedValue")
	RatingOwnRsp.RatingRsp toDtoOwn(Rating src);

	default List<RatingOwnRsp> toDto(SearchResultWrapper src) {
		return src.getResourceSetType().getResources().stream().map(i -> this.toDto(src, i)).toList();
	}

}
