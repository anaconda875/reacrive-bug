package com.vilya.farm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
//import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DispatchEvent implements Comparable<DispatchEvent> {

  @Builder.Default private String type = "message.dispatch.downstream";

  @Builder.Default @NotNull private String specversion = "1.0";
  @NotNull private String source;
  @NotNull @Builder.Default private UUID id = UUID.randomUUID();
  @NotNull @Builder.Default private Long time = Instant.now().toEpochMilli();
  @NotNull @Builder.Default private String datacontenttype = "application/json";

  @NotNull private DispatchEventData data;

  @Override
  public int compareTo(DispatchEvent o) {
    return (int) (o.getTime() - this.getTime());
  }
}
