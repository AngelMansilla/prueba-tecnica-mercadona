package com.mercadona.external.dto;

import java.util.List;

public record ExternalStoreResponseDto(
    List<ExternalStoreDto> content,
    PageInfoDto page
) {
    
    public record PageInfoDto(
        int size,
        int number,
        int totalElements,
        int totalPages
    ) {}
}
