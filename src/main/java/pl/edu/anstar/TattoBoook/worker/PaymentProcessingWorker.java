package pl.edu.anstar.TattoBoook.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentProcessingWorker {

    private final ZeebeClient zeebeClient;

    public PaymentProcessingWorker(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @ZeebeWorker(type = "paymentProcessing")
    public void handlePaymentProcessing(final ActivatedJob job) {
        // Pobierz klucz procesu dla celów debugowania
        String processKey = String.valueOf(job.getProcessInstanceKey());
        System.out.println("Obsługuję zadanie 'paymentProcessing' dla procesu: " + processKey);

        // Na sztywno ustawiamy, że płatność została zatwierdzona
        boolean paymentApproved = true;

        System.out.println("Przetwarzanie płatności zakończone. Płatność zatwierdzona: " + paymentApproved);

        // Przygotuj zmienne do przesłania do procesu
        Map<String, Object> variables = new HashMap<>();
        variables.put("paymentApproved", paymentApproved);

        // Przesyłanie zmiennych do procesu
        try {
            zeebeClient.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            System.out.println("Zadanie 'paymentProcessing' zostało zakończone i przesłane do procesu.");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas kończenia zadania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
