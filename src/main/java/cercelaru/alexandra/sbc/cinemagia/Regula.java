package cercelaru.alexandra.sbc.cinemagia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Regula {
    @JsonProperty("if")
    private List<Clauza> clauze;

    private Then then;
}
