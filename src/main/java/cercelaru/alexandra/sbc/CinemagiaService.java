package cercelaru.alexandra.sbc;

import cercelaru.alexandra.sbc.cinemagia.Cinemagia;
import cercelaru.alexandra.sbc.cinemagia.Film;
import cercelaru.alexandra.sbc.cinemagia.Regula;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CinemagiaService {
    private Cinemagia cinemagia;
    private Map<String, Film> filme;

    public CinemagiaService() {
        try (var is = this.getClass().getResourceAsStream("/cinemagia.xml")) {    // Deschide fișierul ca
            var xmlMapper = new XmlMapper();                                            // InputStream și citește-l
            this.cinemagia = xmlMapper.readValue(is, Cinemagia.class);                  // într-o clasă de tip Cinemagia

            // Transformă lista de filme citită într-o mapă
            // care are ca și cheie titlul filmului și ca și
            // valoare, obiectul de tip Film citit din fișier
            this.filme = cinemagia
                    .getFilme()
                    .stream()
                    .collect(Collectors.toMap(Film::getTitlu, film -> film));

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void aplicaReguli(Film film) {
        this.cinemagia
                .getReguli()
                .forEach(regula -> {                        // Parcurge fiecare regulă și
                    if (verificaClauze(film, regula)) {     // verifică dacă trebuie aplicat
                        aplicaThen(film, regula);           // efectul (dacă da, aplica-l)
                    }
                });
    }

    private boolean verificaClauze(Film film, Regula regula) {
        var shouldApplyEffect = true;
        for (var clauza : regula.getClauze()) { // Parcurge fiecare clauză din regulă
            try {
                var val = clauza.getVal();
                var rel = clauza.getRel();

                var what = clauza.getWhat();

                // Construiește numele metodei de get pentru câmpul ce trebuie verificat
                // Ex: what = "gen", deci numele metodei de get va fi "get" + "Gen" = "getGen"
                var getter = "get" + what.substring(0, 1).toUpperCase() + what.substring(1);
                String get = (String) film.getClass().getMethod(getter).invoke(film);   // Invocă getter-ul

                shouldApplyEffect = switch (rel) {
                    case "egal" -> get.equals(val);
                    case "mare" -> {
                        int getAsNum = Integer.parseInt(get);
                        int valAsNum = Integer.parseInt(val);
                        yield getAsNum > valAsNum;
                    }
                    case "mic" -> {
                        int getAsNum = Integer.parseInt(get);
                        int valAsNum = Integer.parseInt(val);
                        yield getAsNum < valAsNum;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + rel);
                };

                if (!shouldApplyEffect) {
                    break;
                }
            } catch (NoSuchMethodException
                     | IllegalAccessException
                     | InvocationTargetException e) {
                log.error(e.getMessage());
            }
        }
        return shouldApplyEffect;
    }

    private void aplicaThen(Film film, Regula regula) {
        try {
            var then = regula.getThen();
            var what = then.getWhat();

            // Construiește numele metodei de set pentru câmpul ce trebuie modificat
            // Ex: what = "familie", deci numele metodei de set va fi "set" + "Familie" = "setFamilie"
            var setter = "set" + what.substring(0, 1).toUpperCase() + what.substring(1);
            film.getClass().getMethod(setter, String.class).invoke(film, then.getVal());  // Invocă setter-ul

        } catch (NoSuchMethodException
                 | IllegalAccessException
                 | InvocationTargetException e) {
            log.error(e.getMessage());
        }
    }

    public List<Film> getAll() {
        return filme
                .values()
                .stream()
                .toList();
    }

    public Film getOneByTitle(String title) {
        return filme.get(title);
    }

    public List<Film> getAllByYear(Integer beg, Integer end) {
        return filme
                .values()
                .stream()
                .filter(film -> {
                    var an = Integer.parseInt(film.getAn());
                    if (beg == null && end == null) {
                        return true;
                    }
                    if (beg == null) {
                        return an <= end;
                    }
                    if (end == null) {
                        return beg <= an;
                    }
                    return beg <= an && an <= end;
                })
                .toList();
    }

    public Film getAndApply(String title) {
        var film = filme.get(title);
        if (film != null) {
            aplicaReguli(film);
            return film;
        }
        return null;
    }
}
