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

package de.cornelsen.nep.model.entity;

import de.cornelsen.nep.model.entity.enums.RelationshipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@ToString(of = {"oidcId", "relationshipId"})
public class User {

	@Id
	private String oidcId;
	private String relationshipId;
	@Enumerated(EnumType.STRING)
	private RelationshipStatus status = RelationshipStatus.UNAVAILABLE;
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;
	private String enmeshedAddress;
	private LocalDateTime lastLoginAt;
	private LocalDateTime lastLoginAtHavingDw;
	private LocalDateTime lastLoginAtHavingDwRelation;

	public User(String oidcId) {
		this.oidcId = oidcId;
		this.createdAt = LocalDateTime.now();
	}
}
