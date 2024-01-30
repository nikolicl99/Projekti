    package com.asss.pj.util;

    import com.asss.pj.service.VinarijaService;
    import org.aspectj.lang.annotation.AfterReturning;
    import org.aspectj.lang.annotation.Aspect;
    import org.aspectj.lang.annotation.Pointcut;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.EnableAspectJAutoProxy;
    import org.springframework.stereotype.Component;

    import java.io.FileWriter;
    import java.io.IOException;
    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.time.temporal.ChronoUnit;

    @EnableAspectJAutoProxy
    @Aspect
    @Component
    public class Logger {

        @Autowired
        private VinarijaService vinarijaService;

        @Pointcut("execution(* com.asss.pj.controller.VinarijaRestController.findAll(..))")
        public void getMethod() {

        }

        @Pointcut("execution(* com.asss.pj.controller.VinarijaRestController.editWine(..))")
        public void putMethod() {

        }

        @Pointcut("execution(* com.asss.pj.controller.VinarijaRestController.addWine(..))")
        public void postMethod() {

        }

        @Pointcut("execution(* com.asss.pj.controller.VinarijaRestController.removeWine(..))")
        public void deleteMethod() {

        }

        @Pointcut("execution(* com.asss.pj.controller.VinarijaRestController.findAllPrice(..))")
        public void getPrice() {
        }

        @AfterReturning("getMethod()")
        public void gMethod() {
            try (FileWriter fileWriter = new FileWriter(LoggerHelper.getFileName(), true)) {
                fileWriter.write("[" + LocalDate.now() + "]" + " [" + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + "]" + "---Log: GET metoda koriscena" + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @AfterReturning("getPrice()")
        public void gpMethod() {
            try (FileWriter fileWriter = new FileWriter(LoggerHelper.getFileName(), true)) {
                fileWriter.write("[" + LocalDate.now() + "]" + " [" + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + "]" + "---Log: GET po ceni sortirani" + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @AfterReturning("putMethod()")
        public void puMethod() {
            try (FileWriter fileWriter = new FileWriter(LoggerHelper.getFileName(), true)) {
                fileWriter.write("[" + LocalDate.now() + "]" + " [" + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + "]" + "---Log: PUT metoda koriscena" + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @AfterReturning("deleteMethod()")
        public void delMethod() {
            try (FileWriter fileWriter = new FileWriter(LoggerHelper.getFileName(), true)) {
                fileWriter.write("[" + LocalDate.now() + "]" + " [" + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + "]" + "---Log: DELETE metoda koriscena" + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @AfterReturning("postMethod()")
        public void poMethod() {
            try (FileWriter fileWriter = new FileWriter(LoggerHelper.getFileName(), true)) {
                fileWriter.write("[" + LocalDate.now() + "]" + " [" + LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + "]" + "---Log: POST metoda koriscena" + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }