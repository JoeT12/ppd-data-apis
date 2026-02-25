package com.uol.comp3011.coursework1.dto.response;

public record AvgPriceByPostcodeResponse(String postcode, double averagePrice, Long numSales) {}
