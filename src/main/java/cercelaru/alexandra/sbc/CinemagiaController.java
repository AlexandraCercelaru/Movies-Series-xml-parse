package cercelaru.alexandra.sbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cinemagia")
@RequiredArgsConstructor
public class CinemagiaController {
    private final CinemagiaService cinemagiaService;

    @GetMapping
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("home");
        mv.getModel().put("filme", cinemagiaService.getAll());
        return mv;
    }

    @GetMapping("/{title}")
    public ModelAndView byTitle(@PathVariable String title) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("by-title");
        mv.getModel().put("film", cinemagiaService.getOneByTitle(title));
        return mv;
    }

    @GetMapping("/by-year")
    public ModelAndView byYear(@RequestParam(required = false) Integer beg, @RequestParam(required = false) Integer end) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("by-year");
        mv.getModel().put("filme", cinemagiaService.getAllByYear(beg, end));
        return mv;
    }

    @GetMapping("/{title}/family/yes")
    public ModelAndView byTitleAndFamilyYes(@PathVariable String title) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("by-title-and-family-yes");
        mv.getModel().put("film", cinemagiaService.getAndApply(title));
        return mv;
    }

    @GetMapping("/{title}/platform")
    public ModelAndView platformByTitle(@PathVariable String title) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("platform-by-title");
        mv.getModel().put("film", cinemagiaService.getAndApply(title));
        return mv;
    }

    @GetMapping("/{title}/score")
    public ModelAndView scoreByTitle(@PathVariable String title) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("score-by-title");
        mv.getModel().put("film", cinemagiaService.getAndApply(title));
        return mv;
    }

    @GetMapping("/{title}/popular")
    public ModelAndView popularByTitle(@PathVariable String title) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("popular-by-title");
        mv.getModel().put("film", cinemagiaService.getAndApply(title));
        return mv;
    }
}
