# ImageProcessing

Aplikacja okienkowa do podstawowej obróbki obrazów w języku Java z wykorzystaniem JavaFX. Projekt został stworzony jako realizacja Zadania 1 na Politechnice Wrocławskiej.

## Zrealizowane funkcje

- **Aplikacja okienkowa oparta o JavaFX**
- **Wyświetlanie tekstu powitalnego na ekranie głównym**  
  Po uruchomieniu użytkownik widzi powitanie "Witaj poszukiwaczu!".
- **Logo Politechniki Wrocławskiej na górze aplikacji**  
  Logo (`logo_pwr.png`) jest wyświetlane na górze, a w przypadku braku pliku aplikacja obsługuje ten przypadek bez błędu.
- **Lista rozwijana z wyborem operacji**  
  Dostępne operacje: `Negatyw`, `Progowanie`, `Konturowanie`, `Obrót`. (nie zmieniają one obrazu)
- **Przycisk "Wykonaj" z walidacją wyboru operacji**  
  Jeśli użytkownik nie wybierze operacji, pojawia się komunikat toast: "Nie wybrano operacji do wykonania".
- **Wczytywanie obrazów JPG z walidacją typu pliku**  

- **Komunikaty typu toast przy różnych zdarzeniach**  
  Informacje zwrotne dla użytkownika pojawiają się m.in. przy wczytaniu pliku, błędach itp.
- **Podgląd obrazu oryginalnego i przetworzonego**  

- **Przycisk "Zapisz" aktywowany po wczytaniu obrazu**

- **Okno modalne do zapisu obrazu z walidacją długości nazwy i obsługą przypadków błędnych:**
  - Zbyt krótka nazwa (komunikat: "Wpisz co najmniej 3 znaki")
  - Plik już istnieje
- **Zapis do lokalizacji:** `~/Images`
