package karate.runner;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;

import io.qameta.allure.karate.AllureKarate;
import karate.util.JsonHandler;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import karate.util.Cucumber;
import karate.util.services.UploadQMetry;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class TestRunner {

    ZonedDateTime nowStart = ZonedDateTime.now(ZoneId.of("America/Lima"));
    LocalDate fechaActual= LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter formatter2= DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm", Locale.ENGLISH);
    String startDate =nowStart.format(formatter2);

    @Test
    void testAllParallel() {
        String[] tags = {"@AgregarNuevaMascota", "@ConsultarMascota", "@ActualizarMascota", "@EliminarMascota"};

        Results results = Runner
                .path("classpath:resources/features/retoapis")
                .tags("@ConsultarMascota")
                .relativeTo(getClass())
                .hook(new AllureKarate())
                .outputCucumberJson(true)
                .parallel(5);

        cucumberReport(results.getReportDir());


    }
    public void cucumberReport(String path) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(path), new String[] {"json"}, true);
        List<String> jsonPaths = new ArrayList<>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        Configuration config = new Configuration(new File("target"), "cucumber-html-reports");
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
        JsonHandler.cucumberJSON(jsonPaths);
    }
}
