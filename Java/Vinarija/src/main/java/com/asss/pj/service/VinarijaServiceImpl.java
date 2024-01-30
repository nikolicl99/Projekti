    package com.asss.pj.service;

    import com.asss.pj.dao.VinarijaRepository;
    import com.asss.pj.entity.Vinarija;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Sort;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    public class VinarijaServiceImpl implements VinarijaService {

        @Autowired
        private VinarijaRepository vinarijaRepository;

        @Override
        public Vinarija findById(int id) {
            return vinarijaRepository.findById(id).orElse(null);
        }

        @Override
        public List<Vinarija> findAll() {
            return vinarijaRepository.findAll();
        }

        @Override
        public Vinarija save(Vinarija vinarija) {
            return vinarijaRepository.save(vinarija);
        }

        @Override
        public String deleteById(int id) {
            Vinarija vinarija = vinarijaRepository.findById(id).orElse(null);
            if (vinarija != null) {
                vinarijaRepository.deleteById(id);
                return "Wine (" + id + ") has been deleted!";
            } else {
                return "Wine (" + id + ") not found";
            }
        }

        @Override
        public List<Vinarija> findAll(Sort cena) {
            return vinarijaRepository.findAll(Sort.by(Sort.Direction.DESC, "Cena"));
        }
    }
