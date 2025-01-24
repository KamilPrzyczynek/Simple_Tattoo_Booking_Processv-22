package pl.edu.anstar.TattoBoook.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PriceCalculationWorker {

    private final ZeebeClient zeebeClient;

    public PriceCalculationWorker(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @ZeebeWorker(type = "priceCalculation")
    public void handlePriceCalculation(final ActivatedJob job) {
        String processKey = String.valueOf(job.getProcessInstanceKey());
        System.out.println("Obsługuję zadanie 'priceCalculation' dla procesu: " + processKey);


        Map<String, Object> variables = job.getVariablesAsMap();
        double basePrice = (double) variables.getOrDefault("basePrice", 300.0);
        String size = (String) variables.getOrDefault("size", "medium");
        String complexity = (String) variables.getOrDefault("complexity", "detailed");


        double costOfMaterials = 50.0;
        double hourlyRate = 100.0;
        double estimatedTime = 2.0;

        if (size.equals("large")) {
            estimatedTime += 2.0;
        } else if (size.equals("small")) {
            estimatedTime -= 1.0;
        }

        if (complexity.equals("detailed")) {
            estimatedTime += 1.0;
        } else if (complexity.equals("simple")) {
            estimatedTime -= 0.5;
        }

        double totalPrice = basePrice + costOfMaterials + (hourlyRate * estimatedTime);


        System.out.println("Rozmiar: " + size);
        System.out.println("Szczegółowość: " + complexity);
        System.out.println("Czas szacowany: " + estimatedTime + " godziny");
        System.out.println("Całkowita cena: " + totalPrice + " zł");

        variables.put("totalPrice", totalPrice);

        try {
            zeebeClient.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            System.out.println("Zadanie 'priceCalculation' zostało zakończone. Całkowita cena: " + totalPrice + " zł.");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas kończenia zadania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
