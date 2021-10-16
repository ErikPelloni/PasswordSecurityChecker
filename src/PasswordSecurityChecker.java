import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Date;

/**
 * La classe PasswordSecurityChecker contiene i metodi per verificare, in base 
 * a dei dati passati dall'utente, la sicurezza della password. Questa verifica
 * viene eseguita tentando di forzare la password tramite combinazioni e 
 * tramite il confronto con una lista di password più comuni.
 * 
 * @author Erik Pelloni
 * @version 1.0 (14.10.2021)
 */

public class PasswordSecurityChecker{
    private List<String> name;
    private int[] birth;
    private List<String> data;
    private char[] specials = { '!', '&', '?', '+', '%', '$', '-', '_', '@' };
    private String password;
    private List<String> commons;
    private int tries;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private long start;
    private long end;

    /**
     *Costruttore vuoto per poter istanziare un oggetto PasswordSecurityChecker
     */
    private PasswordSecurityChecker() {
        name = new ArrayList<>();
        data = new ArrayList<>();
        birth = new int[3];
        dateFormat.setLenient(false);
    }

    /**
     * Il metodo displayHelp stampa il messaggio di help a terminale
     */
    private static void displayHelp() {
        System.out.println("Password Security Checker\n\nUsage: " 
                            + "java PasswordSecurityChecker [<-h>] "
                            + "<\"name surname\"> <\"dd.mm.yyyy\">" 
                            + " <\"information\"> <\"password\">"
                            + "\n\nCheck the guide for more information " 
                            + "about the program and formatting");
    }

    /**
     * Il metodo checkData controlla se i dati passati da linea di comando sono
     * validi
     * 
     * @param args argomenti passati da linea di comando
     * @return {@code true} se i dati passati sono corretti
     */
    private boolean checkData(String[] args) {
        check: if (args.length > 3) {
            // controllo che non sia richiesto l'help
            for (String s : args) {
                if (s.equals("-h") || s.equals("-help") || s.equals("h") 
                    || s.equals("help")) {
                    break check;
                }
            }
            // controllo la validità del nome
            if (args[0].split(" ").length <= 1) {
                System.err.println("Incorrect name\n");
                break check;
            }

            if(args[2].isBlank() || args[2].isEmpty()){
                System.err.println("Missing third information\n");
                break check;
            }
            
            /**
             * Il controllo della validità della data di nascita viene eseguito
             *  nel metodo loadData per evitare la ridondanza di codice
             */
            return true;
        } else {
            System.err.println("Missing arguments\n");
        }
        displayHelp();
        return false;
    }

    /**
     * Il metodo loadData salva i valori passati come input all'interno degli
     * attributi dell'oggetto PasswordSecurityChecker
     * 
     * @param args argomenti passati da linea di comando
     */
    private void loadData(String[] args) {
        if (checkData(args)) {
            name = Arrays.asList(args[0].split(" "));
            try {
                Date birthDate = dateFormat.parse(args[1]);
                Calendar c = Calendar.getInstance();
                c.setTime(birthDate);
                birth[0] = c.get(Calendar.DAY_OF_MONTH);
                birth[1] = c.get(Calendar.MONTH) + 1;
                birth[2] = c.get(Calendar.YEAR);
                if(birth[2] < 1000){
                    throw new IllegalArgumentException();
                }
            } catch (Exception e){
                // controllo validità sulla data di nascita
                System.err.println("Incorrect birth date\n");
                displayHelp();
                System.exit(0);
            }
            data = Arrays.asList(args[2].split(" "));
            password = args[3];
        }
    }

    private void checkEasy() {
        // controllo se la password è il nome o il cognome
        for (int i = 0; i < name.size(); i++) {
            tryPassword(name.get(i));
        }
        // controllo se la password è l'anno di nascita
        String birthYear = String.valueOf(birth[2]);
        tryPassword(birthYear);
        // controllo se la password è la data di nascita
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(birth[i]);
            sb.append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        tryPassword(sb.toString());
        // controllo le password con nome e anno di nascita
        List<String> nameYear = new ArrayList<>();
        nameYear.add(name.get(0));
        nameYear.add(birthYear);
        tryAllPermutations(nameYear);

        // controllo le password con nome e ultime 2 cifre dell'anno di nascita
        nameYear.remove(1);
        // controllare
        nameYear.add(birthYear.substring((int)Math.log(birth[2])));
        // per substring trovare quel valore che
        // 10 --> 0, 100 --> 1, 1000 --> 2

    }

    /** 
     * la base del seguente metodo è stata presa dal sito
     * https://www.baeldung.com/java-array-permutations
     */

    /**
     * Il metodo prova tutte le combinazioni possibili
     * @param elements lista di stringhe da controllare
     */
    private void tryAllPermutations(List<String> elements){
        int[] indexes = new int[elements.size()];
        tryPassword(elements);

        int i = 0;
        while (i < elements.size()) {
            if (indexes[i] < i) {
                Collections.swap(elements, i % 2 == 0 ?  0 : indexes[i], i);
                tryPassword(elements);
                indexes[i]++;
                i = 0;
            }
            else {
                indexes[i] = 0;
                i++;
            }
        }  
    }

    /**
     * Il metodo tryPassword conntrolla se la password trovata è corretta
     * @param elements lista di strighe che concatenate formano la password
     */
    private void tryPassword(List<String> elements) {
        StringBuilder s = new StringBuilder();
        for (String string : elements) {
            s.append(string);
        }
        tryPassword(s.toString());
    }

    /**
     * Il metodo tryPassword conntrolla se la password trovata è corretta
     * @param s stringa da controllare
     */
    private void tryPassword(String s){
        tries++;
        if(s.equals(password)){
            // trovata
            end = System.currentTimeMillis();
            // metodo finale
            displayResult(true);
        }
    }

    /**
     * Il metodo displayResult stampa a terminale il risultato finale
     * dell'applicazione
     * @param isPasswordFound {@code true} se la password è stata trovata
     */
    private void displayResult(boolean isPasswordFound){
        long time = end - start;
        long minutes = (time / 1000) / 60;
        int seconds = (int)(time / 1000) % 60;
        time -= (minutes * 60000 + seconds * 1000);
        String s = "";
        if(!isPasswordFound){
            s = "not";
        }
        System.out.println("Password " + s + " found in " + minutes + 
                            " minutes, " + seconds + " seconds, " + time +
                            " milliseconds and with " + tries + " tries");

    }

    public static void main(String[] args) {
        PasswordSecurityChecker psc = new PasswordSecurityChecker();
        // controllo argomenti già eseguito
        psc.loadData(args);
        String[] arrayStrings = {"a"};
        List<String> elements = Arrays.asList(arrayStrings);
        System.out.println(elements);
    }
}