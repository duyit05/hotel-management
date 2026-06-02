package com.project.hotelmanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageableData {
    int pageNumber;
    int pageSize;
    int totalPage;
    long totalRecord;

    public PageableData setPageNumber (int pageNumber){
        this.pageNumber = pageNumber + 1;
        return this;
    }

    public PageableData setPageSize (int pageSize){
        this.pageSize = pageSize;
        return this;
    }

    public PageableData setToltalPage (int totalPage){
        this.totalPage = totalPage;
        return this;
    }

    public PageableData setTotalRecord ( long totalRecord){
        this.totalRecord = totalRecord;
        return this;
    }
}
