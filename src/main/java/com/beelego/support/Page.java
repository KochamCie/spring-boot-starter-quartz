package com.beelego.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : hama
 * @since : created in  2018/9/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> extends BaseSerialVersion {

    private List<T> content;    // current page elements
    private int totalElements;  // total elements
    private int pageNum;    // current page , usually it's a parameter pass by
    private int pageSize;   // size of page

    public int getTotalPages() {
        return this.pageSize == 0 ? 1 : (int) Math.ceil((double) this.totalElements / (double) this.pageSize);
    }

}
