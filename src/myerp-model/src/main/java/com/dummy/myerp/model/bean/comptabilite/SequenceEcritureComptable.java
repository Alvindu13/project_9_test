package com.dummy.myerp.model.bean.comptabilite;


import java.util.Objects;

/**
 * Bean représentant une séquence pour les références d'écriture comptable
 */
public class SequenceEcritureComptable {

    // ==================== Attributs ====================
    /** L'année */
    private String codeJournal;
    /** L'année */
    private Integer annee;
    /** La dernière valeur utilisée */
    private Integer derniereValeur;

    // ==================== Constructeurs ====================

    /**
     * Constructeur
     */
    public SequenceEcritureComptable() {
    }


    /**
     * Instantiates a new Sequence ecriture comptable.
     *
     * @param codeJournal    the code journal
     * @param annee          the annee
     * @param derniereValeur the derniere valeur
     */
    public SequenceEcritureComptable(String codeJournal, Integer annee, Integer derniereValeur) {
        this.codeJournal = codeJournal;
        this.annee = annee;
        this.derniereValeur = derniereValeur;
    }




    // ==================== Getters/Setters ====================
    public Integer getAnnee() {
        return annee;
    }
    public void setAnnee(Integer pAnnee) {
        annee = pAnnee;
    }
    public Integer getDerniereValeur() {
        return derniereValeur;
    }
    public void setDerniereValeur(Integer pDerniereValeur) {
        derniereValeur = pDerniereValeur;
    }
    public String getCodeJournal() { return codeJournal; }
    public void setCodeJournal(String codeJournal) { this.codeJournal = codeJournal; }

    // ==================== Méthodes ====================
    @Override
    public String toString() {
        final StringBuilder vStB = new StringBuilder(this.getClass().getSimpleName());
        final String vSEP = ", ";
        vStB.append("{")
                .append("code journal=").append(codeJournal)
                .append("annee=").append(annee)
                .append(vSEP).append("derniereValeur=").append(derniereValeur)
                .append("}");
        return vStB.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SequenceEcritureComptable)) return false;
        SequenceEcritureComptable that = (SequenceEcritureComptable) o;
        return Objects.equals(getAnnee(), that.getAnnee()) &&
                Objects.equals(getDerniereValeur(), that.getDerniereValeur());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAnnee(), getDerniereValeur());
    }
}
