package com.project.hotelmanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PagingResponse<T> {
    List<T> contents = new ArrayList<>();
    PageableData paging;

    public PagingResponse<T> setContents (List<T> contents){
        this.contents = contents;
        return this;
    }

    public PagingResponse<T> setPaging (PageableData paging){
        this.paging = paging;
        return this;
    }
}
