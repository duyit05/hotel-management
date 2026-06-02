package com.project.hotelmanagement.repository.specification;

import lombok.Getter;

import static com.project.hotelmanagement.repository.specification.SearchOperation.EQUALITY;

@Getter
public class SpecSearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;
    private Boolean orPredicate;

    public SpecSearchCriteria (String key, String operation, String value, String prefix, String suffix){
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        if(searchOperation == EQUALITY){

        }
    }
}
