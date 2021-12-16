1. [Introduzione](#introduzione) 

    1. [Informazioni sul progetto](#informazioni-sul-progetto)

    1. [Abstract](#abstract)

    1. [Scopo](#scopo)

1. [Analisi](#analisi)

    1. [Analisi del dominio](#analisi-del-dominio)
  
    1. [Prerequisiti](#prerequisiti)

    1. [Analisi e specifica dei requisiti](#analisi-e-specifica-dei-requisiti)

    1. [Use case](#use-case)

    1. [Pianificazione](#pianificazione)

        - [Gantt Preventivo](#gantt-preventivo)

        - [Gantt Consuntivo](#gantt-consuntivo)
    
    1. [Analisi dei mezzi](#analisi-dei-mezzi)

1. [Progettazione](#progettazione)

    1. [Design dell’architettura del sistema](#design-dellarchitettura-del-sistema)

    1. [Activity Diagram](#activity-diagram)

    1. [Descrizione Activity Diagram](#descrizione)

    1. [Diagramma delle classi](#diagramma-delle-classi)

1. [Implementazione](#implementazione)

1. [Test](#test)

    1. [Protocollo di test](#protocollo-di-test)

    1. [Risultati test](#risultati-test)

    1. [Mancanze/limitazioni conosciute](#mancanze/limitazioni-conosciute)

1. [Consuntivo](#consuntivo)

1. [Conclusioni](#conclusioni)

    1. [Sviluppi futuri](#sviluppi-futuri)

    1. [Considerazioni personali](#considerazioni-personali)

1. [Sitografia](#sitografia)

1. [Allegati](#allegati)


## Introduzione

#### Informazioni sul progetto

  -   Allievo e docente coinvolti nel progetto: Erik Pelloni, Luca Muggiasca (docente)

  -   SAM Trevano sezione informatica I3BB 2021/2022

  -   Data di inizio del progetto: 9 Settembre 2021

  -   Data di consegna del progetto: 23 Dicembre 2021

  -   **Background/Situazione iniziale**:
  Un utente vuole controllare la sicurezza della propria password.

  -   **Descrizione del problema e motivazione**: 
  Il programma, nel modo spiegato seguentemente permette di verificare il grado di sicurezza della password in base a dei dati personali. È importante utilizzare delle password efficaci in modo da evitare il furto di account o dati.

  -   **Approccio/Metodi**: Sulla base di alcuni dati personali eventualmente passati al programma, quest'ultimo proverà a risalire alla password.

  -   **Risultati**: Il programma stampera l'esito finale (password trovata/non trovata), il numero di tentativi eseguiti per trovare la password e il tempo impiegato.

 ### Abstract

  > *Nowadays, everyone has many different accounts for which a password is 
  > required. Choosing a secure password is very important to avoid data theft. 
  > This program checks the security of the password entered, based on optional 
  > information passed in as a parameter.*

### Scopo

  Lo scopo del progetto è quello di creare un programma che sia in grado di dare un feedback riguardante la sicurezza di una password.

## Analisi

### Analisi del dominio

  Questo capitolo descrive il contesto in cui il prodotto verrà
  utilizzato:

  -   Il prodotto verrà utilizzato per controllare la sicurezza della propria password.

### Prerequisiti
  -   Il prodotto può essere utilizzato da chiunque

  -   La competenza richiesta per l'utilizzo di questo programma è saper lanciare un programma tramite linea di comando. Non sono richieste altre conoscenze o competenze.
  - È necessario avere Java installato sul dispositivo che si vuole utilizzare per eseguire il programma.

### Analisi e specifica dei requisiti

  Seguono una descrizione dell'analisi e la specifica dei requisiti.

  -   La funzione del prodotto è quella di controllare la sicurezza di una password, cercando di trovarla utilizzando dei dati relativi alla persona.

  -   Il prodotto è eseguibile tramite linea di comando e stampa i risultati a terminale.

  -  I dati verranno utilizzati unicamente per il funzionamento del programma e vengono eliminati una volta completata l'operazione.

  In base alla lista dei requisiti e all’analisi degli stessi, è stata redatta una *specifica dei requisiti* nella quale sono elencate e
  descritte in modo dettagliato le funzionalità che il prodotto
  fornisce. I requisiti non rappresentano delle attività bensì delle caratteristiche che il prodotto possiede.




  |**ID**	|**Titolo**|**Descrizione**			|**Priorità**|**Vers**|**Note**  |
  |-------|----------|-------------|------------|--------|----------|
  |Req-01|Linea di comando|Il programma sarà eseguibile tramite linea di comando|1|1.0||
  |Req-02 |Dati input|Al programma verranno passati i seguenti dati:<br>  nome e cognome, data di nascita e un terzo dato a propria scelta |1|1.1|I dati sono facoltativi|
  |Req-03 |Controllo validità |Verrà eseguito un controllo della validità dei dati inseriti|1|1.0||
  |Req-04 |Help |Sarà possibile visualizzare un help che mostra il funzionamento del programma|1|1.0||
  |Req-05 |Controllo semplice|Verranno controllate delle password combinate in modo semplice|1|1.0||
  |Req-06 |Controllo complesso|Verranno controllate delle password combinate in modo più complesso|1|1.0||
  |Req-07 |Controllo password frequenti|Verrà controllata una lista contenente delle password frequenti|1|1.0||
  |Req-08 |Brute force|Se la password non viene trovata con i controlli precedenti si prosegue con un attacco brute force, se l'utente lo decide|2|1.0||
  |Req-09 |Stampa valori|Vengono stampati i tentativi effettuati e il tempo impiegato|1|1.0||
  |Req-10|Script|Sarà possibile passare i dati a uno script tramite file csv, lo script invocherà automaticamente il programma|3|1.2|Sostituito dal requisito Req-11|
  |Req-11|Gestione parametri|Il passaggio di parametri viene gestito all'interno del programma, utilizzando una libreria. |2|2.0||
  
  



**Legenda elementi tabella dei requisiti:**

**ID**: identificativo univoco del requisito

**Titolo**: titolo dato al requisito

**Nome**: descrizione del requisito

**Priorità**: indica l’importanza di un requisito nell’insieme del
progetto, definita assieme al committente. Sono defininiti al massimo di 3 livelli di priorità in questo modo:
+ 1: priorità maggiore
+ 3: priorità minore

**Versione**: indica la versione del requisito. Ogni modifica del
requisito avrà una versione aggiornata.

Sulla documentazione apparirà solamente l’ultima versione.

**Note**: eventuali osservazioni importanti o riferimenti ad altri
requisiti.

**Sotto requisiti**: elementi che compongono il requisito.


### Use case
![useCase](../use_case.png)

L'utente è in grado (tramite linea di comando) di verificare la sicurezza della propria password, anche in relazione con i propri dati personali.

### Pianificazione

#### Gantt preventivo

![ganttPreventivo](../Gantt/Gantt-preventivo.png)

#### Gantt consuntivo

### Analisi dei mezzi
Per la creazione del progetto è stato utilizzato un PC con Windows 10, Java 16 e
Visual Studio Code, non ci sono altri requisiti a livello hardware per la 
creazione e lo svolgimento di questo progetto.

Nel mio caso specifico le specifiche del computer sul quale ho lavorato sono le seguenti:
- Nome SO	Microsoft Windows 10 Enterprise
- Processore	Intel(R) Core(TM) i5-7360U CPU @ 2.30GHz, 2301 Mhz, 2 core, 4  processori logici
- Memoria fisica installata (RAM)	16.0 GB


È stata utilizzata una libreria scritta da Paolo Bettelini, che permette una
migliore gestione dei parametri passati come argomenti da linea di comando.

Il prodotto potrà essere eseguito su un qualsiasi dispositivo che abbia Java
installato, indipendentemente dal sistema operativo.

## Progettazione

Questo capitolo descrive esaustivamente come deve essere realizzato il
prodotto fin nei suoi dettagli. (Una buona progettazione permette
all’esecutore di evitare fraintendimenti e imprecisioni
nell’implementazione del prodotto.)

### Design dell’architettura del sistema

-   Si tratta di un programma che viene invocato tramite linea di comando e con il passaggio di argomenti.

-   Composto da 1 classe scritta in java.

### Activity Diagram

![activity](../PSC-Activity_Diagram.png)

#### Descrizione

Lanciando l'applicazione si passano i dati necessari per il funzionmento.
<br>Se le informazioni non sono passate nel modo corretto o se l'utente ha scento di visualizzare l'help, l'applicazione mostra l'help (che spiega la formattazine dei valori di input).
 
Una volta inseriti correttamente i dati il programma comincia a provare a forzare la password.

+ In un primo momento vengono provate le combinazioni più semplici. Se la 
password viene trovata, il programma giunge a termine, se no si passa al controllo successivo.

+ Il secondo controllo che viene effettuato è quello tra la password fornita e una lista di password frequentemente utilizzate. Anche in questo caso la procedura di funzionamento del programma è la medesima: se la password viene trovata, il programma giunge a termine, se no si passa al controllo successivo.

+ Se la password non è presente nemmeno all'interno della lista, il controllo da compiere è quello delle combinazioni più complesse. Nuovamente la procedura finale è la stessa.

+ Infine, se l'utente ha deciso di effettuarla, l'ultima ricerca effettuata è quella brute force. Una volta trovata la password o raggiunto l'eventuale limite (di tempo o di tentativi, stabilito dll'utente) il programma giunge al termine e stampa l'output finale.

+ *N.B. I controlli verranno effettuati solamente sui dati passati come parametro all'esecuzione del programma.*
<br> <br>


### Diagramma delle classi
<img src="../PasswordSecurityChecker_Classe.svg" alt="diagramma classe" width="1400"/>

[Immagine originale diagramma delle classi](../PasswordSecurityChecker_Classe.svg)

## Implementazione

In questo capitolo dovrà essere mostrato come è stato realizzato il
lavoro. Questa parte può differenziarsi dalla progettazione in quanto il
risultato ottenuto non per forza può essere come era stato progettato.

Sulla base di queste informazioni il lavoro svolto dovrà essere
riproducibile.

In questa parte è richiesto l’inserimento di codice sorgente/print
screen di maschere solamente per quei passaggi particolarmente
significativi e/o critici.

Inoltre dovranno essere descritte eventuali varianti di soluzione o
scelte di prodotti con motivazione delle scelte.

Non deve apparire nessuna forma di guida d’uso di librerie o di
componenti utilizzati. Eventualmente questa va allegata.

Per eventuali dettagli si possono inserire riferimenti ai diari.

## Test

### Protocollo di test

Definire in modo accurato tutti i test che devono essere realizzati per
garantire l’adempimento delle richieste formulate nei requisiti. I test
fungono da garanzia di qualità del prodotto. Ogni test deve essere
ripetibile alle stesse condizioni.


|Test Case      | TC-001                               |
|---------------|--------------------------------------|
|**Nome**       |Import a card, but not shown with the GUI |
|**Riferimento**|REQ-012                               |
|**Descrizione**|Import a card with KIC, KID and KIK keys with no obfuscation, but not shown with the GUI |
|**Prerequisiti**|Store on local PC: Profile\_1.2.001.xml (appendix n\_n) and Cards\_1.2.001.txt (appendix n\_n) |
|**Procedura**     | - Go to “Cards manager” menu, in main page click “Import Profiles” link, Select the “1.2.001.xml” file, Import the Profile - Go to “Cards manager” menu, in main page click “Import Cards” link, Select the “1.2.001.txt” file, Delete the cards, Select the “1.2.001.txt” file, Import the cards |
|**Risultati attesi**|Keys visible in the DB (OtaCardKey) but not visible in the GUI (Card details) |


### Risultati test

Tabella riassuntiva in cui si inseriscono i test riusciti e non del
prodotto finale. Se un test non riesce e viene corretto l’errore, questo
dovrà risultare nel documento finale come riuscito (la procedura della
correzione apparirà nel diario), altrimenti dovrà essere descritto
l’errore con eventuali ipotesi di correzione.

### Mancanze/limitazioni conosciute

Descrizione con motivazione di eventuali elementi mancanti o non
completamente implementati, al di fuori dei test case. Non devono essere
riportati gli errori e i problemi riscontrati e poi risolti durante il
progetto.

## Consuntivo

Consuntivo del tempo di lavoro effettivo e considerazioni riguardo le
differenze rispetto alla pianificazione (cap 1.7) (ad esempio Gannt
consuntivo).

## Conclusioni

Quali sono le implicazioni della mia soluzione? Che impatto avrà?
Cambierà il mondo? È un successo importante? È solo un’aggiunta
marginale o è semplicemente servita per scoprire che questo percorso è
stato una perdita di tempo? I risultati ottenuti sono generali,
facilmente generalizzabili o sono specifici di un caso particolare? ecc

### Sviluppi futuri
  + Aggiunta input password non in chiaro in modo interattivo
  + Script per il passaggio dei dati
  + Aggiunta di un'interfaccia grafica

### Considerazioni personali
  Cosa ho imparato in questo progetto? ecc

### Sitografia

- https://www.baeldung.com/java-array-permutations, *Permutations of an Array in Java*, 28.10.2021

- https://stackoverflow.com/questions/50215907/python-brute-force-password-guesser, 2.12.2021



## Allegati

-   [Diari di lavoro](../../Diari/)

-   [Codici sorgente](../../src)

-   [Manuale d'uso](..\PasswordSecurityChecker_Guida.docx)

-   [Qdc](..\QdC_EP_PasswordSecurityChecker.docx)
