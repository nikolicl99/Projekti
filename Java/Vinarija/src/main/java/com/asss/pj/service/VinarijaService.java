    package com.asss.pj.service;

    import com.asss.pj.entity.Vinarija;
    import org.springframework.data.domain.Sort;

    import java.util.List;

    public interface VinarijaService {
        List<Vinarija> findAll();

        Vinarija save(Vinarija vinarija);

        String deleteById(int id);

        Vinarija findById(int id);

        List<Vinarija> findAll(Sort cena);
    }

