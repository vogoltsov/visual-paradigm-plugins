package com.github.vogoltsov.vp.plugins.confluence.client.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@Data
public abstract class DataPage<T> {

    private List<T> results;

    private int start;
    private int limit;
    private int size;
    private int totalSize;

}
