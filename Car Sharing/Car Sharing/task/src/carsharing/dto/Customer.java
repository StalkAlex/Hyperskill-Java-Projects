package carsharing.dto;

public record Customer(
        int id,
        String name,
        Integer rentedCarId
) {}
