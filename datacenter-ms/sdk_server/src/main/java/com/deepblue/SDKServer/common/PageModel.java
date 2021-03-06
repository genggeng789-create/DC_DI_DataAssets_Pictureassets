package com.deepblue.SDKServer.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageModel<T> {

    private T data;
    private int pageIndex;
    private int pageSize;
}
