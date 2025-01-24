package pl.edu.anstar.TattoBoook.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TattooSendingConfirmationWorker {

    private final ZeebeClient zeebeClient;

    public TattooSendingConfirmationWorker(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @ZeebeWorker(type = "tattooSending")
    public void handleTattooSending(final ActivatedJob job) {
        String processKey = String.valueOf(job.getProcessInstanceKey());
        System.out.println("Obsługuję zadanie 'tattooSending' dla procesu: " + processKey);

        System.out.println("Dziękujemy za dokonanie rezerwacji. Do zobaczenia!");

        try {
            zeebeClient.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            System.out.println("Zadanie 'tattooSending' zostało zakończone i przesłane do procesu.");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas kończenia zadania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
