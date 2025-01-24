package pl.edu.anstar.TattoBoook;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Scanner;

@EnableScheduling
@SpringBootApplication
@EnableZeebeClient
@Deployment(resources = "classpath*:/model/*.*")
public class TattoBookProcessApplication implements CommandLineRunner {

    private final ZeebeClient zeebeClient;

    public TattoBookProcessApplication(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(TattoBookProcessApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println();

        System.out.print("Dzień Dobry.Jesteś w systemie rezerwacji tatuażu . Naciśnij Enter, aby rozpocząć: ");
        String input = scanner.nextLine();

        if (input.isEmpty()) {
            try {
                ProcessInstanceEvent processInstance = zeebeClient
                        .newCreateInstanceCommand()
                        .bpmnProcessId("Simple_Tattoo_Booking_Process")
                        .latestVersion()
                        .send()
                        .join();

                System.out.println();
                System.out.println(processInstance.getProcessInstanceKey());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("Błąd Procesu");
        }
    }
}