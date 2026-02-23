package io.github.XanderGI.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "code", "sign"})
public record Currency(Long id, @JsonProperty("name") String fullName, String code, String sign) {
}