package com.example.MEnU.dto;

import com.example.MEnU.dto.response.FieldErrorResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // trường nào null thì nó ko hiện
public class ApiResponse<T> {

  private boolean success;
  private String message;
  private T data;
  private Object pagination;
  private List<FieldErrorResponse> errors; // dành cho 422 validation
  private LocalDateTime timestamp;

  public ApiResponse() {
    this.timestamp = LocalDateTime.now();
  }

  public ApiResponse(
      boolean success, String message, T data, List<FieldErrorResponse> errors, Object pagination) {
    this.pagination = pagination;
    this.success = success;
    this.message = message;
    this.data = data;
    this.errors = errors;
    this.timestamp = LocalDateTime.now();
  }

  // ----- Factory Methods -----

  // Success, có data ko phân trang
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data, null, null);
  }

  // success có data có phân trang
  public static <T> ApiResponse<T> success(String message, T data, Object pagination) {
    return new ApiResponse<>(true, message, data, null, pagination);
  }

  // Success, không data
  public static <T> ApiResponse<T> success(String message) {
    return new ApiResponse<>(true, message, null, null, null);
  }

  // Error chung (401, 403...)
  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null, null, null);
  }

  // Error dạng validation list (422)
  public static <T> ApiResponse<T> validationError(List<FieldErrorResponse> fieldErrors) {
    return new ApiResponse<>(false, "Validation failed", null, fieldErrors, null);
  }

  // ----- Getters & Setters -----

  public Object getPagination() {
    return pagination;
  }

  public void setPagination(Object pagination) {
    this.pagination = pagination;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public List<FieldErrorResponse> getErrors() {
    return errors;
  }

  public void setErrors(List<FieldErrorResponse> errors) {
    this.errors = errors;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
