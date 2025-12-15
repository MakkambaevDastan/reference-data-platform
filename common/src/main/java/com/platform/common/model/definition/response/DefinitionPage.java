package com.platform.common.model.definition.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefinitionPage<T> extends PageImpl<T> {

    @JsonIgnore
    private DefinitionResponse definition;

    @JsonCreator
    public DefinitionPage(Page<T> page, DefinitionResponse definition) {
        int size = page.getSize();
        super(page.getContent(), Pageable.ofSize(size > 0 ? size : 1).withPage(size), page.getTotalElements());
        this.definition = definition;
    }

    @JsonCreator
    public DefinitionPage(@JsonProperty("content") List<T> content,
                          @JsonProperty("page") int page,
                          @JsonProperty("size") int size,
                          @JsonProperty("totalElements") long total) {
        super(content, Pageable.ofSize(size > 0 ? size : 1).withPage(page), total);
    }

    @JsonCreator
    public DefinitionPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    @JsonAnyGetter
    public DefinitionResponse getDefinition() {
        return definition;
    }

    @JsonAnySetter
    public void setDefinition(DefinitionResponse definition) {
        this.definition = definition;
    }
}
