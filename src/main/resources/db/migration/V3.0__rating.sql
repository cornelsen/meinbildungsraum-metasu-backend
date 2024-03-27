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

CREATE SEQUENCE s_ratings;
CREATE TABLE ratings
(
    id         bigint       not null,
    item_id    varchar(32)  not null,
    rating     smallint     not null,
    text       text,
    user_email varchar(255) not null,
    created_at timestamp    NOT NULL default current_timestamp,
    CONSTRAINT pk_ratings PRIMARY KEY (id)
);
CREATE INDEX idx_rating_item_id ON ratings (item_id);
CREATE UNIQUE INDEX rating_item_user_uniq ON ratings (item_id, user_email);