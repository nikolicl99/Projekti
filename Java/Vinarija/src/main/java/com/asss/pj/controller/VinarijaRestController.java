    package com.asss.pj.controller;

    import com.asss.pj.entity.Vinarija;
    import com.asss.pj.service.VinarijaService;
    import com.asss.pj.util.LoggerHelper;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Sort;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    public class VinarijaRestController {

        @Autowired
        private VinarijaService vinarijaService;

        @GetMapping("/vinarija")
        List<Vinarija> findAll() {
            return vinarijaService.findAll();
        }

        @GetMapping("/vinarija/cena")
        List<Vinarija> findAllPrice() {
            return vinarijaService.findAll(Sort.by("Cena"));
        }

        @PostMapping("/vinarija")
        Vinarija addWine(@RequestBody Vinarija vinarija) {
            return vinarijaService.save(vinarija);
        }

        @PutMapping("/vinarija")
        Vinarija editWine(@RequestBody Vinarija vinarija) {
            return vinarijaService.save(vinarija);
        }

        @DeleteMapping("/vinarija/{id}")
        String removeWine(@PathVariable int id) {
            Vinarija vinarija = vinarijaService.findById(id);
            if (vinarija != null) {
                vinarijaService.deleteById(id);
                LoggerHelper.proveraCrveno(vinarijaService.findAll());
                LoggerHelper.proveraBelo(vinarijaService.findAll());
                LoggerHelper.proveraRoze(vinarijaService.findAll());
                return "Knjiga (" + id + ") je obrisana";
            } else return "Knjiga (" + id + ") nije pronadjena";
        }
    }
