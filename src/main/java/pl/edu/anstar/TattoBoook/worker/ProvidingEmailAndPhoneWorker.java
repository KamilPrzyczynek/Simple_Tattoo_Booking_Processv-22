package pl.edu.anstar.TattoBoook.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class ProvidingEmailAndPhoneWorker {

    private final ZeebeClient zeebeClient;

    public ProvidingEmailAndPhoneWorker(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @ZeebeWorker(type = "ProvidingEmailandphone")
    public void handleProvidingEmailAndPhone(final ActivatedJob job) {
        Scanner scanner = new Scanner(System.in);

        // Pobierz klucz procesu dla celów debugowania
        String processKey = String.valueOf(job.getProcessInstanceKey());
        System.out.println("Obsługuję zadanie 'ProvidingEmailandphone' dla procesu: " + processKey);

        // Pobierz e-mail użytkownika
        System.out.print("Podaj swój e-mail: ");
        String tattoEmail = scanner.next();

        if (!tattoEmail.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            System.err.println("Niepoprawny format e-maila. Spróbuj ponownie.");
            return;
        }

        // Pobierz numer telefonu użytkownika
        System.out.print("Podaj swój numer telefonu: ");
        String tattoPhone = scanner.next();

        if (!tattoPhone.matches("^\\+?[0-9]{9,15}$")) {
            System.err.println("Niepoprawny format numeru telefonu. Spróbuj ponownie.");
            return;
        }

        // Przygotuj zmienne do przesłania do procesu
        Map<String, Object> variables = new HashMap<>();
        variables.put("email", tattoEmail);
        variables.put("phone", tattoPhone);

        System.out.println("Podano e-mail: " + tattoEmail);
        System.out.println("Podano numer telefonu: " + tattoPhone);

        // Przesyłanie zmiennych do procesu
        try {
            zeebeClient.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            System.out.println("Zadanie 'ProvidingEmailandphone' zostało zakończone i przesłane do procesu.");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas kończenia zadania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
