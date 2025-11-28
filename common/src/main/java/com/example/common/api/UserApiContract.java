package com.example.common.api;                           // Package che contiene i contratti API condivisi tra client e server.

import com.example.common.dto.UserDTO;                    // DTO che rappresenta un utente applicativo.

import java.util.List;                                    // Utilizzato per restituire elenchi di utenti.
import java.util.Optional;                                // Usato per risultati opzionali (utente non garantito).

/**
 * Contratto API condiviso per la gestione degli utenti applicativi.
 * Definisce le operazioni CRUD fondamentali a livello di amministrazione utenti.
 */
public interface UserApiContract {                        // Interfaccia che espone i metodi di gestione degli utenti.

    List<UserDTO> listUsers();                            // Restituisce l'elenco completo degli utenti presenti nel sistema.

    Optional<UserDTO> findById(Long id);                  // Recupera un utente tramite il suo identificatore.

    UserDTO create(UserDTO user);                         // Crea un nuovo utente con i dati forniti nel DTO.

    UserDTO update(Long id,                              // Aggiorna i dati dell’utente identificato dall’ID specificato,
                    UserDTO user);                       // sostituendo i dati con quelli del DTO.

    void delete(Long id);                                 // Elimina l’utente associato all’ID indicato.
}                                                         // Fine dell’interfaccia UserApiContract.
