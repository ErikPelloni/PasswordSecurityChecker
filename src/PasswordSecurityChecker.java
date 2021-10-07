import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

/**
 * La classe PasswordSecurityChecker contiene i metodi per verificare,
 * in base a dei dati passati dall'utente, la sicurezza della password.
 * Questa verifica viene eseguita tentando di forzare la password tramite
 * combinazioni e tramite il confronto con una lista di password più comuni.
 * @author Erik Pelloni
 * @version 1.0 (30.09.2021)
 */

public class PasswordSecurityChecker{
    private List<String> name;
    private int[] birth;
    private List<String> data;
    private char[] specials;
    private String password;
    private List<String> commons;
    private int tries;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");


    /**
     *Costruttore vuoto per poter istanziare un oggetto PasswordSecurityChecker
     */
    private PasswordSecurityChecker(){
        name = new ArrayList<>();
        data = new ArrayList<>();
        birth = new int[3];
        dateFormat.setLenient(false);
    }

    /**
     * Il metodo displayHelp stampa il messaggio di help a terminale
     */
    private static void displayHelp(){
        System.out.println("Password Security Checker\n\nUsage: " + 
                            "java PasswordSecurityChecker [<-h>] "+
                            "<\"name surname\"> <\"dd.mm.yyyy\">"+
                            " <\"information\"> <\"password\">"+
                            "\n\nCheck the guide for more information " + 
                            "about the program and formatting");
    }

    /**
     * Il metodo checkData controlla se i dati passati da linea di comando
     * sono validi
     * @param args argomenti passati da linea di comando
     * @return {@code true} se i dati passati sono corretti
     */
    private boolean checkData(String[] args){
        check:
        if(args.length >= 3){
            // controllo che non sia richiesto l'help
            for (String s : args) {
                if(s.equals("-h") || s.equals("-help") || s.equals("h") 
                || s.equals("help")){
                    break check;
                }
            }
            // controllo la validità del nome
            if(args[0].split(" ").length <= 1){
                System.err.println("Incorrect name");
                break check;
            }
            /**
             * Il controllo della validità della data di nascita viene eseguito
             * nel metodo loadData per evitare la ridondanza di codice
            */
            return true;
        }
        displayHelp();
        return false;
    }

    /**
     * Il metodo loadData salva i valori passati come input all'interno degli
     * attributi dell'oggetto PasswordSecurityChecker
     * @param args argomenti passati da linea di comando
     */
    private void loadData(String[] args){
        if(checkData(args)){
            name = Arrays.asList(args[0].split(" "));
            try {
                Date birthDate = dateFormat.parse(args[1]);
                Calendar c = Calendar.getInstance();
                c.setTime(birthDate);
                birth[0] = c.get(Calendar.DAY_OF_MONTH);
                birth[1] = c.get(Calendar.MONTH) + 1;
                birth[2] = c.get(Calendar.YEAR);

            } catch (ParseException pe) {
                System.err.println("Incorrect birth date");
                displayHelp();
                System.exit(0);
            }
            data = Arrays.asList(args[2].split(" "));
        }
    }


    private void checkEasy() {
        
    }
    public static void main(String[] args) {
        PasswordSecurityChecker psc = new PasswordSecurityChecker();
        psc.loadData(args);
        for (int i : psc.birth) {
            System.out.println(i);
        }
    }
}