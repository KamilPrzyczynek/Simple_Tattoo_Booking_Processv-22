package pl.edu.anstar.TattoBoook.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TattoNoDateAvailabilityWorker {

    private final ZeebeClient zeebeClient;

    public TattoNoDateAvailabilityWorker(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @ZeebeWorker(type = "TattonodateAvailabilit")
    public void handleNoDateAvailability(final ActivatedJob job) {
        // Pobierz klucz procesu dla celów debugowania
        String processKey = String.valueOf(job.getProcessInstanceKey());
        System.out.println("Obsługuję zadanie 'TattonodateAvailabilit' dla procesu: " + processKey);

        // Ustaw informację, że nie ma dostępności
        boolean dateAvailability = false;

        System.out.println("Dostępność daty ustawiona na: " + dateAvailability + ". Nie ma wolnych miejsc.");

        // Przygotuj zmienne do przesłania do procesu
        Map<String, Object> variables = new HashMap<>();
        variables.put("dateAvailability", dateAvailability);

        // Przesyłanie zmiennych do procesu
        try {
            zeebeClient.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            System.out.println("Zadanie 'TattonodateAvailabilit' zostało zakończone i przesłane do procesu.");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas kończenia zadania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
