package com.sayweee.spock.mockfree.tests;

public final class Person {

    private String firstName = "Weee";
    private String lastName = "Weee";
    private final Address address = new Address("Fremont Blvd");

    private Person() {
    }

    protected final String getFirstName() {
        return firstName;
    }

    private String getLastName() {
        return lastName;
    }

    private final Address getAddress() {
        return address;
    }

    private final static class Address {
        private final String street;

        private Address(final String street) {
            this.street = street;
        }

        public String getStreet() {
            return street;
        }
    }

}
