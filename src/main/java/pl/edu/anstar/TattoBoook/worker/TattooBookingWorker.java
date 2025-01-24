package pl.edu.anstar.TattoBoook.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TattooBookingWorker {

    private final ZeebeClient zeebeClient;

    public TattooBookingWorker(@Qualifier("zeebeClientLifecycle") ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @ZeebeWorker(type = "tattooBooking")
    public void handleTattooBooking(final ActivatedJob job) {
        // Pobierz klucz procesu dla celów debugowania
        String processKey = String.valueOf(job.getProcessInstanceKey());
        System.out.println("Obsługuję zadanie 'tattooBooking' dla procesu: " + processKey);

        // Pobierz zmienne z procesu
        Map<String, Object> variables = job.getVariablesAsMap();
        String customerName = variables.getOrDefault("firstName", "") + " " + variables.getOrDefault("lastName", "");
        String date = (String) variables.getOrDefault("tattoDate", "Nie ustawiono daty");
        String time = (String) variables.getOrDefault("tattoTime", "Nie ustawiono godziny");
        String tattooDetails = "Tatuaż: " + variables.getOrDefault("tattoSelect", "Nie wybrano tatuażu");

        // Logowanie rezerwacji
        System.out.println("Rezerwacja wizyty dla klienta: " + customerName);
        System.out.println("Data: " + date);
        System.out.println("Godzina: " + time);
        System.out.println(tattooDetails);

        // Symulacja rezerwacji
        boolean bookingSuccess = true; // Symulujemy, że rezerwacja zakończyła się sukcesem

        // Przygotuj zmienne do przesłania do procesu
        variables.put("bookingSuccess", bookingSuccess);

        // Przesyłanie zmiennych do procesu
        try {
            zeebeClient.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            System.out.println("Zadanie 'tattooBooking' zostało zakończone i przesłane do procesu. Rezerwacja udana: " + bookingSuccess);
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas kończenia zadania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}