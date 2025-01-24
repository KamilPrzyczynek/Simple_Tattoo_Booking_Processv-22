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
public class SelectionOfTattoWorker {

    private final ZeebeClient zeebeClient;

    public SelectionOfTattoWorker(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @ZeebeWorker(type = "Selectionoftatto")
    public void handleSelectionOfTatto(final ActivatedJob job) {
        Scanner scanner = new Scanner(System.in);

        // Pobierz klucz procesu dla celów debugowania
        String processKey = String.valueOf(job.getProcessInstanceKey());
        System.out.println("Obsługuję zadanie 'Selectionoftatto' dla procesu: " + processKey);

        // Pobierz wybór tatuażu od użytkownika
        System.out.print("Wybierz numer tatuażu (1-10): ");
        int tattoSelect = scanner.nextInt();

        // Walidacja wyboru tatuażu
        if (tattoSelect < 1 || tattoSelect > 10) {
            System.err.println("Niepoprawny numer tatuażu. Wprowadź wartość od 1 do 10.");
            return;
        }

        // Pobierz godzinę i datę
        System.out.print("Podaj godzinę (HH:mm): ");
        String tattoTime = scanner.next();

        if (!tattoTime.matches("\\d{2}:\\d{2}")) {
            System.err.println("Niepoprawny format godziny. Wprowadź w formacie HH:mm.");
            return;
        }

        System.out.print("Podaj datę (YYYY-MM-DD): ");
        String tattoDate = scanner.next();

        if (!tattoDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.err.println("Niepoprawny format daty. Wprowadź w formacie YYYY-MM-DD.");
            return;
        }

        // Przygotuj zmienne do przesłania do procesu
        Map<String, Object> variables = new HashMap<>();
        variables.put("tattoSelect", tattoSelect);
        variables.put("tattoTime", tattoTime);
        variables.put("tattoDate", tattoDate);

        System.out.println("Wybrano tatuaż: " + tattoSelect);
        System.out.println("Godzina: " + tattoTime);
        System.out.println("Data: " + tattoDate);

        // Przesyłanie zmiennych do procesu
        try {
            zeebeClient.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            System.out.println("Zadanie 'Selectionoftatto' zostało zakończone i przesłane do procesu.");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas kończenia zadania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
