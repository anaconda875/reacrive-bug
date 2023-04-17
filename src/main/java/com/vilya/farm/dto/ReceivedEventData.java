package com.vilya.farm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceivedEventData {

  @NotEmpty private String deviceid;
  @NotEmpty private String uri;
  @NotEmpty private String node;

  @NotEmpty @Builder.Default private String payloadcontenttype = "application/json";

  @NotNull
  @JsonProperty("payload_base64")
  private byte[] payloadBase64;

  private Boolean retain;

  private Boolean dup;

  @NotEmpty
  @JsonProperty("occured_at")
  @Builder.Default
  private Long occuredAt = Instant.now().toEpochMilli();
}
