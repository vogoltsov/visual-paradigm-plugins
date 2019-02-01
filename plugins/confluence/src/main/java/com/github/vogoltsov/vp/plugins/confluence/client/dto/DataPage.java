package com.github.vogoltsov.vp.plugins.confluence.client.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataPage<T> {

    public static <T> DataPage<T> empty() {
        return new DataPage<>(Collections.emptyList(), 0, Integer.MAX_VALUE, 0, 0);
    }

    public static <T> DataPage<T> of(List<T> results) {
        return new DataPage<>(new ArrayList<>(results), 0, results.size(), results.size(), results.size());
    }


    private List<T> results;

    private int start;
    private int limit;
    private int size;
    private int totalSize;


    public List<T> getResults() {
        return Collections.unmodifiableList(results);
    }

    public <V> DataPage<V> map(Function<T, V> mapper) {
        return new DataPage<>(results.stream().map(mapper).collect(Collectors.toList()), start, limit, size, totalSize);
    }

}
