package com.vilya.farm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
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
public class ReceivedEvent implements Comparable<ReceivedEvent> {

  @Builder.Default private String type = "message.dispatch.upstream";

  @Builder.Default @NotNull private String specversion = "1.0";
  @NotNull private String source;
  @NotNull @Builder.Default private UUID id = UUID.randomUUID();
  @NotNull @Builder.Default private Long time = Instant.now().toEpochMilli();
  @NotNull @Builder.Default private String datacontenttype = "application/json";

  @NotNull private ReceivedEventData data;

  @Override
  public int compareTo(ReceivedEvent o) {
    return (int) (o.getTime() - this.getTime());
  }
}
