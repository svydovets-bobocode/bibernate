package com.bobocode.svydovets.bibernate.session.service.model;

import java.lang.reflect.Field;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchMetadata<T> {

    Class<T> entityType;
    Field field;
    Object columnValue;
}
