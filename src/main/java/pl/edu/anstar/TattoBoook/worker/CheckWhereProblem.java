package pl.edu.anstar.TattoBoook.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.stereotype.Component;

@Component
public class CheckWhereProblem {
    @JobWorker(type = "CheckWhereProblem")
    public void handleCheckWhereProblemTask(final JobClient client, final ActivatedJob job) {

        String processKey = String.valueOf(job.getProcessInstanceKey());

        System.out.println("Obsługuję zadanie 'CheckWhereProblem' dla procesu: " + processKey);

        System.out.println("Sprawdzanie, gdzie jest problem...");

        job.getVariablesAsMap().forEach((key, value) -> System.out.println("Zmienna procesu: " + key + " = " + value));


        client.newCompleteCommand(job.getKey())
                .variables("{\"problemChecked\": true}")
                .send()
                .join();

        System.out.println("Zadanie 'CheckWhereProblem' zakończone.");
    }
}
