package com.vilya.farm.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DispatchEventData {

  @NotEmpty private String deviceid;
  @NotEmpty private String uri;

  @NotEmpty @Builder.Default private String payloadcontenttype = "application/json";

  @JsonProperty("payload_base64")
  @NotNull
  private byte[] payloadBase64;

  @Builder.Default
  private String notificationResponseTopic = "topic.devicegateway.message.downstream.notification";

  @JsonProperty("dispatch_notification")
  @Builder.Default
  private Boolean dispatchNotification = false;

  @JsonProperty("delivery_notification")
  @Builder.Default
  private Boolean deliveryNotification = false;

  @JsonProperty("expires_at")
  @Builder.Default
  private Long expiresAt = Long.MAX_VALUE;

  @JsonProperty("occured_at")
  @NotEmpty
  @Builder.Default
  private Long occuredAt = Instant.now().toEpochMilli();

  // this field is never serialized, only used at runtime
  @JsonIgnore private String node;
}
