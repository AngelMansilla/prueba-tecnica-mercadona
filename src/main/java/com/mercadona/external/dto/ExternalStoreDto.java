package com.mercadona.external.dto;

public record ExternalStoreDto(
    Long id,
    String description,
    String address,
    String city
) {
}
