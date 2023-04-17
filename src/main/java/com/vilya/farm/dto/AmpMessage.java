package com.vilya.farm.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmpMessage {

  private List<AmpPayload> p;

  public AmpMessage(AmpPayload ampPayload) {
    this.p = Collections.singletonList(ampPayload);
  }

  @JsonIgnore
  public AmpPayload getAmpPayload() {
    return p.get(0);
  }
}
