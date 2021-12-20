import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import utils.ParamHandler;

/**
 * La classe PasswordSecurityChecker contiene i metodi per verificare, in base
 * a dei dati passati dall'utente, la sicurezza della password. Questa verifica
 * viene eseguita tentando di forzare la password tramite combinazioni e
 * tramite il confronto con una lista di password più comuni.
 * 
 * @author Erik Pelloni
 * @version 1.0 (23.12.2021)
 */

public class PasswordSecurityChecker {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static String[] specials =
        { "!", "?", "+", "%", "$", "-", "_", "&", "!!"};

    private ParamHandler handler;

    private List<String> name;
    private int[] birth;
    private long start, end, tries;
    private String info, password, firstName, last2, birthYear;
    private String bruteString = "out";

    /**
     * Costruttore per poter istanziare un oggetto PasswordSecurityChecker
     */
    private PasswordSecurityChecker() {
        name = new ArrayList<>(2);
        birth = new int[3];
        dateFormat.setLenient(false);
        handler = new ParamHandler();
    }

    /**
     * Il metodo displayHelp stampa il messaggio di help a terminale
     * e termina il programma.
     */
    private void displayHelp() {
        System.out.println(handler.help("PasswordSecurityChecker",
                "PasswordSecurityChecker tries to force your password based" +
                        " on the data passed as arguments." +
                        "\n\tCheck the guide for more information."));
        System.exit(0);
    }

    /**
     * Il metodo getData controlla se i dati passati da linea di comando sono
     * validi.
     * Se lo sono, allora salva i loro valori all'interno degli attributi
     * della classe.
     * Per farlo, utilizzo una libreria scritta da Paolo Bettelini, in modo
     * da semplificare il controllo.
     * 
     * @param args argomenti passati da linea di comando
     */
    private void getData(String[] args) {

        // aggiungo i flag e i parametri all'handler
        handler.addFlag(
                "h",
                ParamHandler.propertyOf("Name", "Help"),
                ParamHandler.propertyOf("Description", "Shows this message"));

        handler.addFlag(
                "b",
                ParamHandler.propertyOf("Name", "Brute Force"),
                ParamHandler.propertyOf("Description", "Performs the brute force"
                        + " attack"));

        handler.addArg(
                "name", false, "String",
                ParamHandler.propertyOf("Name", "Name"),
                ParamHandler.propertyOf("Description", "Name and Surname"),
                ParamHandler.propertyOf("Format", "Name Surname"),
                ParamHandler.propertyOf("Example", "John Doe"));

        handler.addArg(
                "birth", false, "String",
                ParamHandler.propertyOf("Name", "Birth date"),
                ParamHandler.propertyOf("Description", "Your date of birth"),
                ParamHandler.propertyOf("Format", "dd.mm.yyyy"),
                ParamHandler.propertyOf("Example", "27.06.1970"));

        handler.addArg(
                "info", false, "String",
                ParamHandler.propertyOf("Name", "Third information"),
                ParamHandler.propertyOf("Description", "An information of your choice"),
                ParamHandler.propertyOf("Example", "Football"),
                ParamHandler.propertyOf("Example", "Barry"),
                ParamHandler.propertyOf("Example", "Arsenal"));

        handler.addArg(
                "password", true, "String",
                ParamHandler.propertyOf("Name", "Password"),
                ParamHandler.propertyOf("Description", "Your password"));

        // salvo i dati all'interno dell'handler
        try {
            handler.parse(args);

            // controllo se è stato richiesto l'help
            if (handler.getFlag("h")) {
                displayHelp();
            }

            // controllo se sono stati passati i parametri obbligatori
            if (!handler.isComplete()) {
                System.out.println(handler.getStatus());
                System.exit(0);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // assegno i valori agli attributi.
        if (handler.getArg("name") == null) {
            name = new ArrayList<>(0);
        } else {
            name = Arrays.asList(handler.getArg("name").split(" "));
        }

        if (handler.getArg("birth") != null) {
            try {
                Date birthDate = dateFormat.parse(handler.getArg("birth"));
                Calendar c = Calendar.getInstance();
                c.setTime(birthDate);
                birth[0] = c.get(Calendar.DAY_OF_MONTH);
                birth[1] = c.get(Calendar.MONTH) + 1;
                birth[2] = c.get(Calendar.YEAR);
                if (birth[2] < 1000) {
                    throw new IllegalArgumentException();
                }
                birthYear = Integer.toString(birth[2]);
                last2 = birthYear.substring(((int) Math.log10(birth[2])) - 1);
            } catch (ParseException | IllegalArgumentException ex) {
                // controllo validità sulla data di nascita
                System.err.println("Incorrect birth date\n");
                displayHelp();
            }
        }

        // se info fosse una lista
        // ↳ info = Arrays.asList(handler.getArg("info").split(" "));
        if (handler.getArg("info") == null) {
            info = "";
        } else {
            info = handler.getArg("info").trim().split(" ")[0];
        }
        // su questa stringa non faccio il controllo perché è obbligatoria
        password = handler.getArg("password");
    }

    /**
     * Il metodo isBirthValid controlla la validità della data di nascita.
     * @return {@code true} se la data di nascita è stata inserita
     */
    private boolean isBirthValid(){
        // se la data è stata inserita e ha passato i controlli, il giorno
        // non puo' sicuramente essere uguale a 0.
        return birth[0] != 0;
    }

    /**
     * Il metodo checkEasy prova a trovare la password utilizzando i dati
     * passati come input e semplici combinazioni tra quest'ultimi.
     * 
     * @param useSpecials se {@code true} le password vengono provate
     *                    utilizzando anche i caratteri speciali.
     */
    private void checkEasy(boolean useSpecials) {
        // faccio partire il tempo della ricerca
        if (!useSpecials) {
            start = System.currentTimeMillis();
        }

        if (!name.isEmpty()) {
            firstName = name.get(0).toLowerCase();
            // controllo se la password è il nome o il cognome
            for (int i = 0; i < name.size(); i++) {
                if (useSpecials) {
                    // aggiunta caratteri speciali
                    for (String c : specials) {
                        tryPassword(name.get(i) + c);
                    }
                } else {
                    tryPassword(name.get(i));
                }
            }
            if (isBirthValid()) {
                // controllo le password con nome e anno di nascita
                List<String> nameYear = new ArrayList<>();
                nameYear.add(firstName);
                nameYear.add(birthYear);
                tryAllPermutations(nameYear, useSpecials);
                // stessa cosa ma con il cognome
                nameYear.remove(firstName);
                nameYear.add(name.get(1).toLowerCase());
                tryAllPermutations(nameYear, useSpecials);

                // controllo le password con nome e ultime 2 cifre dell'anno di nascita
                nameYear.remove(nameYear.size() - 1);
                nameYear.add(firstName);
                nameYear.remove(birthYear);
                nameYear.add(last2);
                tryAllPermutations(nameYear, useSpecials);
                // stessa cosa ma con il cognome
                nameYear.remove(firstName);
                nameYear.add(name.get(1).toLowerCase());
                tryAllPermutations(nameYear, useSpecials);

                // controlli uguali agli ultimi 2 ma con il nome
                // in maiuscolo (prima lettera)
                nameYear.remove(nameYear.size() - 1);
                nameYear.add(firstName.substring(0, 1).toUpperCase() +
                        firstName.substring(1));
                tryAllPermutations(nameYear, useSpecials);

                // cognome
                nameYear.remove(nameYear.size()-1);
                nameYear.add(name.get(1).substring(0, 1).toUpperCase() +
                        name.get(1).substring(1));
                tryAllPermutations(nameYear, useSpecials);

                // con l'anno intero
                nameYear.remove(nameYear.size() - 1);
                nameYear.add(firstName.substring(0, 1).toUpperCase() +
                    firstName.substring(1));
                nameYear.remove(last2);
                nameYear.add(birthYear);
                tryAllPermutations(nameYear, useSpecials);

                // con il cognome
                nameYear.remove(nameYear.size() - 2);
                nameYear.add(name.get(1).substring(0, 1).toUpperCase() +
                        name.get(1).substring(1));
                tryAllPermutations(nameYear, useSpecials);
            }
        }

        if (isBirthValid()) {
            // controllo se la password è l'anno di nascita
            birthYear = String.valueOf(birth[2]);
            if (useSpecials) {
                for (String c : specials) {
                    tryPassword(birthYear + c);
                }
            } else {
                tryPassword(birthYear);
            }
        }

    }

    /**
     * Il metodo checkEasy senza parametri, invoca quello principale con il
     * parametro {@code false}, in modo da eseguire il checkEasy nel modo
     * "semplice".
     */
    private void checkEasy() {
        checkEasy(false);
    }

    /**
     * Il metodo checkFrequent controlla se la password si trova all'interno del
     * file contenente 1 milione di password più frequentemente utilizzate.
     */
    private void checkFrequent() {
        List<String> commons;
        try {
            commons = Files.readAllLines(
                Paths.get("../Documenti/passwordComuni.txt"));

            commons.forEach(s -> tryPassword(s));
        } catch (IOException ioe) {
            try {
                commons = Files.readAllLines(
                    Paths.get("Documenti/passwordComuni.txt"));

                commons.forEach(s -> tryPassword(s));
            } catch (IOException e) {
                System.err.println("Common passwords file reading error.");
            }
        }
    }

    /**
     * Il metodo checkComplex prova a trovare la password utilizzando
     * delle combinazioni più complesse tra i dati passati come input.
     */
    private void checkComplex() {
        // controlla le combinazioni semplici con l'aggiunta dei
        // caratteri speciali (tranne le combinazioni con la data di nascita)
        checkEasy(true);

        StringBuilder sb = new StringBuilder();

        if (!name.isEmpty()) {
            // controllo combinazioni di substring tra nome e cognome
            checkNameCombination(false, false, false);
            checkNameCombination(false, false, true);
        }

        // controllo se la password è la data di nascita
        if (isBirthValid()) {
            // formato 262004
            for (int i = 0; i < 3; i++) {
                sb.append(birth[i]);
            }
            tryPassword(sb.toString());
            sb.setLength(0);
            // formato 02062004
            for (int i = 0; i < 3; i++) {
                if (birth[i] < 10) {
                    sb.append("0" + birth[i]);
                } else {
                    sb.append(birth[i]);
                }
            }
            tryPassword(sb.toString());
            // formato 2.6.2004
            sb.setLength(0);
            for (int i = 0; i < 3; i++) {
                sb.append(birth[i]);
                sb.append(".");
            }
            sb.deleteCharAt(sb.length() - 1);
            tryPassword(sb.toString());
            sb.setLength(0);
            // formato 02.06.2004
            for (int i = 0; i < 3; i++) {
                if (birth[i] < 10) {
                    sb.append("0" + birth[i]);
                } else {
                    sb.append(birth[i]);
                }
                sb.append(".");
            }
            sb.deleteCharAt(sb.length() - 1);
            tryPassword(sb.toString());
            sb.setLength(0);
            if (!name.isEmpty()) {
                // combinazioni di substring tra nome e cognome e anno di nasicta
                checkNameCombination(true, false, false);
                checkNameCombination(true, false, true);
                // substring tra nome e cognome e ultime 2 cifre anno di nascita
                checkNameCombination(false, true, false);
                checkNameCombination(false, true, true);
            }
            if(!info.isEmpty()){
                checkInfo(false);
                checkInfo(true);
            }
        }

    }

    /**
     * Il metodo checkInfo controlla combinazioni con l'informazione aggiuntiva
     */
    private void checkInfo(boolean useSpecials){
        tryPassword(info);
        for (String s : specials) {
            tryPassword(info.concat(s));
        }
        List<String> data = new ArrayList<>();
        data.add(info);
        tryAllPermutations(data, useSpecials);
        if(!name.isEmpty()){
            data.add(name.get(0));
            tryAllPermutations(data, useSpecials);
            data.add(name.get(1));
            tryAllPermutations(data, useSpecials);
            if(isBirthValid()){
                data.clear();
                data.add(info);
                data.add(birthYear);
                tryAllPermutations(data, useSpecials);
                data.remove(name.get(1));
                tryAllPermutations(data, useSpecials);
                data.clear();
                data.add(info);
            }
        }
        if(isBirthValid()){
            data.add(birthYear);
            tryAllPermutations(data, false);
            tryAllPermutations(data, true);
        }

    }


    /**
     * Struttura del metodo presa dalle soluzioni presenti su questa pagina
     * https://stackoverflow.com/questions/50215907/python-brute-force-password-guesser
     */

    private void checkBrute() {
        char[] characters = new char[94];
        for (int i = 0; i < characters.length; i++) {
            characters[i] = (char)(i + 33);
        }
        int base = characters.length + 1;
        StringBuilder guess = new StringBuilder();
        int tests = 1;
        int c = 0;
        int m = 0;
    
        while (true) {
            int y = tests;
            while (true) {
                c = y % base;
                m = (int) Math.floor((y - c) / (double) base);
                y = m;
                int index = (c - 1);
                if (index < 0) {
                    index = -(-index % characters.length);
                    index += characters.length - 1;
                }
                guess.insert(0, characters[index]);
                // guess = characters[index] + guess;
                if (m == 0) {
                    break;
                }
            }
            tryPassword(guess.toString(), true);
            // else
            tests++;
            guess.setLength(0);
        }
    }

    /**
     * Il metodo checkNameCombination controlla combinazioni di substring
     * tra nome e cognome.
     * 
     *                      sarà messo prima il cognome
     * @param fullBirthYear se {@code true} verrà aggiunto al controllo
     *                      l'anno di nascita completo
     * @param last2Digits   se {@code true} verranno agiunte al controllo
     *                      le ultime due cifre dell'anno di nascita
     *                      Attenzione! Se {@code fullBirthYear} è {@code true}
     *                      {@code last2Digits}
     *                      verrà sempre considerato false.
     * @param useSpecials se {@code true} le password vengono provate
     *                    utilizzando anche i caratteri speciali.
     */
    private void checkNameCombination(boolean fullBirthYear, boolean last2Digits,
            boolean useSpecials) {

        List<String> list = new ArrayList<>();
        StringBuilder currentName = new StringBuilder();
        StringBuilder currentSecond = new StringBuilder();
        
        // controlli sulla data di nascita già presenti
        if (fullBirthYear) {
            list.add(birthYear);
        } else if (last2Digits) {
            list.add(last2);
        }
        
        if (!name.isEmpty()) {
            for (int i = 1; i <= name.get(0).length(); i++) {
                list.remove(currentName.toString());
                currentName.setLength(0);
                currentName.append(name.get(0).substring(0, i));
                list.add(currentName.toString());
                tryAllPermutations(list, useSpecials);
                for (int j = 1; j <= name.get(1).length(); j++){
                    list.remove(currentSecond.toString());
                    currentSecond.setLength(0);
                    currentSecond.append(name.get(1).substring(0, j));
                    list.add(currentSecond.toString());

                    tryAllPermutations(list, useSpecials);
                }
            }
        }
    }

    /**
     * la base del seguente metodo è stata presa dal sito
     * https://www.baeldung.com/java-array-permutations
     */

    /**
     * Il metodo prova tutte le combinazioni possibili
     * 
     * @param elements lista di stringhe da controllare
     * @param useSpecials {@code true} se effettuare ricerca caratteri speciali
     */
    private void tryAllPermutations(List<String> elements, boolean useSpecials) {
        List<String> copy = new ArrayList<>(elements);
        int[] indexes = new int[copy.size()];
        tryPassword(copy);

        int i = 0;
        while (i < copy.size()) {
            if (indexes[i] < i) {
                Collections.swap(copy, i % 2 == 0 ? 0 : indexes[i], i);
                tryPassword(copy, useSpecials);
                indexes[i]++;
                i = 0;
            } else {
                indexes[i] = 0;
                i++;
            }
        }
    }

    /**
     * Il metodo tryPassword conntrolla se la password trovata è corretta
     * concatenando tutti gli elementi presenti nella lista.
     * 
     * @param elements    lista di strighe che concatenate formano la password
     * @param useSpecials se {@code true} vengono controllate le password con
     *                    i caratteri speciali in coda.
     */
    private void tryPassword(List<String> elements, boolean useSpecials) {
        for (String string : elements) {
            tryPassword(string);
            if (useSpecials) {
                for (String c : specials) {
                    tryPassword(string + c);
                }
            }
        }
    }

    private void tryPassword(List<String> elements) {
        tryPassword(elements, false);
    }

    /**
     * Il metodo tryPassword conntrolla se la password trovata è corretta
     * 
     * @param s stringa da controllare
     */
    private void tryPassword(String s) {
        tryPassword(s, false);
    }

    /**
     * Il metodo tryPassword conntrolla se la password trovata è corretta
     * 
     * @param s stringa da controllare
     * @param brute {@code true} se brute force
     */
    void tryPassword(String s, boolean brute){
        tries++;
        if (s.equals(password)) {
            // trovata, fermo il tempo di ricerca
            end = System.currentTimeMillis();
            // metodo finale
            displayResult(true, brute);
        }
    }

    /**
     * Il metodo displayResult stampa a terminale il risultato finale
     * dell'applicazione
     * 
     * @param isPasswordFound {@code true} se la password è stata trovata
     */
    private void displayResult(boolean isPasswordFound, boolean brute) {
        String s = "found";
        if (!isPasswordFound) {
            end = System.currentTimeMillis();
            s = "not found";
        }

        if(brute){
            bruteString = "";
        }

        long time = end - start;
        long minutes = (time / 1000) / 60;
        int seconds = (int) (time / 1000) % 60;
        time -= (minutes * 60000 + seconds * 1000);
        System.out.println("Password " + s + " in " + minutes +
                " minutes, " + seconds + " seconds, " + time +
                " milliseconds and with " + tries + " tries with" +
                bruteString + " brute force attack");

        System.exit(0);
    }

    public static void main(String[] args) {
        PasswordSecurityChecker psc = new PasswordSecurityChecker();
        // controllo argomenti già eseguito
        psc.getData(args);
        System.out.println("Easy search started...\n");
        psc.checkEasy();
        System.out.println("Frequent search started...\n");
        psc.checkFrequent();
        System.out.println("Complex search started...\n");
        psc.checkComplex();
        if (psc.handler.getFlag("b")) {
            System.out.println("Brute force search started...\n");
            psc.checkBrute();
        }
        psc.displayResult(false, psc.handler.getFlag("b"));
    }
}