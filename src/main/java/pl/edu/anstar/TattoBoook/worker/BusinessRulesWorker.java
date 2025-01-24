package pl.edu.anstar.TattoBoook.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BusinessRulesWorker {

    private final ZeebeClient zeebeClient;

    public BusinessRulesWorker(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @ZeebeWorker(type = "businessRules")
    public void handleBusinessRules(final ActivatedJob job) {

        String processKey = String.valueOf(job.getProcessInstanceKey());
        System.out.println("Obsługuję zadanie 'Business Rules' dla procesu: " + processKey);


        String size = "medium";
        String complexity = "detailed";
        double basePrice = 300.0;

        if (size.equals("small")) {
            basePrice -= 50.0;
        } else if (size.equals("large")) {
            basePrice += 100.0;
        }

        if (complexity.equals("simple")) {
            basePrice -= 30.0;
        } else if (complexity.equals("detailed")) {
            basePrice += 50.0;
        }

        System.out.println("Zasady biznesowe: Rozmiar = " + size + ", Szczegółowość = " + complexity);
        System.out.println("Cena bazowa po zastosowaniu zasad: " + basePrice);

        Map<String, Object> variables = new HashMap<>();
        variables.put("size", size);
        variables.put("complexity", complexity);
        variables.put("basePrice", basePrice);

        try {
            zeebeClient.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            System.out.println("Zadanie 'Business Rules' zostało zakończone i przesłane do procesu.");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas kończenia zadania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
