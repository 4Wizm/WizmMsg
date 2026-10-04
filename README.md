# WizmMsg

Plugin Minecraft per messaggi privati tra giocatori, con supporto proxy (BungeeCord/Velocity) e database MySQL per la persistenza delle sessioni.

Autore: 4wizm
Versione: 1.0
Compatibilita: Paper/Purpur/Spigot 1.13+ (testato su 1.21.11)

---

## Funzionalita

- Comando /msg <giocatore> <messaggio> per inviare messaggi privati
- Comando /r <messaggio> per rispondere all'ultimo giocatore con cui hai parlato
- Comando /msg on per riattivare la ricezione dei messaggi
- Comando /msg off per bloccare la ricezione dei messaggi
- Comando /msg reload per ricaricare la config senza riavviare il server
- Rilevamento automatico del proxy: se il server e dietro BungeeCord/Velocity, i messaggi funzionano tra server diversi
- Se il server non e dietro proxy, i messaggi restano locali
- Configurazione personalizzabile di tutti i messaggi con placeholder %sender% e %target%
- Tab-complete intelligente: mostra i nomi dei giocatori online e le sottocomandi solo se hai i permessi
- Persistenza della sessione tramite MySQL: l'ultimo interlocutore resta salvato anche dopo il riavvio
- Blocco messaggi persistente: se un giocatore fa /msg off, resta bloccato anche dopo il riavvio

---

## Comandi

| Comando | Descrizione | Permesso |
|---|---|---|
| /msg <giocatore> <messaggio> | Invia un messaggio privato | wizmmsg.use.msg |
| /r <messaggio> | Risponde all'ultimo interlocutore | wizmmsg.use.msg |
| /msg on | Riattiva la ricezione dei messaggi | wizmmsg.use.lockmessage |
| /msg off | Blocca la ricezione dei messaggi | wizmmsg.use.lockmessage |
| /msg reload | Ricarica config.yml | wizmmsg.use.reload |

---

## Permessi

| Permesso | Default | Descrizione |
|---|---|---|
| wizmmsg.use.msg | true | Usare /msg e /r |
| wizmmsg.use.lockmessage | true | Usare /msg on e /msg off |
| wizmmsg.use.reload | op | Usare /msg reload |

---

## Installazione

1. Scarica il file WizmMsg-1.0.jar dalla sezione Releases
2. Copialo nella cartella plugins/ del server
3. Avvia il server una prima volta per generare config.yml
4. Configura il database MySQL nel file plugins/WizmMsg/config.yml
5. Riavvia il server

Se il server e dietro un proxy (BungeeCord o Velocity), copia il plugin anche nella cartella plugins/ del proxy.

---

## Configurazione MySQL

Crea il database e l'utente:

    CREATE DATABASE wizmmsg CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    CREATE USER 'wizmmsg'@'localhost' IDENTIFIED BY 'cambiami';
    GRANT ALL PRIVILEGES ON wizmmsg.* TO 'wizmmsg'@'localhost';
    FLUSH PRIVILEGES;

Poi modifica plugins/WizmMsg/config.yml:

    database:
      host: localhost
      port: 3306
      name: wizmmsg
      user: wizmmsg
      password: "cambiami"
      table: wizmmsg_sessions

La tabella wizmmsg_sessions viene creata automaticamente al primo avvio.

---

## Configurazione messaggi

Tutti i messaggi sono personalizzabili nel file config.yml alla sezione messages.

Placeholder disponibili:

| Placeholder | Significato |
|---|---|
| %sender% | Nome di chi invia il messaggio |
| %target% | Nome di chi riceve il messaggio |
| %message% | Contenuto del messaggio |

## Note tecniche

- Il rilevamento del proxy avviene tramite Plugin Messaging Channel (BungeeCord) al primo join di un giocatore
- Se il proxy non risponde entro il timeout, il plugin passa in modalita locale
- La modalita proxy permette di inviare messaggi tra server diversi collegati allo stesso proxy
- La modalita locale limita i messaggi al singolo server
- Il database viene utilizzato per salvare l'ultimo interlocutore e lo stato di blocco messaggi


## Licenza

MIT
