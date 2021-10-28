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
 * @version 1.0 (28.10.2021)
 */

public class PasswordSecurityChecker{
    private List<String> name;
    private int[] birth;
    //private List<String> info;
    private String info;
    private char[] specials = { '!', '?', '+', '%', '$', '-', '_'};
    private String password;
    private List<String> commons;
    private int tries;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private long start;
    private long end;
    private ParamHandler handler;

    /**
     * Costruttore vuoto per poter istanziare un oggetto PasswordSecurityChecker
     */
    private PasswordSecurityChecker() {
        name = new ArrayList<>();
        //info = new ArrayList<>();
        birth = new int[3];
        dateFormat.setLenient(false);
        handler = new ParamHandler("-");
    }

    /**
     * Il metodo displayHelp stampa il messaggio di help a terminale
     */
    private void displayHelp() {
        /*System.out.println("Password Security Checker\n\nUsage: " 
                            + "java PasswordSecurityChecker [<-h>] "
                            + "<\"name surname\"> <\"dd.mm.yyyy\">" 
                            + " <\"information\"> <\"password\">"
                            + "\n\nCheck the guide for more information " 
                            + "about the program and formatting");*/
        System.out.println(handler.help("PasswordSecurityChecker", 
        "PasswordSecurityChecker tries to force your password based" + 
        " on the data passed as arguments."));
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
    private void getData(String[] args){

        //handler.addArg(argName, mandatory, type, properties);
        handler.addFlag(
			"h",
			ParamHandler.propertyOf("Name", "Help"),
			ParamHandler.propertyOf("Description", "Shows this message")
		);

        handler.addArg(
			"name", false, "String",
			ParamHandler.propertyOf("Name", "Name"),
			ParamHandler.propertyOf("Description", "Name and Surname"),
            ParamHandler.propertyOf("Format", "Name Surname"),
            ParamHandler.propertyOf("Example", "John Doe")
		);

        handler.addArg(
			"birth", false, "String",
			ParamHandler.propertyOf("Name", "Birth date"),
			ParamHandler.propertyOf("Description", "Your date of birth"),
            ParamHandler.propertyOf("Format", "dd.mm.yyyy"),
            ParamHandler.propertyOf("Example", "27.06.1970")
		);

        handler.addArg(
			"info", false, "String",
			ParamHandler.propertyOf("Name", "Third information"),
			ParamHandler.propertyOf("Description", "An information of your choice"),
            ParamHandler.propertyOf("Example", "Football"),
			ParamHandler.propertyOf("Example", "Barry")
		);

        handler.addArg(
			"password", true, "String",
			ParamHandler.propertyOf("Name", "Password"),
			ParamHandler.propertyOf("Description", "Your password")
		);

        boolean flag = false;

        // salvo i dati all'interno dell'handler
        try {
			handler.parse(args);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
            flag = true;
		}

        // controllo se sono stati passati i parametri obbligatori
        if (!handler.isComplete()) {
			System.out.println(handler.getStatus());
            flag = true;
		}

        // se ci sono errori negli input, termino il programma.
        if(flag){
            System.exit(0);
        }

        // controllo se è stato richiesto l'help
		if (handler.getFlag("h")) {
			displayHelp();
		}

        // assegno i valori agli attributi.
        name = Arrays.asList(handler.getArg("name").split(" "));

        try {
            Date birthDate = dateFormat.parse(handler.getArg("birth"));
            Calendar c = Calendar.getInstance();
            c.setTime(birthDate);
            birth[0] = c.get(Calendar.DAY_OF_MONTH);
            birth[1] = c.get(Calendar.MONTH) + 1;
            birth[2] = c.get(Calendar.YEAR);
            if(birth[2] < 1000){
                throw new IllegalArgumentException();
            }
        } catch (ParseException | IllegalArgumentException ex){
            // controllo validità sulla data di nascita
            System.err.println("Incorrect birth date\n");
            displayHelp();
        }

        // se info fosse una lista 
        // ↳ info = Arrays.asList(handler.getArg("info").split(" "));
        info = handler.getArg("info").trim().split(" ")[0];
        password = handler.getArg("password");
    }

    /**
     * Il metodo checkEasy prova a trovare la password utilizzando i dati
     * passati come input e semplici combinazioni tra quest'ultimi.
     */
    private void checkEasy(){
        // faccio partire il tempo della ricerca
        start = System.currentTimeMillis();
        
        String firstName = name.get(0).toLowerCase();

        // controllo se la password è il nome o il cognome
        for (int i = 0; i < name.size(); i++) {
            tryPassword(name.get(i));
        }

        // controllo se la password è l'anno di nascita
        String birthYear = String.valueOf(birth[2]);
        tryPassword(birthYear);

        // controllo le password con nome e anno di nascita
        List<String> nameYear = new ArrayList<>();
        nameYear.add(firstName);
        nameYear.add(birthYear);
        tryAllPermutations(nameYear);

        // controllo le password con nome e ultime 2 cifre dell'anno di nascita
        nameYear.remove(1);
        nameYear.add(birthYear.substring(((int)Math.log10(birth[2])) - 1));
        tryAllPermutations(nameYear);

        // controlli uguali agli ultimi 2 ma con il nome
        // in maiuscolo (prima lettera)
        nameYear.remove(0);
        nameYear.add(firstName.substring(0, 1).toUpperCase() + 
                    firstName.substring(1));
        tryAllPermutations(nameYear);

        // con l'anno intero
        nameYear.remove(0);
        nameYear.add(birthYear);
        tryAllPermutations(nameYear);
    }

    /**
     * Il metodo checkFrequent controlla se la password si trova all'interno del
     * file contenente 1 milione di password più frequentemente utilizzate.
     */
    private void checkFrequent(){
       try{
            List<String> frequents = Files.readAllLines(
                Paths.get("Documenti\\passwordComuni.txt"));
             
            for (String s : frequents) {
                tryPassword(s);
            }
        }catch(IOException ioe){
            System.err.println("Common passwords file reading error.");
        }
    }

    /**
     * Il metodo checkComplex prova a trovare la password utilizzando 
     * delle combinazioni più complesse tra i dati passati come input.
     */
    private void checkComplex(){
        /* questi controlli li metto nel "difficile"
        // controllo se la password è la data di nascita
        StringBuilder sb = new StringBuilder();
        // formato 262004
        for (int i = 0; i < 3; i++) {
            sb.append(birth[i]);
        }
        System.out.println(sb);
        tryPassword(sb.toString());
        // formato 02062004
        for (int i = 0; i < 3; i++) {
            sb.append(birth[i]);
        }
        System.out.println(sb);
        tryPassword(sb.toString());
        // formato 2.6.2004
        sb.setLength(0);
        for (int i = 0; i < 3; i++) {
            sb.append(birth[i]);
            sb.append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        System.out.println(sb);
        tryPassword(sb.toString());*/

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
        List<String> copy =  new ArrayList<>(elements);
        int[] indexes = new int[copy.size()];
        tryPassword(copy);

        int i = 0;
        while (i < copy.size()) {
            if (indexes[i] < i) {
                Collections.swap(copy, i % 2 == 0 ?  0 : indexes[i], i);
                tryPassword(copy);
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
     * concatenando tutti gli elementi presenti nella lista.
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
            // trovata, fermo il tempo di ricerca
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
        String s = "";
        if(!isPasswordFound){
            end = System.currentTimeMillis();
            s = "not";
        }
        long time = end - start;
        long minutes = (time / 1000) / 60;
        int seconds = (int)(time / 1000) % 60;
        time -= (minutes * 60000 + seconds * 1000);
        System.out.println("Password " + s + " found in " + minutes + 
                            " minutes, " + seconds + " seconds, " + time +
                            " milliseconds and with " + tries + " tries");

        System.exit(0);
    }

    public static void main(String[] args){
        PasswordSecurityChecker psc = new PasswordSecurityChecker();
        // controllo argomenti già eseguito
        //psc.loadData(args);
        psc.getData(args);
        String[] arrayStrings = {"a"};
        List<String> elements = Arrays.asList(arrayStrings);
        //System.out.println(elements);
        psc.checkEasy();
        psc.checkFrequent();
        psc.displayResult(false);
    }
}