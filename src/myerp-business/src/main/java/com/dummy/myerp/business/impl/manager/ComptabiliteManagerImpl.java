package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.*;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import jdk.nashorn.internal.parser.DateParser;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ========================

    Calendar calendar = new GregorianCalendar();





    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }

    @Override
    public List<SequenceEcritureComptable> getListSequenceComptable() {
        return getDaoProxy().getComptabiliteDao().getListSequenceEcritureComptable();
    }

    /*@Override
    public SequenceEcritureComptable getSequenceEcritureComptableByAnnee(int pAnnee) throws NotFoundException {
        return getDaoProxy().getComptabiliteDao().getSequenceEcritureComptableByYear(pAnnee);
    }*/

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) {
        // TODO à implémenter

        // Bien se réferer à la JavaDoc de cette méthode !
        /* Le principe :
                1.  Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture
                    (table sequence_ecriture_comptable)
                2.  * S'il n'y a aucun enregistrement pour le journal pour l'année concernée :
                        1. Utiliser le numéro 1.
                    * Sinon :
                        1. Utiliser la dernière valeur + 1
                3.  Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)
                4.  Enregistrer (insert/update) la valeur de la séquence en persitance
                    (table sequence_ecriture_comptable)

         */


        // OK Jérrémie la dernire valeur correspond à la valeur de la dernière séquence du journal (exemple : 00112)
        // L'année de l'écriture correspond surrement à getDate de l'écriture comptable
        calendar.setTime(pEcritureComptable.getDate());
        Integer pAnneeEcriture = calendar.get(Calendar.YEAR);
        Integer derniereValeurSequence = 1;


        for (SequenceEcritureComptable vSequenceEcritureComptable : getListSequenceComptable()) {
            if(vSequenceEcritureComptable.getAnnee() == pEcritureComptable.getDate().getYear()){
                derniereValeurSequence =  vSequenceEcritureComptable.getDerniereValeur() + 1;
            }
        }

        if (derniereValeurSequence == 1)
        getDaoProxy().getComptabiliteDao().insertEcritureComptable();




        //1. On recup la séquence de la dernière écriture comptable dans le journal ##### => (XX-AAAA/#####)


        /*try {
            SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
            EcritureComptable lastDAOEcritureComptable = getDaoProxy()
                    .getComptabiliteDao()
                    .getEcritureComptable(sequenceEcritureComptable.getDerniereValeur());


            String reference = lastDAOEcritureComptable.getReference();
            String[] ref = reference.split("/");
            String codePlusYear = ref[0];
            String sequence = ref[1];
            String[] journalInfos = codePlusYear.split("-");
            String journalCode = journalInfos[0];
            String currentYear = journalInfos[1];


            // On convertit le string en int pour faciliter la condition
            int seqNumber = Integer.parseInt(sequence);

            //2. On vérifie les enregistrements
            if (seqNumber == 0){
                seqNumber = 1;
            } else {
                seqNumber += 1;
            }

            // On reconvert le int en string sequence avec 5 caractères
            String newSequence = String.format("%05d", seqNumber);

            // Definir la référence à partir des infos qu'on a calculé
            String newReference = journalCode + "-" + currentYear + "/" + newSequence;

            //3. Metre à jour la référence de la présente écriture avec la nouvelle référence
            pEcritureComptable.setReference(newReference);

            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);

        } catch (NotFoundException e) {
            e.printStackTrace();
        }*/



    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    // TODO tests à compléter
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }

        /*--------------AJOUT-------
        // ===== RG_Compta_1 : Le solde d'un compte comptable est \u00E9gal \u00E0 la somme des montants
        // au débit des lignes d'écriture diminuées de la somme des montants au crédit.
        // Si le résultat est positif, le solde est dit "débiteur", si le résultat est négatif le solde est dit "créditeur".

        BigDecimal soldeCompte = pEcritureComptable.getTotalDebit().subtract(pEcritureComptable.getTotalCredit());

        if(soldeCompte.compareTo(BigDecimal.ZERO) > 0) pEcritureComptable.setLibelle("solde débiteur");
        else if(soldeCompte.compareTo(BigDecimal.ZERO) < 0) pEcritureComptable.setLibelle("solde créditeur");*/



        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
            || vNbrCredit < 1
            || vNbrDebit < 1) {
            throw new FunctionalException(
                "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }

        // TODO ===== RG_Compta_5 : Format et contenu de la référence
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...

        //addReference(pEcritureComptable);


        /*calendar.setTime(pEcritureComptable.getDate());

        if (Integer.parseInt(pEcritureComptable.getReference().substring(3, 6)) != calendar.get(Calendar.YEAR)){
            //remplacer par un logger ou mettre un try catch
            System.out.println("");
            throw new FunctionalException(
                    "L'année de référence de l'écriture comptable ne correspond pas à la date de son écriture");
        }

        if(!pEcritureComptable.getReference().substring(0, 1).equals(pEcritureComptable.getJournal().getCode())){
            throw new FunctionalException(
                    "Le code journal de référence ne correspond pas à la référence du journal lors de son écriture");
        }


        Integer numberSequence = getListEcritureComptable().size();
        String codeSequence = pEcritureComptable.getJournal().getCode();
        // On met le string au bon format
        String.format("%03d", numberSequence);
        // On reconstruit la séquence
        String sequence = codeSequence + numberSequence;

        if(!pEcritureComptable.getReference().substring(8, 12).equals(sequence)){
            throw new FunctionalException(
                    "La séquence de référence ne correspond pas à la séquence de son écriture");
        }*/

    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                    pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                    || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
}
