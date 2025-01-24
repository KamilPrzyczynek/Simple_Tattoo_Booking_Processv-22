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
public class PaymentInputWorker {

    private final ZeebeClient zeebeClient;

    public PaymentInputWorker(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @ZeebeWorker(type = "PaymentInput")
    public void handlePaymentInput(final ActivatedJob job) {
        Scanner scanner = new Scanner(System.in);

        String processKey = String.valueOf(job.getProcessInstanceKey());
        System.out.println("Obsługuję zadanie 'PaymentInput' dla procesu: " + processKey);


        Map<String, Object> variables = job.getVariablesAsMap();
        double totalPrice = (double) variables.getOrDefault("totalPrice", 0.0);


        System.out.print("Podaj swoje imię: ");
        String firstName = scanner.nextLine();

        System.out.print("Podaj swoje nazwisko: ");
        String lastName = scanner.nextLine();

        System.out.print("Podaj numer karty (16 cyfr): ");
        String cardNumber = scanner.nextLine();

        if (!cardNumber.matches("\\d{16}")) {
            System.err.println("Niepoprawny numer karty. Musi zawierać dokładnie 16 cyfr.");
            return;
        }

        System.out.print("Podaj datę ważności karty (MM/YY): ");
        String cardExpiry = scanner.nextLine();

        if (!cardExpiry.matches("\\d{2}/\\d{2}")) {
            System.err.println("Niepoprawny format daty ważności karty. Wprowadź w formacie MM/YY.");
            return;
        }


        String cardType = "Unknown";
        if (cardNumber.startsWith("4")) {
            cardType = "Visa";
        } else if (cardNumber.startsWith("5")) {
            cardType = "MasterCard";
        } else if (cardNumber.startsWith("3")) {
            cardType = "American Express";
        }

        System.out.println("Typ karty: " + cardType);
        System.out.println("Imię i nazwisko: " + firstName + " " + lastName);
        System.out.println("Numer karty: " + cardNumber);
        System.out.println("Data ważności: " + cardExpiry);
        System.out.println("Całkowita kwota do zapłaty: " + totalPrice + " zł");


        variables.put("firstName", firstName);
        variables.put("lastName", lastName);
        variables.put("cardNumber", cardNumber);
        variables.put("cardExpiry", cardExpiry);
        variables.put("cardType", cardType);
        variables.put("totalPrice", totalPrice);


        try {
            zeebeClient.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            System.out.println("Zadanie 'PaymentInput' zostało zakończone i przesłane do procesu.");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas kończenia zadania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
