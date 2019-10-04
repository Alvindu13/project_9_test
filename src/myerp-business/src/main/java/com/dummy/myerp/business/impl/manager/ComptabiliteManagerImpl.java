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
    private Calendar calendar = new GregorianCalendar();
    private Integer pAnneeEcriture;
    private String pAnnee;
    private String pCodeJournal = "";
    private String newSequence;



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

    @Override
    public SequenceEcritureComptable getSequenceEcritureComptableByAnnee(int pAnnee) throws NotFoundException {
        return getDaoProxy().getComptabiliteDao().getSequenceEcritureComptableByYear(pAnnee);
    }

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

        calendar.setTime(pEcritureComptable.getDate());
        Integer pAnneeEcriture = calendar.get(Calendar.YEAR);

        // On déclare la dernière valeurSéquence à 1
        int derniereValeurSequence = 1;

        // On instancie une séquence que nous allons manipuler
        SequenceEcritureComptable pSequenceEcritureComptable = new SequenceEcritureComptable();
        pSequenceEcritureComptable.setAnnee(pAnneeEcriture);

        //1 et 2
        // Cette boucle ajoute 1 à l'enregistreement s'il existe
        for (SequenceEcritureComptable vSequenceEcritureComptable : getListSequenceComptable()) {
            if(vSequenceEcritureComptable.getAnnee().equals(pAnneeEcriture)){

                // On ajoute 1 à la dernière valeur de la séquence
                derniereValeurSequence =  vSequenceEcritureComptable.getDerniereValeur() + 1;

                // On set la dernière valeur de la séquence
                pSequenceEcritureComptable.setDerniereValeur(derniereValeurSequence);
            }
        }

        //Si l'enregistrement n'existe pas, la séquence vaut alors 1
        if (derniereValeurSequence == 1){
            // On set la séquence avec la dernière valeur (ici il s'agit de la première séquence donc 1)
            pSequenceEcritureComptable.setDerniereValeur(derniereValeurSequence);
        }

        // 3 - On met à jour la référence
        updateReference(pEcritureComptable, derniereValeurSequence);

        // 4 - On persiste la séquence
            // On insert la première séquence pour cette année dans la db
        if(derniereValeurSequence == 1) getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(pSequenceEcritureComptable);
            // On update la séquence avec la dernière valeur actualisée
        else getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(pSequenceEcritureComptable);
    }


    /**
     * Permet de mettre à jour la référence de l'écriture comptable
     * @param pEcritureComptable
     * @param derniereValeur
     */
    private void updateReference(EcritureComptable pEcritureComptable, int derniereValeur){
        pCodeJournal = pEcritureComptable.getJournal().getCode();
        newSequence = String.format("%05d", derniereValeur);
        pAnnee = Integer.toString(pAnneeEcriture);
        String ref = pCodeJournal + "-" + pAnnee + "/" + newSequence;
        pEcritureComptable.setReference(ref);
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


        calendar.setTime(pEcritureComptable.getDate());
        pAnneeEcriture = calendar.get(Calendar.YEAR);

        if (Integer.parseInt(pEcritureComptable.getReference().substring(3, 6)) != pAnneeEcriture){
            throw new FunctionalException(
                    "L'année de référence de l'écriture comptable ne correspond pas à la date de son écriture");
        }

        if(!pEcritureComptable.getReference().substring(0, 1).equals(pEcritureComptable.getJournal().getCode())){
            throw new FunctionalException(
                    "Le code journal de référence ne correspond pas à la référence du journal lors de son écriture");
        }

        try {
            // On remonte la séquence pour l'année d'écriture
            SequenceEcritureComptable pSequenceEcritureComptable = getDaoProxy()
                            .getComptabiliteDao()
                            .getSequenceEcritureComptableByYear(pAnneeEcriture);
            // On reconstruit la séquence au bon format
            String sequence = String.format("%05d", pSequenceEcritureComptable.getDerniereValeur());
            if(!pEcritureComptable.getReference().substring(8, 12).equals(sequence)){
                throw new FunctionalException(
                        "La séquence de référence ne correspond pas à la séquence de son écriture");
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertSequenceEcritureComptable(SequenceEcritureComptable pSequenceEcritureComptable) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(pSequenceEcritureComptable);
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
    public void updateSequenceEcritureComptable(SequenceEcritureComptable pSequenceEcritureComptable) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(pSequenceEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }


}
