package com.uol.comp3011.coursework1.dto.response;

public record AvgPriceByPropertyTypeResponse(
    String townCity, char propertyType, double averagePrice) {}
