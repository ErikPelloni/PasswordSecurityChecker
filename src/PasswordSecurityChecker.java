import java.util.ArrayList;
import java.util.List;

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

    private static void displayHelp(){
        System.out.println("Password Security Checker\n\nUsage: " + 
                            "java PasswordSecurityChecker [<-h>] "+
                            "<name surname> <dd.mm.yyyy> <information>"+
                            " <password>"+
                            "\n\nCheck the guide for more information " + 
                            "about the program and formatting");
    }

    
    public static void main(String[] args) {
        displayHelp();
    }
}