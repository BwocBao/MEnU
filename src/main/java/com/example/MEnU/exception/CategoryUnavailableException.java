package com.example.MEnU.exception;

public class CategoryUnavailableException extends RuntimeException {
  public CategoryUnavailableException(long id) {
    super("The Category is not found or EXPENSE type: " + id);
  }
}
