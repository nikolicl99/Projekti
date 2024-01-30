        package com.asss.pj.entity;

        import com.asss.pj.util.VrstaAmbalaze;
        import com.asss.pj.util.VrstaVina;

        import javax.persistence.*;

        @Entity
        @Table(name = "vinarija")
        public class Vinarija {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            @Column(name = "ID")
            private int id;
            @Column(name = "Vrsta_Vina")
            @Enumerated(EnumType.STRING)
            private VrstaVina vrstaVina;

            @Column(name = "Vrsta_Ambalaze")
            @Enumerated(EnumType.STRING)
            private VrstaAmbalaze vrstaAmbalaze;

            @Column(name = "Zapremina_Ambalaze")
            private int zapremina;
            @Column(name = "Cena")
            private Integer cena;

            public Vinarija() {
            }

            public Vinarija(VrstaVina vrstaVina, VrstaAmbalaze vrstaAmbalaze, int zapremina, int cena) {
                this.vrstaVina = vrstaVina;
                this.vrstaAmbalaze = vrstaAmbalaze;
                this.zapremina = zapremina;
                this.cena = cena;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public VrstaVina getVrstaVina() {
                return vrstaVina;
            }

            public void setVrstaVina(VrstaVina vrstaVina) {
                this.vrstaVina = vrstaVina;
            }

            public VrstaAmbalaze getVrstaAmbalaze() {
                return vrstaAmbalaze;
            }

            public void setVrstaAmbalaze(VrstaAmbalaze vrstaAmbalaze) {
                this.vrstaAmbalaze = vrstaAmbalaze;
            }

            public int getZapremina() {
                return zapremina;
            }

            public void setZapremina(int zapremina) {
                this.zapremina = zapremina;
            }

            public int getCena() {
                return cena;
            }

            public void setCena(int cena) {
                this.cena = cena;
            }

            @Override
            public String toString() {
                return "Vinarija{" +
                        "id=" + id +
                        ", vrstaVina=" + vrstaVina +
                        ", vrstaAmbalaze=" + vrstaAmbalaze +
                        ", zapremina=" + zapremina +
                        ", cena=" + cena +
                        '}';
            }
        }
