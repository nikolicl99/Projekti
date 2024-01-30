    package com.asss.pj.util;

    import com.asss.pj.entity.Vinarija;
    import com.asss.pj.service.VinarijaService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;

    import java.io.File;
    import java.util.List;

    @Component
    public class LoggerHelper {

        @Autowired
        VinarijaService vinarijaService;
        private static String fileName = "Logger.txt";
        private static File file = new File(getFileName());
        private static int minBrVina = 3;
        private static String greskaMsg = "Nema dovoljno instanci, br inst: ";

        public static void proveraCrveno(List<Vinarija> vinarija) {
            int br = 0;
            for (Vinarija vinarija1 : vinarija) {
                if (vinarija1.getVrstaVina().toString().equals(VrstaVina.Crveno.toString())) {
                    br++;
                }
            }
            if (br < getMinBrVina()) {
                System.out.println(greskaMsg + br);
                System.exit(0);
            }
        }

        public static void proveraBelo(List<Vinarija> vinarija) {
            int br = 0;
            for (Vinarija vinarija1 : vinarija) {
                if (vinarija1.getVrstaVina().toString().equals(VrstaVina.Belo.toString())) {
                    br++;
                }
            }
            if (br < getMinBrVina()) {
                System.out.println(greskaMsg + br);
                System.exit(0);
            }
        }

        public static void proveraRoze(List<Vinarija> vinarija) {
            int br = 0;
            for (Vinarija vinarija1 : vinarija) {
                if (vinarija1.getVrstaVina().toString().equals(VrstaVina.Roze.toString())) {
                    br++;
                }
            }
            if (br < getMinBrVina()) {
                System.out.println(greskaMsg + br);
                System.exit(0);
            }
        }

        public static String getFileName() {
            return fileName;
        }

        public File getFile() {
            return file;
        }

        public static int getMinBrVina() {
            return minBrVina;
        }

        public static String getGreskaMsg() {
            return greskaMsg;
        }
    }
