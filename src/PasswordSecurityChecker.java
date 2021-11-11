import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
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
 * @version 1.0 (11.11.2021)
 */

public class PasswordSecurityChecker {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static char[] specials = { '!', '?', '+', '%', '$', '-', '_'};

    private ParamHandler handler;

    private List<String> name, commons;
    //private List<String> info;
    private int[] birth;
    private int tries;
    private long start, end;
    private String info, password, firstName, birthYear;

    /**
     * Costruttore per poter istanziare un oggetto PasswordSecurityChecker
     */
    private PasswordSecurityChecker() {
        name = new ArrayList<>(2);
        //info = new ArrayList<>();
        birth = new int[3];
        dateFormat.setLenient(false);
        handler = new ParamHandler("-");
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
        if(handler.getArg("name") == null){
            name = new ArrayList<>(0);
        }else{
            name = Arrays.asList(handler.getArg("name").split(" "));
        }

        if(handler.getArg("birth") != null){
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
        }

        // se info fosse una lista 
        // ↳ info = Arrays.asList(handler.getArg("info").split(" "));
        if(handler.getArg("info") == null){
            info = "";
        }else{
            info = handler.getArg("info").trim().split(" ")[0];
        }
        // su questa stringa non faccio il controllo perché è obbligatoria
        password = handler.getArg("password");
    }

    /**
     * Il metodo checkEasy prova a trovare la password utilizzando i dati
     * passati come input e semplici combinazioni tra quest'ultimi.
     */
    private void checkEasy(){
        // faccio partire il tempo della ricerca
        start = System.currentTimeMillis();

        if(!name.isEmpty()){            
            firstName = name.get(0).toLowerCase();
            // controllo se la password è il nome o il cognome
            for (int i = 0; i < name.size(); i++) {
                tryPassword(name.get(i));
            }
        }

        if(birth[0] != 0){            
            // controllo se la password è l'anno di nascita
            birthYear = String.valueOf(birth[2]);
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
        StringBuilder sb = new StringBuilder();
        System.out.println(name.get(0).substring(0,name.get(0).length()));
        // controllo combinazioni di substring tra nome e cognome
        if(!name.isEmpty()){
            for (int i = 1; i < name.get(0).length(); i++) {
                sb.setLength(0);
                //sb.append(name.get());
            }
        }

        // controllo se la password è la data di nascita
        if(birth[0] != 0){
            // formato 262004
            for (int i = 0; i < 3; i++) {
                sb.append(birth[i]);
            }
            tryPassword(sb.toString());
            sb.setLength(0);
            // formato 02062004
            for (int i = 0; i < 3; i++) {
                if(birth[i] < 10){
                    sb.append("0" + birth[i]);
                }else{
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
                if(birth[i] < 10){
                    sb.append("0" + birth[i]);
                }else{
                    sb.append(birth[i]);
                }
                sb.append(".");
            }
            sb.deleteCharAt(sb.length() - 1);
            tryPassword(sb.toString());
            sb.setLength(0);
        }

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
        /*String[] arrayStrings = {"a"};
        List<String> elements = Arrays.asList(arrayStrings);*/
        //System.out.println(elements);
        //if()
        psc.checkEasy();
        psc.checkFrequent();
        psc.checkComplex();
        psc.displayResult(false);
    }
}